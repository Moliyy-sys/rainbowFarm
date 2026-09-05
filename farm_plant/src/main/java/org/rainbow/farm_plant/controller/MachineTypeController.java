package org.rainbow.farm_plant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_plant.entity.MachineType;
import org.rainbow.farm_plant.service.MachineTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 农机类别Controller
 */
@RestController
@RequestMapping("/machineType")
public class MachineTypeController {

    @Autowired
    private MachineTypeService machineTypeService;

    /**
     * 分页查询农机类别
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param machineTypeName 农机类别名称
     * @return 农机类别分页数据
     */
    @GetMapping("/list")
    public BaseResult<IPage<MachineType>> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                               @RequestParam(value = "machineTypeName", required = false) String machineTypeName) {
        IPage<MachineType> machineTypePage = machineTypeService.selectPage(pageNum, pageSize, machineTypeName);
        return BaseResult.success(machineTypePage);
    }

    /**
     * 根据ID查询农机类别详情
     * @param id 农机类别ID
     * @return 农机类别详情
     */
    @GetMapping("/getMachineTypeById")
    public BaseResult<MachineType> detail(@RequestParam("id") Long id) {
        MachineType machineType = machineTypeService.getMachineTypeById(id);
        return BaseResult.success(machineType);
    }

    /**
     * 新增农机类别
     * @param machineType 农机类别信息
     * @return 操作结果
     */
    @PostMapping("/addMachineType")
    public BaseResult<?> add(@RequestBody MachineType machineType) {
        machineTypeService.addMachineType(machineType);
        return BaseResult.success();
    }

    /**
     * 修改农机类别
     * @param machineType 农机类别信息
     * @return 操作结果
     */
    @PutMapping("/updateMachineType")
    public BaseResult<?> update(@RequestBody MachineType machineType) {
        machineTypeService.updateMachineType(machineType);
        return BaseResult.success();
    }

    /**
     * 删除农机类别
     * @param ids 农机类别ID字符串，多个ID用逗号分隔
     * @return 操作结果
     */
    @DeleteMapping("/deleteMachineType")
    public BaseResult<?> delete(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        machineTypeService.deleteMachineType(idList);
        return BaseResult.success();
    }
}


