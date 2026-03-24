package cn.lzs.ymcc.mq.consumer;

import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.constant.RocketMQConstants;
import cn.lzs.ymcc.constant.SystemConstants;
import cn.lzs.ymcc.domain.CourseOrder;
import cn.lzs.ymcc.dto.CourseOrderDTO;
import cn.lzs.ymcc.dto.UpdatePayStatusOrderDTO;
import cn.lzs.ymcc.service.ICourseOrderService;
import com.alibaba.fastjson.JSONObject;
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
        consumerGroup = "service-order-delay-consumer",
        messageModel = MessageModel.BROADCASTING)
public class CancelCourseOrderService implements RocketMQListener<MessageExt> {

    @Autowired
    private ICourseOrderService courseOrderService;


    @SneakyThrows
    @Override
    public void onMessage(MessageExt messageExt) {
        //判断消息体是否合法
        if(messageExt==null){
            throw new GlobalBusinessException("Email消费者收到的消息为空");
        }
        //String
        String orderNo = new String(messageExt.getBody());
        log.info("收到取消订单的消息，订单号：{}",orderNo);
        // 超时自动取消课程订单
        UpdatePayStatusOrderDTO updatePayStatusOrderDTO = new UpdatePayStatusOrderDTO();
        updatePayStatusOrderDTO.setOrderNo(orderNo);
        updatePayStatusOrderDTO.setStatusOrder(CourseOrder.AutoCancel);
        updatePayStatusOrderDTO.setUpdateTime(new Date());
        courseOrderService.updatePayStatusByOrderNo(updatePayStatusOrderDTO);
        log.info("取消系统课程订单成功："+orderNo);

    }
}


