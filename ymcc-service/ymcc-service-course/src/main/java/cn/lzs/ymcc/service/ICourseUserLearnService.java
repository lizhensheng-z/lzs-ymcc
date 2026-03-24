package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.Course;
import cn.lzs.ymcc.domain.CourseUserLearn;
import cn.lzs.ymcc.dto.CourseUserLearnDTO;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lzs
 * @since 2025-06-23
 */
public interface ICourseUserLearnService extends IService<CourseUserLearn> {

  List<Course>  getLearnedCourseByLoginId(Long loginId);

    void addCourseUserLearn(CourseUserLearnDTO courseUserLearnDTO);
}
