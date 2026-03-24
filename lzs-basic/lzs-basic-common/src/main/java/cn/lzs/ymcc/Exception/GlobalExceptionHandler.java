package cn.lzs.ymcc.Exception;

import cn.lzs.ymcc.constant.ErrorCode;
import cn.lzs.ymcc.result.JSONResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

/**
 * @author 李振生
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    //拦截异常 : 这个注解就可以拦截器 GlobalException 异常
    @ExceptionHandler(GlobalBusinessException.class)
    public JSONResult globalException(GlobalBusinessException e){
        e.printStackTrace();
        return JSONResult.error(e.getMessage(),ErrorCode.SYSTEM_ERROR.getCode());
    }
//    拦截器其他异常
    @ExceptionHandler(Exception.class)
    public JSONResult exception(Exception e){
        e.printStackTrace();
        if(e.getClass().getSimpleName().equals("AccessDeniedException")){
            return JSONResult.error("不好意思哦，您没有权限访问");
        }
        return JSONResult.error("系统异常,程序员抢修中...");
    }

}
