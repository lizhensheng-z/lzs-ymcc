package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.domain.UserAccount;
import cn.lzs.ymcc.mapper.UserAccountMapper;
import cn.lzs.ymcc.service.IUserAccountService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-06-16
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements IUserAccountService {

    @Override
    public boolean recharge(Long userId, BigDecimal amount) {
        if (userId == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        UserAccount account = selectById(userId);
        if (account == null) {
            // 如果账户不存在，创建新账户
            account = new UserAccount();
            account.setId(userId);
            account.setUsableAmount(amount);
            account.setFrozenAmount(BigDecimal.ZERO);
            account.setCreateTime(System.currentTimeMillis());
            account.setUpdateTime(System.currentTimeMillis());
            return insert(account);
        } else {
            // 累加余额
            account.setUsableAmount(account.getUsableAmount().add(amount));
            account.setUpdateTime(System.currentTimeMillis());
            return updateById(account);
        }
    }
}
