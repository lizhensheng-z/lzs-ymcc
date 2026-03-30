package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.domain.AccountFlow;
import cn.lzs.ymcc.domain.Login;
import cn.lzs.ymcc.domain.User;
import cn.lzs.ymcc.domain.UserAccount;
import cn.lzs.ymcc.domain.UserAddress;
import cn.lzs.ymcc.domain.UserBaseInfo;
import cn.lzs.ymcc.domain.UserGrowLog;
import cn.lzs.ymcc.mapper.AccountFlowMapper;
import cn.lzs.ymcc.mapper.UserGrowLogMapper;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.service.IUserAccountService;
import cn.lzs.ymcc.service.IUserAddressService;
import cn.lzs.ymcc.service.IUserBaseInfoService;
import cn.lzs.ymcc.service.IUserService;
import cn.lzs.ymcc.util.LoginContext;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

/**
 * 用户中心接口 - 匹配前端调用路径
 */
@RestController
@RequestMapping("/user")
public class UserCenterController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IUserAccountService userAccountService;
    @Autowired
    private IUserAddressService userAddressService;
    @Autowired
    private IUserBaseInfoService userBaseInfoService;
    @Autowired
    private AccountFlowMapper accountFlowMapper;
    @Autowired
    private UserGrowLogMapper userGrowLogMapper;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/user/current")
    public JSONResult getCurrentUser() {
        Login login = LoginContext.getLogin();
        if (login == null) {
            return JSONResult.error("用户未登录");
        }
        User user = userService.getUserByLoginId(login.getId());
        UserBaseInfo userBaseInfo = userBaseInfoService.selectById(user.getId());

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
     * 获取账户余额
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
     * 获取交易记录
     */
    @GetMapping("/account/transactions")
    public JSONResult getTransactions() {
        Login login = LoginContext.getLogin();
        if (login == null) {
            return JSONResult.error("用户未登录");
        }
        User user = userService.getUserByLoginId(login.getId());
        UserAccount account = userAccountService.selectById(user.getId());

        if (account == null) {
            return JSONResult.success(Collections.emptyList());
        }

        EntityWrapper<AccountFlow> wrapper = new EntityWrapper<>();
        wrapper.eq("account_id", account.getId());
        wrapper.orderBy("create_time", false);
        List<AccountFlow> flows = accountFlowMapper.selectList(wrapper);
        return JSONResult.success(flows);
    }

    /**
     * 获取积分余额
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
            result.put("points", userBaseInfo.getGrowScore());
        } else {
            result.put("points", 0);
        }
        return JSONResult.success(result);
    }

    /**
     * 获取充值记录
     */
    @GetMapping("/account/recharge/records")
    public JSONResult getRechargeRecords() {
        Login login = LoginContext.getLogin();
        if (login == null) {
            return JSONResult.error("用户未登录");
        }
        User user = userService.getUserByLoginId(login.getId());
        UserAccount account = userAccountService.selectById(user.getId());

        if (account == null) {
            return JSONResult.success(Collections.emptyList());
        }

        EntityWrapper<AccountFlow> wrapper = new EntityWrapper<>();
        wrapper.eq("account_id", account.getId());
        wrapper.eq("business_type", 1); // 充值类型
        wrapper.orderBy("create_time", false);
        List<AccountFlow> flows = accountFlowMapper.selectList(wrapper);
        return JSONResult.success(flows);
    }

    /**
     * 获取用户地址列表
     */
    @GetMapping("/address/list")
    public JSONResult getAddressList() {
        Login login = LoginContext.getLogin();
        if (login == null) {
            return JSONResult.error("用户未登录");
        }
        User user = userService.getUserByLoginId(login.getId());

        EntityWrapper<UserAddress> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id", user.getId());
        wrapper.orderBy("default_address", false);
        List<UserAddress> addresses = userAddressService.selectList(wrapper);
        return JSONResult.success(addresses);
    }

    /**
     * 获取成长值记录
     */
    @GetMapping("/growth/list")
    public JSONResult getGrowthList() {
        Login login = LoginContext.getLogin();
        if (login == null) {
            return JSONResult.error("用户未登录");
        }
        User user = userService.getUserByLoginId(login.getId());

        EntityWrapper<UserGrowLog> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id", user.getId());
        wrapper.orderBy("create_time", false);
        List<UserGrowLog> logs = userGrowLogMapper.selectList(wrapper);
        return JSONResult.success(logs);
    }

    /**
     * 获取积分记录
     */
    @GetMapping("/points/list")
    public JSONResult getPointsList() {
        Login login = LoginContext.getLogin();
        if (login == null) {
            return JSONResult.error("用户未登录");
        }
        User user = userService.getUserByLoginId(login.getId());

        EntityWrapper<UserGrowLog> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id", user.getId());
        wrapper.orderBy("create_time", false);
        List<UserGrowLog> logs = userGrowLogMapper.selectList(wrapper);
        return JSONResult.success(logs);
    }

    /**
     * 获取消息列表
     */
    @GetMapping("/message/list")
    public JSONResult getMessageList() {
        // TODO: 需要实现消息表和相关服务
        return JSONResult.success(Collections.emptyList());
    }

    /**
     * 标记消息已读
     */
    @PostMapping("/message/read")
    public JSONResult markMessageRead(@RequestBody Map<String, Long> params) {
        // TODO: 需要实现消息表和相关服务
        return JSONResult.success();
    }

    /**
     * 获取退款列表
     */
    @GetMapping("/refund/list")
    public JSONResult getRefundList() {
        // TODO: 需要实现退款表和相关服务
        return JSONResult.success(Collections.emptyList());
    }

    /**
     * 获取评价列表
     */
    @GetMapping("/comment/list")
    public JSONResult getCommentList() {
        // TODO: 需要实现评价表和相关服务
        return JSONResult.success(Collections.emptyList());
    }

    /**
     * 获取投诉列表
     */
    @GetMapping("/complaint/list")
    public JSONResult getComplaintList() {
        // TODO: 需要实现投诉表和相关服务
        return JSONResult.success(Collections.emptyList());
    }
}