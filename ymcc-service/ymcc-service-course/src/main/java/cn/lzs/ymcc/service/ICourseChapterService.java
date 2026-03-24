package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.CourseChapter;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 课程章节 ， 一个课程，多个章节，一个章节，多个视频 服务类
 * </p>
 *
 * @author lzs
 * @since 2025-06-23
 */
public interface ICourseChapterService extends IService<CourseChapter> {

    List<CourseChapter> listByCourseId(Long id);

    List<CourseChapter> getCourseChaptersByCourseId(Long courseId);
}
