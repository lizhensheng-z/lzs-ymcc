package cn.lzs.ymcc.Exception;

/**
 * @author 李振生
 * 自定义异常
 */
public class GlobalBusinessException extends RuntimeException{
    public GlobalBusinessException(String message) {
        super(message);
    }
    public GlobalBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
    public GlobalBusinessException() {
        super();
    }
}
