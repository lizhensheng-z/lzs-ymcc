package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.service.ICourseTypeService;
import cn.lzs.ymcc.domain.CourseType;
import cn.lzs.ymcc.query.CourseTypeQuery;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.result.PageList;
import cn.lzs.ymcc.vo.CourseTypeCrumbsVO;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/courseType")
public class CourseTypeController {

    @Autowired
    public ICourseTypeService courseTypeService;

    /**
    * 保存和修改公用的
    */
    @RequestMapping(value="/save",method= RequestMethod.POST)
    public JSONResult saveOrUpdate(@RequestBody CourseType courseType){
        if(courseType.getId()!=null){
            courseTypeService.updateById(courseType);
        }else{
            courseTypeService.insert(courseType);
        }
        return JSONResult.success();
    }


    /**
     * 获取课程类型treeData 无限级树
     * @return
     */
    @RequestMapping(value="/treeData",method= RequestMethod.GET)
    public JSONResult treeData(){
        List<CourseType> courseTypeList =     courseTypeService.treeData();
        return JSONResult.success(courseTypeList);
    }

    /**
     * 面包屑查询
     * @param courseTypeId
     * @return
     */
    @GetMapping("/crumbs/{courseTypeId}")
    public JSONResult crumbs(@PathVariable Long courseTypeId){
        List<CourseTypeCrumbsVO> courseTypeList =     courseTypeService.getCourseTypeCrumbs(courseTypeId);
        return JSONResult.success(courseTypeList);
    }



    /**
    * 删除对象
    */
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    public JSONResult delete(@PathVariable("id") Long id){
        courseTypeService.deleteById(id);
        return JSONResult.success();
    }

    /**
   * 获取对象
   */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public JSONResult get(@PathVariable("id")Long id){
        return JSONResult.success(courseTypeService.selectById(id));
    }


    /**
    * 查询所有对象
    */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public JSONResult list(){
        return JSONResult.success(courseTypeService.selectList(null));
    }


    /**
    * 带条件分页查询数据
    */
    @RequestMapping(value = "/pagelist",method = RequestMethod.POST)
    public JSONResult page(@RequestBody CourseTypeQuery query){
        Page<CourseType> page = new Page<CourseType>(query.getPage(),query.getRows());
        page = courseTypeService.selectPage(page);
        return JSONResult.success(new PageList<CourseType>(page.getTotal(),page.getRecords()));
    }
}
