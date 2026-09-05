package org.rainbow.farm_plant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_plant.entity.Machine;
import org.rainbow.farm_plant.service.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 农机信息Controller
 */
@RestController
@RequestMapping("/machine")
public class MachineController {

    @Autowired
    private MachineService machineService;

    /**
     * 分页查询农机信息
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param machineTypeId 农机类别ID
     * @param machineName 农机名称
     * @return 农机信息分页数据
     */
    @GetMapping("/list")
    public BaseResult<IPage<Machine>> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                           @RequestParam(value = "machineTypeId", required = false) Long machineTypeId,
                                           @RequestParam(value = "machineName", required = false) String machineName,
                                           @RequestParam(value = "machineCode",required = false) String machineCode) {
        IPage<Machine> machinePage = machineService.selectPage(pageNum, pageSize, machineTypeId, machineName, machineCode);
        return BaseResult.success(machinePage);
    }

    /**
     * 根据ID查询农机信息详情
     * @param id 农机ID
     * @return 农机详情
     */
    @GetMapping("/getMachineById")
    public BaseResult<Machine> detail(@RequestParam("id") Long id) {
        Machine machine = machineService.getMachineById(id);
        return BaseResult.success(machine);
    }

    /**
     * 新增农机信息
     * @param machine 农机信息
     * @return 操作结果
     */
    @PostMapping("/addMachine")
    public BaseResult<?> add(@RequestBody Machine machine) {
        machineService.addMachine(machine);
        return BaseResult.success();
    }

    /**
     * 修改农机信息
     * @param machine 农机信息
     * @return 操作结果
     */
    @PutMapping("/updateMachine")
    public BaseResult<?> update(@RequestBody Machine machine) {
        machineService.updateMachine(machine);
        return BaseResult.success();
    }

    /**
     * 删除农机信息
     * @param ids 农机ID字符串，多个ID用逗号分隔
     * @return 操作结果
     */
    @DeleteMapping("/deleteMachine")
    public BaseResult<?> delete(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        machineService.deleteMachine(idList);
        return BaseResult.success();
    }
}


