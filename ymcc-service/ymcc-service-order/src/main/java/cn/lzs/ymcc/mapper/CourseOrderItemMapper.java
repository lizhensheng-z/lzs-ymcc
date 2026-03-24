package cn.lzs.ymcc.mapper;

import cn.lzs.ymcc.domain.CourseOrderItem;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lzs
 * @since 2025-09-19
 */
public interface CourseOrderItemMapper extends BaseMapper<CourseOrderItem> {

    void insertBatch(List<CourseOrderItem> courseOrderItems);

    List<Long> getCourseIdByOrderNo(String orderNo);
}
