package cn.lzs.ymcc.dto;

import cn.lzs.ymcc.domain.User2Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author 李振生
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageDTO {
    private String title;
    private String content;
    /**
     * 用户id和邮箱
     */
    private ArrayList<User2Email> user2Emails;

}
