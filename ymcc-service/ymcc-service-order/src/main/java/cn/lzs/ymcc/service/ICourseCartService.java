package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.CourseCart;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 购物车 服务类
 * </p>
 *
 * @author lzs
 * @since 2026-03-30
 */
public interface ICourseCartService extends IService<CourseCart> {

    /**
     * 添加购物车
     * @param cart 购物车对象
     * @return 是否成功
     */
    Boolean addCart(CourseCart cart);

    /**
     * 获取用户购物车列表
     * @param loginId 用户ID
     * @return 购物车列表
     */
    List<CourseCart> listByLoginId(Long loginId);

    /**
     * 删除购物车项
     * @param id 购物车ID
     * @return 是否成功
     */
    Boolean deleteCart(Long id);

    /**
     * 清空用户购物车
     * @param loginId 用户ID
     * @return 是否成功
     */
    Boolean clearCart(Long loginId);
}