package cn.lzs.ymcc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 李振生
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderDTO {
    //购买的课程id
    private List<Long>  courseIds;
    //支付方式
    private Integer payType;
    //支付 防重复token
    private String token;
    //是否为 秒杀类型 0 否 1是
    private Integer type;
}
