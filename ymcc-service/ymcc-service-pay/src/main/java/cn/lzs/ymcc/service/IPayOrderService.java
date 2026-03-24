package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.PayOrder;
import cn.lzs.ymcc.dto.AlipayNotifyDTO;
import cn.lzs.ymcc.dto.PayOrderDTO;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lzs
 * @since 2025-09-20
 */
public interface IPayOrderService extends IService<PayOrder> {

    PayOrder checkPayOrder(String orderNo);

    String apply(PayOrderDTO payOrderDTO);

    String alipayNotify(AlipayNotifyDTO alipayNotifyDTO);

    void cancelPayOrder(String outOrderNo);
}
