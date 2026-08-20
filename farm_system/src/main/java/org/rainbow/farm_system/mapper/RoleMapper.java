package org.rainbow.farm_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.rainbow.farm_system.entity.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {
    /**
     * 根据角色ID查询权限ID列表
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> selectRolePermissionIds(@Param("roleId") Long roleId);

    /**
     * 根据角色ID删除角色与权限关联
     * @param roleIds 角色ID列表
     */
    void deleteRolePermissionByRoleIds(@Param("roleIds") List<Long> roleIds);


    /**
     * 批量插入角色与权限关联
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    void insertRolePermissions(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);

    /**
     * 根据角色ID删除角色与用户关联
     * @param roleIds 角色ID列表
     */
    void deleteUserRolesByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 根据角色ID查询用户ID列表
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    List<Long> selectRoleUserIds(@Param("roleId") Long roleId);

    /**
     * 根据角色ID和用户ID列表删除用户角色关联
     * @param roleId 角色ID
     * @param userIds 用户ID列表
     */
    void deleteUserRolesByRoleIdAndUserIds(@Param("roleId") Long roleId,@Param("userIds")List<Long> userIds );


    /**
     * 检查用户角色关联是否存在
     * @param roleId 角色ID
     * @param userId 用户ID
     * @return 关联数量
     */
    int countUserRoleExists(@Param("roleId") Long roleId,@Param("userId") Long userId);

    /**
     * 插入用户角色关联
     * @param roleId 角色ID
     * @param userId 用户ID
     */
    void insertUserRole(@Param("roleId") Long roleId,@Param("userId") Long userId);


}
