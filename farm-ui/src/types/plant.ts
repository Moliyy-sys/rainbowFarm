// 种植管理相关类型定义

// 种植任务
export interface PlantTask {
  taskId: number
  batchId: number
  taskHead: number
  taskHeadName: string
  taskName: string
  planStart: string
  planFinish: string
  actualStart: string
  actualFinish: string
  taskDetail: string
  taskImages: string
  taskVideos: string
  remark: string
  status: string // 0未分配 1已分配 2进行中 3已完成
  createBy: number
  createTime: string
  updateBy: number
  updateTime: string
  employeeList: Employee[]
}

// 雇员信息
export interface Employee {
  employeeId: number
  employeeCode: string
  employeeName: string
  employeeType: string
  employeeTel: string
  employeeSex: string
  employeeAddress: string
  remark: string
  createBy: number
  createTime: string
  updateBy: number
  updateTime: string
}

// 种植批次
export interface TaskBatch {
  batchId: number
  batchName: string
  germplasmId: number
  landId: number
  cropArea: number
  startTime: string
  batchHead: number
  remark: string
  createBy: number
  createTime: string
  updateBy: number
  updateTime: string
}

// 种质信息
export interface Germplasm {
  germplasmId: number
  cropName: string
  cropEnName: string
  germplasmName: string
  germplasmEnName: string
  germplasmImg: string
  germplasmDes: string
  remark: string
  createBy: number
  createTime: string
  updateBy: number
  updateTime: string
}

// 地块信息
export interface Land {
  landId?: number
  landName: string
  landType: string
  landArea: number
  strokeWeight?: number
  strokeColor?: string
  strokeOpacity?: number
  landPath?: string
  fillColor?: string
  fillOpacity?: number
  currentBatch?: number
  remark?: string
  createBy?: number
  createTime?: string
  updateBy?: number
  updateTime?: string
}

// 农资信息
export interface Material {
  materialId: number
  materialCode: string
  materialName: string
  materialTypeId: number
  measureUnit: string
  remark: string
  createBy: number
  createTime: string
  updateBy: number
  updateTime: string
}

// 农资类别
export interface MaterialType {
  materialTypeId: number
  materialTypeName: string
  remark: string
  status: string
  orderNum: number
  createBy: number
  createTime: string
  updateBy: number
  updateTime: string
}

// 机械信息
export interface Machine {
  machineId: number
  machineCode: string
  machineName: string
  machineTypeId: number
  measureUnit: string
  remark: string
  createBy: number
  createTime: string
  updateBy: number
  updateTime: string
}

// 机械类别
export interface MachineType {
  machineTypeId: number
  machineTypeName: string
  remark: string
  createBy: number
  createTime: string
  updateBy: number
  updateTime: string
}

// 人工工时
export interface TaskEmployeeTime {
  costId: number
  taskId: number
  employeeId: number
  workingHours: number
  workingStart: string
  workingFinish: string
  remark: string
  createBy: number
  createTime: string
  updateBy: number
  updateTime: string
}

// 机械工时
export interface TaskMachineTime {
  costId: number
  taskId: number
  machineId: number
  machineCount: number
  workingHours: number
  workingStart: string
  workingFinish: string
  remark: string
  createBy: number
  createTime: string
  updateBy: number
  updateTime: string
}

// 农资用量
export interface TaskMaterialTime {
  costId: number
  taskId: number
  materialId: number
  materialCount: number
  measureUnit: string
  workingStart: string
  workingFinish: string
  remark: string
  createBy: number
  createTime: string
  updateBy: number
  updateTime: string
}

// 标准作业任务
export interface GermplasmJob {
  jobId: number
  germplasmId: number
  jobName: string
  cycleUnit: string // 0代表周 1代表天
  jobStart: number
  jobFinish: number
  remark: string
  createBy: number
  createTime: string
  updateBy: number
  updateTime: string
}

// 种质介绍
export interface GermplasmIntro {
  introId: number
  germplasmId: number
  introName: string
  introDes: string
  remark: string
  createBy: number
  createTime: string
  updateBy: number
  updateTime: string
}

// 种植方法
export interface GermplasmMethod {
  methodId: number
  germplasmId: number
  methodName: string
  methodImg: string
  methodDes: string
  remark: string
  createBy: number
  createTime: string
  updateBy: number
  updateTime: string
}
