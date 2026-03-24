package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.CourseSummary;
import cn.lzs.ymcc.domain.CourseTeacher;
import cn.lzs.ymcc.domain.Teacher;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * @author 李振生
 */

public interface ICourseTeacherService extends IService<CourseTeacher> {
    List<Teacher> getTeachersByCourseId(Long courseId);
}
