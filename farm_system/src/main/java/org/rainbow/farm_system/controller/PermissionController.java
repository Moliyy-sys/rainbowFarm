package org.rainbow.farm_system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_system.entity.Permission;
import org.rainbow.farm_system.mapper.PermissionMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    /**
     * 分页查询权限
     * @param pageNum 当前页码，默认1
     * @param pageSize 每页条数，默认10
     * @param permissionName 权限名称，可选
     * @return 权限分页列表
     */
    @GetMapping("/list")
    public BaseResult<IPage<Permission>> gertPermissionList(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "permissionName", required = false) String permissionName) {
        return null;//BaseResult.success()
    }

    /**
     * 根据ID查询权限
     * @param id 权限ID
     * @return 权限信息
     */
    @GetMapping("/getPermissionById")
    public BaseResult<Permission> findById(@RequestParam("id") Long id) {
        return null;
    }

    /**
     * 获取所有权限
     * @return 权限列表
     */
    @GetMapping("/all")
    public BaseResult<List<Permission>> getAllPermissions() {
        return null;
    }

    /**
     * 获取权限下拉列表，这里的权限只包含权限ID和权限名
     * @return 权限下拉列表
     */
    @GetMapping("/selectList")
    public BaseResult<List<Permission>> selectList() {
        return null;
    }

    /**
     * 新增权限
     * @param permission 权限信息
     * @return 操作结果
     */
    @PostMapping("/addPermission")
    public BaseResult addPermission(@RequestBody Permission permission) {
        return null;
    }

    /**
     * 修改权限
     * @param permission 权限信息
     * @return 操作结果
     */
    @PutMapping("/updatePermission")
    public BaseResult updatePermission(@RequestBody Permission permission) {
        return null;
    }

    /**
     * 删除权限
     * @param ids 权限ID字符串，多个ID以逗号分隔
     * @return 操作结果
     */
    @DeleteMapping("/deletePermission")
    public BaseResult deletePermission(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays
                .stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        return null;
    }
}
