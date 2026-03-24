package cn.lzs.ymcc.producer;

import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.constant.SystemConstants;
import cn.lzs.ymcc.dto.EmailMessageDTO;
import cn.lzs.ymcc.dto.SmsMessageDTO;
import cn.lzs.ymcc.dto.StationMessageDTO;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author 李振生
 */
@Component
@Slf4j
public class CourseProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    public void synSendStationMessage(StationMessageDTO stationMessageDTO){
        try {
            String destination = toString(SystemConstants.CoursePublishTopic,SystemConstants.StationPublishTag);

            SendResult result = rocketMQTemplate.syncSend(destination, MessageBuilder.withPayload(stationMessageDTO).build());

            log.info("发送站内消息结果：{}",result);

        }catch (Exception e){
            e.printStackTrace();
            throw new GlobalBusinessException("发送站内消息失败");
        }

    }
    public String toString(String a,String b){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(a).append(":").append(b);
        return  stringBuilder.toString();
    }

    public void asynSendSmsMessage(SmsMessageDTO smsMessageDTO) {
        try {
            String destination = toString(SystemConstants.CoursePublishTopic,SystemConstants.SmsPublishTag);
            rocketMQTemplate.asyncSend(destination, MessageBuilder.withPayload(smsMessageDTO).build(), new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("发送短信消息结果：{}",sendResult);
                }

                @Override
                public void onException(Throwable throwable) {
                    throwable.printStackTrace();
                    log.info("消息发送失败");
                }
            });
//

        }catch (Exception e){
            e.printStackTrace();
            throw new GlobalBusinessException("发送短信消息失败");
        }
    }

    public void asynSendEmailMessage(EmailMessageDTO emailMessageDTO) {
        try {
            String destination = toString(SystemConstants.CoursePublishTopic,SystemConstants.EmailPublishTag);
            rocketMQTemplate.asyncSend(destination, MessageBuilder.withPayload(emailMessageDTO).build(), new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("发送邮件消息结果：{}",sendResult);
                }
                @Override
                public void onException(Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
