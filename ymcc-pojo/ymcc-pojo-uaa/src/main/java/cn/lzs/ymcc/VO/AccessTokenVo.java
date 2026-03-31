package cn.lzs.ymcc.VO;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Access Token 返回对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenVo {
    @JSONField(name = "access_token")
    @JsonProperty("access_token")
    private String accessToken;

    @JSONField(name = "refresh_token")
    @JsonProperty("refresh_token")
    private String refreshToken;

    @JSONField(name = "token_type")
    @JsonProperty("token_type")
    private String tokenType;

    @JSONField(name = "expires_in")
    @JsonProperty("expires_in")
    private Long expiresTime;

    private String scope;
    private String jti;
}
