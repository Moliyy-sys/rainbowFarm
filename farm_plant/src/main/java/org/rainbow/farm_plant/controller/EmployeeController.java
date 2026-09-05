package org.rainbow.farm_plant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.rainbow.farm_common.result.BaseResult;
import org.rainbow.farm_plant.entity.Employee;
import org.rainbow.farm_plant.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 雇员Controller
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 分页查询雇员
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param employeeName 雇员姓名
     * @param employeeCode 雇员编号
     * @param employeeType 雇员类型
     * @return 雇员分页数据
     */
    @GetMapping("/list")
    public BaseResult<IPage<Employee>> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                            @RequestParam(value = "employeeName", required = false) String employeeName,
                                            @RequestParam(value = "employeeCode", required = false) String employeeCode,
                                            @RequestParam(value = "employeeType", required = false) String employeeType) {
        IPage<Employee> employeePage = employeeService.selectPage(pageNum, pageSize, employeeName,employeeCode,employeeType);
        return BaseResult.success(employeePage);
    }

    /**
     * 根据ID查询雇员详情
     * @param employeeId 雇员ID
     * @return 雇员详情
     */
    @GetMapping("/getEmployeeById")
    public BaseResult<Employee> detail(@RequestParam("employeeId") Long employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        return BaseResult.success(employee);
    }

    /**
     * 新增雇员
     * @param employee 雇员信息
     * @return 操作结果
     */
    @PostMapping("/addEmployee")
    public BaseResult<?> add(@RequestBody Employee employee) {
        employeeService.addEmployee(employee);
        return BaseResult.success();
    }

    /**
     * 修改雇员
     * @param employee 雇员信息
     * @return 操作结果
     */
    @PutMapping("/updateEmployee")
    public BaseResult<?> update(@RequestBody Employee employee) {
        employeeService.updateEmployee(employee);
        return BaseResult.success();
    }

    /**
     * 删除雇员
     * @param ids 雇员ID字符串，多个ID用逗号分割
     * @return 操作结果
     */
    @DeleteMapping("/deleteEmployee")
    public BaseResult<?> delete(@RequestParam("employeeId") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        employeeService.deleteEmployee(idList);
        return BaseResult.success();
    }
}


