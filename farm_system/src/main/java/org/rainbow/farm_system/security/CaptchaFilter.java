package org.rainbow.farm_system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.CodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CaptchaFilter extends OncePerRequestFilter {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RainbowLoginFailureHandler failureHandler;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //只拦截登录请求
        if("/user/login".equals(request.getRequestURI())) {
            try {
                //获取请求参数
                String sessionId = request.getSession().getId();
                String captchaCode = request.getParameter("captchaCode");
                if (StringUtils.hasText(captchaCode)) {
                    throw new BusException(CodeEnum.CAPTCHA_ISNULL);
                }
                //从redis中获取验证码
                String redisKey = "captcha:" + sessionId;
                String correctCode = redisTemplate.opsForValue().get(redisKey);
                if (correctCode == null || !correctCode.equalsIgnoreCase(captchaCode)) {
                    throw new BusException(CodeEnum.CAPTCHA_ERROR);
                }
            } catch (BusException e) {
                request.setAttribute("captchaError", e);
                //手动调用登陆失败处理器
                failureHandler.onAuthenticationFailure(
                        request,
                        response,
                        new InvalidCaptchaException(e.getMessage())
                );

                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
