package cn.lzs.ymcc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 李振生
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayOrderDTO {
    private String orderNo;
    private String callUrl;
    private String payType;
}
