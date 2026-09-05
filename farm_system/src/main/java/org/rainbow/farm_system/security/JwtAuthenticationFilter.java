package org.rainbow.farm_system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.CodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器
 * 拦截请求，验证JWT令牌并设置用户认证信息
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RainbowUserDetailsService rainbowUserDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的JWT令牌
        String jwt = getJwtFromRequest(request);
        // 验证JWT令牌并设置用户认证信息
        if (StringUtils.hasText(jwt) && jwtUtil.isValidToken(jwt) && !jwtUtil.isTokenExpired(jwt)) {
            try {
                // 获取用户名
                String username = jwtUtil.getUsernameFromToken(jwt);
                // 如果用户名存在且当前没有认证信息，则设置用户认证信息
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    // 获取用户详情
                    UserDetails userDetails = rainbowUserDetailsService.loadUserByUsername(username);

                    // 从JWT中获取权限信息
                    List<String> authorities = jwtUtil.getAuthoritiesFromToken(jwt);
                    List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails, null, grantedAuthorities);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 将用户认证信息设置到SecurityContextHolder中
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }catch (Exception e){
                throw new BusException(CodeEnum.UNAUTHORIZED);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取JWT令牌
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
