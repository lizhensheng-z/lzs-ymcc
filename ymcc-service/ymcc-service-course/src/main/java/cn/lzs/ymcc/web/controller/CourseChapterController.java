package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.service.ICourseChapterService;
import cn.lzs.ymcc.domain.CourseChapter;
import cn.lzs.ymcc.query.CourseChapterQuery;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.result.PageList;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courseChapter")
public class CourseChapterController {

    @Autowired
    public ICourseChapterService courseChapterService;

    @RequestMapping(value = "/listByCourseId/{id}",method = RequestMethod.GET)
    public JSONResult listByCourseId(@PathVariable("id")Long id){
        List<CourseChapter> courseChapterList = courseChapterService.listByCourseId(id);
        return JSONResult.success(courseChapterList);
    }

    /**
    * 保存和修改公用的
    */
    @RequestMapping(value="/save",method= RequestMethod.POST)
    public JSONResult saveOrUpdate(@RequestBody CourseChapter courseChapter){
        if(courseChapter.getId()!=null){
            courseChapterService.updateById(courseChapter);
        }else{
            courseChapterService.insert(courseChapter);
        }
        return JSONResult.success();
    }

    /**
    * 删除对象
    */
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    public JSONResult delete(@PathVariable("id") Long id){
        courseChapterService.deleteById(id);
        return JSONResult.success();
    }

    /**
   * 获取对象
   */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public JSONResult get(@PathVariable("id")Long id){
        return JSONResult.success(courseChapterService.selectById(id));
    }


    /**
    * 查询所有对象
    */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public JSONResult list(){
        return JSONResult.success(courseChapterService.selectList(null));
    }


    /**
    * 带条件分页查询数据
    */
    @RequestMapping(value = "/pagelist",method = RequestMethod.POST)
    public JSONResult page(@RequestBody CourseChapterQuery query){
        Page<CourseChapter> page = new Page<CourseChapter>(query.getPage(),query.getRows());
        page = courseChapterService.selectPage(page);
        return JSONResult.success(new PageList<CourseChapter>(page.getTotal(),page.getRecords()));
    }
}
