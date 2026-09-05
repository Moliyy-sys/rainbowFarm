package org.rainbow.farm_plant.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.rainbow.farm_common.util.SecurityUtil;
import org.rainbow.farm_plant.entity.TaskEmployeeTime;
import org.rainbow.farm_plant.mapper.TaskEmployTimeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 人工工时表服务类
 */
@Service
@Transactional
public class TaskEmployeeTimeService {
    @Autowired
    private TaskEmployTimeMapper taskEmployTimeMapper;

    /**
     * 分页查询人工工时
     * @param page 页码
     * @param size 每页数量
     * @param taskId 任务ID，可选
     * @param employeeId 员工ID，可选
     * @return 人工工时分页结果
     */
    public IPage<TaskEmployeeTime> findTaskEmployeeTimePage(int page,int size,Long taskId,Long employeeId) {
        Page<TaskEmployeeTime> pageObj = new Page<>(page, size);
        QueryWrapper<TaskEmployeeTime> queryWrapper = new QueryWrapper<>();

        if (taskId != null) {
            queryWrapper.eq("task_id", taskId);
        }
        if (employeeId != null) {
            queryWrapper.eq("employee_id", employeeId);
        }
        queryWrapper.orderByDesc("create_time");
        return taskEmployTimeMapper.selectPage(pageObj, queryWrapper);
    }

    /**
     * 根据ID查询人工工时
     * @param id 工人工时ID
     * @return 人工工时实体
     */
    public TaskEmployeeTime findById(Long id) {
        return taskEmployTimeMapper.selectById(id);
    }

    /**
     * 添加人工工时
     * @param taskEmployeeTime 工人工时实体
     * @return 添加结果
     */
    public boolean add(TaskEmployeeTime taskEmployeeTime) {
        String username = SecurityUtil.getUserName();
        taskEmployeeTime.setCreateTime(LocalDateTime.now());
        taskEmployeeTime.setCreateBy(username);
        taskEmployeeTime.setUpdateTime(LocalDateTime.now());
        taskEmployeeTime.setUpdateBy(username);

        return taskEmployTimeMapper.insert(taskEmployeeTime) > 0;
    }

    /**
     * 修改人工工时
     * @param taskEmployeeTime 工人工时实体
     * @return 修改结果
     */
    public boolean update(TaskEmployeeTime taskEmployeeTime) {
        String username = SecurityUtil.getUserName();
        taskEmployeeTime.setUpdateTime(LocalDateTime.now());
        taskEmployeeTime.setUpdateBy(username);
        return taskEmployTimeMapper.updateById(taskEmployeeTime) > 0;
    }

    /**
     * 删除人工工时
     * @param ids 工人工时ID列表
     * @return 删除结果
     */
    public boolean delete(List<Long> ids) {
        return taskEmployTimeMapper.deleteBatchIds(ids) > 0;
    }

}
