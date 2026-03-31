package cn.lzs.ymcc.VO;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 李振生
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenVo {
    @JSONField(name = "access_token")
    private String accessToken;
    @JSONField(name = "refresh_token")
    private String refreshToken;
    @JSONField(name = "token_type")
    private String tokenType;
    @JSONField(name = "expires_in")
    private Long expiresTime;

    private String scope;
    private String jti;

}
