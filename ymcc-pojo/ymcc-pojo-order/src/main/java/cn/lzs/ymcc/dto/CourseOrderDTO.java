package cn.lzs.ymcc.dto;

import cn.lzs.ymcc.domain.CourseOrder;
import cn.lzs.ymcc.domain.CourseOrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李振生
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseOrderDTO {
    private CourseOrder courseOrder;
    private List<CourseOrderItem> courseOrderItemList = new ArrayList<>();
}
