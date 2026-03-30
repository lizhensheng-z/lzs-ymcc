package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.domain.CourseCart;
import cn.lzs.ymcc.mapper.CourseCartMapper;
import cn.lzs.ymcc.service.ICourseCartService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author lzs
 * @since 2026-03-30
 */
@Service
public class CourseCartServiceImpl extends ServiceImpl<CourseCartMapper, CourseCart> implements ICourseCartService {

    @Autowired
    private CourseCartMapper courseCartMapper;

    @Override
    @Transactional
    public Boolean addCart(CourseCart cart) {
        // 检查课程是否已在购物车中
        com.baomidou.mybatisplus.mapper.EntityWrapper<CourseCart> wrapper = new com.baomidou.mybatisplus.mapper.EntityWrapper<>();
        wrapper.eq("course_id", cart.getCourseId())
               .eq("login_id", cart.getLoginId())
               .eq("status", 1);

        List<CourseCart> existList = this.selectList(wrapper);
        if (!existList.isEmpty()) {
            // 课程已在购物车中，不重复添加
            return true;
        }

        cart.setCreateTime(new Date());
        cart.setUpdateTime(new Date());
        cart.setStatus(1); // 正常状态
        cart.setCount(1);
        return this.insert(cart);
    }

    @Override
    public List<CourseCart> listByLoginId(Long loginId) {
        // 查询用户购物车中状态为正常的记录
        com.baomidou.mybatisplus.mapper.EntityWrapper<CourseCart> wrapper = new com.baomidou.mybatisplus.mapper.EntityWrapper<>();
        wrapper.eq("login_id", loginId).eq("status", 1);
        return this.selectList(wrapper);
    }

    @Override
    @Transactional
    public Boolean deleteCart(Long id) {
        CourseCart cart = this.selectById(id);
        if (cart != null) {
            cart.setStatus(0); // 删除状态
            cart.setUpdateTime(new Date());
            return this.updateById(cart);
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean clearCart(Long loginId) {
        List<CourseCart> carts = this.listByLoginId(loginId);
        for (CourseCart cart : carts) {
            cart.setStatus(0);
            cart.setUpdateTime(new Date());
            this.updateById(cart);
        }
        return true;
    }
}