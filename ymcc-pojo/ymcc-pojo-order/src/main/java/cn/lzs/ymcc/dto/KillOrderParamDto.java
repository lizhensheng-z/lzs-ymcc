package cn.lzs.ymcc.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class KillOrderParamDto {

    //支付方式
    @NotNull(message = "支付方式不能为空")
    private Integer payType;
    //防重复token
    @NotEmpty(message = "token不能为空")
    private String token;
    //标识：0普通订单  1秒杀订单
    @NotEmpty(message = "订单不能为空")
    private String orderNo;


}
