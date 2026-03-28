package cn.lzs.ymcc.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class KillParamDto {
    @NotNull(message = "秒杀Id不能为空")
    private Long killCourseId;
    @NotNull(message = "活动Id不能为空")
    private Long activityId;

}
