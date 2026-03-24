package cn.lzs.ymcc.domain;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 李振生
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_course_teacher")
public class CourseTeacher extends Model<CourseTeacher> {
    private static final long serialVersionUID = 1L;

    private Long id;
    @TableField("course_id")
    private Long courseId;
    @TableField("teacher_id")
    private Long teacherId;
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
