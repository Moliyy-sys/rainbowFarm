package org.rainbow.farm_plant.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.CodeEnum;
import org.rainbow.farm_common.util.SecurityUtil;
import org.rainbow.farm_plant.entity.Land;
import org.rainbow.farm_plant.mapper.LandMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地块服务
 */
@Service
@Transactional
public class LandService {

    @Autowired
    private LandMapper landMapper;

    /**
     * 分页查询地块
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param landName 地块名称
     * @param landType 地块类型
     * @return 地块分页数据
     */
    public IPage<Land> selectPage(Integer pageNum, Integer pageSize, String landName, String landType) {
        Page<Land> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Land> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(landName)) {
            wrapper.like("land_name", landName);
        }
        if (StringUtils.hasText(landType)) {
            wrapper.eq("land_type", landType);
        }
        wrapper.orderByDesc("create_time");
        return landMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询地块详情
     * @param landId 地块ID
     * @return 地块详情
     */
    public Land getLandById(Long landId) {
        return landMapper.selectById(landId);
    }

    /**
     * 新增地块
     * @param land 地块信息
     * @return 操作结果
     */
    public boolean addLand(Land land) {
        // 检查地块名称是否存在
        QueryWrapper<Land> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("land_name", land.getLandName());
        Land existLand = landMapper.selectOne(queryWrapper);
        if (existLand != null){
            throw new BusException(CodeEnum.PLANT_NAME_EXIST);
        }
        String username = SecurityUtil.getUserName();
        land.setCreateBy(username);
        land.setCreateTime(LocalDateTime.now());
        land.setUpdateBy(username);
        land.setUpdateTime(LocalDateTime.now());

        return landMapper.insert(land) > 0;
    }

    /**
     * 修改地块
     * @param land 地块信息
     * @return 操作结果
     */
    public boolean updateLand(Land land) {
        // 检查地块名称是否存在(排除自己)
        QueryWrapper<Land> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("land_name", land.getLandName())
                .ne("land_id", land.getLandId());
        Land existLand = landMapper.selectOne(queryWrapper);
        if (existLand != null){
            throw new BusException(CodeEnum.PLANT_NAME_EXIST);
        }
        String username = SecurityUtil.getUserName();
        land.setUpdateBy(username);
        land.setUpdateTime(LocalDateTime.now());
        return landMapper.updateById(land) > 0;
    }

    /**
     * 删除地块
     * @param ids 地块ID列表
     * @return 操作结果
     */
    public boolean deleteLand(List<Long> ids) {
        return landMapper.deleteBatchIds(ids) > 0;
    }
}


