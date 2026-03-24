package cn.lzs.ymcc.Consumer;


import cn.lzs.ymcc.domain.MediaFile;
import cn.lzs.ymcc.producer.MediaProducer;
import cn.lzs.ymcc.service.IMediaFileService;
import com.alibaba.fastjson.JSON;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@RocketMQMessageListener(topic = MediaProducer.mediaTopic,selectorExpression=MediaProducer.mediaTag,consumerGroup = "service-media-consumer")
@Slf4j
public class MediaConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private IMediaFileService mediaFileService ;

    @Override
    public void onMessage(MessageExt message) {
        String mediaFileJSON = new String(message.getBody(), CharsetUtil.UTF_8);
        log.info("MediaFile 消费者 {}" , mediaFileJSON);
        MediaFile mediaFile = JSON.parseObject(mediaFileJSON, MediaFile.class);
        //把文件转换成 m3u8 格式，并上传到文件服务器
        mediaFileService.handleFile2m3u8(mediaFile);
        log.info("MediaFile 消费者处理完成");
    }
}
