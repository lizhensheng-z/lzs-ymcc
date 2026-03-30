package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.domain.CourseCart;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.service.ICourseCartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车 Controller
 *
 * @author lzs
 * @since 2026-03-30
 */
@RestController
@RequestMapping("/cart")
public class CourseCartController {

    @Autowired
    private ICourseCartService courseCartService;

    /**
     * 加入购物车
     * @param cart 购物车对象
     * @return 结果
     */
    @PostMapping("/add")
    public JSONResult addCart(@RequestBody CourseCart cart) {
        // 从请求头或上下文获取用户ID
        if (cart.getLoginId() == null) {
            return JSONResult.error("用户ID不能为空");
        }
        if (cart.getCourseId() == null) {
            return JSONResult.error("课程ID不能为空");
        }

        Boolean result = courseCartService.addCart(cart);
        if (result) {
            return JSONResult.success();
        }
        return JSONResult.error("添加购物车失败");
    }

    /**
     * 获取购物车列表
     * @param loginId 用户ID
     * @return 购物车列表
     */
    @GetMapping("/list/{loginId}")
    public JSONResult list(@PathVariable Long loginId) {
        if (loginId == null) {
            return JSONResult.error("用户ID不能为空");
        }
        List<CourseCart> list = courseCartService.listByLoginId(loginId);
        return JSONResult.success(list);
    }

    /**
     * 删除购物车项
     * @param id 购物车ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public JSONResult delete(@PathVariable Long id) {
        Boolean result = courseCartService.deleteCart(id);
        if (result) {
            return JSONResult.success();
        }
        return JSONResult.error("删除失败");
    }

    /**
     * 清空购物车
     * @param loginId 用户ID
     * @return 结果
     */
    @DeleteMapping("/clear")
    public JSONResult clear(@RequestParam Long loginId) {
        if (loginId == null) {
            return JSONResult.error("用户ID不能为空");
        }
        Boolean result = courseCartService.clearCart(loginId);
        if (result) {
            return JSONResult.success();
        }
        return JSONResult.error("清空失败");
    }
}