package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.Permission;
import com.baomidou.mybatisplus.service.IService;

import java.util.ArrayList;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author lzs
 * @since 2025-06-16
 */
public interface IPermissionService extends IService<Permission> {
    /**
     * 根据登录id查询该用户的所有权限
     * @param id
     * @return
     */
    ArrayList<Permission> getPermissionByLoginId(Long id);
}
