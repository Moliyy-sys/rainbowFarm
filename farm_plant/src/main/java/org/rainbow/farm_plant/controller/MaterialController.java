package org.rainbow.farm_plant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_plant.entity.Material;
import org.rainbow.farm_plant.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 农资信息Controller
 */
@RestController
@RequestMapping("/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    /**
     * 分页查询农资信息
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param materialTypeId 农资类别ID
     * @param materialName 农资名称
     * @param materialCode 农资编码
     * @return 农资信息分页数据
     */
    @GetMapping("/list")
    public BaseResult<IPage<Material>> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                            @RequestParam(value = "materialTypeId", required = false) Long materialTypeId,
                                            @RequestParam(value = "materialName", required = false) String materialName,
                                            @RequestParam(value = "materialCode", required = false) String materialCode) {
        IPage<Material> materialPage = materialService.selectPage(pageNum, pageSize, materialTypeId, materialName,materialCode);
        return BaseResult.success(materialPage);
    }

    /**
     * 根据ID查询农资信息详情
     * @param id 农资ID
     * @return 农资详情
     */
    @GetMapping("/getMaterialById")
    public BaseResult<Material> detail(@RequestParam("id") Long id) {
        Material material = materialService.getMaterialById(id);
        return BaseResult.success(material);
    }

    /**
     * 新增农资信息
     * @param material 农资信息
     * @return 操作结果
     */
    @PostMapping("/addMaterial")
    public BaseResult<?> add(@RequestBody Material material) {
        materialService.addMaterial(material);
        return BaseResult.success();
    }

    /**
     * 修改农资信息
     * @param material 农资信息
     * @return 操作结果
     */
    @PutMapping("/updateMaterial")
    public BaseResult<?> update(@RequestBody Material material) {
        materialService.updateMaterial(material);
        return BaseResult.success();
    }

    /**
     * 删除农资信息
     * @param ids 农资ID字符串，多个ID用逗号分隔
     * @return 操作结果
     */
    @DeleteMapping("/deleteMaterial")
    public BaseResult<?> delete(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        materialService.deleteMaterial(idList);
        return BaseResult.success();
    }
}


