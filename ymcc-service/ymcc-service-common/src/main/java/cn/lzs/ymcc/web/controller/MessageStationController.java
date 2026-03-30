package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.domain.Login;
import cn.lzs.ymcc.domain.MessageStation;
import cn.lzs.ymcc.query.MessageStationQuery;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.result.PageList;
import cn.lzs.ymcc.service.IMessageStationService;
import cn.lzs.ymcc.util.LoginContext;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messageStation")
public class MessageStationController {

    @Autowired
    public IMessageStationService messageStationService;

    /**
    * 保存和修改公用的
    */
    @RequestMapping(value="/save",method= RequestMethod.POST)
    public JSONResult saveOrUpdate(@RequestBody MessageStation messageStation){
        if(messageStation.getId()!=null){
            messageStationService.updateById(messageStation);
        }else{
            messageStationService.insert(messageStation);
        }
        return JSONResult.success();
    }

    /**
    * 删除对象
    */
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    public JSONResult delete(@PathVariable("id") Long id){
        messageStationService.deleteById(id);
        return JSONResult.success();
    }

    /**
   * 获取对象
   */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public JSONResult get(@PathVariable("id")Long id){
        return JSONResult.success(messageStationService.selectById(id));
    }


    /**
    * 查询所有对象
    */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public JSONResult list(){
        return JSONResult.success(messageStationService.selectList(null));
    }


    /**
    * 带条件分页查询数据
    */
    @RequestMapping(value = "/pagelist",method = RequestMethod.POST)
    public JSONResult page(@RequestBody MessageStationQuery query){
        //获取当前登录用户
        Login login = LoginContext.getLogin();
        if (login != null) {
            query.setUserId(login.getId());
        }

        Page<MessageStation> page = new Page<MessageStation>(query.getPage(), query.getRows());
        //如果传了userId，使用自定义查询
        if (query.getUserId() != null) {
            List<MessageStation> list = messageStationService.selectByUserId(query.getUserId());
            //按type过滤
            if (query.getType() != null && !query.getType().isEmpty()) {
                list = list.stream()
                    .filter(m -> query.getType().equals(m.getType()))
                    .collect(java.util.stream.Collectors.toList());
            }
            //分页处理
            int total = list.size();
            int start = (query.getPage() - 1) * query.getRows();
            int end = Math.min(start + query.getRows(), total);
            List<MessageStation> pageList = start < total ? list.subList(start, end) : java.util.Collections.emptyList();
            return JSONResult.success(new PageList<>((long)total, pageList));
        }

        page = messageStationService.selectPage(page);
        return JSONResult.success(new PageList<>(page.getTotal(), page.getRecords()));
    }

    /**
     * 标记消息为已读
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.PUT)
    public JSONResult markAsRead(@PathVariable("id") Long id){
        boolean result = messageStationService.markAsRead(id);
        if (result) {
            return JSONResult.success();
        }
        return JSONResult.error("标记失败");
    }

    /**
     * 标记所有消息为已读
     */
    @RequestMapping(value = "/readAll",method = RequestMethod.PUT)
    public JSONResult markAllAsRead(){
        Login login = LoginContext.getLogin();
        if (login == null) {
            return JSONResult.error("用户未登录");
        }
        int count = messageStationService.markAllAsRead(login.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return JSONResult.success(result);
    }
}
