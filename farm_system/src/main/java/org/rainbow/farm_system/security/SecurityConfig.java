package org.rainbow.farm_system.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security配置类
 */
@Configuration
public class SecurityConfig {
    @Autowired
    private CaptchaFilter captchaFilter;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private RainbowAccessDeniedHandler rainbowAccessDeniedHandler;
    @Autowired
    private RainbowLoginFailureHandler rainbowLoginFailureHandler;
    @Autowired
    private RainbowLogoutSuccessHandler rainbowLogoutSuccessHandler;
    @Autowired
    private RainbowLoginSuccessHandler rainbowLoginSuccessHandler;
    @Autowired
    private RainbowAuthenticationEntryPoint rainbowAuthenticationEntryPoint;
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //自定义表单目录
        httpSecurity.formLogin(
                form -> {
                    form.usernameParameter("username")//用户名项
                            .passwordParameter("password")//密码项
                            .loginProcessingUrl("/user/login")//登录处理接口
                            .successHandler(rainbowLoginSuccessHandler)//登录成功处理
                            .failureHandler(rainbowLoginFailureHandler);//登录失败处理
                }
        );
        //权限拦截配置
        httpSecurity.authorizeHttpRequests(
                resp -> {
                    resp.requestMatchers("/user/login", "/captcha/generate").permitAll();
                    resp.anyRequest().authenticated();//其他接口需要认证
                }
        );
        //退出登录配置
        httpSecurity.logout(
                logout -> {
                    logout.logoutUrl("user/logout")//退出登录接口
                            .logoutSuccessHandler(rainbowLogoutSuccessHandler);//退出成功处理
                            //.clearAuthentication(true)
                            //.invalidateHttpSession(true);
                }
        );
        //设置session管理策略为无状态
        httpSecurity.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS
                ));

        //异常处理配置
        httpSecurity.exceptionHandling(
                exception -> {
                    exception.authenticationEntryPoint(rainbowAuthenticationEntryPoint)//未登录处理
                            .accessDeniedHandler(rainbowAccessDeniedHandler);//权限不足处理器
                }
        );
        //关闭csrf防护配置
        httpSecurity.csrf(csrf -> csrf.disable());
        //跨域配置
        httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        /**
         * 添加验证码过滤器
         * 添加JWT过滤器
         */
        httpSecurity.addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
    //跨域配置对象
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");//允许所有源
        corsConfiguration.addAllowedHeader("*");//允许所有请求头
        corsConfiguration.addAllowedMethod("*");//允许所有请求方法
        corsConfiguration.setAllowCredentials(false);//不允许cookie跨域

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration);//允许所有路径
        return source;
    }
}
