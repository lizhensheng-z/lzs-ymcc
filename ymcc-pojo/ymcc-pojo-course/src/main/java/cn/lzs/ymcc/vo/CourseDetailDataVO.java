package cn.lzs.ymcc.vo;

import cn.lzs.ymcc.domain.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 李振生
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailDataVO {
    private Course  course;
    private CourseMarket courseMarket;
    private List<CourseChapter> courseChapters;
    private List<Teacher> teachers;
    private CourseSummary courseSummary;
    private CourseDetail courseDetail;
    private Long courseId;

}
