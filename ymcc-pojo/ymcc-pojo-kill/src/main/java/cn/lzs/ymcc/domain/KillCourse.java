package cn.lzs.ymcc.domain;

import com.baomidou.mybatisplus.enums.IdType;
import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author lzs
 * @since 2026-03-28
 */
@TableName("t_kill_course")
public class KillCourse extends Model<KillCourse> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 课程名字
     */
    @TableField("course_name")
    private String courseName;
    /**
     * 对应的课程ID
     */
    @TableField("course_id")
    private Long courseId;
    @TableField("kill_price")
    private BigDecimal killPrice;
    /**
     * 库存
     */
    @TableField("kill_count")
    private Integer killCount;
    /**
     * 每个人可以秒杀的数量,默认1
     */
    @TableField("kill_limit")
    private Integer killLimit;
    /**
     * 秒杀课程排序
     */
    @TableField("kill_sort")
    private Integer killSort;
    /**
     * 秒杀状态:0待发布，1秒杀中，2秒杀结束
     */
    @TableField("publish_status")
    private Integer publishStatus;
    @TableField("course_pic")
    private String coursePic;
    /**
     * 秒杀开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("start_time")
    private Date startTime;
    /**
     * 秒杀结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("end_time")
    private Date endTime;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;
    /**
     * 发布到Redis的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("publish_time")
    private Date publishTime;
    /**
     * 老师，用逗号隔开
     */
    @TableField("teacher_names")
    private String teacherNames;
    /**
     * 下线时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("offline_time")
    private Date offlineTime;
    @TableField("activity_id")
    private Long activityId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("time_str")
    private String timeStr;


    /**
     * 返回时间差
     *  如果是未开始: startTime - now : 秒杀开始时间减去现在时间
     *  如果是秒杀中: endTime - now  : 秒杀结束时间减去现在时间
     * @return
     */
    @com.alibaba.fastjson.annotation.JSONField(serialize = false, deserialize = false)
    public Long getTimeDiffMill() {
        Date now = new Date();
        Date startTime = getStartTime();
        Date endTime = getEndTime();
        if (startTime == null || endTime == null) {
            return null;
        }
        if (isKilling()) { // 如果在秒杀中
            return endTime.getTime() - now.getTime();  // 如果在秒杀中,秒杀结束时间减去当前时间,获取到秒杀结束倒计时
        }
        if (now.before(startTime)){  // 如果是在秒杀之前,
            return startTime.getTime() - now.getTime();  // 如果是在秒杀开始之前, 秒杀开始时间减去现在时间,获取到秒杀开始倒计时

        }
        return null;
    }

    /**
     * 处理秒杀按钮 - 判断是否未开始
     *
     * @return true表示未开始，false表示已开始
     */
    @com.alibaba.fastjson.annotation.JSONField(serialize = false, deserialize = false)
    public Boolean isUnbegin() {
        Date now = new Date();

        Date startTime = getStartTime();
        if (startTime == null) {
            return false;
        }
        // 修复：当前时间在开始时间之前，才是"未开始"
        if (now.before(startTime)) {
            return true;
        }
        return false;
    }


    /**
     * 秒杀中,返回true
     * @return
     */
    @com.alibaba.fastjson.annotation.JSONField(serialize = false, deserialize = false)
    public Boolean isKilling(){
        Date now = new Date();
        Date startTime = getStartTime();
        Date endTime = getEndTime();
        if (startTime == null || endTime == null) {
            return false;
        }
        if (now.after(startTime) && now.before(endTime)){ // 如果当前时间在秒杀开始之后,且在秒杀结束之前,说明正在秒杀中
            return true;
        }
        return false;
    }

    /**
     *  返回秒杀课程状态名称
     */
    @com.alibaba.fastjson.annotation.JSONField(serialize = false, deserialize = false)
    public String getKillStatusName() {
        Date now = new Date();
        Date startTime = getStartTime();
        Date endTime = getEndTime();
        if (startTime == null || endTime == null) {
            return "未开始";
        }
        if (now.before(startTime)){ // 当前时间在秒杀开始之前
            return "未开始";
        } else if (now.after(endTime)){ // 当前时间在秒杀结束之后
            return "秒杀已结束";
        }else { // 不在秒杀开始之前,也不在秒杀结束后
            return "秒杀中";
        }
    };



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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public BigDecimal getKillPrice() {
        return killPrice;
    }

    public void setKillPrice(BigDecimal killPrice) {
        this.killPrice = killPrice;
    }

    public Integer getKillCount() {
        return killCount;
    }

    public void setKillCount(Integer killCount) {
        this.killCount = killCount;
    }

    public Integer getKillLimit() {
        return killLimit;
    }

    public void setKillLimit(Integer killLimit) {
        this.killLimit = killLimit;
    }

    public Integer getKillSort() {
        return killSort;
    }

    public void setKillSort(Integer killSort) {
        this.killSort = killSort;
    }

    public Integer getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(Integer publishStatus) {
        this.publishStatus = publishStatus;
    }

    public String getCoursePic() {
        return coursePic;
    }

    public void setCoursePic(String coursePic) {
        this.coursePic = coursePic;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getTeacherNames() {
        return teacherNames;
    }

    public void setTeacherNames(String teacherNames) {
        this.teacherNames = teacherNames;
    }

    public Date getOfflineTime() {
        return offlineTime;
    }

    public void setOfflineTime(Date offlineTime) {
        this.offlineTime = offlineTime;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "KillCourse{" +
                ", id=" + id +
                ", courseName=" + courseName +
                ", courseId=" + courseId +
                ", killPrice=" + killPrice +
                ", killCount=" + killCount +
                ", killLimit=" + killLimit +
                ", killSort=" + killSort +
                ", publishStatus=" + publishStatus +
                ", coursePic=" + coursePic +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createTime=" + createTime +
                ", publishTime=" + publishTime +
                ", teacherNames=" + teacherNames +
                ", offlineTime=" + offlineTime +
                ", activityId=" + activityId +
                ", timeStr=" + timeStr +
                "}";
    }
}
