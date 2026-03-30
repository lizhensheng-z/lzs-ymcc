package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.constant.SystemConstants;
import cn.lzs.ymcc.domain.MessageStation;
import cn.lzs.ymcc.mapper.MessageStationMapper;
import cn.lzs.ymcc.service.IMessageStationService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-07-15
 */
@Service
public class MessageStationServiceImpl extends ServiceImpl<MessageStationMapper, MessageStation> implements IMessageStationService {

    @Override
    public List<MessageStation> selectByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public boolean markAsRead(Long id) {
        MessageStation messageStation = new MessageStation();
        messageStation.setId(id);
        messageStation.setIsread(SystemConstants.MESSAGE_READ);
        return updateById(messageStation);
    }

    @Override
    public int markAllAsRead(Long userId) {
        return baseMapper.markAllAsRead(userId);
    }
}
