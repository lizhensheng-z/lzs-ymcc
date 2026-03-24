package cn.lzs.ymcc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 李振生
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User2Phone {
    private Long userId;
    private String phone;
}
