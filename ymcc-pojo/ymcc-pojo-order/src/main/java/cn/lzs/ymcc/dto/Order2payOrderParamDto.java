package cn.lzs.ymcc.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 包装生成支付单需要的消息体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order2payOrderParamDto {
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 支付方式:0余额直接，1支付宝，2微信,3银联
     */
    @TableField("pay_type")
    private Integer payType;
    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;
    @TableField("user_id")
    private Long userId;
    /**
     * 扩展参数，格式： xx=1&oo=2
     */
    @TableField("ext_params")
    private String extParams;
    /**
     * 描述
     */
    private String subject;

}
