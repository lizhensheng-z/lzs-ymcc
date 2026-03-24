package cn.lzs.ymcc.mq.consumer;

import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.constant.RocketMQConstants;
import cn.lzs.ymcc.constant.SystemConstants;
import cn.lzs.ymcc.domain.CourseOrder;
import cn.lzs.ymcc.dto.CourseOrderDTO;
import cn.lzs.ymcc.dto.UpdatePayStatusOrderDTO;
import cn.lzs.ymcc.service.IPayOrderService;
import com.alibaba.fastjson.JSONObject;
import feign.form.util.CharsetUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;



import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.constant.SystemConstants;

import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 李振生
 */
@Component
@Slf4j
@RocketMQMessageListener(topic = RocketMQConstants.MQ_TOPIC_COURSEORDER_CANCEL_DELAY,
        selectorExpression=RocketMQConstants.MQ_TAGS_COURSEORDER_CANCEL_DELAY,
        consumerGroup = "service-pay-delay-consumer",
        messageModel = MessageModel.BROADCASTING)
public class CancelPayOrderService implements RocketMQListener<MessageExt> {

    @Autowired
    private IPayOrderService payOrderService;


    @SneakyThrows
    @Override
    public void onMessage(MessageExt messageExt) {
        //判断消息体是否合法
        if(messageExt==null){
            throw new GlobalBusinessException("Email消费者收到的消息为空");
        }
        //String
        String outOrderNo = new String(messageExt.getBody(), CharsetUtil.UTF_8);
        //TODO 支付订单关单并调用支付宝接口 关闭支付宝订单
        

        log.info("收到取消订单的消息，订单号：{}",outOrderNo);
        //向支付宝发起取消订单申请
        payOrderService.cancelPayOrder(outOrderNo);

    }
}


