package cn.lzs.ymcc.api;

import cn.lzs.ymcc.dto.CourseUserLearnDTO;
import cn.lzs.ymcc.result.JSONResult;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseFallbackFactory implements FallbackFactory<CourseFeignApi> {


    @Override
    public CourseFeignApi create(Throwable throwable) {
        return new CourseFeignApi() {
            @Override
            public JSONResult getCourseInfo(List<Long> courseIds) {
                return null;
            }

            @Override
            public JSONResult addCourseUserLearn(CourseUserLearnDTO courseUserLearnDTO) {
                return null;
            }

            @Override
            public JSONResult info(String courseIds) {
                return  JSONResult.error("服务不可用，请稍后重试！");
            }
        };
    }


}
