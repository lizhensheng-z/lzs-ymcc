package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.Course;
import cn.lzs.ymcc.dto.CourseDTO;
import cn.lzs.ymcc.dto.CourseInfoDTO;
import cn.lzs.ymcc.vo.CourseDetailDataVO;

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
public interface ICourseService extends IService<Course> {

    void AddCourse(CourseDTO courseDTO);

    void onLineCourse(Long id);

    CourseDetailDataVO getCourseDetailDataBycourseID(Long courseId);

    CourseInfoDTO getCourseInfoByCourseIds(List<Long> courseIds);

    List<Course> listForUser();
}
