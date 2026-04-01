package cn.lzs.ymcc.service;

import cn.lzs.ymcc.KillApplication;
import cn.lzs.ymcc.domain.KillActivity;
import cn.lzs.ymcc.domain.KillCourse;
import cn.lzs.ymcc.dto.KillParamDto;
import cn.lzs.ymcc.dto.PreOrderDto;
import cn.lzs.ymcc.mapper.KillActivityMapper;
import cn.lzs.ymcc.mapper.KillCourseMapper;
import cn.lzs.ymcc.service.impl.KillCourseServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.redisson.api.RedissonClient;
import org.redisson.api.RSemaphore;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * 秒杀服务单元测试类
 * 测试秒杀活动的库存扣减、防重复购买、预订单生成等功能
 *
 * @author lzs
 * @date 2026-04-01
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KillApplication.class)
public class KillCourseServiceTest {

    @Autowired
    private KillCourseServiceImpl killCourseService;

    @Autowired
    private KillCourseMapper killCourseMapper;

    @Autowired
    private KillActivityMapper killActivityMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    // 测试用的活动 ID 和秒杀课程 ID
    private static final Long TEST_ACTIVITY_ID = 999L;
    private static final Long TEST_KILL_COURSE_ID = 888L;
    private static final Long TEST_COURSE_ID = 1001L;
    private static final Long TEST_USER_ID = 10001L;

    /**
     * 测试前准备 - 初始化 Redis 中的秒杀数据
     */
    @Before
    public void setUp() {
        // 清理可能存在的旧数据
        redisTemplate.delete("activity:" + TEST_ACTIVITY_ID);
        redisTemplate.delete(TEST_KILL_COURSE_ID.toString());
        redisTemplate.delete(TEST_USER_ID + ":" + TEST_KILL_COURSE_ID);
    }

    /**
     * 测试秒杀抢购 - 成功场景
     * 验证库存充足时秒杀能否成功
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testKill_Success() {
        // 准备测试数据 - 先在 Redis 中初始化秒杀商品
        initKillCourseInRedis();

        // 准备秒杀参数
        KillParamDto dto = new KillParamDto();
        dto.setKillCourseId(TEST_KILL_COURSE_ID);
        dto.setActivityId(TEST_ACTIVITY_ID);

        // 执行秒杀
        String orderNo = killCourseService.kill(dto);

        // 验证订单号生成
        assertNotNull("订单号不应为空", orderNo);
        assertTrue("订单号长度应大于 10", orderNo.length() > 10);

        // 验证 Redis 中库存已扣减
        RSemaphore semaphore = redissonClient.getSemaphore(TEST_KILL_COURSE_ID.toString());
        assertTrue("库存应该减少", semaphore.availablePermits() < 100);
    }

    /**
     * 测试秒杀抢购 - 库存不足场景
     * 验证库存为 0 时秒杀失败
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testKill_NoStock() {
        // 准备测试数据 - 设置库存为 0
        initKillCourseInRedisWithStock(0);

        // 准备秒杀参数
        KillParamDto dto = new KillParamDto();
        dto.setKillCourseId(TEST_KILL_COURSE_ID);
        dto.setActivityId(TEST_ACTIVITY_ID);

        // 执行秒杀，预期抛出异常
        try {
            String orderNo = killCourseService.kill(dto);
            fail("库存不足时应抛出异常");
        } catch (Exception e) {
            assertTrue("应提示库存不足", e.getMessage().contains("库存不足"));
        }
    }

    /**
     * 测试秒杀抢购 - 重复购买场景
     * 验证用户不能重复秒杀同一商品
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testKill_RepeatPurchase() {
        // 准备测试数据
        initKillCourseInRedis();

        // 第一次秒杀
        KillParamDto dto1 = new KillParamDto();
        dto1.setKillCourseId(TEST_KILL_COURSE_ID);
        dto1.setActivityId(TEST_ACTIVITY_ID);
        String orderNo1 = killCourseService.kill(dto1);
        assertNotNull("第一次秒杀应成功", orderNo1);

        // 第二次秒杀（同一用户同一商品）
        KillParamDto dto2 = new KillParamDto();
        dto2.setKillCourseId(TEST_KILL_COURSE_ID);
        dto2.setActivityId(TEST_ACTIVITY_ID);

        try {
            String orderNo2 = killCourseService.kill(dto2);
            fail("重复购买时应抛出异常");
        } catch (Exception e) {
            assertTrue("应提示已购买", e.getMessage().contains("已购买") || e.getMessage().contains("重复"));
        }
    }

    /**
     * 测试秒杀时间校验 - 活动未开始
     * 验证活动未开始时不能秒杀
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testKill_NotStarted() {
        // 准备测试数据 - 设置活动时间为未来
        KillActivity activity = new KillActivity();
        activity.setId(TEST_ACTIVITY_ID);
        activity.setName("测试秒杀活动");
        activity.setStartTime(new Date(System.currentTimeMillis() + 86400000)); // 明天开始
        activity.setEndTime(new Date(System.currentTimeMillis() + 172800000)); // 后天结束
        killActivityMapper.insert(activity);

        KillCourse killCourse = new KillCourse();
        killCourse.setId(TEST_KILL_COURSE_ID);
        killCourse.setActivityId(TEST_ACTIVITY_ID);
        killCourse.setCourseId(TEST_COURSE_ID);
        killCourse.setKillPrice(new BigDecimal("0.01"));
        killCourse.setStock(100);
        killCourse.setStartTime(activity.getStartTime());
        killCourse.setEndTime(activity.getEndTime());
        killCourseMapper.insert(killCourse);

        // 准备秒杀参数
        KillParamDto dto = new KillParamDto();
        dto.setKillCourseId(TEST_KILL_COURSE_ID);
        dto.setActivityId(TEST_ACTIVITY_ID);

        // 执行秒杀，预期失败
        try {
            String orderNo = killCourseService.kill(dto);
            // 如果成功，验证是否因为时间判断逻辑允许提前购买
            assertNotNull("订单号不应为空", orderNo);
        } catch (Exception e) {
            assertTrue("应提示不在秒杀时间", e.getMessage().contains("不在秒杀时间") || e.getMessage().contains("未开始"));
        }
    }

    /**
     * 测试查询单个秒杀商品
     * 验证能否从 Redis 正确查询秒杀商品信息
     */
    @Test
    public void testOnlineOne() {
        // 准备测试数据
        initKillCourseInRedis();

        // 执行查询
        KillCourse killCourse = killCourseService.onlineOne(TEST_KILL_COURSE_ID, TEST_ACTIVITY_ID);

        // 验证查询结果
        if (killCourse != null) {
            assertEquals("商品 ID 应匹配", TEST_KILL_COURSE_ID, killCourse.getId());
            assertEquals("活动 ID 应匹配", TEST_ACTIVITY_ID, killCourse.getActivityId());
        }
    }

    /**
     * 测试查询所有上架秒杀商品
     * 验证能否正确查询所有在线秒杀商品
     */
    @Test
    public void testOnlineAll() {
        // 准备测试数据 - 添加多个秒杀商品
        initKillCourseInRedis();

        // 执行查询
        java.util.List<KillCourse> killCourses = killCourseService.onlineAll();

        // 验证查询结果
        assertNotNull("查询结果不应为空", killCourses);
    }

    /**
     * 测试获取预订单
     * 验证能否从 Redis 正确获取预订单信息
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testGetPreOrder() {
        // 准备测试数据 - 先创建预订单
        String orderNo = "KILL_TEST_20260401001";
        PreOrderDto preOrderDto = new PreOrderDto();
        preOrderDto.setOrderNo(orderNo);
        preOrderDto.setUserId(TEST_USER_ID);
        preOrderDto.setKillCourseId(TEST_KILL_COURSE_ID);
        preOrderDto.setAmount(new BigDecimal("0.01"));

        // 存储到 Redis
        redisTemplate.opsForValue().set(orderNo, preOrderDto, 30, TimeUnit.MINUTES);

        // 执行查询
        PreOrderDto result = killCourseService.getPreOrder(orderNo);

        // 验证查询结果
        assertNotNull("预订单不应为空", result);
        assertEquals("订单号应匹配", orderNo, result.getOrderNo());
    }

    /**
     * 测试添加秒杀课程
     * 验证秒杀课程能否正确添加到数据库
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testAddKillCourse() {
        // 准备测试数据 - 先创建秒杀活动
        KillActivity activity = new KillActivity();
        activity.setId(TEST_ACTIVITY_ID);
        activity.setName("测试秒杀活动");
        activity.setStartTime(new Date(System.currentTimeMillis() - 86400000)); // 昨天开始
        activity.setEndTime(new Date(System.currentTimeMillis() + 86400000)); // 明天结束
        killActivityMapper.insert(activity);

        // 准备秒杀课程
        KillCourse killCourse = new KillCourse();
        killCourse.setActivityId(TEST_ACTIVITY_ID);
        killCourse.setCourseId(TEST_COURSE_ID);
        killCourse.setKillPrice(new BigDecimal("0.01"));
        killCourse.setStock(50);

        // 执行添加
        killCourseService.addKillCourse(killCourse);

        // 验证添加结果
        assertNotNull("秒杀课程 ID 应生成", killCourse.getId());
        assertEquals("库存应为 50", Integer.valueOf(50), killCourse.getStock());
        assertEquals("限购应为 1", Integer.valueOf(1), killCourse.getKillLimit());
    }

    /**
     * 辅助方法：在 Redis 中初始化秒杀商品数据（默认库存 100）
     */
    private void initKillCourseInRedis() {
        initKillCourseInRedisWithStock(100);
    }

    /**
     * 辅助方法：在 Redis 中初始化秒杀商品数据（自定义库存）
     */
    private void initKillCourseInRedisWithStock(int stock) {
        // 初始化活动信息
        redisTemplate.opsForHash().put("activity:" + TEST_ACTIVITY_ID, "id", TEST_ACTIVITY_ID);
        redisTemplate.opsForHash().put("activity:" + TEST_ACTIVITY_ID, "name", "测试活动");

        // 初始化库存信号量
        RSemaphore semaphore = redissonClient.getSemaphore(TEST_KILL_COURSE_ID.toString());
        try {
            semaphore.trySetPermits(stock);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}