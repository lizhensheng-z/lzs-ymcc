package cn.lzs.ymcc.util;

import cn.lzs.ymcc.domain.Login;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author 李振生
 */
@Slf4j
public class LoginContext {

    //获取用户信息
    public static Login getLogin(){
        try {
            //获取上下文
            SecurityContext context = SecurityContextHolder.getContext();

            if(context == null){
                return null;
            }

            Authentication authentication = context.getAuthentication();

            if(authentication == null){
                return null;
            }
            //转对象
            return JSON.parseObject((String) authentication.getPrincipal(), Login.class);

        }catch (Exception e){
            log.error("用户登录对象获取失败");
            e.printStackTrace();
            return null;
        }
    }
}

