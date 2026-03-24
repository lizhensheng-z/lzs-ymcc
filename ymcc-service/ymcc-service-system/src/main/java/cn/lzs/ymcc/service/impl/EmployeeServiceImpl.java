package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.domain.Employee;
import cn.lzs.ymcc.mapper.EmployeeMapper;
import cn.lzs.ymcc.service.IEmployeeService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-06-13
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {

}
