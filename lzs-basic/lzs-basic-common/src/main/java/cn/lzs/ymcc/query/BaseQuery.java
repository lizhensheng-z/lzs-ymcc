package cn.lzs.ymcc.query;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 基础查询对象
 */
@Data
public class BaseQuery {

    //关键字
    private String keyword;

    //有公共属性-分页
    private Integer page = 1; //当前页

    private Integer rows = 10; //每页显示多少条

    //前端常用的分页参数别名
    @JsonProperty("pageNum")
    private Integer pageNum;

    @JsonProperty("pageSize")
    private Integer pageSize;

    //排序的字段
    private String sortField;
    //排序的方式：desc ; asc
    private String sortType;

    /**
     * 获取当前页码，优先使用 pageNum
     */
    public Integer getPage() {
        return pageNum != null ? pageNum : page;
    }

    /**
     * 获取每页条数，优先使用 pageSize
     */
    public Integer getRows() {
        return pageSize != null ? pageSize : rows;
    }
}
