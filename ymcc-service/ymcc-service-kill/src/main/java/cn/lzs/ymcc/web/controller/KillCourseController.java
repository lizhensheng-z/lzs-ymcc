package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.dto.KillParamDto;
import cn.lzs.ymcc.service.IKillCourseService;
import cn.lzs.ymcc.domain.KillCourse;
import cn.lzs.ymcc.query.KillCourseQuery;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.result.PageList;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/killCourse")
public class KillCourseController {

    @Autowired
    private IKillCourseService killCourseService;




    /**
     * 执行秒杀,生成预订单返回
     *
     * @param dto
     * @return
     */
    @PostMapping("/kill")
    public JSONResult kill(@RequestBody @Valid KillParamDto dto) {
        String orderNo = killCourseService.kill(dto);
        return JSONResult.success(orderNo);
    }

    /**
     * 从redis查询单个秒杀商品
     *
     * @return
     */
    @GetMapping("/online/one/{killId}/{activityId}")
    public JSONResult onlineOne(@PathVariable("killId") Long killId, @PathVariable("activityId") Long activityId) {
        KillCourse killCourse = killCourseService.onlineOne(killId, activityId);
        return JSONResult.success(killCourse);
    }


    /**
     * 查询所有上架的秒杀课程
     *
     * @return
     */
    @GetMapping("online/all")
    public JSONResult onlineAll() {
        List<KillCourse> killCourses = killCourseService.onlineAll();
        return JSONResult.success(killCourses);
    }


    /**
     * 保存和修改公用的
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public JSONResult saveOrUpdate(@RequestBody KillCourse killCourse) {
        if (killCourse.getId() != null) {
            killCourseService.updateById(killCourse);
        } else {
            killCourseService.addKillCourse(killCourse);
        }
        return JSONResult.success();
    }

    /**
     * 删除对象
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public JSONResult delete(@PathVariable("id") Long id) {
        killCourseService.deleteById(id);
        return JSONResult.success();
    }

    /**
     * 获取对象
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JSONResult get(@PathVariable("id") Long id) {
        return JSONResult.success(killCourseService.selectById(id));
    }


    /**
     * 查询所有对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JSONResult list() {
        return JSONResult.success(killCourseService.selectList(null));
    }


    /**
     * 带条件分页查询数据
     */
    @RequestMapping(value = "/pagelist", method = RequestMethod.POST)
    public JSONResult page(@RequestBody KillCourseQuery query) {
        Page<KillCourse> page = new Page<KillCourse>(query.getPage(), query.getRows());
        page = killCourseService.selectPage(page);
        return JSONResult.success(new PageList<KillCourse>(page.getTotal(), page.getRecords()));
    }
}
