package org.rainbow.farm_system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security配置类
 */
@Configuration
public class SecurityConfig {
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //自定义表单目录
        httpSecurity.formLogin(
                form -> {
                    form.usernameParameter("username")//用户名项
                            .passwordParameter("password")//密码项
                            .loginProcessingUrl("/user/login")//登录处理接口
                            .successHandler(new RainbowLoginSuccessHandler())
                            .failureHandler(new RainbowLoginFailureHandler());
                }
        );
        //权限拦截配置
        httpSecurity.authorizeHttpRequests(
                resp -> {
                    resp.requestMatchers("/user/login", "captcha/generate").permitAll();
                    resp.anyRequest().authenticated();//其他接口需要认证
                }
        );
        //退出登录配置
        httpSecurity.logout(
                logout -> {
                    logout.logoutUrl("user/logout")//退出登录接口
                            .logoutSuccessHandler(new RainbowLogoutSuccessHandler())//退出成功处理
                            .clearAuthentication(true)
                            .invalidateHttpSession(true);
                }
        );

        //异常处理配置
        httpSecurity.exceptionHandling(
                exception -> {
                    exception.authenticationEntryPoint(new RainbowAuthenticationEntryPoint())//未登录处理
                            .accessDeniedHandler(new RainbowAccessDeniedHandler());//权限不足处理器
                }
        );
        //关闭csrf防护配置
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        //跨域配置
        httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()));

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
        source.registerCorsConfiguration("/**",corsConfiguration);//允许所有路径r
        return source;
    }
}
