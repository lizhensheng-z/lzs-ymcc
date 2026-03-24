package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.domain.PayOrder;
import cn.lzs.ymcc.dto.AlipayNotifyDTO;
import cn.lzs.ymcc.dto.PayOrderDTO;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.service.IPayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 李振生
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    @Autowired
    private IPayOrderService payOrderService;
    /**
     * 查询支付订单创建状态
     */
    @GetMapping("/checkPayOrder/{orderNo}")
    public JSONResult checkPayOrder(@PathVariable("orderNo") String orderNo){
         PayOrder payOrder =  payOrderService.checkPayOrder(orderNo);
         if(payOrder == null){
             return JSONResult.error("订单创建中");
         }
         return JSONResult.success("订单创建成功");
    }
    /**
     * 发起支付请求
     */
    @PostMapping("/apply")
    public JSONResult apply(@RequestBody PayOrderDTO payOrderDTO){
        //发起支付请求，返回三方支付的支付宝的表单页面
        String form = payOrderService.apply(payOrderDTO);
        return JSONResult.success(form);
    }
    /**
     * 支付宝支付成功异步回调接收接口
     * 不能用RequestBody接收，支付宝返回的不是json格式
     * success 如果支付成功一定要返回success给阿里，否则阿里认为失败继续回调
     */
    @PostMapping("/alipay/notify")
    public JSONResult alipayNotify(AlipayNotifyDTO alipayNotifyDTO){
        //TODO 支付宝支付成功回调
        String res = payOrderService.alipayNotify(alipayNotifyDTO);
        return JSONResult.success(res);
    }
}
