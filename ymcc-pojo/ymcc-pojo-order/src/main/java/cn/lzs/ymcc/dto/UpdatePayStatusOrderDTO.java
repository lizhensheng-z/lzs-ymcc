package cn.lzs.ymcc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @author 李振生
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePayStatusOrderDTO {
    private String orderNo;
    private Integer statusOrder;
    private Date updateTime;
}
