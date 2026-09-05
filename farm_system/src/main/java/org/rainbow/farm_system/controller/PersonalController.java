package org.rainbow.farm_system.controller;

import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_common.util.SecurityUtil;
import org.rainbow.farm_system.entity.User;
import org.rainbow.farm_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 个人中心控制器
 * 提供用户个人信息管理相关接口
 */
@RestController
@RequestMapping("/personal")
public class PersonalController {
    @Autowired
    private UserService userService;

    /**
     * 查询当前用户的个人信息
     * @return 用户的个人信息
     */
    @GetMapping("/info")
    public BaseResult<User> getUserInfo(){
        // 获取当前登录用户名
        String userName = SecurityUtil.getUserName();
        // 根据用户名查询用户信息
        User user = userService.findByUserName(userName);
        // 返回用户信息
        return BaseResult.success(user);
    }

    /**
     * 修改当前用户的个人信息
     * @param user 修改后的用户信息
     * @return 修改结果
     */
    @PutMapping("/update")
    public BaseResult updateUserInfo(@RequestBody User user){
        // 获取当前登录用户名
        user.setUserName(SecurityUtil.getUserName());
        // 修改用户信息
        userService.updateCurrentUserInfo(user);
        return BaseResult.success();
    }

    /**
     *  修改当前用户的密码
     * @param currentPassword 当前密码
     * @param newPassword 新密码
     * @param confirmPassword 确认密码
     * @return 修改结果
     */
    @PutMapping("/password")
    public BaseResult updateUserPassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword){
        // 获取当前登录用户名
        String userName = SecurityUtil.getUserName();
        // 修改用户密码
        userService.updateCurrentUserPassword(currentPassword, newPassword, confirmPassword, userName);
        return BaseResult.success();
    }
}
