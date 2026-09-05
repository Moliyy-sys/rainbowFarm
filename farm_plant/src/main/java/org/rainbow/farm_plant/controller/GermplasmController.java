package org.rainbow.farm_plant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_plant.entity.Germplasm;
import org.rainbow.farm_plant.service.GermplasmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 种质Controller
 */
@RestController
@RequestMapping("/germplasm")
public class GermplasmController {

    @Autowired
    private GermplasmService germplasmService;

    /**
     * 分页查询种质
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param cropName 作物名称
     * @param germplasmName 种质名称
     * @return 种质分页数据
     */
    @GetMapping("/list")
    public BaseResult<IPage<Germplasm>> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                             @RequestParam(value = "cropName", required = false) String cropName,
                                             @RequestParam(value = "germplasmName", required = false) String germplasmName) {
        IPage<Germplasm> germplasmPage = germplasmService.selectPage(pageNum, pageSize, cropName, germplasmName);
        return BaseResult.success(germplasmPage);
    }

    /**
     * 根据ID查询种质详情
     * @param id 种质ID
     * @return 种质详情
     */
    @GetMapping("/getGermplasmById")
    public BaseResult<Germplasm> getGermplasmById(@RequestParam("id") Long id) {
        Germplasm germplasm = germplasmService.getGermplasmById(id);
        return BaseResult.success(germplasm);
    }

    /**
     * 新增种质
     * @param germplasm 种质信息
     * @return 操作结果
     */
    @PostMapping("/addGermplasm")
    public BaseResult<?> addGermplasm(@RequestBody Germplasm germplasm) {
        germplasmService.addGermplasm(germplasm);
        return BaseResult.success();
    }

    /**
     * 修改种质
     * @param germplasm 种质信息
     * @return 操作结果
     */
    @PutMapping("/updateGermplasm")
    public BaseResult<?> updateGermplasm(@RequestBody Germplasm germplasm) {
        germplasmService.updateGermplasm(germplasm);
        return BaseResult.success();
    }

    /**
     * 删除种质
     * @param ids 种质ID字符串，多个id用逗号分割
     * @return 操作结果
     */
    @DeleteMapping("/deleteGermplasm")
    public BaseResult<?> deleteGermplasm(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        germplasmService.deleteGermplasm(idList);
        return BaseResult.success();
    }
}


