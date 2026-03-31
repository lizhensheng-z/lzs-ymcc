package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.KillCourse;
import cn.lzs.ymcc.dto.KillParamDto;
import cn.lzs.ymcc.dto.PreOrderDto;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lzs
 * @since 2026-03-28
 */
public interface IKillCourseService extends IService<KillCourse> {
    /**
     *  为活动添加秒杀商品
     * @param killCourse
     */
    void addKillCourse(KillCourse killCourse);

    /**
     * 从redis中查询所有秒杀商品
     *
     * @return
     */
    List<KillCourse> onlineAll();

    /**
     *  从redis查询单个秒杀商品
     * @param killId
     * @param activityId
     * @return
     */
    KillCourse onlineOne(Long killId, Long activityId);

    /**
     *  执行秒杀,生成预订单返回
     * @param dto
     * @return
     */
    String kill(KillParamDto dto);

    /**
     * 从Redis查询预订单
     * @param orderNo 订单号
     * @return 预订单信息
     */
    PreOrderDto getPreOrder(String orderNo);

    /**
     * 支付秒杀订单（简化版，直接支付成功）
     * @param orderNo 订单号
     * @param payType 支付方式
     * @return 订单号
     */
    String payKillOrder(String orderNo, Integer payType);

}
