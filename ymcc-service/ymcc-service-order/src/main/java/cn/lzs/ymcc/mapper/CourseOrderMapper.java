package cn.lzs.ymcc.mapper;

import cn.lzs.ymcc.domain.CourseOrder;
import cn.lzs.ymcc.dto.UpdatePayStatusOrderDTO;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lzs
 * @since 2025-09-19
 */
public interface CourseOrderMapper extends BaseMapper<CourseOrder> {

    Boolean updatePayStatusByOrderNo(UpdatePayStatusOrderDTO updatePayStatusOrderDTO);
}
