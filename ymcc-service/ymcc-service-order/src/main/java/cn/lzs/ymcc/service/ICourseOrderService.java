package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.CourseOrder;
import cn.lzs.ymcc.dto.PlaceOrderDTO;
import cn.lzs.ymcc.dto.UpdatePayStatusOrderDTO;
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
}
