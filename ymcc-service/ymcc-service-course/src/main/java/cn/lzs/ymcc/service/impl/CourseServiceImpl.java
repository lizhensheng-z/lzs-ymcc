package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.ElDoc.CourseDoc;
import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.api.ESCourseApi;
import cn.lzs.ymcc.api.UserApi;
import cn.lzs.ymcc.constant.SystemConstants;
import cn.lzs.ymcc.domain.*;
import cn.lzs.ymcc.dto.*;
import cn.lzs.ymcc.mapper.*;
import cn.lzs.ymcc.producer.CourseProducer;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.service.ICourseChapterService;
import cn.lzs.ymcc.service.ICourseService;
import cn.lzs.ymcc.service.ICourseTeacherService;
import cn.lzs.ymcc.service.ICourseUserLearnService;
import cn.lzs.ymcc.util.LoginContext;

import cn.lzs.ymcc.vo.CourseDetailDataVO;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-06-23
 */
@Slf4j
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {


    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseDetailMapper courseDetailMapper;
    @Autowired
    private CourseResourceMapper courseResourceMapper;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;
    @Autowired
    private CourseTypeMapper courseTypeMapper;
    @Autowired
    private CourseSummaryMapper courseSummaryMapper;
    @Resource
    private ESCourseApi esCourseApi;
    @Autowired
    private UserApi userApi;
    @Autowired
    private CourseProducer courseProducer;
    @Autowired
    private ICourseChapterService courseChapterSerivce;
    @Autowired
    private ICourseTeacherService courseTeacherService;
    @Autowired
    private ICourseUserLearnService userLearnService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void AddCourse(CourseDTO courseDTO) {
        //先新增课程
        Course course = courseDTO.getCourse();
        //设置课程状态
        course.setStatus(SystemConstants.COURSE_OffLineCourse);
        //设置添加课程的用户id和用户名
        course.setLoginId(Objects.requireNonNull(LoginContext.getLogin()).getId());
        course.setLoginUserName(LoginContext.getLogin().getUsername());
        //查询讲师姓名然后存入teachersName字段
        ArrayList<Long> teacherIds = courseDTO.getTeacherIds();
        List<Teacher> teachers = teacherMapper.selectBatchIds(teacherIds);
        StringBuffer teachersName = new StringBuffer();
        for (Teacher teacher : teachers) {
            teachersName.append(teacher.getName()).append(",");
        }
        course.setTeacherNames(teachersName.toString());
        course.setTotalMinute(SystemConstants.COURSE_DEFAULT_DURATION);
        courseMapper.insert(course);
        //TODO 需要新增中间表teacher_course
        //新增课程中间表
        //批量新增
        List<CourseTeacher> courseTeachers = new ArrayList<>();
        for (Long teacherId : teacherIds) {
            CourseTeacher courseTeacher = new CourseTeacher();
            courseTeacher.setCourseId(course.getId());
            courseTeacher.setTeacherId(teacherId);
            courseTeachers.add(courseTeacher);
        }
        courseTeacherMapper.insertBatch(courseTeachers);
        //再新增课程详情
        CourseDetail courseDetail = courseDTO.getCourseDetail();
        courseDetail.setId(course.getId());//课程详情Id和课程Id一一对应
        courseDetailMapper.insert(courseDetail);
        //新增课程价格
        CourseMarket courseMarket = courseDTO.getCourseMarket();
        courseMarket.setId(course.getId());
        courseMarketMapper.insert(courseMarket);
        //新增课程资源
        CourseResource courseResource = courseDTO.getCourseResource();
        courseResource.setCourseId(course.getId());
        courseResource.setType(SystemConstants.FILE_TYPE_COURSEWARE);
        courseResourceMapper.insert(courseResource);
    }

    @Override
    public CourseDetailDataVO getCourseDetailDataBycourseID(Long courseId) {
        //参数校验:查看课程详情，该课程一定存在
        if(courseId == null){
            throw new GlobalBusinessException("课程id不能为空");
        }
        CourseDetailDataVO courseDetailDataVO = new CourseDetailDataVO();
        //查询课程信息
        Course course = courseMapper.selectById(courseId);
        if(course == null){
            throw new GlobalBusinessException("课程不存在");
        }
        courseDetailDataVO.setCourse(course);
        //查询课程详情
        CourseDetail courseDetail = courseDetailMapper.selectById(courseId);
        courseDetailDataVO.setCourseDetail(courseDetail);
        //查询课程资源
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        courseDetailDataVO.setCourseMarket(courseMarket);
        //查询课程章节  一个章节对应多个视频，还要把章节对应的视频查出来
        List<CourseChapter> courseChapters = courseChapterSerivce.getCourseChaptersByCourseId(courseId);


        courseDetailDataVO.setCourseChapters(courseChapters);
        //查询课程讲师 优化一下，查一遍
        List<Teacher> teachers =    courseTeacherService.getTeachersByCourseId(courseId);
        courseDetailDataVO.setTeachers(teachers);
        //查询课程购买信息
        CourseSummary courseSummary = courseSummaryMapper.selectById(courseId);
        courseDetailDataVO.setCourseSummary(courseSummary);
        //设置课程id
        courseDetailDataVO.setCourseId(courseId);


        return courseDetailDataVO;
    }

    @Override
    public void onLineCourse(Long id) {
        if(id == null){
            throw new GlobalBusinessException("课程id不能为空");
        }
        //1先查询课程状态
        Course course = courseMapper.selectById(id);
        if (course.getStatus().equals(SystemConstants.COURSE_OnLineCourse)){
            //说明课程状态为上线，返回错误
            throw new GlobalBusinessException("课程状态为上线，不能重复上线");
        }
        //2修改课程状态为上线
        course.setStatus(SystemConstants.COURSE_OnLineCourse);
        course.setOnlineTime(new Date());
        courseMapper.updateById(course);
        //3生成CourseDoc对象
        CourseDoc courseDoc = new CourseDoc();
        BeanUtils.copyProperties(course,courseDoc);
        CourseType courseType = courseTypeMapper.selectById(course.getCourseTypeId());
        if (courseType == null) {
            throw new GlobalBusinessException("课程类型不存在，课程类型ID: " + course.getCourseTypeId());
        }
        courseDoc.setCourseTypeName(courseType.getName());
        CourseMarket courseMarket = courseMarketMapper.selectById(course.getId());
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket,courseDoc);
            courseDoc.setCharge(courseMarket.getCharge() == 1 ? "免费" :"收费");
        }

        CourseSummary courseSummary = courseSummaryMapper.selectById(course.getId());
        if (courseSummary != null) {
            BeanUtils.copyProperties(courseSummary,courseDoc);
        }

        //4 调用Es进行保存
        JSONResult jsonResult = null;
        try {
            jsonResult = esCourseApi.saveCourseDoc(courseDoc);
//            if (!jsonResult.isSuccess()){
//                throw new GlobalBusinessException("调用ESCourse保存失败:" + jsonResult.getMessage());
//            }
        } catch (feign.RetryableException e) {
            // 捕获超时异常,但不阻塞主流程
            log.error("ES课程文档保存超时,课程ID:{},错误信息:{}", id, e.getMessage());
            // 可以选择发送到MQ进行异步重试,或者记录日志人工处理
            // 注意:这里不抛出异常,确保课程上线主流程成功
        } catch (Exception e) {
            log.error("ES课程文档保存失败,课程ID:{},错误信息:{}", id, e.getMessage());
            throw new GlobalBusinessException("ES课程文档保存失败:" + e.getMessage());
        }

        try{
            publish(course);
        }catch (Exception e){
            log.error("课程发布失败,课程ID:{},错误信息:{}", id, e.getMessage());

        }
    }
    //课程上线后发布通知
    public void publish(Course course){
        try {
            //课程成功上线，调用MQ通知用户 先引入mq然后调用mq推送
            //准备消息体
            //先准备站内消息进行发送
            StationMessageDTO stationMessageDTO = new StationMessageDTO();
            stationMessageDTO.setTitle(course.getName());
            stationMessageDTO.setType(SystemConstants.SYSTEM_MESSAGE);
            CourseDetail courseDetail = courseDetailMapper.selectById(course.getId());
            stationMessageDTO.setContent(courseDetail.getDescription());
            ArrayList<Long> userIds = new ArrayList<>();
            
            // 调用用户服务获取用户列表
            JSONResult list = userApi.list();
            Object data = list.getData();
            List<User> users = (List<User>) data;
            
            // 检查数据类型是否是 List
            for (Object obj : users) {
                if (obj instanceof LinkedHashMap) {
                    LinkedHashMap map = (LinkedHashMap) obj;
                    User user = new User();
                    //Integer转Long
                    Integer idInt = (Integer) map.get("id");
                    Long idLong = idInt.longValue(); // 或 Long.valueOf(idInt)
                    user.setId(idLong);
                    userIds.add(idLong);  //将用户ID添加到列表中
                }
            }

            stationMessageDTO.setUserIds(userIds);
            //调用生产者发送站内消息
            courseProducer.synSendStationMessage(stationMessageDTO);
            
            //准备短信通知消息
            SmsMessageDTO smsMessageDTO = new SmsMessageDTO();
            smsMessageDTO.setTitle(course.getName());
            smsMessageDTO.setContent(courseDetail.getDescription());
            ArrayList<User2Phone> user2PhoneArrayList = new ArrayList<>();
            for (Object obj : users) {
                if(obj instanceof LinkedHashMap){
                    LinkedHashMap map = (LinkedHashMap) obj;
                    User2Phone user2Phone = new User2Phone();
                    String phone = (String) map.get("phone");
                    user2Phone.setPhone(phone);
                    Integer idInt = (Integer) map.get("id");
                    Long idLong = idInt.longValue(); // 或 Long.valueOf(idInt)
                    user2Phone.setUserId(idLong);
                    user2PhoneArrayList.add(user2Phone);
                }
            }

            smsMessageDTO.setUser2PhoneList(user2PhoneArrayList);
            //调用生产者异步发送短信
            courseProducer.asynSendSmsMessage(smsMessageDTO);
            
            //准备邮箱通知消息
            EmailMessageDTO emailMessageDTO = new EmailMessageDTO();
            emailMessageDTO.setTitle(course.getName());
            emailMessageDTO.setContent(courseDetail.getDescription());
            ArrayList<User2Email> user2EmailArrayList = new ArrayList<>();

            for (Object obj : users) {
                if(obj instanceof LinkedHashMap){
                    LinkedHashMap map = (LinkedHashMap) obj;
                    User2Email user2Email = new User2Email();
                    String email = (String) map.get("email");
                    user2Email.setEmail(email);
                    Integer idInt = (Integer) map.get("id");
                    Long idLong = idInt.longValue(); // 或 Long.valueOf(idInt)
                    user2Email.setUserId(idLong);
                    user2EmailArrayList.add(user2Email);
                }
            }
            emailMessageDTO.setUser2Emails(user2EmailArrayList);
            //调用生产者异步发送邮件
            courseProducer.asynSendEmailMessage(emailMessageDTO);
            
            log.info("课程上线通知发送成功,课程ID:{},课程名称:{}", course.getId(), course.getName());
        } catch (feign.RetryableException e) {
            // 捕获Feign超时异常,记录日志但不阻塞主流程
            log.error("调用用户服务超时,课程ID:{},错误信息:{}", course.getId(), e.getMessage());
            // 超时可能是用户数据量过大导致,建议后续优化为异步批量处理
        } catch (Exception e) {
            // 捕获其他异常,记录日志但不阻塞主流程
            log.error("课程上线通知发送失败,课程ID:{},错误信息:{}", course.getId(), e.getMessage(), e);
        }
    }

    /**
     * 根据课程id查询课程信息
     * @param courseIds
     * @return
     */
    @Override
    public CourseInfoDTO getCourseInfoByCourseIds(List<Long> courseIds) {
        CourseInfoDTO courseInfoDTO  = new CourseInfoDTO();
        //只需要course，courseMarket
        List<Course> courses = courseMapper.selectBatchIds(courseIds);
        List<CourseMarket> courseMarkets = courseMarketMapper.selectBatchIds(courseIds);
        //整合成CourseDOTList
        List<CourseDTO> courseDTOS = new ArrayList<>();
        //stream流整合course和courseMarket
        courseDTOS = courses.stream()
                .flatMap(course -> courseMarkets.stream()
                        .filter(courseMarket -> course.getId().equals(courseMarket.getId()))
                        .flatMap(courseMarket ->
                                // 找到对应的 CourseDetail 和 CourseResource
                                Stream.of(new CourseDTO(
                                        course,
                                        courseMarket
                                ))
                        )
                )
                .collect(Collectors.toList());
        courseInfoDTO.setCourses(courseDTOS);
        BigDecimal totalPrice = courseMarkets.stream()
                .map(CourseMarket::getPrice)  // 提取每个CourseMarket的price
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        courseInfoDTO.setTotalAmount(totalPrice);
        return courseInfoDTO;
    }

    @Override
    public List<Course> listForUser() {
        Long loginId = LoginContext.getLogin().getId();

        List<Course> courses = userLearnService.getLearnedCourseByLoginId(loginId);
        return  courses;
    }
}
