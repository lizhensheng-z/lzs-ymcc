package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.domain.Permission;
import cn.lzs.ymcc.mapper.PermissionMapper;
import cn.lzs.ymcc.service.IPermissionService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-06-16
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {
    @Autowired
    private PermissionMapper permissionMapper;
    @Override
    public ArrayList<Permission> getPermissionByLoginId(Long id) {
        return permissionMapper.getPermissionByLoginId(id);
    }
}
