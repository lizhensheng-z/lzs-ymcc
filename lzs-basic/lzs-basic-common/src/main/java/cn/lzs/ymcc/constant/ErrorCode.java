package cn.lzs.ymcc.constant;

/**
 * @author 李振生
 */
/** 系统错误码 **/

public enum ErrorCode{
    SYSTEM_ERROR("1001","系统内部异常");
    //错误码
    private String code;
    //错误信息
    private String message;
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message+"["+code+"]";
    }
}