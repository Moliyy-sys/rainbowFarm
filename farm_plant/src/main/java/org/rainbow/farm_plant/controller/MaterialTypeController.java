package org.rainbow.farm_plant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_plant.entity.MaterialType;
import org.rainbow.farm_plant.service.MaterialTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 农资类别Controller
 */
@RestController
@RequestMapping("/materialType")
public class MaterialTypeController {

    @Autowired
    private MaterialTypeService materialTypeService;

    /**
     * 分页查询农资类别
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param materialTypeName 农资类别名称
     * @return 农资类别分页数据
     */
    @GetMapping("/list")
    public BaseResult<IPage<MaterialType>> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                @RequestParam(value = "materialTypeName", required = false) String materialTypeName) {
        IPage<MaterialType> materialTypePage = materialTypeService.selectPage(pageNum, pageSize, materialTypeName);
        return BaseResult.success(materialTypePage);
    }

    /**
     * 根据ID查询农资类别详情
     * @param id 农资类别ID
     * @return 农资类别详情
     */
    @GetMapping("/getMaterialTypeById")
    public BaseResult<MaterialType> detail(@RequestParam("id") Long id) {
        MaterialType materialType = materialTypeService.getMaterialTypeById(id);
        return BaseResult.success(materialType);
    }

    /**
     * 新增农资类别
     * @param materialType 农资类别信息
     * @return 操作结果
     */
    @PostMapping("/addMaterialType")
    public BaseResult<?> add(@RequestBody MaterialType materialType) {
        materialTypeService.addMaterialType(materialType);
        return BaseResult.success();
    }

    /**
     * 修改农资类别
     * @param materialType 农资类别信息
     * @return 操作结果
     */
    @PutMapping("/updateMaterialType")
    public BaseResult<?> update(@RequestBody MaterialType materialType) {
        materialTypeService.updateMaterialType(materialType);
        return BaseResult.success();
    }

    /**
     * 删除农资类别
     * @param ids 农资类别ID字符串，多个ID用逗号分隔
     * @return 操作结果
     */
    @DeleteMapping("/deleteMaterialType")
    public BaseResult<?> delete(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        materialTypeService.deleteMaterialType(idList);
        return BaseResult.success();
    }
}


