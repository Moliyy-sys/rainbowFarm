package org.rainbow.farm_plant.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.CodeEnum;
import org.rainbow.farm_common.util.SecurityUtil;
import org.rainbow.farm_plant.entity.MaterialType;
import org.rainbow.farm_plant.mapper.MaterialTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 农资类别服务
 */
@Service
@Transactional
public class MaterialTypeService {

    @Autowired
    private MaterialTypeMapper materialTypeMapper;

    /**
     * 分页查询农资类别
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param materialTypeName 农资类别名称
     * @return 农资类别分页数据
     */
    public IPage<MaterialType> selectPage(Integer pageNum, Integer pageSize, String materialTypeName) {
        Page<MaterialType> page = new Page<>(pageNum, pageSize);
        QueryWrapper<MaterialType> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(materialTypeName)) {
            wrapper.like("material_type_name", materialTypeName);
        }
        wrapper.orderByDesc("create_time");
        return materialTypeMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询农资类别详情
     * @param materialTypeId 农资类别ID
     * @return 农资类别详情
     */
    public MaterialType getMaterialTypeById(Long materialTypeId) {
        return materialTypeMapper.selectById(materialTypeId);
    }

    /**
     * 新增农资类别
     * @param materialType 农资类别信息
     * @return 操作结果
     */
    public boolean addMaterialType(MaterialType materialType) {
        QueryWrapper<MaterialType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_type_name", materialType.getMaterialTypeName());
        MaterialType existType = materialTypeMapper.selectOne(queryWrapper);
        if (existType != null) {
            throw new BusException(CodeEnum.PLANT_NAME_EXIST);
        }
        String username = SecurityUtil.getUserName();
        materialType.setCreateBy(username);
        materialType.setCreateTime(LocalDateTime.now());
        materialType.setUpdateBy(username);
        materialType.setUpdateTime(LocalDateTime.now());
        return materialTypeMapper.insert(materialType) > 0;
    }

    /**
     * 修改农资类别
     * @param materialType 农资类别信息
     * @return 操作结果
     */
    public boolean updateMaterialType(MaterialType materialType) {
        QueryWrapper<MaterialType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_type_name", materialType.getMaterialTypeName())
                .ne("material_type_id", materialType.getMaterialTypeId());
        MaterialType existType = materialTypeMapper.selectOne(queryWrapper);
        if (existType != null) {
            throw new BusException(CodeEnum.PLANT_NAME_EXIST);
        }
        String username = SecurityUtil.getUserName();
        materialType.setUpdateBy(username);
        materialType.setUpdateTime(LocalDateTime.now());
        return materialTypeMapper.updateById(materialType) > 0;
    }

    /**
     * 删除农资类别
     * @param materialTypeIds 农资类别ID集合
     * @return 操作结果
     */
    public boolean deleteMaterialType(List<Long> materialTypeIds) {
        return materialTypeMapper.deleteBatchIds(materialTypeIds) > 0;
    }
}


