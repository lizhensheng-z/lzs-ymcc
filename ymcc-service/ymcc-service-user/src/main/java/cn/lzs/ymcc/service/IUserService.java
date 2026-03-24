package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.User;
import cn.lzs.ymcc.dto.PhoneRegisterDTO;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 会员登录账号 服务类
 * </p>
 *
 * @author lzs
 * @since 2025-06-16
 */
public interface IUserService extends IService<User> {

    void registerByPhone(PhoneRegisterDTO phoneRegisterDTO);

    User getUserByLoginId(Long loginId);
}
