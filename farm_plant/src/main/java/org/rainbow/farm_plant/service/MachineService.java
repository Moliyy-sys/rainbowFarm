package org.rainbow.farm_plant.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.CodeEnum;
import org.rainbow.farm_common.util.SecurityUtil;
import org.rainbow.farm_plant.entity.Machine;
import org.rainbow.farm_plant.mapper.MachineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 农机信息服务
 */
@Service
@Transactional
public class MachineService {

    @Autowired
    private MachineMapper machineMapper;

    /**
     * 分页查询农机信息
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param machineTypeId 农机类别ID
     * @param machineName 农机名称
     * @param machineCode 农机编码
     * @return 农机信息分页数据
     */
    public IPage<Machine> selectPage(Integer pageNum, Integer pageSize, Long machineTypeId, String machineName, String machineCode) {
        Page<Machine> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Machine> wrapper = new QueryWrapper<>();
        if (machineTypeId != null) {
            wrapper.eq("machine_type_id", machineTypeId);
        }
        if (StringUtils.hasText(machineName)) {
            wrapper.like("machine_name", machineName);
        }
        if (StringUtils.hasText(machineCode)) {
            wrapper.like("machine_code", machineCode);
        }
        wrapper.orderByDesc("create_time");
        return machineMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询农机信息详情
     * @param machineId 农机ID
     * @return 农机详情
     */
    public Machine getMachineById(Long machineId) {
        return machineMapper.selectById(machineId);
    }

    /**
     * 新增农机信息
     * @param machine 农机信息
     * @return 操作结果
     */
    public boolean addMachine(Machine machine) {
        // 检查农机编码是否已存在
        QueryWrapper<Machine> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("machine_code", machine.getMachineCode());
        Machine existMachine = machineMapper.selectOne(queryWrapper);
        if (existMachine != null) {
            throw new BusException(CodeEnum.PLANT_CODE_EXIST);
        }

        String username = SecurityUtil.getUserName();
        machine.setCreateBy(username);
        machine.setCreateTime(LocalDateTime.now());
        machine.setUpdateBy(username);
        machine.setUpdateTime(LocalDateTime.now());
        return machineMapper.insert(machine) > 0;
    }

    /**
     * 修改农机信息
     * @param machine 农机信息
     * @return 操作结果
     */
    public boolean updateMachine(Machine machine) {
        // 检查农机编码是否已存在
        QueryWrapper<Machine> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("machine_code", machine.getMachineCode())
                .ne("machine_id", machine.getMachineId());
        Machine existMachine = machineMapper.selectOne(queryWrapper);
        if (existMachine != null) {
            throw new BusException(CodeEnum.PLANT_CODE_EXIST);
        }

        String username = SecurityUtil.getUserName();
        machine.setUpdateBy(username);
        machine.setUpdateTime(LocalDateTime.now());
        return machineMapper.updateById(machine) > 0;
    }

    /**
     * 删除农机信息
     * @param machineIds 农机ID集合
     * @return 操作结果
     */
    public boolean deleteMachine(List<Long> machineIds) {
        return machineMapper.deleteBatchIds(machineIds) > 0;
    }
}


