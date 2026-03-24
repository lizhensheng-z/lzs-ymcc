package cn.lzs.ymcc.dto;

import cn.lzs.ymcc.domain.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 李振生
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseInfoDTO{
    private List<CourseDTO> courses = new ArrayList<>();
    private BigDecimal totalAmount = new BigDecimal(0);

}
