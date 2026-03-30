package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.constant.RedisConstants;
import cn.lzs.ymcc.constant.SystemConstants;
import cn.lzs.ymcc.pojo.VerifyCode;
import cn.lzs.ymcc.util.VerifyCodeUtils;
import cn.lzs.ymcc.service.VerifyCodeService;
import cn.lzs.ymcc.util.SendSmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author 李振生
 */
@Service
@Slf4j
public class verifyCodeServiceImpl implements VerifyCodeService {
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public String generateCode(String phone) {
        //校验手机号是否合法
        if(StringUtils.isEmpty(phone)){
            throw new GlobalBusinessException(SystemConstants.PHONE_IS_NOT_NULL);
        }

        if(!phone.matches(SystemConstants.PHONE_VALIDATE_REGEX)){
            throw new GlobalBusinessException(SystemConstants.PHONE_IS_NOT_VALID);
        }

        //从redis查询验证码是否存在
        //定义key
        StringBuffer key = new StringBuffer(RedisConstants.REDIS_VERIFY_CODE_KEY_PREFIX);
        key.append(phone);

        VerifyCode verifyCode = (VerifyCode) redisTemplate.opsForValue().get(key);
        if(verifyCode == null){
            //不存在生成验证码返回
            String code = VerifyCodeUtils.generateVerifyCode(6);
            Long time  = new Date().getTime();
            verifyCode = new VerifyCode();
            verifyCode.setTime(time);
            log.info("生成的验证码为：{}",code);
            verifyCode.setCode(code);
            //将验证码存入redis，设置过期时间
        }else{
            //存在则判断验证码存活时间
            if(new Date().getTime() - verifyCode.getTime() < 60*1000){
                //存活时间小于一分钟，抛出提升并返回
                throw new GlobalBusinessException(SystemConstants.VERIFY_CODE_IS_NOT_SEND_AGAIN);
            }
            //存活时间大于一分钟，刷新验证码过期时间返回
            verifyCode.setTime(new Date().getTime());

        }
        // 两部分都要做 抽出来放到最后将验证码存入redis，设置过期时间
        redisTemplate.opsForValue().set(key,verifyCode,5, TimeUnit.MINUTES);
        // 模拟发送短信
        SendSmsUtil.sendMessageCode(phone, verifyCode.getCode());
        return verifyCode.getCode();
    }
}
