package cn.lzs.ymcc.service.Impl;

import cn.lzs.ymcc.DTO.CourseSearchDTO;
import cn.lzs.ymcc.ElDoc.CourseDoc;
import cn.lzs.ymcc.repository.EsCourseCrudRepository;
import cn.lzs.ymcc.repository.EsCrudRepository;
import cn.lzs.ymcc.result.PageList;
import cn.lzs.ymcc.service.ESCourseService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

/**
 * @author 李振生
 */
@Service
public class ESCourseServiceImpl implements ESCourseService {

    //不支持高亮查询
    @Autowired
    private EsCourseCrudRepository courseCrudRepository;
    //支持高亮查询
    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Override
    public void saveCourseDoc(CourseDoc courseDoc) {
        courseCrudRepository.save(courseDoc);
    }

    @Override
    public PageList<CourseDoc> searchCourse(CourseSearchDTO param){


        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();


        if(StringUtils.isNotEmpty(param.getSortField())){
            String fieldName="price";
            switch(param.getSortField().toLowerCase()){
                case "xp" : fieldName = "onlineTime"; break;
                case "pl" : fieldName = "commentCount"; break;
                case "rq" : fieldName = "viewCount"; break;
                case "xl" : fieldName = "saleCount"; break;
            }
            SortOrder sortOrder = SortOrder.DESC;

            if(StringUtils.isNotEmpty(param.getSortType())&&
                    param.getSortType().equalsIgnoreCase("asc")){
                sortOrder = SortOrder.ASC;
            }
            // 指定查询条件
            builder.withSort(new FieldSortBuilder(fieldName).order(sortOrder));
        }






        // 分页
        builder.withPageable(PageRequest.of(param.getPage()-1,param.getRows()));

        // 条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();


        // 关键字
        if(StringUtils.isNotEmpty(param.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("name",param.getKeyword()));
        }



        // 分类，等级，价格等。。
        if(param.getCourseTypeId()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("courseTypeId",
                    param.getCourseTypeId()));
        }

        if(StringUtils.isNotEmpty(param.getGradeName())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("gradeName",
                    param.getGradeName()));
        }

        if(null != param.getPriceMin()){
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").
                    gte(param.getPriceMin()));
        }

        if(null != param.getPriceMax()){
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").
                    gte(param.getPriceMax()));
        }



        builder.withQuery(boolQueryBuilder);



        //Page<CourseDoc> page = courseCrudRepository.search(builder.build());
        //支持高亮查询
        Page<CourseDoc> page = elasticsearchTemplate.queryForPage(builder.build(), CourseDoc.class);

        // 总条数，  每页数据
        return new PageList(page.getTotalElements(),page.getContent());

    }
}
