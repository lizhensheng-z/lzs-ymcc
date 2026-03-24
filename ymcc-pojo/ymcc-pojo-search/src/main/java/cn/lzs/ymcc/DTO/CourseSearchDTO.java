package cn.lzs.ymcc.DTO;

import cn.lzs.ymcc.query.BaseQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * @author 李振生
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseSearchDTO extends BaseQuery {
    /**
     * ?
     */
    private String chargeName;
    /**
     * 课程类型
     */
    private Long courseTypeId;
    /**
     * 课程等级
     */
    private String gradeName;
    /**
     * 价格区间
     */
    private BigDecimal priceMax;
    private BigDecimal priceMin;

}
