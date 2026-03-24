package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.domain.UserAddress;
import cn.lzs.ymcc.mapper.UserAddressMapper;
import cn.lzs.ymcc.service.IUserAddressService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 收货地址 服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-06-16
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements IUserAddressService {

}
