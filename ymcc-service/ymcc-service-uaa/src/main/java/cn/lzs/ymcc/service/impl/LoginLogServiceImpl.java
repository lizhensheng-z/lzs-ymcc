package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.domain.LoginLog;
import cn.lzs.ymcc.mapper.LoginLogMapper;
import cn.lzs.ymcc.service.ILoginLogService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 登录记录 服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-06-16
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements ILoginLogService {

}
