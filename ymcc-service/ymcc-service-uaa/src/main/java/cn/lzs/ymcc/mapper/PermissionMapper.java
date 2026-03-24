package cn.lzs.ymcc.mapper;

import cn.lzs.ymcc.domain.Permission;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.ArrayList;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author lzs
 * @since 2025-06-16
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    ArrayList<Permission> getPermissionByLoginId(Long id);
}
