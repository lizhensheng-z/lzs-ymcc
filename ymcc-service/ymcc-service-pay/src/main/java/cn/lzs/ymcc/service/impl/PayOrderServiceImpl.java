package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.api.CourseFeignApi;
import cn.lzs.ymcc.api.OrderFeignApi;
import cn.lzs.ymcc.api.UserApi;
import cn.lzs.ymcc.domain.*;
import cn.lzs.ymcc.dto.*;
import cn.lzs.ymcc.mapper.PayOrderMapper;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.service.IAlipayInfoService;
import cn.lzs.ymcc.service.IPayFlowService;
import cn.lzs.ymcc.service.IPayOrderService;
import com.alibaba.fastjson.JSONObject;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-09-20
 */
@Service
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder> implements IPayOrderService {


    @Autowired
    private IAlipayInfoService alipayInfoService;
    @Autowired
    private IPayOrderService payOrderService;
    @Autowired
    private IPayFlowService payFlowService;
    @Autowired
    private OrderFeignApi orderFeignApi;
    @Autowired
    private UserApi userApi;
    @Autowired
    private CourseFeignApi courseFeignApi;
    /**
     * 检查支付订单是否创建成功
     * @param orderNo
     * @return
     */
    @Override
    public PayOrder checkPayOrder(String orderNo) {
        EntityWrapper<PayOrder> payOrderEntityWrapper = new EntityWrapper<>();
        payOrderEntityWrapper.eq("order_no", orderNo);
        return this.selectOne(payOrderEntityWrapper);
    }

    /**
     * 发起支付请求
     * @param payOrderDTO
     * @return
     */
    @Override
    public String apply(PayOrderDTO payOrderDTO) {
        //从数据库查询alipayInfo 因为数据库只有一条，直接查询全部即可
        List<AlipayInfo> alipayInfos = alipayInfoService.selectList(null);
        AlipayInfo alipayInfo = alipayInfos.get(0);

        String orderNo = payOrderDTO.getOrderNo();
        String callUrl = payOrderDTO.getCallUrl();
        if(callUrl == null){
            //从数据库查询返回
            callUrl = alipayInfo.getReturnUrl();
        }
        //从数据库查询订单相关信息
        PayOrder payOrder = payOrderService.selectOne(new EntityWrapper<PayOrder>().eq("order_no", orderNo));
        Factory.setOptions(getOptions(alipayInfo));
        try {
            // 2. 发起API调用（以创建当面付收款二维码为例）

            AlipayTradePagePayResponse response = Factory.Payment.Page()
                    .pay(payOrder.getSubject(),
                            orderNo,
                            payOrder.getAmount().toString(),//注意单位转换，分，角，元 应该统一
                            callUrl);
            // 3. 处理响应或异常
            if (ResponseChecker.success(response)) {
                return response.getBody();
            } else {
                System.err.println("调用失败，原因：" + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("调用遭遇异常，原因：" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
        return "";
    }

    /**
     * 支付宝回调 TODO 分布式事务交给seata管理，用事务消息更好
     * @param alipayNotifyDTO
     * @return
     * 第一步：在通知返回参数列表中，除去 sign、sign_type 两个参数外，凡是通知返回回来的参数皆是待验签的参数。
     * 第二步：将剩下参数进行 url_decode，然后进行字典排序，组成字符串，得到待签名字符串：
     * 第三步：将签名参数（sign）使用 base64 解码为字节码串。
     * 第四步：使用 RSA 的验签方法，通过签名字符串、签名参数（经过 base64 解码）及支付宝公钥验证签名。
     */
    @Override
    public String alipayNotify(@Valid AlipayNotifyDTO alipayNotifyDTO) {
        //1 参数校验
        //2 业务参数校验：异步返回结果验签 第五步：
        //2.1. 商家需要验证该通知数据中的 out_trade_no 是否为商家系统中创建的订单号。
        PayOrder payOrder = payOrderService.selectOne(
                new EntityWrapper<PayOrder>()
                .eq("order_no", alipayNotifyDTO.getOut_trade_no()));
        if(payOrder == null){
            return "fail";
        }
        //2.2. 判断 total_amount 是否确实为该订单的实际金额（即商家订单创建时的金额）。
        if(!alipayNotifyDTO.getTotal_amount().contains(payOrder.getAmount().toString())){
            return "fail";
        }
        //2.3. 校验通知中的 seller_id（或者 seller_email）是否为 out_trade_no 这笔单据的对应的操作方（有的时候，一个商家可能有多个 seller_id/seller_email）。
        //2.4. 验证 app_id 是否为该商家本身。
        AlipayInfo alipayInfo = alipayInfoService.selectOne(
                new EntityWrapper<AlipayInfo>()
                        .eq("app_id", alipayNotifyDTO.getApp_id()));
        if(alipayInfo == null){
            return "fail";
        }
        //TODO 将alipayNotifyDTO转map并验签


        //3 异步回调成功，修改系统支付订单支付状态，支付流水表，课程交易订单状态，用户课程关系表
        //跨库操作，这里使用seata AT 或者使用RocketMQ事务消息
        //3.1 修改支付订单状态
        payOrder.setPayStatus(PayOrder.OrderComplete);
        payOrder.setUpdateTime(new Date());
        payOrderService.updateById(payOrder);
        //3.2 新增支付流水表
        PayFlow payFlow = new PayFlow();
        payFlow.setOutTradeNo(alipayNotifyDTO.getOut_trade_no());
        payFlow.setCode(alipayNotifyDTO.getCode());
        payFlow.setNotifyTime(new Date());
        payFlow.setSubject(alipayNotifyDTO.getSubject());
        payFlow.setTotalAmount(new BigDecimal(alipayNotifyDTO.getTotal_amount()));
        payFlow.setMsg(alipayNotifyDTO.getMsg());
        payFlow.setPaySuccess(alipayNotifyDTO.isTradeSuccess());
        payFlow.setTradeStatus(alipayNotifyDTO.getTrade_status());
        payFlow.setResultDesc("支付交易结果："+ (alipayNotifyDTO.isTradeSuccess() ? "成功": "失败"));
        payFlowService.insert(payFlow);
        //3.3 修改课程交易订单状态 远程调用 order服务 修改order的status by order_no
        UpdatePayStatusOrderDTO updatePayStatusOrderDTO = new UpdatePayStatusOrderDTO();
        updatePayStatusOrderDTO.setOrderNo(payOrder.getOrderNo());
        updatePayStatusOrderDTO.setUpdateTime(new Date());
        updatePayStatusOrderDTO.setStatusOrder(CourseOrder.OrderComplete);
        JSONResult booleanUpdateFlag = orderFeignApi.updatePayStatusByOrderNo(updatePayStatusOrderDTO);
//        if(Integer.parseInt(booleanUpdateFlag.getCode()) != 200){
//            throw new GlobalBusinessException("远程调用order服务修改订单状态失败");
//        }
        //3.4 新增用户课程关系表 远程调用 course服务 新增用户课程关系 需要的是loginId
        Long userId = payOrder.getUserId();
        JSONResult userJSONReSult = userApi.get(userId);
        //获取User的loginId
        Object data = userJSONReSult.getData();
        String jsonData = JSONObject.toJSONString(data);
        User user = JSONObject.parseObject(jsonData, User.class);
        Long loginId = user.getLoginId();
        //根据order_no查询订单详情表，拿到courseId
        JSONResult courseIdByOrderNo = orderFeignApi.getCourseIdByOrderNo(payOrder.getOrderNo());
        Object datas =courseIdByOrderNo.getData();
        String jsonDatas = JSONObject.toJSONString(datas);
        List<Long> courseIdList = JSONObject.parseArray(jsonDatas, Long.class);
        //新增用户课程关系表
        CourseUserLearnDTO courseUserLearnDTO = new CourseUserLearnDTO();
        courseUserLearnDTO.setCourseOrderNo(payOrder.getOrderNo());
        courseUserLearnDTO.setLoginId(loginId);
        courseUserLearnDTO.setCourseIds(courseIdList);
        courseUserLearnDTO.setCreateTime(new Date());
        courseUserLearnDTO.setStartTime(new Date());
        //当前时间的两年后 结束 TODO
        courseUserLearnDTO.setEndTime(new Date());
        courseUserLearnDTO.setStatus(CourseUserLearnDTO.BUYED);
        //远程调用course服务
        JSONResult jsonResult = courseFeignApi.addCourseUserLearn(courseUserLearnDTO);
//        if(Integer.parseInt(jsonResult.getCode()) != 200){
//            throw new GlobalBusinessException("远程调用course服务新增用户课程关系失败");
//        }
        return "success";
    }

    @Override
    public void cancelPayOrder(String outOrderNo) {

    }

    public  Config getOptions(AlipayInfo alipayInfo) {
        Config config = new Config();
        config.protocol = alipayInfo.getProtocol();
        config.gatewayHost = alipayInfo.getGatewayHost();
        config.signType = alipayInfo.getSignType();
        config.appId = alipayInfo.getAppId();
        // 为避免私钥随源码泄露，推荐从文件中读取私钥字符串而不是写入源码中
        config.merchantPrivateKey = alipayInfo.getMerchantPrivateKey();
        //注：证书文件路径支持设置为文件系统中的路径或CLASS_PATH中的路径，优先从文件系统中加载，加载失败后会继续尝试从CLASS_PATH中加载

        //注：如果采用非证书模式，则无需赋值上面的三个证书路径，改为赋值如下的支付宝公钥字符串即可
        config.alipayPublicKey = alipayInfo.getAlipayPublicKey();
        //可设置异步通知接收服务地址（可选）
        config.notifyUrl = alipayInfo.getNotifyUrl();
        return config;
    }
}
