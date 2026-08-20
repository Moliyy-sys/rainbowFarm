package org.rainbow.farm_system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.CodeEnum;
import org.rainbow.farm_system.entity.Permission;
import org.rainbow.farm_system.entity.Role;
import org.rainbow.farm_system.entity.User;
import org.rainbow.farm_system.mapper.PermissionMapper;
import org.rainbow.farm_system.mapper.RoleMapper;
import org.rainbow.farm_system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 根据id查询用户
     * @param id
     * @return 用户
     */
    public User findById(Long id) {
        //查询
        User user = userMapper.selectById(id);
        if(user != null){
            //查询角色id列表
            List<Long> roleIds = userMapper.selectUserRoleIds(id);
            if(roleIds != null && !roleIds.isEmpty()){
                //查询角色详细信息
                List<Role> roles = roleMapper.selectList(
                        new LambdaQueryWrapper<Role>().in(Role::getRoleId, roleIds)
                );
                //为每个角色查询权限信息
                for (Role role : roles) {
                    List<Long> permissionIds = roleMapper.selectRolePermissionIds(role.getRoleId());
                    if(permissionIds != null && !permissionIds.isEmpty()){
                        List<Permission> permissions = permissionMapper.selectList(
                                new LambdaQueryWrapper<Permission>().in(Permission::getPermissionId, permissionIds)
                        );
                        role.setPermissions(permissions);
                    }
                }
                user.setRoles(roles);
            }
        }
        return user;
        }

    /**
     *
     * @param user //用户信息
     * @return //操作结果
     */
    public boolean addUser(User user){
        //检查用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", user.getUserName());
        User existuser = userMapper.selectOne(queryWrapper);
        if(existuser != null){
            throw new BusException(CodeEnum.SYS_USER_EXIST);
        }
        //设置默认值
        user.setCreateTime(LocalDateTime.now());
        user.setStatus("0)");
        if (StringUtils.hasText(user.getPassword())){
            user.setPassword("123456");
        }
        //保存用户
        return userMapper.insert(user) > 0;
    }

    /**
     *
     * @param user //用户信息
     * @return //是否成功
     */
    public boolean updateUser(User user){
        //检查用户名是否重复(排除自己)
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", user.getUserName()).
                ne("user_id", user.getUserId());
        User existuser = userMapper.selectOne(queryWrapper);
        if(existuser != null){
            throw new BusException(CodeEnum.SYS_USER_EXIST);
        }
        user.setUpdateTime(LocalDateTime.now());
        if(!StringUtils.hasText(user.getPassword())){
            user.setPassword(null);
        }
        return userMapper.updateById(user)>0;
    }

    /**
     * 重置用户密码
     * @param userId //用户ID
     * @param newPassword //新密码
     * @return //是否成功
     */
    public boolean resetPassword(Long userId,String newPassword){
        User user = new User();
        user.setUserId(userId);
        user.setPassword(newPassword);
        user.setPwdUpdateDate(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        return userMapper.updateById(user)>0;
    }

    /**
     * 修改用户状态
     * @param userId //用户ID
     * @param status //用户状态
     * @return //操作结果
     */
    public boolean updateStatus(Long userId,String status){
        User user = new User();
        user.setUserId(userId);
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());

        return userMapper.updateById(user)>0;
    }

    /**
     * 删除用户
     * @param ids //用户id列表
     * @return //操作结果
     */
    public boolean deleteUser(List<Long> ids){
        //删除角色关联
        userMapper.deleteUserRoleByUserId(ids);
        //删除用户
        return userMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 分页查询用户
     * @param page //当前页
     * @param size //每页大小
     * @param userName //用户名
     * @param status //用户状态
     * @return //分页结果
     */
    public IPage<User> findUsersPage(int page, int size, String userName, String status) {
        Page<User> pageObj = new Page<>(page, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if(StringUtils.hasText(userName)){
            queryWrapper.like("user_name", userName)
                    .or()
                    .like("nick_name", userName);
        }
        if(StringUtils.hasText(status)){
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        return userMapper.selectPage(pageObj, queryWrapper);
    }

    /**
     * 给用户分配角色
     * @param userId //用户ID
     * @param roleIds //角色ID列表
     * @return //操作结果
     */
    public boolean assignRoles(Long userId, List<Long> roleIds){
        // 删除用户已有的角色关联
        userMapper.deleteUserRoleByUserId(List.of(userId));
        // 插入新的角色关联
        if(roleIds != null && !roleIds.isEmpty()){
            userMapper.insertUserRoles(userId, roleIds);
        }
        return true;
    }
}
