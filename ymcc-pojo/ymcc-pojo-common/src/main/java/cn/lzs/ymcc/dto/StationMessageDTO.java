package cn.lzs.ymcc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * @author 李振生
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StationMessageDTO {
    private String title;
    private String content;
    private String type;
    private ArrayList<Long> userIds;
}
