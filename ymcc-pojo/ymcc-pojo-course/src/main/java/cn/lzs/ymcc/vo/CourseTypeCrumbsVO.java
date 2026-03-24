package cn.lzs.ymcc.vo;

import cn.lzs.ymcc.domain.CourseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 李振生
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseTypeCrumbsVO {
    //当前分类
    private CourseType ownerProductType;
    //当前分类的同级分类列表
    private List<CourseType> otherProductTypes;
}
