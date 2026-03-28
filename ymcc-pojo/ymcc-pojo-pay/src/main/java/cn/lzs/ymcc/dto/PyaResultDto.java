package cn.lzs.ymcc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  支付单表数据库中,扩展参数表:所需要的字段
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PyaResultDto {
    private String orderNo;
    private String extParams;  //  loginId + courseIds

}
