package org.rainbow.farm_plant.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.CodeEnum;
import org.rainbow.farm_common.util.SecurityUtil;
import org.rainbow.farm_plant.entity.MachineType;
import org.rainbow.farm_plant.mapper.MachineTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 农机类别服务
 */
@Service
@Transactional
public class MachineTypeService {

    @Autowired
    private MachineTypeMapper machineTypeMapper;

    /**
     * 分页查询农机类别
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param machineTypeName 农机类别名称
     * @return 农机类别分页数据
     */
    public IPage<MachineType> selectPage(Integer pageNum, Integer pageSize, String machineTypeName) {
        Page<MachineType> page = new Page<>(pageNum, pageSize);
        QueryWrapper<MachineType> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(machineTypeName)) {
            wrapper.like("machine_type_name", machineTypeName);
        }
        wrapper.orderByDesc("create_time");
        return machineTypeMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询农机类别详情
     * @param machineTypeId 农机类别ID
     * @return 农机类别详情
     */
    public MachineType getMachineTypeById(Long machineTypeId) {
        return machineTypeMapper.selectById(machineTypeId);
    }

    /**
     * 新增农机类别
     * @param machineType 农机类别信息
     * @return 操作结果
     */
    public boolean addMachineType(MachineType machineType) {
        // 判断农机类别名称是否存在
        QueryWrapper<MachineType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("machine_type_name", machineType.getMachineTypeName());
        MachineType existType = machineTypeMapper.selectOne(queryWrapper);
        if (existType != null) {
            throw new BusException(CodeEnum.PLANT_NAME_EXIST);
        }

        String username = SecurityUtil.getUserName();
        machineType.setCreateBy(username);
        machineType.setCreateTime(LocalDateTime.now());
        machineType.setUpdateBy(username);
        machineType.setUpdateTime(LocalDateTime.now());

        return machineTypeMapper.insert(machineType) > 0;
    }

    /**
     * 修改农机类别
     * @param machineType 农机类别信息
     * @return 操作结果
     */
    public boolean updateMachineType(MachineType machineType) {
        // 判断农机类别名称是否存在
        QueryWrapper<MachineType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("machine_type_name", machineType.getMachineTypeName())
                .ne("machine_type_id", machineType.getMachineTypeId());
        MachineType existType = machineTypeMapper.selectOne(queryWrapper);
        if (existType != null) {
            throw new BusException(CodeEnum.PLANT_NAME_EXIST);
        }

        String username = SecurityUtil.getUserName();
        machineType.setUpdateBy(username);
        machineType.setUpdateTime(LocalDateTime.now());
        return machineTypeMapper.updateById(machineType) > 0;
    }

    /**
     * 删除农机类别
     * @param machineTypeIds 农机类别ID集合
     * @return 操作结果
     */
    public boolean deleteMachineType(List<Long> machineTypeIds) {
        return machineTypeMapper.deleteBatchIds(machineTypeIds) > 0;
    }
}


