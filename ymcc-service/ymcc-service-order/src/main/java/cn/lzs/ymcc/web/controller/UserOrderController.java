package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.domain.Login;
import cn.lzs.ymcc.dto.UserOrderQueryDTO;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.service.ICourseOrderService;
import cn.lzs.ymcc.util.LoginContext;
import cn.lzs.ymcc.vo.UserOrderVO;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端订单控制器
 * 提供用户端订单查询功能
 */
@RestController
@RequestMapping("/user/order")
public class UserOrderController {

    @Autowired
    private ICourseOrderService courseOrderService;

    /**
     * 用户端订单列表查询（支持日期范围查询）
     *
     * @param query 查询参数
     * @return 订单分页列表
     */
    @PostMapping("/list")
    public JSONResult queryOrderList(@RequestBody UserOrderQueryDTO query) {
        // 1. 获取当前登录用户
        Login login = LoginContext.getLogin();
        if (login == null) {
            return JSONResult.error("用户未登录");
        }

        // 2. 查询订单列表
        Page<UserOrderVO> page = courseOrderService.queryUserOrderPage(login.getId(), query);

        // 3. 返回结果
        return JSONResult.success(page);
    }

    /**
     * 用户端订单详情查询
     *
     * @param orderNo 订单编号
     * @return 订单详情
     */
    @GetMapping("/detail/{orderNo}")
    public JSONResult getOrderDetail(@PathVariable("orderNo") String orderNo) {
        // 1. 获取当前登录用户
        Login login = LoginContext.getLogin();
        if (login == null) {
            return JSONResult.error("用户未登录");
        }

        // 2. 查询订单详情
        UserOrderVO vo = courseOrderService.getUserOrderDetail(login.getId(), orderNo);
        if (vo == null) {
            return JSONResult.error("订单不存在");
        }

        return JSONResult.success(vo);
    }
}
