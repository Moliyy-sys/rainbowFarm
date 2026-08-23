package org.rainbow.farm_system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_system.entity.User;
import org.rainbow.farm_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 根据id查询用户
     * @param id //用户id
     * @return //用户详情
     */
    @GetMapping("/getUserById")
    public BaseResult<User> findById(Long id) {
        User user = userService.findById(id);
        return BaseResult.success(user);
    }

    /**
     * 分页查询用户列表
     * @param pageNum 当前页码,默认为1
     * @param pageSize 每页大小,默认为10
     * @param userName 用户名,可选
     * @param status 用户状态,可选
     * @return 分页结果
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('/user/list')")
    public BaseResult<IPage<User>> getUserList(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "PageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "status", required = false) String status
            ) {
        IPage<User> userPage = userService.findUsersPage(pageNum, pageSize, userName, status);
        return BaseResult.success(userPage);
    }

    /**
     *
     * @param user //用户信息
     * @return  //操作结果
     */
    @PostMapping("/addUser")
    public BaseResult addUser(@RequestBody User user) {
        boolean result = userService.addUser(user);
        return BaseResult.success();

    }

    /**
     * 修改信息
     * @param user 用户信息
     * @return 操作结果
     */
    @PutMapping("/updateUser")
    public BaseResult updateUser(@RequestBody User user){
        boolean result = userService.updateUser(user);
        return BaseResult.success();
    }

    /**
     * 重置密码
     * @param userId // 用户ID
     * @param newPassword // 新密码
     * @return // 操作结果
     */
    @PutMapping("/resetPassword")
    public BaseResult resetPassword(@RequestParam Long userId, @RequestParam String newPassword){
        boolean result = userService.resetPassword(userId, newPassword);
        return BaseResult.success();
    }

    /**
     *修改用户状态
     * @param userId // 用户ID
     * @param status // 状态
     * @return // 操作结果
     */
    @PutMapping("/changeStatus")
    public BaseResult changeStatus(@RequestParam Long userId, @RequestParam String status){
        boolean result = userService.updateStatus(userId, status);
        return BaseResult.success();
    }

    /**
     * 删除用户
     * @param ids 用户ID字符串,多个ID用逗号分隔
     * @return 操作结果
     */
    @DeleteMapping("/deleteUser")
    public BaseResult deleteUser(String ids){
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                        .collect(Collectors.toList());
        userService.deleteUser(idList);
        return BaseResult.success();
    }

    @PutMapping("/assignRoles")
    public BaseResult assignRoles(@RequestParam("userId") Long userId,
                                  @RequestParam(value = "roleIds", required = false) List<Long> roleIds){
        boolean result = userService.assignRoles(userId, roleIds);
        return BaseResult.success();
    }
}