package org.rainbow.farm_plant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_plant.entity.Land;
import org.rainbow.farm_plant.service.LandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 地块Controller
 */
@RestController
@RequestMapping("/land")
public class LandController {

    @Autowired
    private LandService landService;

    /**
     * 分页查询地块
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param landName 地块名称
     * @param landType 地块类型
     * @return 地块分页数据
     */
    @GetMapping("/list")
    public BaseResult<IPage<Land>> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                        @RequestParam(value = "landName", required = false) String landName,
                                        @RequestParam(value = "landType", required = false) String landType) {
        IPage<Land> result = landService.selectPage(pageNum, pageSize, landName, landType);
        return BaseResult.success(result);
    }

    /**
     * 根据ID查询地块详情
     * @param id 地块ID
     * @return 地块详情
     */
    @GetMapping("/getLandById")
    public BaseResult<Land> detail(@RequestParam("id") Long id) {
        Land land = landService.getLandById(id);
        return BaseResult.success(land);
    }

    /**
     * 新增地块
     * @param land 地块信息
     * @return 操作结果
     */
    @PostMapping("/addLand")
    public BaseResult<?> add(@RequestBody Land land) {
        landService.addLand(land);
        return BaseResult.success();
    }

    /**
     * 修改地块
     * @param land 地块信息
     * @return 操作结果
     */
    @PutMapping("/updateLand")
    public BaseResult<?> update(@RequestBody Land land) {
        landService.updateLand(land);
        return BaseResult.success();
    }

    /**
     * 删除地块
     * @param ids 地块ID字符串，多个ID用逗号分隔
     * @return 操作结果
     */
    @DeleteMapping("/deleteLand")
    public BaseResult<?> delete(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        landService.deleteLand(idList);
        return BaseResult.success();
    }
}


