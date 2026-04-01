package cn.lzs.ymcc.dto;

import cn.lzs.ymcc.domain.Course;
import cn.lzs.ymcc.domain.CourseDetail;
import cn.lzs.ymcc.domain.CourseMarket;
import cn.lzs.ymcc.domain.CourseResource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 李振生
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Course course;
    private CourseDetail courseDetail;
    private CourseMarket courseMarket;
    private CourseResource courseResource;
    private List<Long> teacherIds;

    public CourseDTO(Course course, CourseMarket courseMarket) {
        this.course = course;
        this.courseMarket = courseMarket;
    }

}
