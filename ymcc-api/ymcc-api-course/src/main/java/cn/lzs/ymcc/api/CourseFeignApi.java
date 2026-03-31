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
public interface CourseFeignApi {
    /**
     * 根据课程id获取课程信息
     * @param courseIds
     * @return
     */
    @GetMapping("/course/info/{courseIds}")
    public JSONResult getCourseInfo(@PathVariable("courseIds") List<Long> courseIds);

    /**
     * 增加用户学习课程表
     */
    @PostMapping("/course/addCourseUserLearn")
    public JSONResult addCourseUserLearn(@RequestBody CourseUserLearnDTO courseUserLearnDTO);

    /**
     * 获取订单信息
     * @param courseIds
     * @return
     */
    @GetMapping("/course/rpc/info/{courseIds}")
    JSONResult info(@PathVariable String courseIds);

    /**
     * 获取用户已购买的课程列表
     * @param loginId 用户登录ID
     * @return
     */
    @GetMapping("/courseUserLearn/myCourses")
    JSONResult getMyCourses(@RequestParam("loginId") Long loginId);

}