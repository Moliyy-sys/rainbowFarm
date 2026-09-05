package org.rainbow.farm_plant.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.CodeEnum;
import org.rainbow.farm_common.util.SecurityUtil;
import org.rainbow.farm_plant.entity.GermplasmMethod;
import org.rainbow.farm_plant.mapper.GermplasmMethodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 种植方法服务
 */
@Service
@Transactional
public class GermplasmMethodService {

    @Autowired
    private GermplasmMethodMapper germplasmMethodMapper;

    /**
     * 分页查询种植方法
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param germplasmId 种质ID
     * @param methodName 方法名称
     * @return 种植方法分页数据
     */
    public IPage<GermplasmMethod> selectPage(Integer pageNum, Integer pageSize, Long germplasmId, String methodName) {
        Page<GermplasmMethod> page = new Page<>(pageNum, pageSize);
        QueryWrapper<GermplasmMethod> wrapper = new QueryWrapper<>();
        if (germplasmId != null) {
            wrapper.eq("germplasm_id", germplasmId);
        }
        if (StringUtils.hasText(methodName)) {
            wrapper.like("method_name", methodName);
        }
        wrapper.orderByDesc("create_time");
        return germplasmMethodMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询种植方法详情
     * @param methodId 种植方法ID
     * @return 种植方法详情
     */
    public GermplasmMethod getGermplasmMethodById(Long methodId) {
        return germplasmMethodMapper.selectById(methodId);
    }

    /**
     * 新增种植方法
     * @param germplasmMethod 种植方法信息
     */
    public boolean addGermplasmMethod(GermplasmMethod germplasmMethod) {
        QueryWrapper<GermplasmMethod> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("germplasm_id", germplasmMethod.getGermplasmId())
                .eq("method_name", germplasmMethod.getMethodName());
        GermplasmMethod existMethod = germplasmMethodMapper.selectOne(queryWrapper);
        if (existMethod != null) {
            throw new BusException(CodeEnum.PLANT_NAME_EXIST);
        }

        String username = SecurityUtil.getUserName();
        germplasmMethod.setCreateBy(username);
        germplasmMethod.setCreateTime(LocalDateTime.now());
        germplasmMethod.setUpdateBy(username);
        germplasmMethod.setUpdateTime(LocalDateTime.now());
        return germplasmMethodMapper.insert(germplasmMethod) > 0;
    }

    /**
     * 修改种植方法
     * @param germplasmMethod 种植方法信息
     */
    public boolean updateGermplasmMethod(GermplasmMethod germplasmMethod) {
        QueryWrapper<GermplasmMethod> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("germplasm_id", germplasmMethod.getGermplasmId())
                .eq("method_name", germplasmMethod.getMethodName())
                .ne("method_id", germplasmMethod.getMethodId());
        GermplasmMethod existMethod = germplasmMethodMapper.selectOne(queryWrapper);
        if (existMethod != null) {
            throw new BusException(CodeEnum.PLANT_NAME_EXIST);
        }

        String username = SecurityUtil.getUserName();
        germplasmMethod.setUpdateBy(username);
        germplasmMethod.setUpdateTime(LocalDateTime.now());
        return germplasmMethodMapper.updateById(germplasmMethod) > 0;
    }

    /**
     * 删除种植方法
     * @param ids 种植方法ID列表
     */
    public boolean deleteGermplasmMethod(List<Long> ids) {
        return germplasmMethodMapper.deleteBatchIds(ids) > 0;
    }
}


