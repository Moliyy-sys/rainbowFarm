package org.rainbow.farm_common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 权限工具类
 */
public class SecurityUtil {
    /**
     * 获取登录用户名
     * @return 用户名
     */
    public static String getUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() !=null){
            return authentication.getName();
        }else {
            return "unknown";
        }
    }
}
