package org.rainbow.farm_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.rainbow.farm_system.entity.Permission;
import org.rainbow.farm_system.entity.User;

import java.util.List;


public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据用户id列表批量删除用户角色关联
     * @param userIds //用户id列表
     */
    void deleteUserRoleByUserId(@Param("userIds") List<Long> userIds);

    /**
     * 批量插入用户角色关联
     * @param userId //用户ID
     * @param roleIds //角色ID列表
     */
    void insertUserRoles(@Param("user_id") Long userId, @Param("role_ids") List<Long> roleIds);


    /**
     * 根据用户id查询角色id列表
     * @param userId //用户id
     * @return //角色id列表
     */
    List<Long> selectUserRoleIds(@Param("userId") Long userId);
    /**
     * 根据用户名查询权限列表
     * @param username 用户名
     * @return 权限列表
     */
    List<Permission> selectUserPermissions(String username);
}
