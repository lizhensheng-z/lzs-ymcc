package cn.lzs.ymcc.util;

import cn.lzs.ymcc.domain.Login;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录上下文工具类
 * 从请求头中获取 JWT Token 并解析用户信息
 */
@Slf4j
public class LoginContext {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 获取当前登录用户信息
     */
    public static Login getLogin() {
        try {
            String token = getToken();
            if (token == null || token.isEmpty()) {
                return null;
            }
            return JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.error("获取登录用户信息失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从请求头获取 Token
     */
    public static String getToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        String header = request.getHeader(TOKEN_HEADER);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            return null;
        }
        return header.substring(TOKEN_PREFIX.length());
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getLoginId() {
        Login login = getLogin();
        return login != null ? login.getId() : null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getUsername() {
        Login login = getLogin();
        return login != null ? login.getUsername() : null;
    }
}