package org.rainbow.farm_plant.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.CodeEnum;
import org.rainbow.farm_common.util.SecurityUtil;
import org.rainbow.farm_plant.entity.Material;
import org.rainbow.farm_plant.mapper.MaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 农资信息服务
 */
@Service
@Transactional
public class MaterialService {

    @Autowired
    private MaterialMapper materialMapper;

    /**
     * 分页查询农资信息
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param materialTypeId 农资类别ID
     * @param materialName 农资名称
     * @param materialCode 农资编码
     * @return 农资信息分页数据
     */
    public IPage<Material> selectPage(Integer pageNum, Integer pageSize, Long materialTypeId, String materialName,String materialCode) {
        Page<Material> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Material> wrapper = new QueryWrapper<>();
        if (materialTypeId != null) {
            wrapper.eq("material_type_id", materialTypeId);
        }
        if (StringUtils.hasText(materialName)) {
            wrapper.like("material_name", materialName);
        }
        if (StringUtils.hasText(materialCode)) {
            wrapper.like("material_code", materialCode);
        }
        wrapper.orderByDesc("create_time");
        return materialMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询农资信息详情
     * @param materialId 农资ID
     * @return 农资详情
     */
    public Material getMaterialById(Long materialId) {
        return materialMapper.selectById(materialId);
    }

    /**
     * 新增农资信息
     * @param material 农资信息
     * @return 操作结果
     */
    public boolean addMaterial(Material material) {
        // 检查农资编码是否已存在
        QueryWrapper<Material> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_code", material.getMaterialCode());
        Material existMaterial = materialMapper.selectOne(queryWrapper);
        if (existMaterial != null) {
            throw new BusException(CodeEnum.PLANT_CODE_EXIST);
        }

        String username = SecurityUtil.getUserName();
        material.setCreateBy(username);
        material.setCreateTime(LocalDateTime.now());
        material.setUpdateBy(username);
        material.setUpdateTime(LocalDateTime.now());
        return materialMapper.insert(material) > 0;
    }

    /**
     * 修改农资信息
     * @param material 农资信息
     * @return 操作结果
     */
    public boolean updateMaterial(Material material) {
        QueryWrapper<Material> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_code", material.getMaterialCode())
                .ne("material_id", material.getMaterialId());
        Material existMaterial = materialMapper.selectOne(queryWrapper);
        if (existMaterial != null) {
            throw new BusException(CodeEnum.PLANT_CODE_EXIST);
        }

        String username = SecurityUtil.getUserName();
        material.setUpdateBy(username);
        material.setUpdateTime(LocalDateTime.now());
        return materialMapper.updateById(material) > 0;
    }

    /**
     * 删除农资信息
     * @param materialIds 农资ID集合
     * @return 操作结果
     */
    public boolean deleteMaterial(List<Long> materialIds) {
        return materialMapper.deleteBatchIds(materialIds) > 0;
    }
}


