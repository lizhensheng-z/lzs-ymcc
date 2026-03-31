package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.dto.CourseDTO;
import cn.lzs.ymcc.dto.CourseInfoDTO;
import cn.lzs.ymcc.dto.CourseUserLearnDTO;
import cn.lzs.ymcc.service.ICourseService;
import cn.lzs.ymcc.domain.Course;
import cn.lzs.ymcc.query.CourseQuery;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.result.PageList;
import cn.lzs.ymcc.service.ICourseUserLearnService;
import cn.lzs.ymcc.vo.CourseDetailDataVO;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    public ICourseService courseService;
    @Autowired
    private ICourseUserLearnService courseUserLearnService;

    /**
     * 新增课程
     * @param courseDTO
     * @return
     */
    @RequestMapping(value="/save",method= RequestMethod.POST)
    public JSONResult AddCourse(@RequestBody CourseDTO courseDTO){
        courseService.AddCourse(courseDTO);
        return JSONResult.success();
    }
    /**
     *  课程上线
     * @param id
     * @return
     */
    @RequestMapping(value="/onLineCourse/{id}",method= RequestMethod.POST)
    public JSONResult onLineCourse(@PathVariable("id") Long id){
        courseService.onLineCourse(id);

        return JSONResult.success();
    }

    /**
     * 获取课程详细信息
     * @param courseId
     * @return
     */
    @GetMapping("/detail/data/{courseId}")
    public JSONResult getCourseDetailData(@PathVariable("courseId") Long courseId){
         CourseDetailDataVO courseDetailDataVO = courseService.getCourseDetailDataBycourseID(courseId);
         return JSONResult.success(courseDetailDataVO);
    }


    /**
     * 根据课程id获取课程信息
     * @param courseIds
     * @return
     */
    @GetMapping("/info/{courseIds}")
    public JSONResult getCourseInfo(@PathVariable("courseIds")List<Long> courseIds){
        CourseInfoDTO courseInfoDTO = courseService.getCourseInfoByCourseIds(courseIds);
        return JSONResult.success(courseInfoDTO);
    }

    /**
     * 查询用户已购买的课程
     * @return
     */
    @GetMapping("/listForUser")
    public JSONResult listForUser(){
          List<Course> courseList = courseService.listForUser();
          return JSONResult.success(courseList);
    }

    /**
     * 增加用户学习课程表
     */
    @PostMapping("/addCourseUserLearn")
    public JSONResult addCourseUserLearn(@RequestBody CourseUserLearnDTO courseUserLearnDTO){
        courseUserLearnService.addCourseUserLearn(courseUserLearnDTO);
        return JSONResult.success();
    }

    /**
     * 课程下架
     */
    @PostMapping("/offLineCourse/{id}")
    public JSONResult offLineCourse(@PathVariable("id") Long id){
        Course course = courseService.selectById(id);
        if (course != null) {
            course.setStatus(2); // 2表示下架
            courseService.updateById(course);
            return JSONResult.success();
        }
        return JSONResult.error("课程不存在");
    }



    /**
    * 保存和修改公用的
    */
//    @RequestMapping(value="/save",method= RequestMethod.POST)
//    public JSONResult saveOrUpdate(@RequestBody Course course){
//        if(course.getId()!=null){
//            courseService.updateById(course);
//        }else{
//            courseService.insert(course);
//        }
//        return JSONResult.success();
//    }

    /**
    * 删除对象
    */
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    public JSONResult delete(@PathVariable("id") Long id){
        courseService.deleteById(id);
        return JSONResult.success();
    }

    /**
   * 获取对象
   */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public JSONResult get(@PathVariable("id")Long id){
        return JSONResult.success(courseService.selectById(id));
    }


    /**
    * 查询所有对象
    */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public JSONResult list(){
        return JSONResult.success(courseService.selectList(null));
    }


    /**
    * 带条件分页查询数据
    */
    @RequestMapping(value = "/pagelist",method = RequestMethod.POST)
    public JSONResult page(@RequestBody CourseQuery query){
        Page<Course> page = new Page<Course>(query.getPage(),query.getRows());
        page = courseService.selectPage(page);
        return JSONResult.success(new PageList<Course>(page.getTotal(),page.getRecords()));
    }
}
