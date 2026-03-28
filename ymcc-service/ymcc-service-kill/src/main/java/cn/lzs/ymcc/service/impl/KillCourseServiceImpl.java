package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.GlobalExceptionCode;
import cn.lzs.ymcc.domain.KillActivity;
import cn.lzs.ymcc.domain.KillCourse;
import cn.lzs.ymcc.dto.KillParamDto;
import cn.lzs.ymcc.dto.PreOrderDto;
import cn.lzs.ymcc.mapper.KillCourseMapper;
import cn.lzs.ymcc.service.ICourseMarketService;
import cn.lzs.ymcc.service.IKillActivityService;
import cn.lzs.ymcc.service.IKillCourseService;
import cn.lzs.ymcc.util.AssertUtil;
import cn.lzs.ymcc.util.CodeGenerateUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzs
 * @since 2026-03-28
 */
@Service
public class KillCourseServiceImpl extends ServiceImpl<KillCourseMapper, KillCourse> implements IKillCourseService {

    @Autowired
    private IKillActivityService killActivityService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ICourseMarketService courseMarket;

    /**
     *  添加秒杀课程
     * @param killCourse
     */
    @Override
    public void addKillCourse(KillCourse killCourse) {

        // 查询出秒杀课程id,和活动id
        Wrapper<KillCourse> wrapper = new EntityWrapper<>();
        wrapper.eq("course_id",killCourse.getCourseId());
        wrapper.eq("activity_id",killCourse.getActivityId());
        KillCourse tmp = selectOne(wrapper);
        // 判断秒杀课程和活动是否存在
        AssertUtil.isNull(tmp, GlobalExceptionCode.KILL_ACTIVITY_IS_NOTNULL_ERROR.getMessage()); // 存在就不能在添加秒杀课程了

        // 查询出秒杀活动
        KillActivity killActivity = killActivityService.selectById(killCourse.getActivityId());

        // 通过秒杀活动,设置秒杀课程的所需要的值
        killCourse.setCreateTime(new Date()); // 设置秒杀课程创建时间
        killCourse.setKillLimit(1); // 设置每个人可以秒杀的数量
        killCourse.setStartTime(killActivity.getStartTime()); // 设置秒杀课程的秒杀活动时间
        killCourse.setEndTime(killActivity.getEndTime()); // 秒杀活动结束时间
        killCourse.setTimeStr(killActivity.getTimeStr());
        killCourse.setPublishStatus(killActivity.getPublishStatus()); // 秒杀发布状态
        // 保存秒杀课程
        insert(killCourse);

    }

    /**
     * 从redis中查询所有秒杀商品
     *
     * @return 通过redis先拿到所有秒杀活动的key, 在循环去拿到秒杀活动key下面对应的商品
     */
    @Override
    public List<KillCourse> onlineAll() {
        // 从redis中查询所有秒杀活动, 拿到所有以activity: 开头的Key
        Set<Object> keys = redisTemplate.keys("activity:*");
        // 校验redis中存储的秒杀活动是否存在
        AssertUtil.isNotNull(keys,GlobalExceptionCode.KILL_ACTIVITY_IS_NULL_ERROR.getMessage());

        // 要给前端返回一个List<KillCourse>数组, 创建一个空的List,往里面追加redis中的商品
        List<KillCourse> killCourses = new ArrayList<>();

        // 遍历从redis中获取到的秒杀活动
        for (Object key : keys) {
            // 通过遍历到的每一个秒杀活动,key去redis中获取活动的value (秒杀活动,商品id,商品库存)
            List values = redisTemplate.opsForHash().values(key);
            // 通过List的addAll():添加所有的, [秒杀活动,商品id,商品库存]
            killCourses.addAll(values);
        }
        return killCourses;
    }

    /**
     *  从redis查询单个秒杀商品
     * @param killId
     * @param activityId
     * @return
     */
    @Override
    public KillCourse onlineOne(Long killId, Long activityId) {
        String key = "activity:"+activityId;
        return  (KillCourse) redisTemplate.opsForHash().get(key, killId.toString());

    }

    /**
     * ==== 校验当前登录人是否已经秒杀过
     *  执行秒杀,生成预订单返回
     *  1.根据活动Id和秒杀Id,查询出秒杀课程是否存在
     *  执行秒杀:
     *  2.参试去扣减库存
     *      ,扣除不成功: 手速太慢,下次再来
     *      ,扣除成功: 生成预订单,存储redis
     *      key:orderNo
     *      value: PreOrder{}
     *      为了保证一个人只能购买一个课程一次,单独存入redis
     *      key:loginId:killId 只要key存在就不可以在抢了
     *      value:xxoo
     * @param dto
     * @return
     */
    @Override
    public String kill(KillParamDto dto) {
        // 1.创建登录人Id
        Long loginId = 3L;
        // 创建登录人+秒杀课程ID,Key存入redis
        String repeatKy = loginId + ":" + dto.getKillCourseId();
        // 查询redis,repeatKy是否存在
        Object repeat = redisTemplate.opsForValue().get(repeatKy);
        // 判断登录人是否在多次秒杀课程
        AssertUtil.isNull(repeat,GlobalExceptionCode.KILL_ACTIVITY_IS_REPEAT_ERROR.getMessage());
        // 根据活动id和秒杀id,查询出秒杀课程是否存在
        KillCourse killCourse = onlineOne( dto.getKillCourseId(),dto.getActivityId());
        AssertUtil.isNotNull(killCourse,GlobalExceptionCode.KILL_ACTIVITY_IS_NULL_ERROR.getMessage());
        // 校验秒杀时间是否结束
        AssertUtil.isTrue(killCourse.isKilling(),GlobalExceptionCode.KILL_ACTIVITY_TIME_ERROR.getMessage());
        //   执行秒杀: 2.参试去扣减库存
        String xiaoKey = killCourse.getId().toString(); // 创建key,秒杀商品id
        // 获取semaphore信号量对象
        RSemaphore semaphore = redissonClient.getSemaphore(xiaoKey); // 使用redisson获取到信号量,
        int killCount = 1; // 设置扣除信号量的数量
        boolean tryAcquire = semaphore.tryAcquire(killCount);// 扣除信号量
        // 校验库存扣除失败,说明库存为空
        AssertUtil.isTrue(tryAcquire,GlobalExceptionCode.KILL_COURSE_IS_NULL_ERROR.getMessage());
        //扣除成功: 生成预订单,存储redis
        String orderNo = CodeGenerateUtils.generateOrderSn(loginId); // 生成预订单号
        // 创建preOrderDto,设置需要的值
        PreOrderDto preOrderDto = new PreOrderDto(
                orderNo,
                killCourse.getKillPrice(),
                1,
                loginId,
                killCourse.getCourseId()
        );
        // 生成预订单,存储redis
        redisTemplate.opsForValue().set(orderNo,preOrderDto);
        // 为了保证一个人只能购买一个课程一次,单独存入redis
        redisTemplate.opsForValue().set(repeatKy,orderNo);
        return orderNo;
    }

}
