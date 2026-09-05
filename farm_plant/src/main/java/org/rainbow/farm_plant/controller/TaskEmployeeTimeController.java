package org.rainbow.farm_plant.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_plant.entity.TaskEmployeeTime;
import org.rainbow.farm_plant.service.TaskEmployeeTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 人工工时管理控制器
 */
@RestController
@RequestMapping("/taskEmployeeTime")
public class TaskEmployeeTimeController {
    @Autowired
    private TaskEmployeeTimeService taskEmployeeTimeService;

    /**
     * 分页查询人工工时列表
     * @param pageNum 页码，默认1
     * @param pageSize 每页数量，默认10
     * @param taskId 任务ID，可选
     * @param employeeId 员工ID，可选
     * @return
     */
    @GetMapping("/list")
    public BaseResult<IPage<TaskEmployeeTime>> getTaskTimeList(
            @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(value = "taskId",required = false )Long taskId,
            @RequestParam(value = "employeeId",required = false ) Long employeeId){
        IPage<TaskEmployeeTime> page = taskEmployeeTimeService.findTaskEmployeeTimePage(pageNum, pageSize, taskId, employeeId);
        return BaseResult.success(page);
    }

    /**
     * 根据ID查询人工工时信息
     * @param id 人工工时ID
     * @return 人工工时信息
     */
    @GetMapping("/getTaskEmployeeTimeById")
    public BaseResult<TaskEmployeeTime> getTaskEmployeeTimeById(@RequestParam("id") Long id){
        TaskEmployeeTime taskEmployeeTime = taskEmployeeTimeService.findById(id);
        return BaseResult.success(taskEmployeeTime);
    }

    /**
     * 添加人工工时
     * @param taskEmployeeTime 人工工时信息
     * @return 添加结果
     */
    @PostMapping("/addTaskEmployeeTime")
    public BaseResult<?> add(@RequestBody TaskEmployeeTime taskEmployeeTime){
        taskEmployeeTimeService.add(taskEmployeeTime);
        return BaseResult.success();
    }

    /**
     * 修改人工工时
     * @param taskEmployeeTime 人工工时信息
     * @return 修改结果
     */
    @PutMapping("/updateTaskEmployeeTime")
    public BaseResult<?> update(@RequestBody TaskEmployeeTime taskEmployeeTime){
        taskEmployeeTimeService.update(taskEmployeeTime);
        return BaseResult.success();
    }

    /**
     * 删除人工工时
     * @param ids 人工工时ID字符串，多个ID用逗号分割
     * @return 删除结果
     */
    @DeleteMapping("/deleteTaskEmployeeTime")
    public BaseResult<?> delete(@RequestParam("ids") String ids){
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        taskEmployeeTimeService.delete(idList);
        return BaseResult.success();
    }
}
