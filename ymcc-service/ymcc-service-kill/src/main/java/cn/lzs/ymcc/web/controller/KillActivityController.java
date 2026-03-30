package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.service.IKillActivityService;
import cn.lzs.ymcc.domain.KillActivity;
import cn.lzs.ymcc.query.KillActivityQuery;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.result.PageList;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/killActivity")
public class KillActivityController {

    @Autowired
    public IKillActivityService killActivityService;


    /**
     * 秒杀活动发布
     * @param activityId
     * @return
     */
    @PostMapping("/publish/{activityId}")
    public JSONResult publish(@PathVariable Long activityId){
        killActivityService.publish(activityId);
        return JSONResult.success();
    }

    /**
     * 秒杀活动下架
     * @param activityId
     * @return
     */
    @PostMapping("/unpublish/{activityId}")
    public JSONResult unpublish(@PathVariable Long activityId){
        killActivityService.unpublish(activityId);
        return JSONResult.success();
    }

    /**
     * 保存和修改公用的
     */
    @RequestMapping(value="/save",method= RequestMethod.POST)
    public JSONResult saveOrUpdate(@RequestBody KillActivity killActivity){
        if(killActivity.getId()!=null){
            killActivityService.updateById(killActivity);
        }else{
            killActivityService.insert(killActivity);
        }
        return JSONResult.success();
    }

    /**
     * 删除对象
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public JSONResult delete(@PathVariable("id") Long id){
        killActivityService.deleteById(id);
        return JSONResult.success();
    }

    /**
     * 获取对象
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public JSONResult get(@PathVariable("id")Long id){
        return JSONResult.success(killActivityService.selectById(id));
    }


    /**
     * 查询所有对象
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public JSONResult list(){
        return JSONResult.success(killActivityService.selectList(null));
    }


    /**
     * 带条件分页查询数据
     */
    @RequestMapping(value = "/pagelist",method = RequestMethod.POST)
    public JSONResult page(@RequestBody KillActivityQuery query){
        Page<KillActivity> page = new Page<KillActivity>(query.getPage(),query.getRows());
        page = killActivityService.selectPage(page);
        return JSONResult.success(new PageList<KillActivity>(page.getTotal(),page.getRecords()));
    }
}