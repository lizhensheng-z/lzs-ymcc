package cn.lzs.ymcc.domain;

import com.baomidou.mybatisplus.enums.IdType;
import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 购物车表
 * </p>
 *
 * @author lzs
 * @since 2026-03-30
 */
@TableName("t_course_cart")
public class CourseCart extends Model<CourseCart> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程名称
     */
    @TableField("course_name")
    private String courseName;

    /**
     * 课程图片
     */
    @TableField("course_pic")
    private String coursePic;

    /**
     * 价格冗余
     */
    @TableField("course_price")
    private BigDecimal coursePrice;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 状态：正常，删除
     */
    private Integer status;

    /**
     * 备注
     */
    @TableField("course_note")
    private String courseNote;

    /**
     * 课程ID
     */
    @TableField("course_id")
    private Long courseId;

    /**
     * 用户ID
     */
    @TableField("login_id")
    private Long loginId;

    /**
     * 班级名称
     */
    @TableField("grade_name")
    private String gradeName;

    /**
     * 原价
     */
    @TableField("old_course_price")
    private BigDecimal oldCoursePrice;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCoursePic() {
        return coursePic;
    }

    public void setCoursePic(String coursePic) {
        this.coursePic = coursePic;
    }

    public BigDecimal getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(BigDecimal coursePrice) {
        this.coursePrice = coursePrice;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCourseNote() {
        return courseNote;
    }

    public void setCourseNote(String courseNote) {
        this.courseNote = courseNote;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public BigDecimal getOldCoursePrice() {
        return oldCoursePrice;
    }

    public void setOldCoursePrice(BigDecimal oldCoursePrice) {
        this.oldCoursePrice = oldCoursePrice;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "CourseCart{" +
        "id=" + id +
        ", courseName=" + courseName +
        ", coursePic=" + coursePic +
        ", coursePrice=" + coursePrice +
        ", count=" + count +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", status=" + status +
        ", courseNote=" + courseNote +
        ", courseId=" + courseId +
        ", loginId=" + loginId +
        ", gradeName=" + gradeName +
        ", oldCoursePrice=" + oldCoursePrice +
        "}";
    }
}