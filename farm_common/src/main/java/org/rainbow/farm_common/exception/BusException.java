package org.rainbow.farm_common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.rainbow.farm_common.result.CodeEnum;

/**
 * 自定义业务异常
 */
@Data
@AllArgsConstructor
public class BusException extends RuntimeException {
    public BusException(String message) {
        super(message);
    }
    //状态码+错误信息
    private CodeEnum codeEnum;


}
