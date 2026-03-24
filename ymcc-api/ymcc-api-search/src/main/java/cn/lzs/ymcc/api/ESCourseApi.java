package cn.lzs.ymcc.api;

import cn.lzs.ymcc.ElDoc.CourseDoc;
import cn.lzs.ymcc.result.JSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author 李振生
 */
@FeignClient(name = "service-search")
@RequestMapping("/esCourse")
public interface ESCourseApi {

    @PostMapping(value = "/save", consumes = "application/json")
    JSONResult saveCourseDoc(@RequestBody CourseDoc courseDoc);
}


