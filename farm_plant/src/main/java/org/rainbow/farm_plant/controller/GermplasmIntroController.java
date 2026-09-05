package org.rainbow.farm_plant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_plant.entity.GermplasmIntro;
import org.rainbow.farm_plant.service.GermplasmIntroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 种质介绍Controller
 */
@RestController
@RequestMapping("/germplasmIntro")
public class GermplasmIntroController {

    @Autowired
    private GermplasmIntroService germplasmIntroService;

    /**
     * 分页查询种质介绍
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param germplasmId 种质ID
     * @param introName 介绍名称
     * @return 种质介绍分页数据
     */
    @GetMapping("/list")
    public BaseResult<IPage<GermplasmIntro>> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                  @RequestParam(value = "germplasmId", required = false) Long germplasmId,
                                                  @RequestParam(value = "introName", required = false) String introName) {
        IPage<GermplasmIntro> germplasmIntroPage = germplasmIntroService.selectPage(pageNum, pageSize, germplasmId, introName);
        return BaseResult.success(germplasmIntroPage);
    }

    /**
     * 根据ID查询种质介绍详情
     * @param id 种质介绍ID
     * @return 种质介绍详情
     */
    @GetMapping("/getGermplasmIntroById")
    public BaseResult<GermplasmIntro> getGermplasmIntroById(@RequestParam("id") Long id) {
        GermplasmIntro germplasmIntro = germplasmIntroService.getGermplasmIntroById(id);
        return BaseResult.success(germplasmIntro);
    }

    /**
     * 新增种质介绍
     * @param germplasmIntro 种质介绍信息
     * @return 操作结果
     */
    @PostMapping("/addGermplasmIntro")
    public BaseResult<?> addGermplasmIntro(@RequestBody GermplasmIntro germplasmIntro) {
        germplasmIntroService.addGermplasmIntro(germplasmIntro);
        return BaseResult.success();
    }

    /**
     * 修改种质介绍
     * @param germplasmIntro 种质介绍信息
     * @return 操作结果
     */
    @PutMapping("/updateGermplasmIntro")
    public BaseResult<?> updateGermplasmIntro(@RequestBody GermplasmIntro germplasmIntro) {
        germplasmIntroService.updateGermplasmIntro(germplasmIntro);
        return BaseResult.success();
    }

    /**
     * 删除种质介绍
     * @param ids 种质介绍ID字符串，多个ID用逗号分割
     * @return 操作结果
     */
    @DeleteMapping("/deleteGermplasmIntro")
    public BaseResult<?> deleteGermplasmIntro(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        germplasmIntroService.deleteGermplasmIntro(idList);
        return BaseResult.success();
    }
}


