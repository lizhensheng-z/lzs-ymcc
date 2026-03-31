package cn.lzs.ymcc.filter;

import cn.lzs.ymcc.util.JwtUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Token 验证过滤器
 * 在网关层统一验证 JWT Token
 */
@Slf4j
@Component
public class TokenAuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String REDIS_TOKEN_PREFIX = "TOKEN:";

    // 白名单路径（不需要 Token 验证）
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/uaa/login/**",
            "/user/user/register",
            "/kill/killCourse/online/all",     // 查询秒杀课程列表
            "/kill/killCourse/online/one/**",  // 查询单个秒杀课程
            "/system/tenant/entering",
            "/system/tenantType/list",
            "/auth/meal/list",
            "/common/verifyCode/**",
            "/common/oss/**",
            "/v2/api-docs",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 白名单放行
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        // 2. 获取 Token
        String token = getToken(request);
        if (token == null || token.isEmpty()) {
            return unauthorized(exchange, "未登录或Token已过期");
        }

        // 3. 验证 Token
        if (!JwtUtil.validateToken(token)) {
            return unauthorized(exchange, "Token无效或已过期");
        }

        // 4. 将用户信息添加到请求头（传递给下游服务）
        String loginJson = JwtUtil.getLoginJson(token);
        if (loginJson != null) {
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Info", loginJson)
                    .build();
            exchange = exchange.mutate().request(mutatedRequest).build();
        }

        log.debug("Token验证通过: path={}", path);
        return chain.filter(exchange);
    }

    /**
     * 判断是否为白名单路径
     */
    private boolean isWhitePath(String path) {
        // 移除前缀 /ymcc
        String actualPath = path;
        if (path.startsWith("/ymcc")) {
            actualPath = path.substring(5);
        }
        for (String pattern : WHITE_LIST) {
            if (pathMatcher.match(pattern, actualPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从请求头获取 Token
     */
    private String getToken(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(TOKEN_HEADER);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            return null;
        }
        return header.substring(TOKEN_PREFIX.length());
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        result.put("code", "401");

        byte[] bytes = JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100; // 优先级高，先执行
    }
}