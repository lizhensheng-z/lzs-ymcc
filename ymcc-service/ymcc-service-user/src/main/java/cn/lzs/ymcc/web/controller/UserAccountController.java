package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.domain.Login;
import cn.lzs.ymcc.domain.User;
import cn.lzs.ymcc.domain.UserAccount;
import cn.lzs.ymcc.domain.UserBaseInfo;
import cn.lzs.ymcc.query.UserAccountQuery;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.result.PageList;
import cn.lzs.ymcc.service.IUserAccountService;
import cn.lzs.ymcc.service.IUserBaseInfoService;
import cn.lzs.ymcc.service.IUserService;
import cn.lzs.ymcc.util.LoginContext;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/userAccount")
public class UserAccountController {

    @Autowired
    public IUserAccountService userAccountService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserBaseInfoService userBaseInfoService;

    /**
    * 保存和修改公用的
    */
    @RequestMapping(value="/save",method= RequestMethod.POST)
    public JSONResult saveOrUpdate(@RequestBody UserAccount userAccount){
        if(userAccount.getId()!=null){
            userAccountService.updateById(userAccount);
        }else{
            userAccountService.insert(userAccount);
        }
        return JSONResult.success();
    }

    /**
    * 删除对象
    */
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    public JSONResult delete(@PathVariable("id") Long id){
        userAccountService.deleteById(id);
        return JSONResult.success();
    }

    /**
   * 获取对象
   */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public JSONResult get(@PathVariable("id")Long id){
        return JSONResult.success(userAccountService.selectById(id));
    }


    /**
    * 查询所有对象
    */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public JSONResult list(){
        return JSONResult.success(userAccountService.selectList(null));
    }


    /**
    * 带条件分页查询数据
    */
    @RequestMapping(value = "/pagelist",method = RequestMethod.POST)
    public JSONResult page(@RequestBody UserAccountQuery query){
        Page<UserAccount> page = new Page<UserAccount>(query.getPage(),query.getRows());
        page = userAccountService.selectPage(page);
        return JSONResult.success(new PageList<UserAccount>(page.getTotal(),page.getRecords()));
    }

    /**
     * 获取账户余额 - 处理网关 StripPrefix=2 后的路径
     * 前端: /user/account/balance → 网关: /ymcc/user/account/balance → /account/balance
     */
    @GetMapping("/account/balance")
    public JSONResult getAccountBalance() {
        Login login = LoginContext.getLogin();
        if (login == null) {
            return JSONResult.error("用户未登录");
        }
        User user = userService.getUserByLoginId(login.getId());
        UserAccount account = userAccountService.selectById(user.getId());

        Map<String, Object> result = new HashMap<>();
        if (account != null) {
            result.put("usableAmount", account.getUsableAmount());
            result.put("frozenAmount", account.getFrozenAmount());
        } else {
            result.put("usableAmount", 0);
            result.put("frozenAmount", 0);
        }
        return JSONResult.success(result);
    }

    /**
     * 获取积分 - 处理网关 StripPrefix=2 后的路径
     * 前端: /user/account/points → 网关: /ymcc/user/account/points → /account/points
     */
    @GetMapping("/account/points")
    public JSONResult getPoints() {
        Login login = LoginContext.getLogin();
        if (login == null) {
            return JSONResult.error("用户未登录");
        }
        User user = userService.getUserByLoginId(login.getId());
        UserBaseInfo userBaseInfo = userBaseInfoService.selectById(user.getId());

        Map<String, Object> result = new HashMap<>();
        if (userBaseInfo != null) {
            result.put("points", userBaseInfo.getPoints());
        } else {
            result.put("points", 0);
        }
        return JSONResult.success(result);
    }
}