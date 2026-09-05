package org.rainbow.farm_plant.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.rainbow.farm_common.exception.BusException;
import org.rainbow.farm_common.result.CodeEnum;
import org.rainbow.farm_common.util.SecurityUtil;
import org.rainbow.farm_plant.entity.Employee;
import org.rainbow.farm_plant.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 雇员服务
 */
@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 分页查询雇员
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param employeeName 雇员姓名
     * @param employeeCode 雇员编号
     * @param employeeType 雇员类型
     * @return 雇员分页数据
     */
    public IPage<Employee> selectPage(Integer pageNum, Integer pageSize, String employeeName, String employeeCode, String employeeType) {
        Page<Employee> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(employeeName)) {
            wrapper.like("employee_name", employeeName);
        }
        if (StringUtils.hasText(employeeCode)) {
            wrapper.eq("employee_code", employeeCode);
        }
        if (StringUtils.hasText(employeeType)) {
            wrapper.eq("employee_type", employeeType);
        }
        wrapper.orderByDesc("create_time");
        return employeeMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询雇员详情
     * @param employeeId 雇员ID
     * @return 雇员详情
     */
    public Employee getEmployeeById(Long employeeId) {
        return employeeMapper.selectById(employeeId);
    }

    /**
     * 新增雇员
     * @param employee 雇员信息
     * @return 新增结果
     */
    public boolean addEmployee(Employee employee) {
        // 检查雇员编号是否已存在
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.eq("employee_code", employee.getEmployeeCode());
        Employee existEmployee = employeeMapper.selectOne(wrapper);
        if (existEmployee != null) {
            throw new BusException(CodeEnum.PLANT_CODE_EXIST);
        }
        String username = SecurityUtil.getUserName();
        employee.setCreateBy(username);
        employee.setUpdateBy(username);
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        return employeeMapper.insert(employee) > 0;
    }

    /**
     * 修改雇员
     * @param employee 雇员信息
     * @return 新增结果
     */
    public boolean updateEmployee(Employee employee) {
        // 检查雇员编号是否已存在
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.eq("employee_code", employee.getEmployeeCode())
                .ne("employee_id", employee.getEmployeeId());
        Employee existEmployee = employeeMapper.selectOne(wrapper);
        if (existEmployee != null) {
            throw new BusException(CodeEnum.PLANT_CODE_EXIST);
        }
        String username = SecurityUtil.getUserName();
        employee.setUpdateBy(username);
        employee.setUpdateTime(LocalDateTime.now());

        return employeeMapper.updateById(employee) > 0;
    }

    /**
     * 删除雇员
     * @param ids 雇员ID列表
     * @return 删除结果
     */
    public boolean deleteEmployee(List<Long> ids) {
        return employeeMapper.deleteBatchIds(ids) > 0;
    }
}


