package cn.lzs.ymcc.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用户订单列表VO
 * 用于返回用户端订单列表数据
 */
@Data
public class UserOrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单标题
     */
    private String title;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单数量
     */
    private Integer totalCount;

    /**
     * 订单状态：0待支付 1支付成功 2用户取消 3支付失败 4超时取消
     */
    private Integer statusOrder;

    /**
     * 订单状态描述
     */
    private String statusDesc;

    /**
     * 支付方式：0余额 1支付宝 2微信 3银联
     */
    private Integer payType;

    /**
     * 支付方式描述
     */
    private String payTypeDesc;

    /**
     * 创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 订单明细列表
     */
    private List<OrderItemVO> items;

    /**
     * 订单明细VO
     */
    @Data
    public static class OrderItemVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 明细ID
         */
        private Long id;

        /**
         * 课程ID
         */
        private Long courseId;

        /**
         * 课程名称
         */
        private String courseName;

        /**
         * 课程封面
         */
        private String coursePic;

        /**
         * 课程价格
         */
        private BigDecimal amount;

        /**
         * 数量
         */
        private Integer count;
    }
}
