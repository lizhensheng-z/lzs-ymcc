package cn.lzs.ymcc.constant;

/**
 * @author 李振生
 */
public interface RedisConstants {
    //验证码key前缀
    String REDIS_VERIFY_CODE_KEY_PREFIX = "sms:code:";
    String REDIS_COURSE_TYPE_TREE_DATA = "course:type:tree:data";
    //防止重复刷新 前缀
    String REDIS_REPEAT_REFRESH_KEY_PREFIX = "repeat:refresh:%s:%s";

}
