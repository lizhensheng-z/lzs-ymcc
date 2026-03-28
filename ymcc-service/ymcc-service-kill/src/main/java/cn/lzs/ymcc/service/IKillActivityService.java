package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.KillActivity;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lzs
 * @since 2026-03-28
 */
public interface IKillActivityService extends IService<KillActivity> {
    /**
     * 发布秒杀活动
     * @param activityId
     */
    void publish(Long activityId);
}
