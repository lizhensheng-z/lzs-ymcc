package cn.lzs.ymcc.ElDoc;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author 李振生
 */
@Document(indexName = "employee",type = "_doc")
@Data
public class EmployeeDoc {
    @Id
    private Long id;
    //FieldType.Text 分词      Keyword 不分词
    @Field(type = FieldType.Text, analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String name;
    @Field(type = FieldType.Keyword)
    private String sex;
}
