package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.MessageStation;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lzs
 * @since 2025-07-15
 */
public interface IMessageStationService extends IService<MessageStation> {

    /**
     * 根据用户ID查询消息列表
     * @param userId 用户ID
     * @return 消息列表
     */
    java.util.List<MessageStation> selectByUserId(Long userId);

    /**
     * 标记消息为已读
     * @param id 消息ID
     * @return 是否成功
     */
    boolean markAsRead(Long id);

    /**
     * 标记所有消息为已读
     * @param userId 用户ID
     * @return 影响行数
     */
    int markAllAsRead(Long userId);
}
