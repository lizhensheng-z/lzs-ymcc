package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.domain.Course;
import cn.lzs.ymcc.service.ICourseUserLearnService;

import cn.lzs.ymcc.domain.CourseUserLearn;
import cn.lzs.ymcc.query.CourseUserLearnQuery;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.result.PageList;
import cn.lzs.ymcc.util.LoginContext;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courseUserLearn")
public class CourseUserLearnController {

    @Autowired
    public ICourseUserLearnService courseUserLearnService;

    /**
    * 保存和修改公用的
    */
    @RequestMapping(value="/save",method= RequestMethod.POST)
    public JSONResult saveOrUpdate(@RequestBody CourseUserLearn courseUserLearn){
        if(courseUserLearn.getId()!=null){
            courseUserLearnService.updateById(courseUserLearn);
        }else{
            courseUserLearnService.insert(courseUserLearn);
        }
        return JSONResult.success();
    }

    /**
    * 删除对象
    */
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    public JSONResult delete(@PathVariable("id") Long id){
        courseUserLearnService.deleteById(id);
        return JSONResult.success();
    }

    /**
   * 获取对象
   */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public JSONResult get(@PathVariable("id")Long id){
        return JSONResult.success(courseUserLearnService.selectById(id));
    }

    /**
     * 获取用户已购买的课程列表
     */
    @GetMapping("/myCourses")
    public JSONResult getMyCourses(@RequestParam("loginId") Long loginId) {
        if (loginId == null) {
            return JSONResult.error("用户未登录");
        }
        List<Course> courses = courseUserLearnService.getLearnedCourseByLoginId(loginId);
        return JSONResult.success(courses);
    }


    /**
    * 查询所有对象
    */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public JSONResult list(){
        return JSONResult.success(courseUserLearnService.selectList(null));
    }


    /**
    * 带条件分页查询数据
    */
    @RequestMapping(value = "/pagelist",method = RequestMethod.POST)
    public JSONResult page(@RequestBody CourseUserLearnQuery query){
        Page<CourseUserLearn> page = new Page<CourseUserLearn>(query.getPage(),query.getRows());
        page = courseUserLearnService.selectPage(page);
        return JSONResult.success(new PageList<CourseUserLearn>(page.getTotal(),page.getRecords()));
    }
}
