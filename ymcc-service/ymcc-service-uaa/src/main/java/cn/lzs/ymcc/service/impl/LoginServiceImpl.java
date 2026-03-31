package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.DTO.LoginDTO;
import cn.lzs.ymcc.DTO.RefreshTokenDTO;
import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.VO.AccessTokenVo;
import cn.lzs.ymcc.constant.SystemConstants;
import cn.lzs.ymcc.domain.Login;
import cn.lzs.ymcc.mapper.LoginMapper;
import cn.lzs.ymcc.service.ILoginService;
import cn.lzs.ymcc.util.JwtUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 登录表 服务实现类
 * 使用 JWT 生成 Token，不再依赖 OAuth2
 */
@Service
@Slf4j
public class LoginServiceImpl extends ServiceImpl<LoginMapper, Login> implements ILoginService {

    @Resource
    private LoginMapper loginMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    private static final String REDIS_TOKEN_PREFIX = "TOKEN:";
    private static final long ACCESS_TOKEN_EXPIRE_SECONDS = 7200; // 2小时
    private static final long REFRESH_TOKEN_EXPIRE_SECONDS = 2592000; // 30天

    @Override
    public AccessTokenVo loginCommon(LoginDTO loginDTO) {
        // 1. 查询用户
        Login login = selectOne(new EntityWrapper<Login>().eq("username", loginDTO.getUsername()));
        if (login == null) {
            throw new GlobalBusinessException(SystemConstants.USER_IS_NOT_EXIST);
        }

        // 2. 验证用户类型
        if (!loginDTO.getType().equals(login.getType())) {
            throw new GlobalBusinessException(SystemConstants.USER_TYPE_IS_NOT_MATCH);
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), login.getPassword())) {
            throw new GlobalBusinessException("密码错误");
        }

        // 4. 检查账户状态
        if (!Boolean.TRUE.equals(login.getEnabled())) {
            throw new GlobalBusinessException("账户已被禁用");
        }

        // 5. 生成 JWT Token
        String accessToken = JwtUtil.generateAccessToken(login);
        String refreshToken = JwtUtil.generateRefreshToken(login);

        // 6. 存储到 Redis（用于主动踢人下线等场景）
        String tokenKey = REDIS_TOKEN_PREFIX + login.getId();
        redisTemplate.opsForValue().set(tokenKey, accessToken, ACCESS_TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);

        // 7. 构建返回对象
        AccessTokenVo tokenVo = new AccessTokenVo();
        tokenVo.setAccessToken(accessToken);
        tokenVo.setRefreshToken(refreshToken);
        tokenVo.setTokenType("Bearer");
        tokenVo.setExpiresTime(ACCESS_TOKEN_EXPIRE_SECONDS);

        log.info("用户登录成功: username={}", login.getUsername());
        return tokenVo;
    }

    @Override
    public AccessTokenVo refresh(RefreshTokenDTO refreshTokenDTO) {
        // 1. 验证 refresh token
        Login login = JwtUtil.parseToken(refreshTokenDTO.getRefreshToken());
        if (login == null) {
            throw new GlobalBusinessException("Refresh Token 无效或已过期");
        }

        // 2. 验证用户是否存在
        Login dbLogin = selectById(login.getId());
        if (dbLogin == null) {
            throw new GlobalBusinessException(SystemConstants.USER_IS_NOT_EXIST);
        }

        // 3. 生成新的 Token
        String accessToken = JwtUtil.generateAccessToken(dbLogin);
        String refreshToken = JwtUtil.generateRefreshToken(dbLogin);

        // 4. 更新 Redis
        String tokenKey = REDIS_TOKEN_PREFIX + dbLogin.getId();
        redisTemplate.opsForValue().set(tokenKey, accessToken, ACCESS_TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);

        // 5. 返回
        AccessTokenVo tokenVo = new AccessTokenVo();
        tokenVo.setAccessToken(accessToken);
        tokenVo.setRefreshToken(refreshToken);
        tokenVo.setTokenType("Bearer");
        tokenVo.setExpiresTime(ACCESS_TOKEN_EXPIRE_SECONDS);

        log.info("Token刷新成功: username={}", dbLogin.getUsername());
        return tokenVo;
    }
}