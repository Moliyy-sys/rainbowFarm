package org.rainbow.farm_system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_system.entity.DictType;
import org.rainbow.farm_system.service.DictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典类型Controller
 */
@RestController
@RequestMapping("/dictType")
public class DictTypeController {
    @Autowired
    private DictTypeService dictTypeService;

    /**
     * 分页查询字典类型
     * @param pageNum 当前页码，默认1
     * @param pageSize 每页大小，默认10
     * @param dictName 字典名称，可选
     * @param status 状态，可选
     * @return 字典类型分页结果
     */
    @GetMapping("/list")
    public BaseResult<IPage<DictType>> getDictTypeList(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "dictName", required = false) String dictName,
            @RequestParam(value = "status", required = false) String status) {
        IPage<DictType> dictTypePage = dictTypeService.findDictTypePage(pageNum, pageSize, dictName, status);
        return BaseResult.success(dictTypePage);
    }

    /**
     * 根据id查询字典类型详情
     * @param id 字典类型ID
     * @return 字典类型详情
     */
    @GetMapping("/getDictTypeById")
    public BaseResult<DictType> findById(@RequestParam("id") Long id) {
        DictType dictType = dictTypeService.findById(id);
        return BaseResult.success(dictType);
    }

    /**
     * 新增字典类型
     * @param dictType 字典类型信息
     * @return 操作结果
     */
    @PostMapping("/addDictType")
    public BaseResult addDictType(@RequestBody DictType dictType) {
        dictTypeService.addDictType(dictType);
        return BaseResult.success();
    }

    /**
     * 修改字典类型
     * @param dictType 字典类型信息
     * @return 操作结果
     */
    @PutMapping("/updateDictType")
    public BaseResult updateDictType(@RequestBody DictType dictType) {
        dictTypeService.updateDictType(dictType);
        return BaseResult.success();
    }

    /**
     * 批量删除字典类型
     * @param dictIds 字典类型ID列表，多个ID使用逗号分割
     * @return 操作结果
     */
    @DeleteMapping("/deleteDictType")
    public BaseResult deleteDictType(@RequestParam("dictIds") String dictIds) {
        List<Long> idList = Arrays.stream(dictIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        dictTypeService.deleteDictType(idList);
        return BaseResult.success();
    }

    /**
     * 查询所有有效的字典类型
     * @return 有效字典类型列表
     */
    @DeleteMapping("/getAllDictTypes")
    public BaseResult<List<DictType>> deleteDictType() {
        List<DictType> result = dictTypeService.findAll();
        return BaseResult.success(result);
    }

}
