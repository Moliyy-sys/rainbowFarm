package org.rainbow.farm_common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_common.result.CodeEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 统一异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    //处理系统异常
    @ExceptionHandler(Exception.class)
    public BaseResult defaultExceptionHandle(HttpServletRequest req, HttpServletResponse resp, Exception e){
        e.printStackTrace();
        return BaseResult.error(CodeEnum.SYSTEM_ERROR,e.getMessage());
    }
    //处理业务异常
    @ExceptionHandler(BusException.class)
    public BaseResult defaultExceptionHandle(BusException e){
        return BaseResult.error(e.getCodeEnum());
    }

    //处理权限不足异常,捕捉到异常后再次抛出
    @ExceptionHandler(AccessDeniedException.class)
    public void defaultExceptionHandle(AccessDeniedException e) throws AccessDeniedException {
        throw e;
    }
}
