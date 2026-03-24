package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.domain.Course;
import cn.lzs.ymcc.domain.CourseUserLearn;
import cn.lzs.ymcc.dto.CourseUserLearnDTO;
import cn.lzs.ymcc.mapper.CourseUserLearnMapper;
import cn.lzs.ymcc.service.ICourseUserLearnService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-06-23
 */
@Service
public class CourseUserLearnServiceImpl extends ServiceImpl<CourseUserLearnMapper, CourseUserLearn> implements ICourseUserLearnService {


    @Autowired
    private CourseUserLearnMapper courseUserLearnMapper;

    @Override
    public List<Course> getLearnedCourseByLoginId(Long loginId) {
      return  courseUserLearnMapper.getLearnedCourseByLoginId(loginId);
    }

    /**
     * 批量新增用户购买的课程，这样用户才有权限观看
     * @param courseUserLearnDTO
     */
    @Override
    public void addCourseUserLearn(CourseUserLearnDTO courseUserLearnDTO) {
        //把CourseUserLearnDTO 转换成 CourseUserLearn
        List<CourseUserLearn> courseUserLearnList = new ArrayList<>();
        for (Long courseId : courseUserLearnDTO.getCourseIds()) {
            CourseUserLearn courseUserLearn = new CourseUserLearn();
            BeanUtils.copyProperties(courseUserLearnDTO,courseUserLearn);
            courseUserLearn.setCourseId(courseId);
            courseUserLearnList.add(courseUserLearn);
        }
        courseUserLearnMapper.addCourseUserLearn(courseUserLearnList);
    }



}
