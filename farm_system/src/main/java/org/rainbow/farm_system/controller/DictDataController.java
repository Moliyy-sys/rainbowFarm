package org.rainbow.farm_system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_system.entity.DictData;
import org.rainbow.farm_system.service.DictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典数据Controller
 */
@RestController
@RequestMapping("/dictData")
public class DictDataController {
    @Autowired
    private DictDataService dictDataService;

    /**
     * 分页查询字典数据
     * @param pageNum 当前页码，默认1
     * @param pageSize 每页大小，默认10
     * @param dictLabel 字典标签，可选
     * @param dictType 字典类型ID，可选
     * @param status 状态，可选
     * @return 字典数据分页结果
     */
    @GetMapping("/list")
    public BaseResult<IPage<DictData>> getDictDataList(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "dictLabel", required = false) String dictLabel,
            @RequestParam(value = "dictType", required = false) Long dictType,
            @RequestParam(value = "status", required = false) String status) {
        IPage<DictData> dictDataPage = dictDataService.findDictDataPage(pageNum, pageSize, dictLabel, dictType, status);
        return BaseResult.success(dictDataPage);
    }

    /**
     * 根据id查询字典数据详情
     * @param dictCode 字典数据编码
     * @return 字典数据详情
     */
    @GetMapping("/getDictDataById")
    public BaseResult<DictData> findById(@RequestParam("dictCode") Long dictCode) {
        DictData dictData = dictDataService.findById(dictCode);
        return BaseResult.success(dictData);
    }

    /**
     * 根据字典类型查询字典数据信息
     * @param dictType 字典类型ID
     * @return 字典数据列表
     */
    @GetMapping("/getDictDataByType")
    public BaseResult<List<DictData>> getDictDataByType(@RequestParam("dictType") Long dictType) {
        List<DictData> dictDataList = dictDataService.findDictDataByType(dictType);
        return BaseResult.success(dictDataList);
    }

    /**
     * 新增字典数据
     * @param dictData 字典数据信息
     * @return 操作结果
     */
    @PostMapping("/addDictData")
    public BaseResult<?> addDictData(@RequestBody DictData dictData) {
        dictDataService.addDictData(dictData);
        return BaseResult.success();
    }

    /**
     * 修改字典数据
     * @param dictData 字典数据信息
     * @return 操作结果
     */
    @PutMapping("/updateDictData")
    public BaseResult<?> updateDictData(@RequestBody DictData dictData) {
        dictDataService.updateDictData(dictData);
        return BaseResult.success();
    }

    /**
     * 删除字典数据
     * @param dictCodes 字典数据编码列表，多个编码用逗号分割
     * @return 操作结果
     */
    @DeleteMapping("/deleteDictData")
    public BaseResult<?> deleteDictData(@RequestParam("dictCodes") String dictCodes) {
        List<Long> idList = Arrays.stream(dictCodes.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        dictDataService.deleteDictData(idList);
        return BaseResult.success();
    }
}
