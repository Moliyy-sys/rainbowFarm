package org.rainbow.farm_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.rainbow.farm_system.entity.Permission;
import org.rainbow.farm_system.entity.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {
    /**
     * 根据权限ID列表批量删除角色权限关联
     * @param permissionIds 权限ID列表
     */
    void deleteRolePermissionByPermissionIds(@Param("permissionIds") List<Long> permissionIds);
}
