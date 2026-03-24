package cn.lzs.ymcc.constant;

import java.math.BigDecimal;

/**
 * @author 李振生
 */
public interface SystemConstants {
    /**
     * 手机号合法校验
     */
   String PHONE_VALIDATE_REGEX = "^[1][3,4,5,6,7,8,9][0-9]{9}$";
   String PHONE_IS_NOT_NULL = "手机号不能为空";
   String PHONE_IS_NOT_VALID = "手机号不合法";
   String PHONE_IS_REGISTERED = "手机号已被注册";

   String PASSWORD_VALIDATE_REGEX = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$";


   String VERIFY_CODE_IS_NOT_SEND_AGAIN = "一分钟之内不能重复发送";
   //验证码不能为空
   String VERIFY_CODE_IS_NOT_NULL = "验证码不能为空";
   //验证码已过期
   String VERIFY_CODE_IS_EXPIRED = "验证码已过期";
   //验证码输入错误
   String VERIFY_CODE_IS_NOT_VALID = "验证码输入错误";


   //注册人员类型 0后台人员 1前台用户
   Integer REGISTER_TYPE_SYSTEM = 0;
   Integer REGISTER_TYPE_USER = 1;

   //用户状态 0启用
   Long USER_STATUS_Enable = 0L;
   //用户状态 1禁用
   Long USER_STATUS_Disable = 1L;

   //用户账户初始可用余额
   BigDecimal USER_ACCOUNT_INIT_BALANCE = new BigDecimal("0");
   //用户账户初始冻结余额
   BigDecimal USER_ACCOUNT_INIT_FREEZE_BALANCE = new BigDecimal("0");

   //"用户不存在"
   String USER_IS_NOT_EXIST = "用户不存在";
   String USER_TYPE_IS_NOT_MATCH = "用户类型不匹配";


   /**
    * 课程状态：0 下线 1 上线
    */
   Integer COURSE_OnLineCourse = 1;
   Integer COURSE_OffLineCourse = 0;


   /**
    * 资料类型：0课件，1其他
    */
   Integer FILE_TYPE_COURSEWARE = 0;
   Integer FILE_TYPE_OTHER = 1;

   /**
    * 课程默认时长
    */
   Integer COURSE_DEFAULT_DURATION = 0;

   /**
    * 系统消息
    */
   String SYSTEM_MESSAGE = "系统消息";

   /**
    * RocketMQ topic tag
    */
    String CoursePublishTopic = "course-publish-topic";
    String StationPublishTag = "station-publish-tag";
    String EmailPublishTag = "email-publish-tag";
    String SmsPublishTag = "sms-publish-tag";

   /**
    * 消息读取状态
    */
   Integer MESSAGE_READ = 1;
   Integer MESSAGE_UNREAD = 0;
}
