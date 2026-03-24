package cn.lzs.ymcc.mq.consumer;



import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.constant.RocketMQConstants;
import cn.lzs.ymcc.domain.PayOrder;
import cn.lzs.ymcc.service.IPayOrderService;
import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@RocketMQMessageListener(topic = RocketMQConstants.MQ_TOPIC_ORDER,selectorExpression=RocketMQConstants.MQ_TAGS_COURSEORDER_PAYORDER,consumerGroup = RocketMQConstants.MQ_COURSEORDER_PAY_GROUP_TRANSACTION)
@Slf4j
public class PayOrderConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    private IPayOrderService payOrderService;


    @Override
    public void onMessage(MessageExt message) {
        //检验消息体
        if(message.getBody() == null){
            log.error("消息体为空");
            throw new GlobalBusinessException("消息体不能为空");
        }
        //解析消息体
        String messageBody = new String(message.getBody());
        log.info("接收到的消息体为:{}",messageBody);
        PayOrder payOrder = JSONObject.parseObject(messageBody, PayOrder.class);
        //处理消息
        //接口幂等：校验消息是否被消费，该支付订单是否已经落库
        PayOrder payOrderFromDB = payOrderService.checkPayOrder(payOrder.getOrderNo());
        if(payOrderFromDB != null){
            log.error("该支付订单已经生成过了，不能重复消费");
            throw new GlobalBusinessException("该支付订单已经生成过了，不能重复消费");
        }
        payOrderService.insert(payOrder);
        log.info("该支付订单消费成功，已落库"+payOrder.getOrderNo());
    }
}
