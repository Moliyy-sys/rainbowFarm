package org.rainbow.farm_plant.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.CodeEnum;
import org.rainbow.farm_common.util.SecurityUtil;
import org.rainbow.farm_plant.entity.GermplasmIntro;
import org.rainbow.farm_plant.mapper.GermplasmIntroMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 种质介绍服务
 */
@Service
@Transactional
public class GermplasmIntroService {

    @Autowired
    private GermplasmIntroMapper germplasmIntroMapper;

    /**
     * 分页查询种质介绍
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param germplasmId 种质ID
     * @param introName 介绍名称
     * @return 种质介绍分页数据
     */
    public IPage<GermplasmIntro> selectPage(Integer pageNum, Integer pageSize, Long germplasmId, String introName) {
        Page<GermplasmIntro> page = new Page<>(pageNum, pageSize);
        QueryWrapper<GermplasmIntro> wrapper = new QueryWrapper<>();
        if (germplasmId != null) {
            wrapper.eq("germplasm_id", germplasmId);
        }
        if (StringUtils.hasText(introName)) {
            wrapper.like("intro_name", introName);
        }
        wrapper.orderByDesc("create_time");
        return germplasmIntroMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询种质介绍详情
     * @param introId 介绍ID
     * @return 种质介绍详情
     */
    public GermplasmIntro getGermplasmIntroById(Long introId) {
        return germplasmIntroMapper.selectById(introId);
    }

    /**
     * 新增种质介绍
     * @param germplasmIntro 种质介绍信息
     */
    public boolean addGermplasmIntro(GermplasmIntro germplasmIntro) {
        QueryWrapper<GermplasmIntro> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("germplasm_id", germplasmIntro.getGermplasmId())
                .eq("intro_name", germplasmIntro.getIntroName());
        GermplasmIntro existIntro = germplasmIntroMapper.selectOne(queryWrapper);
        if (existIntro != null) {
            throw new BusException(CodeEnum.PLANT_NAME_EXIST);
        }

        String username = SecurityUtil.getUserName();
        germplasmIntro.setCreateBy(username);
        germplasmIntro.setCreateTime(LocalDateTime.now());
        germplasmIntro.setUpdateBy(username);
        germplasmIntro.setUpdateTime(LocalDateTime.now());
        return germplasmIntroMapper.insert(germplasmIntro) > 0;
    }

    /**
     * 修改种质介绍
     * @param germplasmIntro 种质介绍信息
     */
    public boolean updateGermplasmIntro(GermplasmIntro germplasmIntro) {
        QueryWrapper<GermplasmIntro> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("germplasm_id", germplasmIntro.getGermplasmId())
                .eq("intro_name", germplasmIntro.getIntroName())
                .ne("intro_id", germplasmIntro.getIntroId());
        GermplasmIntro existIntro = germplasmIntroMapper.selectOne(queryWrapper);
        if (existIntro != null) {
            throw new BusException(CodeEnum.PLANT_NAME_EXIST);
        }

        String username = SecurityUtil.getUserName();
        germplasmIntro.setUpdateBy(username);
        germplasmIntro.setUpdateTime(LocalDateTime.now());
        return germplasmIntroMapper.updateById(germplasmIntro) > 0;
    }

    /**
     * 删除种质介绍
     * @param ids 介绍ID列表
     */
    public boolean deleteGermplasmIntro(List<Long> ids) {
        return germplasmIntroMapper.deleteBatchIds(ids) > 0;
    }
}


