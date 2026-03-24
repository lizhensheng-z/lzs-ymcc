package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.constant.RedisConstants;
import cn.lzs.ymcc.constant.SystemConstants;
import cn.lzs.ymcc.domain.CourseType;
import cn.lzs.ymcc.mapper.CourseTypeMapper;
import cn.lzs.ymcc.service.ICourseTypeService;
import cn.lzs.ymcc.vo.CourseTypeCrumbsVO;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;

/**
 * <p>
 * 课程目录 服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-06-23
 */
@Service
@Slf4j
public class CourseTypeServiceImpl extends ServiceImpl<CourseTypeMapper, CourseType> implements ICourseTypeService {


    @Autowired
    private CourseTypeMapper courseTypeMapper;
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    public List<CourseTypeCrumbsVO> getCourseTypeCrumbs(Long courseTypeId) {
        // 判断参数
        if(courseTypeId==null){
            throw new GlobalBusinessException("参数异常");

        }

        // 根据id查询
        CourseType courseType = selectById(courseTypeId);

        if(courseType==null){
            throw new  GlobalBusinessException("参数异常");
        }

        // 获取path，
        String[] idStrs = courseType.getPath().split("\\.");

        List<CourseType> courseTypes = selectBatchIds(Arrays.asList(idStrs));

        List<CourseTypeCrumbsVO> courseTypeCrumbsVos = new ArrayList<>();

        courseTypes.forEach(type -> {
            EntityWrapper<CourseType> wrapper = new EntityWrapper<>();

            wrapper.eq("pid",type.getPid());
            wrapper.notIn("id",type.getId());
            List<CourseType> others = selectList(wrapper);

            courseTypeCrumbsVos.add(new CourseTypeCrumbsVO(type,others));
        });

        return courseTypeCrumbsVos;
    }


    @Override
    @CacheEvict(cacheNames = RedisConstants.REDIS_COURSE_TYPE_TREE_DATA,key = "'ALL'")
    public boolean updateById(CourseType entity) {
        return super.updateById(entity);

    }

    @Override
    @CacheEvict(cacheNames = RedisConstants.REDIS_COURSE_TYPE_TREE_DATA,key = "'ALL'")
    public boolean deleteById(Serializable id) {
        return  super.deleteById(id);

    }

    @Override
    @CacheEvict(cacheNames = RedisConstants.REDIS_COURSE_TYPE_TREE_DATA,key = "'ALL'")
    public boolean insert(CourseType entity) {
        return super.insert(entity);

    }

    //    @Override
//    public List<CourseType> treeData(){
//        //先查redis
//        Object o = redisTemplate.opsForValue().get(RedisConstants.REDIS_COURSE_TYPE_TREE_DATA);
//        if(o!=null){
//            return (List<CourseType>) o;
//        }
//        //redis没有查数据库
//        //查数据库
//        List<CourseType> firstCourseTypes = getCourseTypes();
//        //将数据存入redis
//
//        redisTemplate.opsForValue().set(RedisConstants.REDIS_COURSE_TYPE_TREE_DATA,firstCourseTypes);
//
//        return firstCourseTypes;
//    }
    @Override
    @Cacheable(cacheNames = RedisConstants.REDIS_COURSE_TYPE_TREE_DATA,key = "'ALL'")
    public List<CourseType> treeData(){
        log.info("从数据库中查询课程分类树形数据");
        List<CourseType> firstCourseTypes = getCourseTypes();

        return firstCourseTypes;
    }

    private List<CourseType> getCourseTypes() {
        List<CourseType> allCourseTypes = selectList(null);

        HashMap<Long, CourseType> allCourseTypeMap = new HashMap<>();
        for (CourseType tmp : allCourseTypes) {
            allCourseTypeMap.put(tmp.getId(), tmp);
        }

        // 第一层课程分类
        List<CourseType> firstCourseTypes = new ArrayList<>();

        for (CourseType courseTypeTmp : allCourseTypes) {
            if (courseTypeTmp.getPid() == 0 || courseTypeTmp.getPid() == null) {
                firstCourseTypes.add(courseTypeTmp);
            } else {
                CourseType parent = allCourseTypeMap.get(courseTypeTmp.getPid());
                if (parent != null) {
                    // 确保parent.getChildren()返回的是一个已初始化的列表
                    List<CourseType> children = parent.getChildren();
                    if (children == null) {
                        children = new ArrayList<>();
                        parent.setChildren(children);
                    }
                    children.add(courseTypeTmp);
                }
            }
        }
        return firstCourseTypes;
    }


}
