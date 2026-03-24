package cn.lzs.ymcc.api;

import cn.lzs.ymcc.domain.Login;
import cn.lzs.ymcc.result.JSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author 李振生
 */
@FeignClient(name = "service-uaa")//调用的服务名
@RequestMapping("/login")
public interface LoginApi {
    @RequestMapping(value="/registerByPhone",method= RequestMethod.POST)
    public JSONResult registerByPhone(@RequestBody Login login);
}
