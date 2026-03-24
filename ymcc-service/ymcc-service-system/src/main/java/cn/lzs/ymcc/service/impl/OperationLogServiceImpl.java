package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.domain.OperationLog;
import cn.lzs.ymcc.mapper.OperationLogMapper;
import cn.lzs.ymcc.service.IOperationLogService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志记录 服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-06-13
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {

}
