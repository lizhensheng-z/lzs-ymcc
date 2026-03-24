package cn.lzs.ymcc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpFilter;

/**
 * @author 李振生
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerifyCode {
    // 验证码
    private String code;
    // 过期时间
    private Long time;


}
