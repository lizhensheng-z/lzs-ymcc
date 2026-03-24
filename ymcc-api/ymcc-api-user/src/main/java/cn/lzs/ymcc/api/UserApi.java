package cn.lzs.ymcc.api;

import cn.lzs.ymcc.domain.User;
import cn.lzs.ymcc.result.JSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author 李振生
 */
@FeignClient(name = "service-user")
@RequestMapping("/user")
public interface UserApi {
    /**
     * 查询所有对象
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    JSONResult list();
    /**
     * 根据loginId获取userId
     */
    @RequestMapping(value = "/loginId/{loginId}",method = RequestMethod.GET)
    JSONResult getUserByLoginId(@PathVariable("loginId")Long loginId);

    /**
     * 获取对象
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    JSONResult get(@PathVariable("id")Long id);
}
