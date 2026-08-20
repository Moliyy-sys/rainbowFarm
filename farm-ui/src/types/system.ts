import type { PageParams, PageResult } from './api'

// 用户相关类型
export interface User {
  userId: number
  userName: string
  nickName: string
  email: string
  phonenumber: string
  sex: string // 0男 1女 2未知
  avatar: string
  password?: string
  status: string // 0正常 1停用
  loginIp?: string
  loginDate?: string
  pwdUpdateDate?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  remark?: string
  roles?: Role[]
  roleIds?: number[] // 角色ID列表
}

// 角色相关类型
export interface Role {
  roleId: number
  roleName: string
  roleSort: number
  status: string // 0正常 1停用
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  remark?: string
  permissions?: Permission[]
}

// 权限相关类型
export interface Permission {
  id?: number
  permissionId: number
  permissionName: string
  name?: string
  code?: string
  type?: 'menu' | 'button' | 'api'
  parentId?: number | null
  path?: string
  component: string
  icon?: string
  sort?: number
  hidden?: boolean
  keepAlive?: boolean
  status?: number
  description?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  remark?: string
  children?: Permission[]
  hasChildren?: boolean
}

// 字典类型相关类型
export interface DictType {
  dictId: number
  dictName: string
  dictType: string // 字典编码/类型
  status: string // 0正常 1停用
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  remark?: string
}

// 字典数据相关类型
export interface DictData {
  dictCode: number
  dictSort: number
  dictLabel: string
  dictValue: string
  dictType: string
  isDefault: string // Y是 N否
  status: string // 0正常 1停用
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  remark?: string
}

// 登录信息相关类型
export interface LoginInfo {
  id: number
  infoId?: number
  username: string
  userName?: string
  realName?: string
  loginIp: string
  ipaddr?: string
  loginLocation?: string
  browser: string
  os: string
  status: string // success成功 fail失败
  message: string
  msg?: string
  loginTime: string
  userAgent?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  remark?: string
}

// 操作日志相关类型
export interface OperLog {
  operId: number
  title: string
  businessType: 'OTHER' | 'INSERT' | 'UPDATE' | 'DELETE'
  method: string
  requestMethod: string
  operName: string
  operUrl: string
  operIp: string
  operParam: string
  jsonResult: string
  status: number // 0正常 1异常
  errorMsg: string
  operTime: string
  costTime: number
}

// 操作日志分页响应
export interface OperLogPageResult {
  records: OperLog[]
  total: number
  size: number
  current: number
  orders: Array<{
    column: string
    asc: boolean
  }>
  optimizeCountSql: boolean
  searchCount: boolean
  optimizeJoinOfCountSql: boolean
  maxLimit: number
  countId: string
}

// 查询参数类型
export interface UserListParams extends PageParams {
  userName?: string
  status?: string
}

export interface RoleListParams extends PageParams {
  roleName?: string
  status?: string
}

export interface PermissionListParams extends PageParams {
  permissionName?: string
  name?: string
  code?: string
  type?: string
  status?: string
}

export interface DictTypeListParams extends PageParams {
  dictName?: string
  status?: string
}

export interface DictDataListParams extends PageParams {
  dictLabel?: string
  dictType?: number
  status?: string
}

export interface LoginInfoListParams extends PageParams {
  userName?: string
  status?: string
  ipaddr?: string
  beginTime?: string
  endTime?: string
}

export interface OperLogListParams extends PageParams {
  title?: string
  businessType?: 'OTHER' | 'INSERT' | 'UPDATE' | 'DELETE'
  operName?: string
  requestMethod?: string
  operIp?: string
  status?: string
  startTime?: string
  endTime?: string
}

export interface AssignedUsersParams extends PageParams {
  roleId: number
  userName?: string
}

export interface UnassignedUsersParams extends PageParams {
  roleId: number
  userName?: string
}

// 密码修改参数
export interface ChangePasswordParams {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}

// 重置密码参数
export interface ResetPasswordParams {
  userId: number
  newPassword: string
}

// 修改用户状态参数
export interface ChangeUserStatusParams {
  userId: number
  status: string
}

// 修改角色状态参数
export interface ChangeRoleStatusParams {
  roleId: number
  status: string
}

// 分配角色参数
export interface AssignRolesParams {
  userId: number
  roleIds?: string
}

// 分配权限参数
export interface AssignPermissionsParams {
  roleId: number
  permissionIds: string
}

// 角色用户分配参数
export interface AssignRoleToUsersParams {
  roleId: number
  userIds: string
}

// 角色用户取消参数
export interface CancelRoleFromUsersParams {
  roleId: number
  userIds: string
}
