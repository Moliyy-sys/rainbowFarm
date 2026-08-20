package org.rainbow.farm_common.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseResult<T> {
    //状态码 200成功  else失败
    private Integer code;
    //提示信息
    private String msg;
    //返回数据
    private T data;
    //构建成功结果
    public static <T> BaseResult<T> success() {
        return new BaseResult<>(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMsg(), null);
    }
    //构建有数据的成功结果
    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<>(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMsg(), data);
    }
    //构建失败结果
    public static <T> BaseResult<T> error(CodeEnum codeEnum) {
        return new BaseResult<>(codeEnum.getCode(), codeEnum.getMsg(), null);
    }
    //构建有数据的失败结果
    public static <T> BaseResult<T> error(CodeEnum codeEnum, T data) {
        return new BaseResult<>(codeEnum.getCode(), codeEnum.getMsg(), data);
    }
}
