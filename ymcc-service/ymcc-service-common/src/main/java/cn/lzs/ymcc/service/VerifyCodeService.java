package cn.lzs.ymcc.service;

/**
 * @author 李振生
 */
public interface VerifyCodeService {

    /**
     * 生成验证码并返回
     * @param phone 手机号
     * @return 验证码
     */
    String generateCode(String phone);
}
