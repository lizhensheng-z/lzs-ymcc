package cn.lzs.ymcc.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author lzs
 * @since 2025-06-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseQuery extends BaseQuery {

    // 课程分类ID
    private Long courseTypeId;

    // 课程等级 (1-初级, 2-中级, 3-高级)
    private Integer level;

    // 价格类型 (0-免费, 1-付费)
    private Integer priceType;

    // 前端分页参数兼容
    public void setPageNum(Integer pageNum) {
        if (pageNum != null) {
            this.setPage(pageNum);
        }
    }

    public void setPageSize(Integer pageSize) {
        if (pageSize != null) {
            this.setRows(pageSize);
        }
    }
}