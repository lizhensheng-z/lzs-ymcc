package cn.lzs.ymcc.VO;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JSONField(name= "expires_in")
    private Long expiresTime;

    private String scope;
    private String jti;

}
