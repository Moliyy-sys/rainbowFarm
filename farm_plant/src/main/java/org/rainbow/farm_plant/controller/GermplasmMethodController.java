package org.rainbow.farm_plant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_plant.entity.GermplasmMethod;
import org.rainbow.farm_plant.service.GermplasmMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 种植方法Controller
 */
@RestController
@RequestMapping("/germplasmMethod")
public class GermplasmMethodController {

    @Autowired
    private GermplasmMethodService germplasmMethodService;

    /**
     * 分页查询种植方法
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param germplasmId 种质ID,可选
     * @param methodName 方法名称,可选
     * @return 种植方法分页数据
     */
    @GetMapping("/list")
    public BaseResult<IPage<GermplasmMethod>> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                   @RequestParam(value = "germplasmId", required = false) Long germplasmId,
                                                   @RequestParam(value = "methodName", required = false) String methodName) {
        IPage<GermplasmMethod> germplasmMethodPage = germplasmMethodService.selectPage(pageNum, pageSize, germplasmId, methodName);
        return BaseResult.success(germplasmMethodPage);
    }

    /**
     * 根据ID查询种植方法详情
     * @param id 种植方法ID
     * @return 种植方法详情
     */
    @GetMapping("/getGermplasmMethodById")
    public BaseResult<GermplasmMethod> getGermplasmMethodById(@RequestParam("id") Long id) {
        GermplasmMethod germplasmMethod = germplasmMethodService.getGermplasmMethodById(id);
        return BaseResult.success(germplasmMethod);
    }

    /**
     * 新增种植方法
     * @param germplasmMethod 种植方法信息
     * @return 操作结果
     */
    @PostMapping("/addGermplasmMethod")
    public BaseResult<?> addGermplasmMethod(@RequestBody GermplasmMethod germplasmMethod) {
        germplasmMethodService.addGermplasmMethod(germplasmMethod);
        return BaseResult.success();
    }

    /**
     * 修改种植方法
     * @param germplasmMethod 种植方法信息
     * @return 操作结果
     */
    @PutMapping("/updateGermplasmMethod")
    public BaseResult<?> updateGermplasmMethod(@RequestBody GermplasmMethod germplasmMethod) {
        germplasmMethodService.updateGermplasmMethod(germplasmMethod);
        return BaseResult.success();
    }

    /**
     * 删除种植方法
     * @param ids 种植方法ID字符串，多个ID用逗号分割
     * @return 操作结果
     */
    @DeleteMapping("/deleteGermplasmMethod")
    public BaseResult<?> deleteGermplasmMethod(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        germplasmMethodService.deleteGermplasmMethod(idList);
        return BaseResult.success();
    }
}


