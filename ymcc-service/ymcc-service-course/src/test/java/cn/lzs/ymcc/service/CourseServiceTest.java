package cn.lzs.ymcc.service;

import cn.lzs.ymcc.CourseApplication;
import cn.lzs.ymcc.domain.Course;
import cn.lzs.ymcc.domain.CourseDetail;
import cn.lzs.ymcc.domain.CourseMarket;
import cn.lzs.ymcc.dto.CourseDTO;
import cn.lzs.ymcc.mapper.CourseMapper;
import cn.lzs.ymcc.mapper.CourseDetailMapper;
import cn.lzs.ymcc.mapper.CourseMarketMapper;
import cn.lzs.ymcc.service.impl.CourseServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 课程服务单元测试类
 * 测试课程的发布、查询、更新等功能
 *
 * @author lzs
 * @date 2026-04-01
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CourseApplication.class)
public class CourseServiceTest {

    @Autowired
    private CourseServiceImpl courseService;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseDetailMapper courseDetailMapper;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    // 测试用的课程数据
    private static final String TEST_COURSE_NAME = "Java 编程从入门到精通";
    private static final BigDecimal TEST_PRICE = new BigDecimal("99.00");
    private static final Long TEST_COURSE_TYPE_ID = 1L;

    /**
     * 测试前准备 - 清理可能存在的旧数据
     */
    @Before
    public void setUp() {
        // 清理测试数据
        List<Course> existingCourses = courseMapper.selectList(
            new com.baomidou.mybatisplus.mapper.EntityWrapper<Course>()
                .like("name", "测试课程")
        );
        for (Course course : existingCourses) {
            courseMapper.deleteById(course.getId());
        }
    }

    /**
     * 测试后清理
     */
    @After
    public void tearDown() {
        // 清理测试数据
        List<Course> testCourses = courseMapper.selectList(
            new com.baomidou.mybatisplus.mapper.EntityWrapper<Course>()
                .like("name", "测试课程")
        );
        for (Course course : testCourses) {
            courseMapper.deleteById(course.getId());
        }
    }

    /**
     * 测试新增课程
     * 验证课程能否正确创建
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testAddCourse() {
        // 准备课程数据
        CourseDTO courseDTO = new CourseDTO();

        Course course = new Course();
        course.setName("测试课程-" + System.currentTimeMillis());
        course.setCourseTypeId(TEST_COURSE_TYPE_ID);
        course.setStatus(1); // 草稿状态
        courseDTO.setCourse(course);

        // 执行新增
        courseService.AddCourse(courseDTO);

        // 验证课程已创建
        assertNotNull("课程 ID 应生成", course.getId());

        Course savedCourse = courseMapper.selectById(course.getId());
        assertNotNull("课程应已保存", savedCourse);
        assertEquals("课程名称应匹配", course.getName(), savedCourse.getName());
    }

    /**
     * 测试课程发布
     * 验证课程能否成功发布
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testPublishCourse() {
        // 准备测试数据 - 先创建课程
        Course course = new Course();
        course.setName("测试发布课程-" + System.currentTimeMillis());
        course.setCourseTypeId(TEST_COURSE_TYPE_ID);
        course.setStatus(1); // 草稿状态
        courseMapper.insert(course);

        // 创建课程详情
        CourseDetail detail = new CourseDetail();
        detail.setCourseId(course.getId());
        detail.setDescription("这是一个测试课程的详情介绍");
        courseDetailMapper.insert(detail);

        // 创建课程市场信息
        CourseMarket market = new CourseMarket();
        market.setCourseId(course.getId());
        market.setPrice(TEST_PRICE);
        market.setOriginalPrice(new BigDecimal("199.00"));
        courseMarketMapper.insert(market);

        // 执行发布
        courseService.publish(course.getId());

        // 验证课程状态已更新
        Course publishedCourse = courseMapper.selectById(course.getId());
        assertEquals("课程状态应更新为已发布", Course.Online, publishedCourse.getStatus());
    }

    /**
     * 测试根据 ID 查询课程
     * 验证能否正确查询课程信息
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testGetCourseById() {
        // 准备测试数据
        Course course = new Course();
        course.setName("测试查询课程-" + System.currentTimeMillis());
        course.setCourseTypeId(TEST_COURSE_TYPE_ID);
        course.setStatus(1);
        courseMapper.insert(course);

        // 执行查询
        Course result = courseService.selectById(course.getId());

        // 验证查询结果
        assertNotNull("课程不应为空", result);
        assertEquals("课程 ID 应匹配", course.getId(), result.getId());
        assertEquals("课程名称应匹配", course.getName(), result.getName());
    }

    /**
     * 测试课程分页查询
     * 验证分页查询功能是否正常工作
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testPageQuery() {
        // 准备测试数据 - 添加多个课程
        for (int i = 0; i < 5; i++) {
            Course course = new Course();
            course.setName("测试分页课程-" + i);
            course.setCourseTypeId(TEST_COURSE_TYPE_ID);
            course.setStatus(1);
            courseMapper.insert(course);
        }

        // 执行分页查询
        com.baomidou.mybatisplus.plugins.Page<Course> page = new com.baomidou.mybatisplus.plugins.Page<>(1, 10);
        List<Course> courses = courseService.selectPage(page).getRecords();

        // 验证查询结果
        assertNotNull("查询结果不应为空", courses);
        assertTrue("应至少包含 5 个课程", courses.size() >= 5);
    }

    /**
     * 测试更新课程
     * 验证课程信息能否正确更新
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testUpdateCourse() {
        // 准备测试数据
        Course course = new Course();
        course.setName("原始课程名称");
        course.setCourseTypeId(TEST_COURSE_TYPE_ID);
        course.setStatus(1);
        courseMapper.insert(course);

        // 更新课程信息
        String newName = "更新后的课程名称-" + System.currentTimeMillis();
        course.setName(newName);
        courseService.updateById(course);

        // 验证更新结果
        Course updatedCourse = courseMapper.selectById(course.getId());
        assertNotNull("更新后的课程不应为空", updatedCourse);
        assertEquals("课程名称应已更新", newName, updatedCourse.getName());
    }

    /**
     * 测试删除课程
     * 验证课程能否被正确删除
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testDeleteCourse() {
        // 准备测试数据
        Course course = new Course();
        course.setName("待删除课程-" + System.currentTimeMillis());
        course.setCourseTypeId(TEST_COURSE_TYPE_ID);
        course.setStatus(1);
        courseMapper.insert(course);

        Integer courseId = course.getId();

        // 执行删除
        courseService.deleteById(courseId);

        // 验证删除结果
        Course deletedCourse = courseMapper.selectById(courseId);
        assertNull("课程应已被删除", deletedCourse);
    }

    /**
     * 测试课程价格查询
     * 验证能否正确获取课程价格信息
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testGetCourseMarket() {
        // 准备测试数据
        Course course = new Course();
        course.setName("测试价格课程-" + System.currentTimeMillis());
        course.setCourseTypeId(TEST_COURSE_TYPE_ID);
        course.setStatus(1);
        courseMapper.insert(course);

        // 创建市场信息
        CourseMarket market = new CourseMarket();
        market.setCourseId(course.getId());
        market.setPrice(TEST_PRICE);
        market.setOriginalPrice(new BigDecimal("199.00"));
        courseMarketMapper.insert(market);

        // 执行查询
        CourseMarket result = courseMarketMapper.selectById(course.getId());

        // 验证查询结果
        assertNotNull("市场信息不应为空", result);
        assertEquals("价格应匹配", TEST_PRICE, result.getPrice());
    }

    /**
     * 测试课程详情查询
     * 验证能否正确获取课程详情
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testGetCourseDetail() {
        // 准备测试数据
        Course course = new Course();
        course.setName("测试详情课程-" + System.currentTimeMillis());
        course.setCourseTypeId(TEST_COURSE_TYPE_ID);
        course.setStatus(1);
        courseMapper.insert(course);

        // 创建课程详情
        CourseDetail detail = new CourseDetail();
        detail.setCourseId(course.getId());
        detail.setDescription("这是一个测试课程的详细介绍内容");
        detail.setTeachplan("课程大纲内容");
        courseDetailMapper.insert(detail);

        // 执行查询
        CourseDetail result = courseDetailMapper.selectOne(
            new com.baomidou.mybatisplus.mapper.EntityWrapper<CourseDetail>()
                .eq("course_id", course.getId())
        );

        // 验证查询结果
        assertNotNull("课程详情不应为空", result);
        assertEquals("详情内容应匹配", "这是一个测试课程的详细介绍内容", result.getDescription());
    }

    /**
     * 测试课程状态流转
     * 验证课程状态能否正确变更
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testCourseStatusTransition() {
        // 准备测试数据 - 草稿状态
        Course course = new Course();
        course.setName("测试状态流转课程-" + System.currentTimeMillis());
        course.setCourseTypeId(TEST_COURSE_TYPE_ID);
        course.setStatus(1); // 草稿
        courseMapper.insert(course);

        // 验证初始状态
        Course savedCourse = courseMapper.selectById(course.getId());
        assertEquals("初始状态应为草稿", Integer.valueOf(1), savedCourse.getStatus());

        // 更新为已发布
        course.setStatus(2); // 已发布
        courseService.updateById(course);

        // 验证状态变更
        Course publishedCourse = courseMapper.selectById(course.getId());
        assertEquals("状态应更新为已发布", Integer.valueOf(2), publishedCourse.getStatus());
    }
}