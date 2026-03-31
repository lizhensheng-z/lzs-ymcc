package cn.lzs.ymcc.util;

import cn.lzs.ymcc.domain.Login;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * 用于生成和解析 JWT Token
 */
@Slf4j
public class JwtUtil {

    // 密钥（建议后续配置到配置文件中）
    private static final String SECRET_KEY = "ymcc-secret-key-for-jwt-token-generation-2024-secure";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    // Access Token 有效期：2小时（毫秒）
    private static final long ACCESS_TOKEN_EXPIRE = 2 * 60 * 60 * 1000L;
    // Refresh Token 有效期：30天（毫秒）
    private static final long REFRESH_TOKEN_EXPIRE = 30 * 24 * 60 * 60 * 1000L;

    /**
     * 生成 Access Token
     */
    public static String generateAccessToken(Login login) {
        return generateToken(login, ACCESS_TOKEN_EXPIRE);
    }

    /**
     * 生成 Refresh Token
     */
    public static String generateRefreshToken(Login login) {
        return generateToken(login, REFRESH_TOKEN_EXPIRE);
    }

    /**
     * 生成 Token
     */
    private static String generateToken(Login login, long expireTime) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .setSubject(login.getId().toString())
                .claim("login", JSON.toJSONString(login))
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token 获取 Login 对象
     */
    public static Login parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String loginJson = claims.get("login", String.class);
            return JSON.parseObject(loginJson, Login.class);
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("不支持的Token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Token格式错误: {}", e.getMessage());
        } catch (SecurityException e) {
            log.error("Token签名验证失败: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Token为空: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Token解析失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 验证 Token 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取 Token 剩余有效时间（秒）
     */
    public static long getExpiresInSeconds(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Date expiration = claims.getExpiration();
            long remaining = (expiration.getTime() - System.currentTimeMillis()) / 1000;
            return remaining > 0 ? remaining : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 从 Token 获取用户ID
     */
    public static Long getUserId(String token) {
        Login login = parseToken(token);
        return login != null ? login.getId() : null;
    }
}