package cn.lzs.ymcc.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * 用户端订单查询DTO
 * 用于用户端订单列表的日期范围查询
 */
@Data
public class UserOrderQueryDTO {

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 开始日期（订单创建时间范围查询）
     */
    @JSONField(format = "yyyy-MM-dd")
    private Date startDate;

    /**
     * 结束日期（订单创建时间范围查询）
     */
    @JSONField(format = "yyyy-MM-dd")
    private Date endDate;

    /**
     * 订单状态（可选，不传则查询所有状态）
     * 0-待支付 1-支付成功 2-用户取消 3-支付失败 4-超时取消
     */
    private Integer statusOrder;

    /**
     * 订单编号（模糊查询，可选）
     */
    private String orderNo;
}
