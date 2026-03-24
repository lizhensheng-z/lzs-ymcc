package cn.lzs.ymcc.service;

import cn.lzs.ymcc.DTO.CourseSearchDTO;
import cn.lzs.ymcc.ElDoc.CourseDoc;
import cn.lzs.ymcc.result.PageList;

/**
 * @author 李振生
 */
public interface ESCourseService {
    void saveCourseDoc(CourseDoc courseDoc);

    PageList<CourseDoc> searchCourse(CourseSearchDTO courseSearchDTO);
}
