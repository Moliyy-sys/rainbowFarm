package org.rainbow.farm_system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_system.entity.OperLog;
import org.rainbow.farm_system.log.BusinessType;
import org.rainbow.farm_system.service.OperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/operlog")
public class OperLogController {
    @Autowired
    private OperLogService operLogService;

    /**
     * 查询操作日志列表
     * @param pageNum 当前页码
     * @param pageSize 每页显示数量
     * @param title 操作日志标题
     * @param businessType 操作日志类型
     * @param operName 操作用户
     * @param requestMethod 请求方式
     * @param operIp 操作IP
     * @param status 操作状态
     * @param startTime 操作开始时间
     * @param endTime 操作结束时间
     * @return
     */
    @GetMapping("/list")
    public BaseResult<Page<OperLog>> selectOperLogList(
            @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value = "businessType",required = false) BusinessType businessType,
            @RequestParam(value = "operName",required = false) String operName,
            @RequestParam(value = "requestMethod",required = false)String requestMethod,
            @RequestParam(value = "operIp",required = false)String operIp,
            @RequestParam(value = "status",required = false)Integer status,
            @RequestParam(value = "startTime",required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime startTime,
            @RequestParam(value = "endTime",required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime endTime){
        Page<OperLog> page = operLogService.selectOperLogList(pageNum, pageSize, title,
                businessType, operName, requestMethod, operIp, status, startTime, endTime);
        return BaseResult.success(page);
    }

    /**
     * 根据ID查询操作日志
     * @param operId 操作日志ID
     * @return 操作日志
     */
    @GetMapping("/getOperLogById")
    public BaseResult<OperLog> selectOperLogById(Long operId){
        OperLog operLog = operLogService.selectOperLogById(operId);
        return BaseResult.success(operLog);
    }
}
