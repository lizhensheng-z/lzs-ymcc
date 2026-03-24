package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.domain.CourseTeacher;
import cn.lzs.ymcc.domain.Teacher;
import cn.lzs.ymcc.mapper.CourseTeacherMapper;
import cn.lzs.ymcc.service.ICourseTeacherService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author 李振生
 */
@Service
public class CourseTeacherServiceImpl  extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements ICourseTeacherService{
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;
    @Override
    public List<Teacher> getTeachersByCourseId(Long courseId) {
        return courseTeacherMapper.getTeachersByCourseId(courseId);
    }
}
