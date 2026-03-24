package cn.lzs.ymcc.repository;

import cn.lzs.ymcc.ElDoc.CourseDoc;
import cn.lzs.ymcc.ElDoc.EmployeeDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author 李振生
 */
public interface EsCourseCrudRepository extends ElasticsearchRepository<CourseDoc,Long>  {
}
