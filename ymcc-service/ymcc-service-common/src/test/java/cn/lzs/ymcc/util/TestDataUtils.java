package cn.lzs.ymcc.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * 测试数据生成工具类
 * 提供常用的测试数据生成方法，提高测试代码的可读性和可维护性
 *
 * @author lzs
 * @date 2026-04-01
 */
public class TestDataUtils {

    private static final Random RANDOM = new Random();

    // ==================== 字符串生成方法 ====================

    /**
     * 生成随机 UUID 字符串
     * @return 去掉横杠的 UUID
     */
    public static String randomUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成随机手机号
     * @return 11 位手机号
     */
    public static String randomPhone() {
        String[] prefixes = {"138", "139", "150", "151", "158", "159", "182", "187", "188"};
        String prefix = prefixes[RANDOM.nextInt(prefixes.length)];
        return prefix + String.format("%08d", RANDOM.nextInt(100000000));
    }

    /**
     * 生成随机邮箱
     * @return 随机邮箱地址
     */
    public static String randomEmail() {
        String[] domains = {"qq.com", "163.com", "gmail.com", "outlook.com", "sina.com"};
        String domain = domains[RANDOM.nextInt(domains.length)];
        return randomUuid().substring(0, 8) + "@" + domain;
    }

    /**
     * 生成随机用户名
     * @return 随机用户名
     */
    public static String randomUsername() {
        String[] prefixes = {"user", "test", "admin", "guest", "vip"};
        String prefix = prefixes[RANDOM.nextInt(prefixes.length)];
        return prefix + "_" + System.currentTimeMillis();
    }

    /**
     * 生成随机中文姓名
     * @return 2-3 字中文姓名
     */
    public static String randomChineseName() {
        String[] surnames = {"赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫"};
        String[] names = {"伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军", "洋", "勇"};

        String surname = surnames[RANDOM.nextInt(surnames.length)];
        String name1 = names[RANDOM.nextInt(names.length)];
        String name2 = names[RANDOM.nextInt(names.length)];

        return RANDOM.nextBoolean() ? surname + name1 : surname + name1 + name2;
    }

    /**
     * 生成随机字符串
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // ==================== 数字生成方法 ====================

    /**
     * 生成随机整数
     * @param min 最小值
     * @param max 最大值
     * @return 随机整数
     */
    public static int randomInt(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    /**
     * 生成随机长整数
     * @param min 最小值
     * @param max 最大值
     * @return 随机长整数
     */
    public static long randomLong(long min, long max) {
        return (long) (RANDOM.nextDouble() * (max - min + 1)) + min;
    }

    /**
     * 生成随机金额
     * @param min 最小金额
     * @param max 最大金额
     * @return 随机金额（保留 2 位小数）
     */
    public static BigDecimal randomAmount(int min, int max) {
        int amount = randomInt(min * 100, max * 100);
        return new BigDecimal(amount).divide(new BigDecimal(100));
    }

    // ==================== 日期生成方法 ====================

    /**
     * 生成随机日期（过去某天）
     * @param days 最大天数
     * @return 随机日期
     */
    public static Date randomPastDate(int days) {
        long pastMillis = RANDOM.nextInt(days * 24 * 60 * 60 * 1000);
        return new Date(System.currentTimeMillis() - pastMillis);
    }

    /**
     * 生成随机日期（未来某天）
     * @param days 最大天数
     * @return 随机日期
     */
    public static Date randomFutureDate(int days) {
        long futureMillis = RANDOM.nextInt(days * 24 * 60 * 60 * 1000);
        return new Date(System.currentTimeMillis() + futureMillis);
    }

    /**
     * 生成指定天数后的日期
     * @param daysAfter 天数
     * @return 日期
     */
    public static Date dateAfterDays(int daysAfter) {
        return new Date(System.currentTimeMillis() + daysAfter * 24L * 60 * 60 * 1000);
    }

    /**
     * 生成指定天数前的日期
     * @param daysBefore 天数
     * @return 日期
     */
    public static Date dateBeforeDays(int daysBefore) {
        return new Date(System.currentTimeMillis() - daysBefore * 24L * 60 * 60 * 1000);
    }

    // ==================== 短信验证码生成方法 ====================

    /**
     * 生成 6 位数字验证码
     * @return 6 位验证码字符串
     */
    public static String randomSmsCode() {
        return String.format("%06d", RANDOM.nextInt(1000000));
    }

    /**
     * 生成 4 位数字验证码
     * @return 4 位验证码字符串
     */
    public static String randomSmsCode4() {
        return String.format("%04d", RANDOM.nextInt(10000));
    }

    // ==================== 订单号生成方法 ====================

    /**
     * 生成模拟订单号
     * @return 订单号
     */
    public static String randomOrderNo() {
        return "TEST" + System.currentTimeMillis() + randomInt(1000, 9999);
    }

    // ==================== 文件相关方法 ====================

    /**
     * 生成随机文件 MD5
     * @return 32 位 MD5 字符串
     */
    public static String randomFileMd5() {
        return randomString(32).toLowerCase();
    }

    // ==================== 课程相关方法 ====================

    /**
     * 生成随机课程名称
     * @return 课程名称
     */
    public static String randomCourseName() {
        String[] prefixes = {"Java", "Python", "前端", "大数据", "人工智能", "MySQL", "Redis"};
        String[] suffixes = {"从入门到精通", "实战教程", "高级编程", "基础教程", "进阶课程"};
        return prefixes[RANDOM.nextInt(prefixes.length)] + suffixes[RANDOM.nextInt(suffixes.length)];
    }
}