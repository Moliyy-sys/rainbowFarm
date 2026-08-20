package org.rainbow.farm_system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.rainbow.farm_system.entity.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleService {

    /**
     * 分页查询角色
     * @param page //当前页
     * @param size //每页显示条数
     * @param roleName //角色名称
     * @param status //角色状态
     * @return //角色分页数据
     */
    public IPage<Role> findRolePage(int page, int size, String roleName, String status){
        return null;
    }

    /**
     * 根据ID查询角色(包含权限信息)
     * @param id //角色ID
     * @return //角色信息,包含权限列表,如果不存在返回null
     */
    public Role findById(Long id){
        return null;
    }

    /**
     * 新增角色
     * @param role //角色信息
     * @return //true成功,false失败
     */
    public boolean addRole(Role role){
        return false;
    }

    /**
     * 修改角色
     * @param role //角色信息
     * @return //true成功,false失败
     */
    public boolean updateRole(Role role){
        return false;
    }

}
