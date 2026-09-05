package org.rainbow.farm_common.result;

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

    SYS_ROLE_EXIST(603,"角色已存在"),

    SYS_PERMISSION_EXIST(604,"权限已存在"),

    CAPTCHA_ISNULL(605,"验证码不能为空"),

    CAPTCHA_ERROR(606,"验证码错误"),

    UNAUTHORIZED(605, "用户未登录"),

    PERSONAL_USER_NOT_FOUND(607, "登录用户不存在"),

    PERSONAL_PASSWORD_EMPTY(608, "密码不能为空"),

    PERSONAL_PASSWORD_NOT_MATCH(609, "两次输入密码不一致"),

    PERSONAL_PASSWORD_ERROR(610, "密码错误"),

    DICT_TYPE_EXIST(611, "字典类型已存在"),

    DICT_DATA_EXIST(612, "字典数据已存在"),

    WAREHOUSING_CODE_EXIST(613, "编码已存在"),

    WAREHOUSING_NAME_EXIST(614, "名称已存在"),

    WAREHOUSING_CATEGORY_HAS_ITEM(615, "该类型下存在物料，无法删除"),

    WAREHOUSING_ORDER_ALL_READY(616, "该订单已完成，无法删除修改"),

    WAREHOUSING_ORDER_NOT_EXIST(617, "该订单不存在"),

    WAREHOUSING_ORDER_DETAIL_ISNULL(618, "该订单的订单详情为空"),

    WAREHOUSING_INSUFFICIENT(619, "库存不足"),

    PLANT_NAME_EXIST(620, "名称已存在"),

    PLANT_CODE_EXIST(621, "编码已存在")
    ;
    private final Integer code;
    private final String msg;
}
