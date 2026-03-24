package cn.lzs.ymcc.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 李振生
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDTO {
    private String refreshToken;
    private String username;
}
