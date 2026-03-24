package cn.lzs.ymcc.producer;


import cn.lzs.ymcc.domain.MediaFile;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


//生产者
@Component
@Slf4j
public class MediaProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public static final String mediaTopic = "media-topic";
    public static final String mediaTag = "media-tag";


    public boolean synSend(MediaFile mediaFile ){
        try {

            String mediaFileJSON = JSON.toJSONString(mediaFile);

            SendResult result = rocketMQTemplate.syncSend(mediaTopic+":"+mediaTag, MessageBuilder.withPayload(mediaFileJSON).build());

            log.info("媒体文件发送到MQ处理 mediaFile = {}",mediaFile);

            return result.getSendStatus() == SendStatus.SEND_OK;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}
