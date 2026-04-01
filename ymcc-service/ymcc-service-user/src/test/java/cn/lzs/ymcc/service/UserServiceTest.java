package cn.lzs.ymcc.service;

import cn.lzs.ymcc.UserApplication;
import cn.lzs.ymcc.domain.User;
import cn.lzs.ymcc.domain.UserAccount;
import cn.lzs.ymcc.domain.UserBaseInfo;
import cn.lzs.ymcc.dto.PhoneRegisterDTO;
import cn.lzs.ymcc.mapper.UserMapper;
import cn.lzs.ymcc.pojo.VerifyCode;
import cn.lzs.ymcc.service.impl.UserServiceImpl;
import cn.lzs.ymcc.constant.RedisConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * 用户服务单元测试类
 * 测试用户注册、登录、信息查询等功能
 *
 * @author lzs
 * @date 2026-04-01
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    // 测试用的手机号
    private static final String TEST_PHONE = "13800138000";
    private static final String TEST_PASSWORD = "test123456";
    private static final String TEST_SMS_CODE = "123456";

    /**
     * 测试前准备 - 清理可能存在的旧数据
     */
    @Before
    public void setUp() {
        // 清理测试数据
        User existingUser = userMapper.selectOne(
            new com.baomidou.mybatisplus.mapper.EntityWrapper<User>().eq("phone", TEST_PHONE)
        );
        if (existingUser != null) {
            userMapper.deleteById(existingUser.getId());
        }

        // 清理 Redis 中的验证码
        redisTemplate.delete(RedisConstants.REDIS_VERIFY_CODE_KEY_PREFIX + TEST_PHONE);
    }

    /**
     * 测试后清理
     */
    @After
    public void tearDown() {
        // 清理 Redis 数据
        redisTemplate.delete(RedisConstants.REDIS_VERIFY_CODE_KEY_PREFIX + TEST_PHONE);
    }

    /**
     * 测试手机号注册 - 成功场景
     * 验证验证码正确时注册成功
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testRegisterByPhone_Success() {
        // 准备验证码
        VerifyCode verifyCode = new VerifyCode();
        verifyCode.setCode(TEST_SMS_CODE);
        verifyCode.setExpireTime(new Date(System.currentTimeMillis() + 300000)); // 5 分钟后过期
        redisTemplate.opsForValue().set(
            RedisConstants.REDIS_VERIFY_CODE_KEY_PREFIX + TEST_PHONE,
            verifyCode,
            5,
            TimeUnit.MINUTES
        );

        // 准备注册参数
        PhoneRegisterDTO dto = new PhoneRegisterDTO();
        dto.setMobile(TEST_PHONE);
        dto.setSmsCode(TEST_SMS_CODE);
        dto.setPassword(TEST_PASSWORD);

        // 执行注册
        userService.registerByPhone(dto);

        // 验证用户已创建
        User newUser = userMapper.selectOne(
            new com.baomidou.mybatisplus.mapper.EntityWrapper<User>().eq("phone", TEST_PHONE)
        );

        assertNotNull("用户应已创建", newUser);
        assertEquals("手机号应匹配", TEST_PHONE, newUser.getPhone());
    }

    /**
     * 测试手机号注册 - 手机号已存在
     * 验证手机号已注册时抛出异常
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testRegisterByPhone_PhoneExists() {
        // 先创建一个用户
        User existingUser = new User();
        existingUser.setPhone(TEST_PHONE);
        existingUser.setNickName("测试用户");
        existingUser.setCreateTime(new Date());
        userMapper.insert(existingUser);

        // 准备验证码
        VerifyCode verifyCode = new VerifyCode();
        verifyCode.setCode(TEST_SMS_CODE);
        redisTemplate.opsForValue().set(
            RedisConstants.REDIS_VERIFY_CODE_KEY_PREFIX + TEST_PHONE,
            verifyCode,
            5,
            TimeUnit.MINUTES
        );

        // 准备注册参数
        PhoneRegisterDTO dto = new PhoneRegisterDTO();
        dto.setMobile(TEST_PHONE);
        dto.setSmsCode(TEST_SMS_CODE);
        dto.setPassword(TEST_PASSWORD);

        // 执行注册，预期抛出异常
        try {
            userService.registerByPhone(dto);
            fail("手机号已注册时应抛出异常");
        } catch (Exception e) {
            assertTrue("应提示手机号已注册", e.getMessage().contains("已注册") || e.getMessage().contains("PHONE"));
        }
    }

    /**
     * 测试手机号注册 - 验证码错误
     * 验证验证码错误时注册失败
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testRegisterByPhone_WrongCode() {
        // 准备错误的验证码
        VerifyCode verifyCode = new VerifyCode();
        verifyCode.setCode("999999"); // 正确的验证码
        redisTemplate.opsForValue().set(
            RedisConstants.REDIS_VERIFY_CODE_KEY_PREFIX + TEST_PHONE,
            verifyCode,
            5,
            TimeUnit.MINUTES
        );

        // 准备注册参数 - 使用错误的验证码
        PhoneRegisterDTO dto = new PhoneRegisterDTO();
        dto.setMobile(TEST_PHONE);
        dto.setSmsCode("111111"); // 错误的验证码
        dto.setPassword(TEST_PASSWORD);

        // 执行注册，预期抛出异常
        try {
            userService.registerByPhone(dto);
            fail("验证码错误时应抛出异常");
        } catch (Exception e) {
            assertTrue("应提示验证码错误", e.getMessage().contains("验证码") || e.getMessage().contains("VALID"));
        }
    }

    /**
     * 测试手机号注册 - 验证码过期
     * 验证验证码过期时注册失败
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testRegisterByPhone_CodeExpired() {
        // 准备过期的验证码
        VerifyCode verifyCode = new VerifyCode();
        verifyCode.setCode(TEST_SMS_CODE);
        verifyCode.setExpireTime(new Date(System.currentTimeMillis() - 1000)); // 已过期
        redisTemplate.opsForValue().set(
            RedisConstants.REDIS_VERIFY_CODE_KEY_PREFIX + TEST_PHONE,
            verifyCode,
            5,
            TimeUnit.MINUTES
        );

        // 准备注册参数
        PhoneRegisterDTO dto = new PhoneRegisterDTO();
        dto.setMobile(TEST_PHONE);
        dto.setSmsCode(TEST_SMS_CODE);
        dto.setPassword(TEST_PASSWORD);

        // 执行注册，预期抛出异常
        try {
            userService.registerByPhone(dto);
            fail("验证码过期时应抛出异常");
        } catch (Exception e) {
            assertTrue("应提示验证码过期", e.getMessage().contains("过期") || e.getMessage().contains("EXPIRED"));
        }
    }

    /**
     * 测试根据登录 ID 获取用户信息
     * 验证能否正确查询用户信息
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testGetUserByLoginId() {
        // 准备测试数据
        User testUser = new User();
        testUser.setPhone(TEST_PHONE);
        testUser.setNickName("测试用户");
        testUser.setCreateTime(new Date());
        userMapper.insert(testUser);

        // 执行查询
        User user = userService.getUserByLoginId(testUser.getId());

        // 验证查询结果
        assertNotNull("用户不应为空", user);
        assertEquals("用户 ID 应匹配", testUser.getId(), user.getId());
        assertEquals("昵称应匹配", "测试用户", user.getNickName());
    }

    /**
     * 测试获取用户账户信息
     * 验证能否正确查询用户账户
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testGetUserAccount() {
        // 准备测试数据
        User testUser = new User();
        testUser.setPhone(TEST_PHONE);
        testUser.setNickName("测试用户");
        testUser.setCreateTime(new Date());
        userMapper.insert(testUser);

        // 创建用户账户
        UserAccount account = new UserAccount();
        account.setUserId(testUser.getId());
        account.setUsableAmount(new java.math.BigDecimal("1000.00"));
        account.setFrozenAmount(new java.math.BigDecimal("0.00"));
        userAccountService.insert(account);

        // 执行查询
        UserAccount userAccount = userAccountService.selectById(testUser.getId());

        // 验证查询结果
        assertNotNull("账户不应为空", userAccount);
        assertEquals("可用余额应匹配", new java.math.BigDecimal("1000.00"), userAccount.getUsableAmount());
    }

    /**
     * 测试获取用户基础信息
     * 验证能否正确查询用户基础信息
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testGetUserBaseInfo() {
        // 准备测试数据
        User testUser = new User();
        testUser.setPhone(TEST_PHONE);
        testUser.setNickName("测试用户");
        testUser.setCreateTime(new Date());
        userMapper.insert(testUser);

        // 创建用户基础信息
        UserBaseInfo baseInfo = new UserBaseInfo();
        baseInfo.setUserId(testUser.getId());
        baseInfo.setLevel(1);
        baseInfo.setGrowScore(100);
        userBaseInfoService.insert(baseInfo);

        // 执行查询
        UserBaseInfo info = userBaseInfoService.selectById(testUser.getId());

        // 验证查询结果
        assertNotNull("用户基础信息不应为空", info);
        assertEquals("等级应匹配", Integer.valueOf(1), info.getLevel());
        assertEquals("成长值应匹配", Integer.valueOf(100), info.getGrowScore());
    }

    /**
     * 测试密码加密
     * 验证 BCrypt 密码加密是否正常工作
     */
    @Test
    public void testPasswordEncoding() {
        String rawPassword = "test123456";
        String encodedPassword = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(rawPassword);

        // 验证加密后的密码
        assertNotNull("加密后的密码不应为空", encodedPassword);
        assertTrue("加密后的密码长度应大于原密码", encodedPassword.length() > rawPassword.length());
        assertTrue("密码验证应通过", new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().matches(rawPassword, encodedPassword));
    }

    // 需要注入的服务
    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IUserBaseInfoService userBaseInfoService;
}