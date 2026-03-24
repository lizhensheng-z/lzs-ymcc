package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.constant.RedisConstants;
import cn.lzs.ymcc.service.ITokenService;
import cn.lzs.ymcc.util.LoginContext;
import cn.lzs.ymcc.util.StrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.concurrent.TimeUnit;


/**
 * @author 李振生
 */
@Service
public class TokenServiceImpl implements ITokenService {
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public String createToken(Long courseId) {
       //参数校验
        if(courseId == null){
            throw new GlobalBusinessException("课程id不能为空");
        }
        Long loginId = LoginContext.getLogin().getId();
        if(loginId == null){
            throw new GlobalBusinessException("用户未登录");
        }
        //开始生成token
        String token  = StrUtils.getComplexRandomString(16);
        String key = String.format(RedisConstants.REDIS_REPEAT_REFRESH_KEY_PREFIX,loginId,courseId);
        //存入redis
        redisTemplate.opsForValue().set(key,token,5, TimeUnit.MINUTES);
        return token;
    }
}
