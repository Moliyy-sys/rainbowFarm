package org.rainbow.farm_system.security;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_common.result.CodeEnum;
import org.rainbow.farm_system.service.LoginInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
//登陆失败处理类
public class RainbowLoginFailureHandler implements AuthenticationFailureHandler {
    @Autowired
    private LoginInfoService loginInfoService;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        BaseResult result = null;
        // 验证码异常
        if (exception instanceof InvalidCaptchaException){
            BusException exception1 = (BusException) request.getAttribute("captchaError");
            CodeEnum codeEnum = exception1.getCodeEnum();
            result = new BaseResult(codeEnum.getCode(),codeEnum.getMsg(),null);
        }else {
            // 登录失败异常
            result = new BaseResult(402, "用户名或密码错误", null);
        }

        // 记录登录信息
        loginInfoService.recordLoginInfo("unknown","1",result.getMsg(),request);

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(JSON.toJSONString(result));
    }
}
