package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.CourseOrder;
import cn.lzs.ymcc.dto.KillOrderParamDto;
import cn.lzs.ymcc.dto.PlaceOrderDTO;
import cn.lzs.ymcc.dto.PyaResultDto;
import cn.lzs.ymcc.dto.UpdatePayStatusOrderDTO;
import cn.lzs.ymcc.dto.UserOrderQueryDTO;
import cn.lzs.ymcc.vo.UserOrderVO;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lzs
 * @since 2025-09-19
 */
public interface ICourseOrderService extends IService<CourseOrder> {

    String placeOrder(PlaceOrderDTO placeOrderDTO);

    Boolean updatePayStatusByOrderNo(UpdatePayStatusOrderDTO updatePayStatusOrderDTO);

    List<Long> getCourseIdByOrderNo(String orderNo);

    /**
     *  保存主订单和子订单
     * @param courseOrder
     */
    void saveOrderAndItems(CourseOrder courseOrder);

    /**
     *  根据订单号查询订单
     * @param orderNo
     * @return
     */
    CourseOrder selectByOrderNo(String orderNo);

    /**
     *  处理订单的支付结果
     * @param orderDto
     */
    void payResultHandle(PyaResultDto orderDto);

    /**
     *  处理超时订单 修改订单支付状态为取消
     * @param orderNo
     */
    void payTimeOutCancelOrder(String orderNo);


    /**
     * 秒杀下单
     * @param dto
     * @return
     */
    String killPlaceOrder(KillOrderParamDto dto);

    /**
     * 保存秒杀订单（简化版，直接标记为已支付）
     * @param orderNo 订单号
     * @param courseId 课程ID
     * @param amount 金额
     * @param userId 用户ID
     * @param payType 支付方式
     * @return 订单号
     */
    String saveKillOrder(String orderNo, Long courseId, java.math.BigDecimal amount, Long userId, Integer payType);

    /**
     * 用户端分页查询订单列表（支持日期范围查询）
     * @param userId 用户ID
     * @param query 查询参数
     * @return 分页结果
     */
    Page<UserOrderVO> queryUserOrderPage(Long userId, UserOrderQueryDTO query);

    /**
     * 用户端订单详情查询
     * @param userId 用户ID
     * @param orderNo 订单编号
     * @return 订单详情
     */
    UserOrderVO getUserOrderDetail(Long userId, String orderNo);
}
