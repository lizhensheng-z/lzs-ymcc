package cn.lzs.ymcc.dto;

import cn.lzs.ymcc.constant.SystemConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author 李振生
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneRegisterDTO {
    @Pattern(regexp = SystemConstants.PHONE_VALIDATE_REGEX,message = "手机号格式不正确")
    private String mobile;
    @Pattern(regexp = SystemConstants.PASSWORD_VALIDATE_REGEX,message = "密码必须包含字母和数字，6-20位")
    private String password;
    @NotNull
    private Integer regChannel;
    @NotNull(message = SystemConstants.VERIFY_CODE_IS_NOT_NULL)
    private String smsCode;
}
