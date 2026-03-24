package cn.lzs.ymcc.controller;

import cn.lzs.ymcc.DTO.CourseSearchDTO;
import cn.lzs.ymcc.ElDoc.CourseDoc;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.result.PageList;
import cn.lzs.ymcc.service.ESCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李振生
 */
@RestController
//@RequestMapping("/esCourse")
public class ESCourseController {

    @Autowired
    private ESCourseService esCourseService;

    /**
     * 保存课程信息到ES
     * @param courseDoc
     * @return
     */
    @PostMapping(value = "/esCourse/save", consumes = "application/json")
    public JSONResult saveCourseDoc(@RequestBody CourseDoc courseDoc){
        esCourseService.saveCourseDoc(courseDoc);
        return JSONResult.success();
    }
    /**
     * 课程列表查询
     */
    @PostMapping(value = "course/search", consumes = "application/json")
    public JSONResult searchCourse(@RequestBody CourseSearchDTO courseSearchDTO){
        PageList<CourseDoc> courseDocs = esCourseService.searchCourse(courseSearchDTO);
        return JSONResult.success(courseDocs);
    }
}
