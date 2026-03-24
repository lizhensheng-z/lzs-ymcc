package cn.lzs.ymcc.repository;

import cn.lzs.ymcc.ElDoc.EmployeeDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author 李振生
 */
public interface EsCrudRepository extends ElasticsearchRepository<EmployeeDoc,Long> {
}
