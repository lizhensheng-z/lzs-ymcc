package cn.lzs.ymcc.mapper;

import cn.lzs.ymcc.domain.CourseTeacher;
import cn.lzs.ymcc.domain.Teacher;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 李振生
 * 维护t_course t_teacher 关系表（中间表）
 */
@Mapper
public interface CourseTeacherMapper extends BaseMapper<CourseTeacher> {
    void insertBatch(List<CourseTeacher> courseTeachers);

    List<Teacher> getTeachersByCourseId(Long courseId);
}
