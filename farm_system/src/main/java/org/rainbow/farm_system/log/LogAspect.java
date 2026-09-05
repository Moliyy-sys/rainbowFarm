package org.rainbow.farm_system.log;

import com.alibaba.fastjson2.JSON;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.util.SecurityUtil;
import org.rainbow.farm_system.entity.OperLog;
import org.rainbow.farm_system.service.OperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

/**
 * 操作日志处理切面
 */
@Aspect
@Component
public class LogAspect {
    @Autowired
    private OperLogService operLogService;

    /**
     * 定义切点，拦截标注了@Log注解的方法
     */
    @Pointcut("@annotation(com.itbaizhan.farm_system.log.Log)")
    public void logPointCut(){}

    /**
     * 环绕通知，记录操作日志
     */
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录方法的开始时间
        long beginTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;

        try {
            // 执行目标方法
            result = joinPoint.proceed();
            return result;
        }catch (Exception e){
            exception = e;
            throw e;
        }finally {
            // 计算执行时间
            long costTime = System.currentTimeMillis() - beginTime;
            // 记录日志
            handleLog(joinPoint, result, exception, costTime);
        }
    }

    /**
     * 处理日志记录
     * @param joinPoint 切点
     * @param result 目标方法返回结果
     * @param exception 目标方法异常
     * @param costTime 执行时间
     */
    private void handleLog(ProceedingJoinPoint joinPoint, Object result, Exception exception, long costTime){
        try {
            OperLog operLog = new OperLog();

            // 获取注解信息
            Log logAnnotation = getAnnotationLog(joinPoint);
            if (logAnnotation == null){
                return;
            }
            operLog.setTitle(logAnnotation.title());
            //operLog.setBusinessType(logAnnotation.businessType());

            // 设置基本信息
            operLog.setOperTime(LocalDateTime.now());
            operLog.setCostTime(costTime);
            operLog.setStatus(exception == null ? 0 : 1);

            // 设置请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null){
                HttpServletRequest request = attributes.getRequest();

                operLog.setOperUrl(request.getRequestURI());
                operLog.setRequestMethod(request.getMethod());
                operLog.setOperIp(getClientIp(request));

                // 设置请求参数
                setRequestParams(request, joinPoint, operLog);
            }

            // 设置操作人员
            setOperName(operLog);

            // 设置方法信息
            setMethodInfo(joinPoint,operLog);

            // 设置返回结果
            if (result != null){
                String jsonResult = JSON.toJSONString(result);
                operLog.setJsonResult(jsonResult.length() > 2000 ? jsonResult.substring(0, 2000) : jsonResult);
            }

            // 设置异常信息
            if (exception != null){
                String errorMsg = null;
                if (exception instanceof BusException){
                    BusException busException = (BusException) exception;
                    errorMsg = busException.getCodeEnum().getMsg();
                }else {
                    errorMsg = exception.getMessage();
                }
                operLog.setErrorMsg(errorMsg.length() > 2000 ? errorMsg.substring(0, 2000) : errorMsg);
            }

            // 保存操作日志
            operLogService.insert(operLog);
        }catch (Exception e){
            // 记录日志失败时，不影响业务执行
            e.printStackTrace();
        }
    }

    /**
     * 获取方法上的@Log注解
     */
    private Log getAnnotationLog(JoinPoint joinPoint) {
        try {
            return joinPoint.getTarget().getClass()
                    .getMethod(joinPoint.getSignature().getName(),
                            ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterTypes())
                    .getAnnotation(Log.class);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 获取客户端真实IP地址
     * @param request HTTP请求对象
     * @return 客户端IP地址，如果无法获取则返回请求的远程地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 设置请求参数
     */
    private void setRequestParams(HttpServletRequest request, JoinPoint joinPoint, OperLog operLog) {
        try {
            // 获取请求参数
            Map<String, String[]> paramMap = request.getParameterMap();
            if (paramMap != null && !paramMap.isEmpty()) {
                String params = JSON.toJSONString(paramMap);
                operLog.setOperParam(params.length() > 2000 ? params.substring(0, 2000) : params);
            } else {
                // 如果没有请求参数，尝试获取方法参数
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0) {
                    // 过滤掉HttpServletRequest、HttpServletResponse等不需要记录的参数
                    Object[] filteredArgs = Arrays.stream(args)
                            .filter(arg -> arg != null &&
                                    !arg.getClass().getName().startsWith("jakarta.servlet") &&
                                    !arg.getClass().getName().startsWith("org.springframework.web"))
                            .toArray();
                    if (filteredArgs.length > 0) {
                        String params = JSON.toJSONString(filteredArgs);
                        operLog.setOperParam(params.length() > 2000 ? params.substring(0, 2000) : params);
                    }
                }
            }
        } catch (Exception e) {
            operLog.setOperParam("参数获取失败：" + e.getMessage());
        }
    }

    /**
     * 设置操作人员
     */
    private void setOperName(OperLog operLog) {
        try {
            String userName = SecurityUtil.getUserName();
            if (userName != null){
                operLog.setOperName(userName);
            }
        }catch (Exception e){
            operLog.setOperName("unknown");
        }
    }

    /**
     * 设置方法信息
     */
    private void setMethodInfo(JoinPoint joinPoint,OperLog operLog) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        operLog.setMethod(className + "." + methodName + "()");
    }
}
