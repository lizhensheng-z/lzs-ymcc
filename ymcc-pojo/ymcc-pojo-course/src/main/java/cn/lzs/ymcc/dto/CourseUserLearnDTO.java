package cn.lzs.ymcc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 李振生
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseUserLearnDTO {
    //已购买 0
    public static final Integer  BUYED = 0;
    private List<Long> courseIds = new ArrayList<>();
    private String courseOrderNo;
    private Long loginId;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private Date createTime;
}
