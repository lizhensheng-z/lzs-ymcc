package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.domain.Config;
import cn.lzs.ymcc.mapper.ConfigMapper;
import cn.lzs.ymcc.service.IConfigService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 参数配置表 服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-06-13
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements IConfigService {

}
