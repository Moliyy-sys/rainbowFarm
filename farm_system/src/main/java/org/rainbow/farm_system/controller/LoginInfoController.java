package org.rainbow.farm_system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_system.entity.LoginInfo;
import org.rainbow.farm_system.service.LoginInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录日志控制器
 */
@RestController
@RequestMapping("/loginInfo")
public class LoginInfoController {
    @Autowired
    private LoginInfoService loginInfoService;

    /**
     * 根据id查询登录日志
     * @param id 登录日志id
     * @return 登录日志
     */
    @GetMapping("/getLoginInfoById")
    public BaseResult<LoginInfo> findById(Long id){
        LoginInfo loginInfo = loginInfoService.getLoginInfo(id);
        return BaseResult.success(loginInfo);
    }

    /**
     * 分页查询登录日志
     * @param pageNum 当前页
     * @param pageSize 每页显示数量
     * @param userName 用户名
     * @param status 登录状态
     * @param ipaddr 登录IP地址
     * @return 登录日志列表
     */
    @GetMapping("/list")
    public BaseResult<IPage<LoginInfo>> getLoginInfoList(
            @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
            @RequestParam(value = "userName",required = false)String userName,
            @RequestParam(value = "status",required = false)String status,
            @RequestParam(value = "ipaddr",required = false)String ipaddr){
        IPage<LoginInfo> result = loginInfoService.getLoginInfoList(pageNum, pageSize, userName, status, ipaddr);
        return BaseResult.success(result);
    }
}
