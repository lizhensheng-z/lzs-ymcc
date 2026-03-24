package cn.lzs.ymcc.mq.consumer;

/**
 * @author 李振生
 */

import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.constant.SystemConstants;
import cn.lzs.ymcc.domain.MessageSms;
import cn.lzs.ymcc.domain.MessageStation;
import cn.lzs.ymcc.domain.User2Phone;
import cn.lzs.ymcc.dto.SmsMessageDTO;
import cn.lzs.ymcc.dto.StationMessageDTO;
import cn.lzs.ymcc.mapper.MessageSmsMapper;
import cn.lzs.ymcc.mapper.MessageStationMapper;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 李振生
 */
@Component
@Slf4j
@RocketMQMessageListener(topic = SystemConstants.CoursePublishTopic,selectorExpression=SystemConstants.SmsPublishTag,consumerGroup = "service-common-sms-consumer")
public class SmsConsumerService implements RocketMQListener<MessageExt> {

    @Autowired
    private MessageSmsMapper messageSmsMapper;


    @SneakyThrows
    @Override
    public void onMessage(MessageExt messageExt) {
        //判断消息体是否合法
        if(messageExt==null){
            throw new GlobalBusinessException("Sms消费者收到的消息为空");
        }
        //转换为实体类MessageStation
        String jsonMessage = new String(messageExt.getBody(),"utf-8");
        SmsMessageDTO smsMessageDTO = JSONObject.parseObject(jsonMessage, SmsMessageDTO.class);
        List<User2Phone> user2PhoneList = smsMessageDTO.getUser2PhoneList();
        user2PhoneList.stream().forEach(user2Phone -> {
            MessageSms messageSms = new MessageSms();
            BeanUtils.copyProperties(user2Phone,messageSms);
            messageSms.setSendTime(new Date());
            BeanUtils.copyProperties(smsMessageDTO,messageSms);
            messageSmsMapper.insert(messageSms);
        });
        log.info("短信消息消费成功");
    }
}

