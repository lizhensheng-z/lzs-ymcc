package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.domain.Login;
import cn.lzs.ymcc.domain.User;
import cn.lzs.ymcc.domain.UserAccount;
import cn.lzs.ymcc.domain.UserBaseInfo;
import cn.lzs.ymcc.service.IUserAccountService;
import cn.lzs.ymcc.service.IUserBaseInfoService;
import cn.lzs.ymcc.service.IUserService;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.util.LoginContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 账户相关接口 - 处理网关 StripPrefix=2 后的路径
 * 前端: /user/account/balance → 网关: /ymcc/user/account/balance → /account/balance
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IUserAccountService userAccountService;
    @Autowired
    private IUserBaseInfoService userBaseInfoService;

    /**
     * 获取账户余额
     */
    @GetMapping("/balance")
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
     * 获取积分
     */
    @GetMapping("/points")
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