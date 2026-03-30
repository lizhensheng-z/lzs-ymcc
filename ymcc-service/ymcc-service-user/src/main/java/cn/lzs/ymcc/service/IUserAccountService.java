package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.UserAccount;
import com.baomidou.mybatisplus.service.IService;

import java.math.BigDecimal;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lzs
 * @since 2025-06-16
 */
public interface IUserAccountService extends IService<UserAccount> {

    /**
     * 充值
     * @param userId 用户ID
     * @param amount 充值金额
     * @return 是否成功
     */
    boolean recharge(Long userId, BigDecimal amount);
}
