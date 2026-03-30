package cn.lzs.ymcc.api;

import cn.lzs.ymcc.dto.CourseUserLearnDTO;
import cn.lzs.ymcc.result.JSONResult;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CourseFallbackFactory implements FallbackFactory<CourseFeignApi> {


    @Override
    public CourseFeignApi create(Throwable throwable) {
        return new CourseFeignApi() {
            @Override
            public JSONResult getCourseInfo(List<Long> courseIds) {
                return JSONResult.error("服务不可用，请稍后重试！");
            }

            @Override
            public JSONResult addCourseUserLearn(CourseUserLearnDTO courseUserLearnDTO) {
                return JSONResult.error("服务不可用，请稍后重试！");
            }

            @Override
            public JSONResult info(String courseIds) {
                return  JSONResult.error("服务不可用，请稍后重试！");
            }

            @Override
            public JSONResult getMyCourses(Long loginId) {
                // 返回空列表而不是null，避免空指针异常
                return JSONResult.success(new ArrayList<>());
            }
        };
    }



}