import { http } from '@/utils/request'
import type { PageParams, PageResult } from '@/types/api'
import type {
  PlantTask,
  TaskBatch,
  Germplasm,
  GermplasmJob,
  GermplasmIntro,
  GermplasmMethod,
  Employee,
  Land,
  Material,
  MaterialType,
  Machine,
  MachineType,
  TaskEmployeeTime,
  TaskMachineTime,
  TaskMaterialTime
} from '@/types/plant'

// ========== 种植任务管理控制器 ==========

/**
 * 分页查询种植任务
 */
export const getTaskListApi = (params: PageParams & {
  taskName?: string
  batchId?: number
  status?: string
}): Promise<PageResult<PlantTask>> => {
  return http.get('/task/list', { params })
}

/**
 * 分页查询登录用户负责的种植任务（根据批次负责人）
 */
export const getUserTaskListApi = (params: PageParams & {
  batchHead: number
  batchId?: number
  taskName?: string
  status?: string
}): Promise<PageResult<PlantTask>> => {
  return http.get('/task/userList', { params })
}

/**
 * 根据ID查询种植任务详情
 */
export const getTaskByIdApi = (id: number): Promise<PlantTask> => {
  return http.get(`/task/getTaskById?id=${id}`)
}

/**
 * 新增种植任务
 */
export const addTaskApi = (task: Partial<PlantTask>): Promise<void> => {
  return http.post('/task/addTask', task)
}

/**
 * 修改种植任务
 */
export const updateTaskApi = (task: Partial<PlantTask>): Promise<void> => {
  return http.put('/task/updateTask', task)
}

/**
 * 删除种植任务
 */
export const deleteTaskApi = (ids: string): Promise<void> => {
  return http.delete(`/task/deleteTask?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 根据种植任务id获取参与任务的雇员列表
 */
export const getTaskEmployeesApi = (taskId: number): Promise<Employee[]> => {
  return http.get(`/task/getTaskEmployees?taskId=${taskId}`)
}

/**
 * 为种植任务新增雇员
 */
export const addTaskEmployeesApi = (params: {
  taskId: number
  employeeIds: string
}): Promise<void> => {
  const formData = new URLSearchParams()
  formData.append('taskId', params.taskId.toString())
  formData.append('employeeIds', params.employeeIds)
  
  return http.post('/task/addTaskEmployees', formData, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 为种植任务删除雇员
 */
export const removeTaskEmployeesApi = (params: {
  taskId: number
  employeeIds: string
}): Promise<void> => {
  const formData = new URLSearchParams()
  formData.append('taskId', params.taskId.toString())
  formData.append('employeeIds', params.employeeIds)
  
  return http.delete('/task/removeTaskEmployees', {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    data: formData
  })
}

/**
 * 根据种植任务id删除所有参与任务的雇员
 */
export const removeAllTaskEmployeesApi = (taskId: number): Promise<void> => {
  return http.delete(`/task/removeAllTaskEmployees?taskId=${taskId}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 机械类别管理控制器 ==========

/**
 * 分页查询机械类别
 */
export const getMachineTypeListApi = (params: PageParams & {
  machineTypeName?: string
}): Promise<PageResult<MachineType>> => {
  return http.get('/machineType/list', { params })
}

/**
 * 根据ID查询机械类别详情
 */
export const getMachineTypeByIdApi = (id: number): Promise<MachineType> => {
  return http.get(`/machineType/getMachineTypeById?id=${id}`)
}

/**
 * 新增机械类别
 */
export const addMachineTypeApi = (machineType: Partial<MachineType>): Promise<void> => {
  return http.post('/machineType/addMachineType', machineType)
}

/**
 * 修改机械类别
 */
export const updateMachineTypeApi = (machineType: Partial<MachineType>): Promise<void> => {
  return http.put('/machineType/updateMachineType', machineType)
}

/**
 * 删除机械类别
 */
export const deleteMachineTypeApi = (ids: string): Promise<void> => {
  return http.delete(`/machineType/deleteMachineType?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 标准作业任务管理控制器 ==========

/**
 * 分页查询作业任务
 */
export const getGermplasmJobListApi = (params: PageParams & {
  germplasmId?: number
  jobName?: string
}): Promise<PageResult<GermplasmJob>> => {
  return http.get('/germplasmJob/list', { params })
}

/**
 * 根据ID查询作业任务详情
 */
export const getGermplasmJobByIdApi = (id: number): Promise<GermplasmJob> => {
  return http.get(`/germplasmJob/getGermplasmJobById?id=${id}`)
}

/**
 * 新增作业任务
 */
export const addGermplasmJobApi = (job: Partial<GermplasmJob>): Promise<void> => {
  return http.post('/germplasmJob/addGermplasmJob', job)
}

/**
 * 修改作业任务
 */
export const updateGermplasmJobApi = (job: Partial<GermplasmJob>): Promise<void> => {
  return http.put('/germplasmJob/updateGermplasmJob', job)
}

/**
 * 删除作业任务
 */
export const deleteGermplasmJobApi = (ids: string): Promise<void> => {
  return http.delete(`/germplasmJob/deleteGermplasmJob?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 作物种植批次管理控制器 ==========

/**
 * 分页查询作物种植批次
 */
export const getTaskBatchListApi = (params: PageParams & {
  batchName?: string
  germplasmId?: number
  createBy?: number
}): Promise<PageResult<TaskBatch>> => {
  return http.get('/taskBatch/list', { params })
}

/**
 * 分页查询登录用户负责的作物种植批次
 */
export const getUserTaskBatchListApi = (params: PageParams & {
  batchHead: number
  batchName?: string
}): Promise<PageResult<TaskBatch>> => {
  return http.get('/taskBatch/userList', { params })
}

/**
 * 根据ID查询作物种植批次详情
 */
export const getTaskBatchByIdApi = (id: number): Promise<TaskBatch> => {
  return http.get(`/taskBatch/getTaskBatchById?id=${id}`)
}

/**
 * 新增作物种植批次
 */
export const addTaskBatchApi = (batch: Partial<TaskBatch>): Promise<void> => {
  return http.post('/taskBatch/addTaskBatch', batch)
}

/**
 * 修改作物种植批次
 */
export const updateTaskBatchApi = (batch: Partial<TaskBatch>): Promise<void> => {
  return http.put('/taskBatch/updateTaskBatch', batch)
}

/**
 * 删除作物种植批次
 */
export const deleteTaskBatchApi = (ids: string): Promise<void> => {
  return http.delete(`/taskBatch/deleteTaskBatch?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 农资信息管理控制器 ==========

/**
 * 分页查询农资信息
 */
export const getMaterialListApi = (params: PageParams & {
  materialName?: string
  materialCode?: string
  materialTypeId?: number
}): Promise<PageResult<Material>> => {
  return http.get('/material/list', { params })
}

/**
 * 根据ID查询农资信息详情
 */
export const getMaterialByIdApi = (id: number): Promise<Material> => {
  return http.get(`/material/getMaterialById?id=${id}`)
}

/**
 * 新增农资信息
 */
export const addMaterialApi = (material: Partial<Material>): Promise<void> => {
  return http.post('/material/addMaterial', material)
}

/**
 * 修改农资信息
 */
export const updateMaterialApi = (material: Partial<Material>): Promise<void> => {
  return http.put('/material/updateMaterial', material)
}

/**
 * 删除农资信息
 */
export const deleteMaterialApi = (ids: string): Promise<void> => {
  return http.delete(`/material/deleteMaterial?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 机械工时管理控制器 ==========

/**
 * 分页查询机械工时
 */
export const getTaskMachineTimeListApi = (params: PageParams & {
  taskId?: number
  machineId?: number
}): Promise<PageResult<TaskMachineTime>> => {
  return http.get('/taskMachineTime/list', { params })
}

/**
 * 根据ID查询机械工时详情
 */
export const getTaskMachineTimeByIdApi = (id: number): Promise<TaskMachineTime> => {
  return http.get(`/taskMachineTime/getTaskMachineTimeById?id=${id}`)
}

/**
 * 新增机械工时
 */
export const addTaskMachineTimeApi = (machineTime: Partial<TaskMachineTime>): Promise<void> => {
  return http.post('/taskMachineTime/addTaskMachineTime', machineTime)
}

/**
 * 修改机械工时
 */
export const updateTaskMachineTimeApi = (machineTime: Partial<TaskMachineTime>): Promise<void> => {
  return http.put('/taskMachineTime/updateTaskMachineTime', machineTime)
}

/**
 * 删除机械工时
 */
export const deleteTaskMachineTimeApi = (ids: string): Promise<void> => {
  return http.delete(`/taskMachineTime/deleteTaskMachineTime?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 人工工时管理控制器 ==========

/**
 * 分页查询人工工时
 */
export const getTaskEmployeeTimeListApi = (params: PageParams & {
  taskId?: number
  employeeId?: number
}): Promise<PageResult<TaskEmployeeTime>> => {
  return http.get('/taskEmployeeTime/list', { params })
}

/**
 * 根据ID查询人工工时详情
 */
export const getTaskEmployeeTimeByIdApi = (id: number): Promise<TaskEmployeeTime> => {
  return http.get(`/taskEmployeeTime/getTaskEmployeeTimeById?id=${id}`)
}

/**
 * 新增人工工时
 */
export const addTaskEmployeeTimeApi = (employeeTime: Partial<TaskEmployeeTime>): Promise<void> => {
  return http.post('/taskEmployeeTime/addTaskEmployeeTime', employeeTime)
}

/**
 * 修改人工工时
 */
export const updateTaskEmployeeTimeApi = (employeeTime: Partial<TaskEmployeeTime>): Promise<void> => {
  return http.put('/taskEmployeeTime/updateTaskEmployeeTime', employeeTime)
}

/**
 * 删除人工工时
 */
export const deleteTaskEmployeeTimeApi = (ids: string): Promise<void> => {
  return http.delete(`/taskEmployeeTime/deleteTaskEmployeeTime?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 种质管理控制器 ==========

/**
 * 分页查询种质
 */
export const getGermplasmListApi = (params: PageParams & {
  germplasmName?: string
  cropName?: string
}): Promise<PageResult<Germplasm>> => {
  return http.get('/germplasm/list', { params })
}

/**
 * 根据ID查询种质详情
 */
export const getGermplasmByIdApi = (id: number): Promise<Germplasm> => {
  return http.get(`/germplasm/getGermplasmById?id=${id}`)
}

/**
 * 新增种质
 */
export const addGermplasmApi = (germplasm: Partial<Germplasm>): Promise<void> => {
  return http.post('/germplasm/addGermplasm', germplasm)
}

/**
 * 修改种质
 */
export const updateGermplasmApi = (germplasm: Partial<Germplasm>): Promise<void> => {
  return http.put('/germplasm/updateGermplasm', germplasm)
}

/**
 * 删除种质
 */
export const deleteGermplasmApi = (ids: string): Promise<void> => {
  return http.delete(`/germplasm/deleteGermplasm?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 机械信息管理控制器 ==========

/**
 * 分页查询机械信息
 */
export const getMachineListApi = (params: PageParams & {
  machineName?: string
  machineCode?: string
  machineTypeId?: number
}): Promise<PageResult<Machine>> => {
  return http.get('/machine/list', { params })
}

/**
 * 根据ID查询机械信息详情
 */
export const getMachineByIdApi = (id: number): Promise<Machine> => {
  return http.get(`/machine/getMachineById?id=${id}`)
}

/**
 * 新增机械信息
 */
export const addMachineApi = (machine: Partial<Machine>): Promise<void> => {
  return http.post('/machine/addMachine', machine)
}

/**
 * 修改机械信息
 */
export const updateMachineApi = (machine: Partial<Machine>): Promise<void> => {
  return http.put('/machine/updateMachine', machine)
}

/**
 * 删除机械信息
 */
export const deleteMachineApi = (ids: string): Promise<void> => {
  return http.delete(`/machine/deleteMachine?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 种质介绍管理控制器 ==========

/**
 * 分页查询种质介绍
 */
export const getGermplasmIntroListApi = (params: PageParams & {
  germplasmId?: number
  introName?: string
}): Promise<PageResult<GermplasmIntro>> => {
  return http.get('/germplasmIntro/list', { params })
}

/**
 * 根据ID查询种质介绍详情
 */
export const getGermplasmIntroByIdApi = (id: number): Promise<GermplasmIntro> => {
  return http.get(`/germplasmIntro/getGermplasmIntroById?id=${id}`)
}

/**
 * 新增种质介绍
 */
export const addGermplasmIntroApi = (intro: Partial<GermplasmIntro>): Promise<void> => {
  return http.post('/germplasmIntro/addGermplasmIntro', intro)
}

/**
 * 修改种质介绍
 */
export const updateGermplasmIntroApi = (intro: Partial<GermplasmIntro>): Promise<void> => {
  return http.put('/germplasmIntro/updateGermplasmIntro', intro)
}

/**
 * 删除种质介绍
 */
export const deleteGermplasmIntroApi = (ids: string): Promise<void> => {
  return http.delete(`/germplasmIntro/deleteGermplasmIntro?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 雇员管理控制器 ==========

/**
 * 分页查询雇员
 */
export const getEmployeeListApi = (params: PageParams & {
  employeeName?: string
  employeeCode?: string
  employeeType?: string
}): Promise<PageResult<Employee>> => {
  return http.get('/employee/list', { params })
}

/**
 * 根据ID查询雇员详情
 */
export const getEmployeeByIdApi = (employeeId: number): Promise<Employee> => {
  return http.get('/employee/getEmployeeById', {
    params: { employeeId }
  })
}

/**
 * 新增雇员
 */
export const addEmployeeApi = (employee: Partial<Employee>): Promise<void> => {
  return http.post('/employee/addEmployee', employee)
}

/**
 * 修改雇员
 */
export const updateEmployeeApi = (employee: Partial<Employee>): Promise<void> => {
  return http.put('/employee/updateEmployee', employee)
}

/**
 * 删除雇员
 */
export const deleteEmployeeApi = (employeeId: string): Promise<void> => {
  return http.delete('/employee/deleteEmployee', {
    params: { employeeId },
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 地块管理控制器 ==========

/**
 * 分页查询地块
 */
export const getLandListApi = (params: PageParams & {
  landName?: string
  landType?: string
}): Promise<PageResult<Land>> => {
  return http.get('/land/list', { params })
}

/**
 * 根据ID查询地块详情
 */
export const getLandByIdApi = (id: number): Promise<Land> => {
  return http.get(`/land/getLandById?id=${id}`)
}

/**
 * 新增地块
 */
export const addLandApi = (land: Partial<Land>): Promise<void> => {
  return http.post('/land/addLand', land)
}

/**
 * 修改地块
 */
export const updateLandApi = (land: Partial<Land>): Promise<void> => {
  return http.put('/land/updateLand', land)
}

/**
 * 删除地块
 */
export const deleteLandApi = (ids: string): Promise<void> => {
  return http.delete(`/land/deleteLand?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 农资用量管理控制器 ==========

/**
 * 分页查询农资用量
 */
export const getTaskMaterialTimeListApi = (params: PageParams & {
  taskId?: number
  materialId?: number
}): Promise<PageResult<TaskMaterialTime>> => {
  return http.get('/taskMaterialTime/list', { params })
}

/**
 * 根据ID查询农资用量详情
 */
export const getTaskMaterialTimeByIdApi = (id: number): Promise<TaskMaterialTime> => {
  return http.get(`/taskMaterialTime/getTaskMaterialTimeById?id=${id}`)
}

/**
 * 新增农资用量
 */
export const addTaskMaterialTimeApi = (materialTime: Partial<TaskMaterialTime>): Promise<void> => {
  return http.post('/taskMaterialTime/addTaskMaterialTime', materialTime)
}

/**
 * 修改农资用量
 */
export const updateTaskMaterialTimeApi = (materialTime: Partial<TaskMaterialTime>): Promise<void> => {
  return http.put('/taskMaterialTime/updateTaskMaterialTime', materialTime)
}

/**
 * 删除农资用量
 */
export const deleteTaskMaterialTimeApi = (ids: string): Promise<void> => {
  return http.delete(`/taskMaterialTime/deleteTaskMaterialTime?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 种植方法管理控制器 ==========

/**
 * 分页查询种植方法
 */
export const getGermplasmMethodListApi = (params: PageParams & {
  germplasmId?: number
  methodName?: string
}): Promise<PageResult<GermplasmMethod>> => {
  return http.get('/germplasmMethod/list', { params })
}

/**
 * 根据ID查询种植方法详情
 */
export const getGermplasmMethodByIdApi = (id: number): Promise<GermplasmMethod> => {
  return http.get(`/germplasmMethod/getGermplasmMethodById?id=${id}`)
}

/**
 * 新增种植方法
 */
export const addGermplasmMethodApi = (method: Partial<GermplasmMethod>): Promise<void> => {
  return http.post('/germplasmMethod/addGermplasmMethod', method)
}

/**
 * 修改种植方法
 */
export const updateGermplasmMethodApi = (method: Partial<GermplasmMethod>): Promise<void> => {
  return http.put('/germplasmMethod/updateGermplasmMethod', method)
}

/**
 * 删除种植方法
 */
export const deleteGermplasmMethodApi = (ids: string): Promise<void> => {
  return http.delete(`/germplasmMethod/deleteGermplasmMethod?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 农资类别管理控制器 ==========

/**
 * 分页查询农资类别
 */
export const getMaterialTypeListApi = (params: PageParams & {
  materialTypeName?: string
}): Promise<PageResult<MaterialType>> => {
  return http.get('/materialType/list', { params })
}

/**
 * 根据ID查询农资类别详情
 */
export const getMaterialTypeByIdApi = (id: number): Promise<MaterialType> => {
  return http.get(`/materialType/getMaterialTypeById?id=${id}`)
}

/**
 * 新增农资类别
 */
export const addMaterialTypeApi = (materialType: Partial<MaterialType>): Promise<void> => {
  return http.post('/materialType/addMaterialType', materialType)
}

/**
 * 修改农资类别
 */
export const updateMaterialTypeApi = (materialType: Partial<MaterialType>): Promise<void> => {
  return http.put('/materialType/updateMaterialType', materialType)
}

/**
 * 删除农资类别
 */
export const deleteMaterialTypeApi = (ids: string): Promise<void> => {
  return http.delete(`/materialType/deleteMaterialType?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}
