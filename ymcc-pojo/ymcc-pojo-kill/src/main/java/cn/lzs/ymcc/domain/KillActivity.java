package cn.lzs.ymcc.domain;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

@TableName("t_kill_activity")
public class KillActivity extends Model<KillActivity> {
      //  0待发布，1已发布 ，2已取消
    public static final Integer ACTIVITY_WAIT_PUBLISH = 0;
    public static final Integer ACTIVITY_PUBLISH = 1;
    public static final Integer ACTIVITY_CANCE_PUBLISH = 2;

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("time_str")
    private String timeStr;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("begin_time")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("end_time")
    private Date endTime;
    /**
     * 0待发布，1已发布 ，2已取消
     */
    @TableField("publish_status")
    private Integer publishStatus = 0;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("publish_time")
    private Date publishTime;
    @TableField("create_time")
    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
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

    public Integer getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(Integer publishStatus) {
        this.publishStatus = publishStatus;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "KillActivity{" +
        ", id=" + id +
        ", name=" + name +
        ", timeStr=" + timeStr +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", publishStatus=" + publishStatus +
        ", publishTime=" + publishTime +
        "}";
    }
}
