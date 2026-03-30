package cn.lzs.ymcc.mapper;

import cn.lzs.ymcc.domain.MessageStation;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lzs
 * @since 2025-07-15
 */
public interface MessageStationMapper extends BaseMapper<MessageStation> {

    /**
     * 根据用户ID查询消息列表
     * @param userId 用户ID
     * @return 消息列表
     */
    List<MessageStation> selectByUserId(Long userId);

    /**
     * 标记用户所有消息为已读
     * @param userId 用户ID
     * @return 影响行数
     */
    int markAllAsRead(Long userId);
}
