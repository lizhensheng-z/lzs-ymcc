package cn.lzs.ymcc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreOrderDto {

    private String orderNo; // 订单号

    private BigDecimal totalAmount ;  // 秒杀金额

    private Integer totalCount=1;  // 秒杀数量

    private Long userId; // 用户Id

    private Long courseId; // 秒杀课程id





}
