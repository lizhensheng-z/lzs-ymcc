package cn.lzs.ymcc.mq.consumer;

import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.constant.SystemConstants;
import cn.lzs.ymcc.domain.MessageStation;
import cn.lzs.ymcc.dto.StationMessageDTO;
import cn.lzs.ymcc.mapper.MessageStationMapper;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author 李振生
 */
@Component
@Slf4j
@RocketMQMessageListener(topic = SystemConstants.CoursePublishTopic,selectorExpression=SystemConstants.StationPublishTag,consumerGroup = "service-common-station-consumer")
public class StationConsumerService implements RocketMQListener<MessageExt> {

    @Autowired
    private MessageStationMapper messageStationMapper;


    @SneakyThrows
    @Override
    public void onMessage(MessageExt messageExt) {
        //判断消息体是否合法
        if(messageExt==null){
            throw new GlobalBusinessException("Station消费者收到的消息为空");
        }
        //转换为实体类MessageStation
        String jsonMessage = new String(messageExt.getBody(),"utf-8");
        StationMessageDTO stationMessageDTO = JSONObject.parseObject(jsonMessage, StationMessageDTO.class);
        ArrayList<Long> userIds = stationMessageDTO.getUserIds();
        userIds.stream().forEach(userId-> {
            //将消息存入数据库
            MessageStation messageStation = new MessageStation();
            BeanUtils.copyProperties(stationMessageDTO,messageStation);
            messageStation.setUserId(userId);
            messageStation.setSendTime(new Date());
            messageStation.setIsread(SystemConstants.MESSAGE_UNREAD);
            messageStationMapper.insert(messageStation);
        });
        log.info("站内消息消费成功");
    }
}
