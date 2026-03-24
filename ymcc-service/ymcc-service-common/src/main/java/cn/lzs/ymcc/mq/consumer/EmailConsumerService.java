package cn.lzs.ymcc.mq.consumer;

import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.constant.SystemConstants;
import cn.lzs.ymcc.domain.MessageEmail;
import cn.lzs.ymcc.domain.MessageSms;
import cn.lzs.ymcc.domain.User2Email;
import cn.lzs.ymcc.domain.User2Phone;
import cn.lzs.ymcc.dto.EmailMessageDTO;
import cn.lzs.ymcc.dto.SmsMessageDTO;
import cn.lzs.ymcc.mapper.MessageEmailMapper;
import cn.lzs.ymcc.mapper.MessageSmsMapper;
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
@RocketMQMessageListener(topic = SystemConstants.CoursePublishTopic,selectorExpression=SystemConstants.EmailPublishTag,consumerGroup = "service-common-email-consumer")
public class EmailConsumerService implements RocketMQListener<MessageExt> {

    @Autowired
    private MessageEmailMapper messageEmailMapper;


    @SneakyThrows
    @Override
    public void onMessage(MessageExt messageExt) {
        //判断消息体是否合法
        if(messageExt==null){
            throw new GlobalBusinessException("Email消费者收到的消息为空");
        }
        //转换为实体类MessageStation
        String jsonMessage = new String(messageExt.getBody(),"utf-8");
        EmailMessageDTO emailMessageDTO = JSONObject.parseObject(jsonMessage, EmailMessageDTO.class);
        ArrayList<User2Email> user2Emails = emailMessageDTO.getUser2Emails();
        user2Emails.stream().forEach(user2Email -> {
            MessageEmail messageEmail = new MessageEmail();
            BeanUtils.copyProperties(user2Email, messageEmail);
            messageEmail.setSendTime(new Date());
            BeanUtils.copyProperties(emailMessageDTO, messageEmail);
            messageEmailMapper.insert(messageEmail);
        });

        log.info("邮箱消息消费成功");
    }
}

