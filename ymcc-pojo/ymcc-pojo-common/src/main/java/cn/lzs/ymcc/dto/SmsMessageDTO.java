package cn.lzs.ymcc.dto;

import cn.lzs.ymcc.domain.User2Phone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

/**
 * @author 李振生
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsMessageDTO {
    private String title;
    private String content;
    /**
     * 手机号和用户id
     */
    private List<User2Phone> user2PhoneList;
    private String ip;
}
