package cn.lzs.ymcc.mapper;

import cn.lzs.ymcc.domain.Course;
import cn.lzs.ymcc.domain.CourseUserLearn;
import cn.lzs.ymcc.dto.CourseUserLearnDTO;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lzs
 * @since 2025-06-23
 */
public interface CourseUserLearnMapper extends BaseMapper<CourseUserLearn> {

    List<Course> getLearnedCourseByLoginId(Long loginId);


    void addCourseUserLearn(@Param("list") List<CourseUserLearn> courseUserLearnList);
}
