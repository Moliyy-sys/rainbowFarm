package org.rainbow.farm_plant.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.CodeEnum;
import org.rainbow.farm_common.util.SecurityUtil;
import org.rainbow.farm_plant.entity.Germplasm;
import org.rainbow.farm_plant.mapper.GermplasmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 种质服务
 */
@Service
@Transactional
public class GermplasmService {

    @Autowired
    private GermplasmMapper germplasmMapper;

    /**
     * 分页查询种质
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param cropName 作物名称
     * @param germplasmName 种质名称
     * @return 种质分页数据
     */
    public IPage<Germplasm> selectPage(Integer pageNum, Integer pageSize, String cropName, String germplasmName) {
        Page<Germplasm> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Germplasm> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(cropName)) {
            wrapper.like("crop_name", cropName);
        }
        if (StringUtils.hasText(germplasmName)) {
            wrapper.like("germplasm_name", germplasmName);
        }
        wrapper.orderByDesc("create_time");
        return germplasmMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询种质详情
     * @param germplasmId 种质ID
     * @return 种质详情
     */
    public Germplasm getGermplasmById(Long germplasmId) {
        return germplasmMapper.selectById(germplasmId);
    }

    /**
     * 新增种质
     * @param germplasm 种质信息
     */
    public boolean addGermplasm(Germplasm germplasm) {
        // 检查种质名称是否存在
        QueryWrapper<Germplasm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("germplasm_name", germplasm.getGermplasmName());
        Germplasm existGermplasm = germplasmMapper.selectOne(queryWrapper);
        if (existGermplasm != null) {
            throw new BusException(CodeEnum.PLANT_NAME_EXIST);
        }

        String username = SecurityUtil.getUserName();
        germplasm.setCreateBy(username);
        germplasm.setCreateTime(LocalDateTime.now());
        germplasm.setUpdateBy(username);
        germplasm.setUpdateTime(LocalDateTime.now());
        return germplasmMapper.insert(germplasm) > 0;
    }

    /**
     * 修改种质
     * @param germplasm 种质信息
     */
    public boolean updateGermplasm(Germplasm germplasm) {
        // 检查种质名称是否存在
        QueryWrapper<Germplasm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("germplasm_name", germplasm.getGermplasmName())
                .ne("germplasm_id", germplasm.getGermplasmId());
        Germplasm existGermplasm = germplasmMapper.selectOne(queryWrapper);
        if (existGermplasm != null) {
            throw new BusException(CodeEnum.PLANT_NAME_EXIST);
        }

        String username = SecurityUtil.getUserName();
        germplasm.setUpdateBy(username);
        germplasm.setUpdateTime(LocalDateTime.now());
        return germplasmMapper.updateById(germplasm) > 0;
    }

    /**
     * 删除种质
     * @param ids 种质ID列表
     */
    public boolean deleteGermplasm(List<Long> ids) {
        return germplasmMapper.deleteBatchIds(ids) > 0;
    }
}


