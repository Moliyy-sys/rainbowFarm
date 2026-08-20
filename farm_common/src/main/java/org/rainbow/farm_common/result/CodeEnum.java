package org.rainbow.farm_common.result;

import com.sun.net.httpserver.Authenticator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态码枚举类
 */
@Getter
@AllArgsConstructor
public enum CodeEnum {
    //正常
    SUCCESS(200, "操作正常"),
    //系统异常
    SYSTEM_ERROR(500,"系统错误，请稍后重试"),
    //业务异常
    TEST_ERROR(601,"测试业务异常"),

    SYS_USER_EXIST(602,"用户名已存在"),

    SYS_ROLE_EXIST(603,"角色已存在");
    private final Integer code;
    private final String msg;
}
