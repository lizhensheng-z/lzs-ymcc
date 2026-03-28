package cn.lzs.ymcc;

/**
 * 系统错误码
 **/
public enum GlobalExceptionCode {

    // 成功
    SERVICE_OK("0", "服务正常！"),

    // 失败
    SERVICE_ERROR("-1", "服务不可用，请稍后重试！"),

    // 100xx公共异常
    SERVICE_PARAM_IS_NULL("10001", "参数不能为空！"),

    RETURN_PARAMETER_IS_NULL("10002", "返回参数为空！"),
    PAY_ERROR("XXXX","支付方式错误"),

    // system服务：200xx

    // uaa服务：300xx

    LOGIN_ACCOUNT_ERROR("30001", "登录失败,账号不存在或错误！"),
    LOGIN_PASSWORD_ERROR("30001", "登录失败,密码错误！"),
    // user服务：400xx开头
    PHONE_REGISTER_ERROR("40001", "注册失败,手机号已被注册！"),

    REFERRAL_CODE_ERROR("40002", "注册失败,推荐码错误或已被使用！"),

    // 500xx公共异常
    PHONE_PARAM_IS_NULL("50001", "手机号码不能为空！"),

    VERIF_CODE_SENT_REPEATEDLY("50002", "五分钟之内请勿重复发送验证码！"),


    MAP_VERIF_PARAM_IS_NULL("50003", "图形验证码为空或过期！"),

    PHONE_VERIF_PARAM_IS_NULL("50004", "短信验证码为空或过期！"),

    PHONE_VERIF_PARAM_IS_ERROR("50005", "短信验证码错误！"),

    MAP_VERIF_PARAM_IS_ERROR("50006", "图形验证码错误！"),

    PHONE_FORMAT_ERROR("50007", "手机号码格式错误！"),

    PHONE_BLACKLIST_ERROR("50008", "您已经被禁⽤发送短信验证码，请联系管理员！"),

    INSERT_BLACKLIST_ERROR("50009", "用户已经存在黑名单，请勿重复添加！"),
    TOKEN_IS_NULL_ERROR("50010", "token失效，请重新发送！"),
    TOKEN_IS_ERROR("50011", "token错误，请重新发送！"),
    // 600xx课程服务异常
    COURSE_REPEAT_ERROR("60001", "课程已经存在，请勿重复添加！"),
    COURSE_IS_NULL_ERROR("60002", "课程不存在，请重新输入！"),
    COURSE_STATUS_ERROR("60003", "课程状态非法！"),
    COURSE_RELEASE_ERROR("60004", "发布课程错误！"),
    COURSE_WATCH_ERROR("60005", "观看失败,请购买课程"),
    COURSE_EXPIRED_ERROR("60006", "观看失败,课程已过期,请重新购买课程"),
    COURSE_NUMBER_ERROR("60007", "课程数量不正确,请重新发送!"),
    // 700xx媒体服务异常
    MEDIA_IS_ERROR("70001", "视频不存在或状态异常"),
    // 800xx订单服务异常
    ORDER_FAILED_ERROR("80001","下单失败!"),
    DUPLICATE_ORDER_ERROR("80002","下单失败,订单重复!"),
    ORDER_IS_NULL_ERROR("80003","订单不存在!"),
    ORDER_INFO_ERROR("80004","订单信息异常"),
    ORDER_ALIPAY_STATE_ERROR("80005","支付宝状态不合法"),
    PAY_ORDER_STATE_ERROR("80008","支付状态不合法"),
    ORDER_EXIST_ERROR("80006","订单已存在"),
    // 900xx秒杀服务异常
    KILL_ACTIVITY_IS_NOTNULL_ERROR("90001","已经存在秒杀课程，请勿重复添加"),
    KILL_ACTIVITY_IS_REPEAT_ERROR("90004","已经参与过秒杀课程，请勿重复秒杀"),
    KILL_ACTIVITY_TIME_ERROR("90002","秒杀活动时间异常"),
    KILL_ACTIVITY_IS_NULL_ERROR("90003","秒杀活动不存在!"),
    LOGIN_USER_IS_NULL_ERROR("90006","用户不存在!"),
    KILL_COURSE_IS_NULL_ERROR("90005","手速太慢.秒杀已结束!");


    // 异常码
    private String code;

    // 异常信息
    private String message;

    private GlobalExceptionCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}