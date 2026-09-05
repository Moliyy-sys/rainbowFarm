package org.rainbow.farm_plant.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.CodeEnum;
import org.rainbow.farm_common.util.SecurityUtil;
import org.rainbow.farm_plant.entity.GermplasmJob;
import org.rainbow.farm_plant.mapper.GermplasmJobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业任务服务
 */
@Service
@Transactional
public class GermplasmJobService {

    @Autowired
    private GermplasmJobMapper germplasmJobMapper;

    /**
     * 分页查询作业任务
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param germplasmId 种质ID
     * @param jobName 作业任务名称
     * @return 作业任务分页数据
     */
    public IPage<GermplasmJob> selectPage(Integer pageNum, Integer pageSize, Long germplasmId, String jobName) {
        Page<GermplasmJob> page = new Page<>(pageNum, pageSize);
        QueryWrapper<GermplasmJob> wrapper = new QueryWrapper<>();
        if (germplasmId != null) {
            wrapper.eq("germplasm_id", germplasmId);
        }
        if (StringUtils.hasText(jobName)) {
            wrapper.like("job_name", jobName);
        }
        wrapper.orderByDesc("create_time");
        return germplasmJobMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询作业任务详情
     * @param jobId 作业任务ID
     * @return 作业任务详情
     */
    public GermplasmJob getGermplasmJobById(Long jobId) {
        return germplasmJobMapper.selectById(jobId);
    }

    /**
     * 新增作业任务
     * @param germplasmJob 作业任务信息
     */
    public boolean addGermplasmJob(GermplasmJob germplasmJob) {
        QueryWrapper<GermplasmJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("germplasm_id", germplasmJob.getGermplasmId())
                .eq("job_name", germplasmJob.getJobName());
        GermplasmJob existJob = germplasmJobMapper.selectOne(queryWrapper);
        if (existJob != null) {
            throw new BusException(CodeEnum.PLANT_NAME_EXIST);
        }

        String username = SecurityUtil.getUserName();
        germplasmJob.setCreateBy(username);
        germplasmJob.setCreateTime(LocalDateTime.now());
        germplasmJob.setUpdateBy(username);
        germplasmJob.setUpdateTime(LocalDateTime.now());
        return germplasmJobMapper.insert(germplasmJob) > 0;
    }

    /**
     * 修改作业任务
     * @param germplasmJob 作业任务信息
     */
    public boolean updateGermplasmJob(GermplasmJob germplasmJob) {
        QueryWrapper<GermplasmJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("germplasm_id", germplasmJob.getGermplasmId())
                .eq("job_name", germplasmJob.getJobName())
                .ne("job_id", germplasmJob.getJobId());
        GermplasmJob existJob = germplasmJobMapper.selectOne(queryWrapper);
        if (existJob != null) {
            throw new BusException(CodeEnum.PLANT_NAME_EXIST);
        }

        String username = SecurityUtil.getUserName();
        germplasmJob.setUpdateBy(username);
        germplasmJob.setUpdateTime(LocalDateTime.now());
        return germplasmJobMapper.updateById(germplasmJob) > 0;
    }

    /**
     * 删除作业任务
     * @param ids 作业任务ID列表
     */
    public boolean deleteGermplasmJob(List<Long> ids) {
        return germplasmJobMapper.deleteBatchIds(ids) > 0;
    }
}


