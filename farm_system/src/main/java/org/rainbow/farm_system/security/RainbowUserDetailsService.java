package org.rainbow.farm_system.security;

import org.rainbow.farm_system.entity.Permission;
import org.rainbow.farm_system.entity.User;
import org.rainbow.farm_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RainbowUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1.认证
        User user = userService.findByUserName(username);
        if(user == null){
            throw new UsernameNotFoundException("用户名不存在");
        }
        //2.授权
        List<Permission> permissions = userService.findAllPermission(username);
        List<GrantedAuthority> authorities = new ArrayList<>();
        if(permissions != null && !permissions.isEmpty() && !permissions.stream().allMatch(Objects::isNull)){
            for(Permission permission : permissions){
                authorities.add(new SimpleGrantedAuthority(permission.getComponent()));
            }
        }
        //3.封装为UserDetails对象
        UserDetails userDetails =org.springframework.security.core.userdetails.User
                .withUsername(user.getUserName())
                .password(user.getPassword())
                .disabled(user.getStatus().equals("1")) //如果status = 1 表示禁用该用户
                .authorities(authorities)
                .build();
        //4.返回
        return userDetails;
    }

}
