package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.GlobalExceptionCode;
import cn.lzs.ymcc.domain.KillActivity;
import cn.lzs.ymcc.domain.KillCourse;
import cn.lzs.ymcc.mapper.KillActivityMapper;
import cn.lzs.ymcc.service.IKillActivityService;
import cn.lzs.ymcc.service.IKillCourseService;
import cn.lzs.ymcc.util.AssertUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzs
 * @since 2026-03-28
 */
@Service
public class KillActivityServiceImpl extends ServiceImpl<KillActivityMapper, KillActivity> implements IKillActivityService {
    @Autowired
    private IKillCourseService killCourseService;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;



    /**
     *  发布秒杀活动
     * @param activityId
     */
    @Override
    public void publish(Long activityId) {
        // 参数校验
        // 查询活动id,获取到秒杀活动对象
        KillActivity killActivity = selectById(activityId);
        AssertUtil.isNotNull(killActivity, GlobalExceptionCode.SERVICE_PARAM_IS_NULL.getMessage()); // 校验前端传过来的活动id是否为空

        // 业务校验:   活动状态要合法  活动的时间要合法 [活动开始时间小于结束时间]
        Date now = new Date();  // 活动发布开始时间
        boolean before = now.before(killActivity.getEndTime()); // 校验活动开始时间必须在活动结束之前
        AssertUtil.isTrue(before,GlobalExceptionCode.KILL_ACTIVITY_TIME_ERROR.getMessage());

        // 校验活动必须存在秒杀课程
        EntityWrapper<KillCourse> wrapper = new EntityWrapper<>();
        wrapper.eq("activity_id",killActivity.getId());
        List<KillCourse> killCourses = killCourseService.selectList(wrapper); // 注入秒杀课程,查询:秒杀课程对应秒杀活动

        // 修复：同时检查 null 和 empty
        if (killCourses == null || killCourses.isEmpty()) {
            throw new RuntimeException("该活动下没有秒杀课程，请先添加秒杀课程");
        }

        // 修改活动状态 + 时间
        killActivity.setPublishStatus(KillActivity.ACTIVITY_PUBLISH); // 设置活动发布状态为已发布
        killActivity.setPublishTime(now);
        updateById(killActivity); // 保存修改

        // 活动下的产品[课程]: 存储到redisson的信号量中
        // 遍历多个秒杀商品(killCourses),获取到每个秒杀商品的id,和库存存储到redisson的信号量中
        for (KillCourse killCourse : killCourses) {
            // 同步活动的最新时间到秒杀课程（防止活动时间被修改后，课程时间未同步）
            killCourse.setStartTime(killActivity.getStartTime());
            killCourse.setEndTime(killActivity.getEndTime());
            killCourse.setTimeStr(killActivity.getTimeStr());
            // 更新秒杀课程状态为已发布
            killCourse.setPublishStatus(KillActivity.ACTIVITY_PUBLISH);
            killCourse.setPublishTime(now);
            killCourseService.updateById(killCourse);

            // 获取到秒杀商品的id,将商品id作为Key存入到redisson的信号量中
            String xiaoKey = killCourse.getId().toString(); // 信号量的Key: 遍历到的每个商品Id
            // 获取semaphore信号量对象
            RSemaphore semaphore = redissonClient.getSemaphore(xiaoKey);

            // 修复：先删除已存在的信号量，再重新设置
            semaphore.delete();
            semaphore.trySetPermits(killCourse.getKillCount());

            // 把秒杀课程存储到redis
            String daKey = "activity:" + killActivity.getId(); // 大key: 秒杀活动id
            // 使用redis的hash存储秒杀活动下的商品   大Key:活动id  小Key:秒杀商品id value: 商品
            redisTemplate.opsForHash().put(daKey,killCourse.getId().toString(),killCourse); // 小key:商品id,value:商品
        }
    }

    /**
     * 下架秒杀活动
     * @param activityId
     */
    @Override
    public void unpublish(Long activityId) {
        // 参数校验
        KillActivity killActivity = selectById(activityId);
        AssertUtil.isNotNull(killActivity, GlobalExceptionCode.SERVICE_PARAM_IS_NULL.getMessage());

        // 修改活动状态为下架
        killActivity.setPublishStatus(KillActivity.ACTIVITY_UNPUBLISH);
        updateById(killActivity);

        // 清除Redis中的相关数据
        String daKey = "activity:" + activityId;
        redisTemplate.delete(daKey);
    }

}
