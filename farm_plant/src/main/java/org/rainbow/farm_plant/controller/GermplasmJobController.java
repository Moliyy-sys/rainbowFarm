package org.rainbow.farm_plant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_plant.entity.GermplasmJob;
import org.rainbow.farm_plant.service.GermplasmJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 作业任务Controller
 */
@RestController
@RequestMapping("/germplasmJob")
public class GermplasmJobController {

    @Autowired
    private GermplasmJobService germplasmJobService;

    /**
     * 分页查询作业任务
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param germplasmId 种质ID
     * @param jobName 作业任务名称
     * @return 作业任务分页数据
     */
    @GetMapping("/list")
    public BaseResult<IPage<GermplasmJob>> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                @RequestParam(value = "germplasmId", required = false) Long germplasmId,
                                                @RequestParam(value = "jobName", required = false) String jobName) {
        IPage<GermplasmJob> germplasmJobPage = germplasmJobService.selectPage(pageNum, pageSize, germplasmId, jobName);
        return BaseResult.success(germplasmJobPage);
    }

    /**
     * 根据ID查询作业任务详情
     * @param id 作业任务ID
     * @return 作业任务详情
     */
    @GetMapping("/getGermplasmJobById")
    public BaseResult<GermplasmJob> getGermplasmJobById(@RequestParam("id") Long id) {
        GermplasmJob germplasmJob = germplasmJobService.getGermplasmJobById(id);
        return BaseResult.success(germplasmJob);
    }

    /**
     * 新增作业任务
     * @param germplasmJob 作业任务信息
     * @return 操作结果
     */
    @PostMapping("/addGermplasmJob")
    public BaseResult<?> addGermplasmJob(@RequestBody GermplasmJob germplasmJob) {
        germplasmJobService.addGermplasmJob(germplasmJob);
        return BaseResult.success();
    }

    /**
     * 修改作业任务
     * @param germplasmJob 作业任务信息
     * @return 操作结果
     */
    @PutMapping("/updateGermplasmJob")
    public BaseResult<?> updateGermplasmJob(@RequestBody GermplasmJob germplasmJob) {
        germplasmJobService.updateGermplasmJob(germplasmJob);
        return BaseResult.success();
    }

    /**
     * 删除作业任务
     * @param ids 作业任务ID字符串，多个id用逗号分割
     * @return 操作结果
     */
    @DeleteMapping("/deleteGermplasmJob")
    public BaseResult<?> deleteGermplasmJob(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        germplasmJobService.deleteGermplasmJob(idList);
        return BaseResult.success();
    }
}


