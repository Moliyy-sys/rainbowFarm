package org.rainbow.farm_system.security;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_system.service.LoginInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
//登陆成功处理类
@Component
public class RainbowLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private LoginInfoService loginInfoService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 记录登录日志
        String userName = authentication.getName();
        loginInfoService.recordLoginInfo(userName, "0", "登录成功", request);

        // 获取用户信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // 生成JWT令牌
        String token = jwtUtil.generateToken(userDetails);
        // 将令牌响应给用户
        BaseResult result = new BaseResult(200, "登录成功", token);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(JSON.toJSONString(result));
    }
}
