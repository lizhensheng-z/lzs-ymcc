package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.CourseType;
import cn.lzs.ymcc.vo.CourseTypeCrumbsVO;
import com.baomidou.mybatisplus.service.IService;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 课程目录 服务类
 * </p>
 *
 * @author lzs
 * @since 2025-06-23
 */
public interface ICourseTypeService extends IService<CourseType> {


    List<CourseType> treeData();

    @Override
    boolean insert(CourseType courseType);

    @Override
    boolean deleteById(Serializable serializable);

    @Override
    boolean updateById(CourseType courseType);

    List<CourseTypeCrumbsVO> getCourseTypeCrumbs(Long courseTypeId);
}
