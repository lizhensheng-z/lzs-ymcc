package cn.lzs.ymcc.ElDoc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 李振生
 */
@Document(indexName = "course",type = "_doc")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseDoc {
    /**
     * 文档id
     */
    @Id
    private Long id;
    /**
     * 课程名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String name;
    @Field(type = FieldType.Text, analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String forUser;
    /**
     * 课程类型
     */
    @Field(type = FieldType.Keyword)
    private String CourseTypeName;
    /**
     * 课程等级
     */
    @Field(type = FieldType.Keyword)
    private String gradeName;
    @Field(type = FieldType.Date)
    private Date startTime;
    @Field(type = FieldType.Date)
    private Date endTime;
    /**
     * 课程封面
     * */
    @Field(type = FieldType.Keyword)
    private String pic;

    /**
     * 上线时间
     */
    @Field(type = FieldType.Date)
    private Date onlineTime;
    /**
     * 授课老师
     */
    @Field(type = FieldType.Text)
    private String teacherNames;



    /**
     * 是否收费
     */
    @Field(type = FieldType.Keyword)
    private String charge;
    /**
     * 价格
     */
    @Field(type = FieldType.Keyword)
    private BigDecimal price;
    /**
     * 原价
     */
    @Field(type = FieldType.Keyword)
    private BigDecimal priceOld;
    /**
     * 销量
     */
    @Field(type = FieldType.Integer)
    private Integer saleCount;
    /**
     * 浏览量
     */
    @Field(type = FieldType.Integer)
    private Integer viewCount;
    /**
     * 评论数
     */
    @Field(type = FieldType.Integer)
    private Integer commentCount;


}
