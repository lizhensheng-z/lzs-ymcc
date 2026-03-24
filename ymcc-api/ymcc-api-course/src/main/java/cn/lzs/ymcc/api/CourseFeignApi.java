package cn.lzs.ymcc.api;

import cn.lzs.ymcc.dto.CourseUserLearnDTO;
import cn.lzs.ymcc.result.JSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 李振生
 */
@FeignClient(name = "service-course")
@RequestMapping("/course")
public interface CourseFeignApi {
    /**
     * 根据课程id获取课程信息
     * @param courseIds
     * @return
     */
    @GetMapping("/info/{courseIds}")
    public JSONResult getCourseInfo(@PathVariable("courseIds") List<Long> courseIds);

    /**
     * 增加用户学习课程表
     */
    @PostMapping("/addCourseUserLearn")
    public JSONResult addCourseUserLearn(@RequestBody CourseUserLearnDTO courseUserLearnDTO);

}
