package org.rainbow.farm_system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.rainbow.farm_system.entity.LoginInfo;
import org.rainbow.farm_system.mapper.LoginInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户日志服务
 */
@Service
@Transactional
public class LoginInfoService {
    @Autowired
    private LoginInfoMapper loginInfoMapper;

    /**
     * 记录用户登录信息
     * @param username 用户名
     * @param status 登录状态,0登录成功,1登录失败
     * @param msg 登录信息,如“登录成功”，“用户名不存在”等
     * @param request HTTP请求对象，用户获取客户端信息
     */
    public void recordLoginInfo(String username, String status, String msg, HttpServletRequest request) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUserName(username);
        loginInfo.setStatus(status);
        loginInfo.setMsg(msg);
        loginInfo.setLoginTime(LocalDateTime.now());
        // 设置客户端信息
        loginInfo.setIpaddr(getClientIp(request));
        loginInfo.setBrowser(getBrowser(request));
        loginInfo.setOs(getOs(request));

        loginInfoMapper.insert(loginInfo);
    }

    /**
     * 获取登录信息
     * @param infoId 登录信息ID
     * @return 登录信息对象
     */
    public LoginInfo getLoginInfo(Long infoId) {
        return loginInfoMapper.selectById(infoId);
    }

    /**
     * 获取登录信息列表
     * @param page 页码
     * @param size 每页记录数
     * @param userName 用户名
     * @param status 登录状态
     * @param ipaddr IP地址
     * @return 分页结果
     */
    public IPage<LoginInfo> getLoginInfoList(int page, int size,String userName,String status,String ipaddr) {
        Page<LoginInfo> pageObj = new Page<>(page, size);
        QueryWrapper<LoginInfo> queryWrapper = new QueryWrapper<>();

        if (StringUtils.hasText(userName)){
            queryWrapper.eq("user_name",userName);
        }
        if (StringUtils.hasText(status)){
            queryWrapper.eq("status",status);
        }
        if (StringUtils.hasText(ipaddr)){
            queryWrapper.eq("ipaddr",ipaddr);
        }
        queryWrapper.orderByDesc("login_time");
        return loginInfoMapper.selectPage(pageObj, queryWrapper);
    }

    /**
     * 获取客户端真实IP地址
     * @param request HTTP请求对象
     * @return 客户端IP地址，如果无法获取则返回请求的远程地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 解析并获取客户端浏览器类型
     * @param request HTTP请求对象
     * @return 浏览器类型名称，包括：
     *         Chrome - Google Chrome浏览器
     *         Firefox - Mozilla Firefox浏览器
     *         Safari - Apple Safari浏览器
     *         Edge - Microsoft Edge浏览器
     *         Other - 其他浏览器
     *         Unknown - 无法识别的浏览器
     */
    private String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        if (userAgent.contains("Edge")) return "Edge";
        return "Other";
    }

    /**
     * 解析并获取客户端操作系统类型
     * @param request HTTP请求对象
     * @return 操作系统类型名称，包括：
     *         Windows - Microsoft Windows系统
     *         macOS - Apple macOS系统
     *         Linux - Linux系统
     *         Android - Google Android系统
     *         iOS - Apple iOS系统
     *         Other - 其他操作系统
     *         Unknown - 无法识别的操作系统
     */
    private String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "macOS";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone")) return "iOS";
        return "Other";
    }
}
