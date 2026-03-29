package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.domain.Login;
import cn.lzs.ymcc.dto.PhoneRegisterDTO;
import cn.lzs.ymcc.service.IUserBaseInfoService;
import cn.lzs.ymcc.service.IUserService;
import cn.lzs.ymcc.domain.User;
import cn.lzs.ymcc.query.UserQuery;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.result.PageList;
import cn.lzs.ymcc.util.LoginContext;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    public IUserService userService;
    @Autowired
    public IUserBaseInfoService userBaseInfoService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public JSONResult getCurrentUser() {
        Login login = LoginContext.getLogin();
        if (login == null) {
            return JSONResult.error("用户未登录");
        }
        // 获取用户详细信息
        User user = userService.getUserByLoginId(login.getId());
        // 获取用户基本信息（成长值、等级等）
        cn.lzs.ymcc.domain.UserBaseInfo userBaseInfo = userBaseInfoService.selectById(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("nickName", user.getNickName());
        result.put("phone", user.getPhone());
        result.put("email", user.getEmail());
        result.put("avatar", login.getAvatar());
        result.put("username", login.getUsername());
        if (userBaseInfo != null) {
            result.put("level", userBaseInfo.getLevel());
            result.put("growScore", userBaseInfo.getGrowScore());
        }
        return JSONResult.success(result);
    }
    /**
     * 用户手机注册功能
     */
    @RequestMapping(value="/register",method= RequestMethod.POST)
    public JSONResult registerByPhone(@RequestBody @Valid PhoneRegisterDTO phoneRegisterDTO){
        userService.registerByPhone(phoneRegisterDTO);
        return JSONResult.success();
    }
    /**
    * 保存和修改公用的
    */
    @RequestMapping(value="/save",method= RequestMethod.POST)
    public JSONResult saveOrUpdate(@RequestBody User user){
        if(user.getId()!=null){
            userService.updateById(user);
        }else{
            userService.insert(user);
        }
        return JSONResult.success();
    }

    /**
    * 删除对象
    */
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    public JSONResult delete(@PathVariable("id") Long id){
        userService.deleteById(id);
        return JSONResult.success();
    }

    /**
     * 根据loginId获取userId
     */
    @RequestMapping(value = "/loginId/{loginId}",method = RequestMethod.GET)
    public JSONResult getUserByLoginId(@PathVariable("loginId")Long loginId){
      User user =   userService.getUserByLoginId(loginId);
        return JSONResult.success(user);
    }
    /**
   * 获取对象
   */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public JSONResult get(@PathVariable("id")Long id){
        return JSONResult.success(userService.selectById(id));
    }


    /**
    * 查询所有对象
    */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public JSONResult list(){
        return JSONResult.success(userService.selectList(null));
    }


    /**
    * 带条件分页查询数据
    */
    @RequestMapping(value = "/pagelist",method = RequestMethod.POST)
    public JSONResult page(@RequestBody UserQuery query){
        Page<User> page = new Page<User>(query.getPage(),query.getRows());
        page = userService.selectPage(page);
        return JSONResult.success(new PageList<User>(page.getTotal(),page.getRecords()));
    }
}
