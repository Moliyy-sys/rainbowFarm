package org.rainbow.farm_system.security;

import org.springframework.security.core.AuthenticationException;

/**
 * 自定义验证码异常
 */
public class InvalidCaptchaException extends AuthenticationException {
    public InvalidCaptchaException(String msg){
        super(msg);
    }
}
