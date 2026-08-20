import { http } from '@/utils/request'
import type { PageResult } from '@/types/api'
import type {
  User,
  Role,
  Permission,
  DictType,
  DictData,
  LoginInfo,
  OperLog,
  OperLogPageResult,
  UserListParams,
  RoleListParams,
  PermissionListParams,
  DictTypeListParams,
  DictDataListParams,
  LoginInfoListParams,
  OperLogListParams,
  AssignedUsersParams,
  UnassignedUsersParams,
  ChangePasswordParams,
  ResetPasswordParams,
  ChangeUserStatusParams,
  ChangeRoleStatusParams,
  AssignRolesParams,
  AssignPermissionsParams,
  AssignRoleToUsersParams,
  CancelRoleFromUsersParams
} from '@/types/system'

// ========== 个人中心控制器 ==========

/**
 * 查询当前用户的个人信息
 */
export const getPersonalInfoApi = (): Promise<User> => {
  return http.get('/personal/info')
}

/**
 * 修改当前用户的个人信息
 */
export const updatePersonalInfoApi = (user: Partial<User>): Promise<void> => {
  return http.put('/personal/update', user)
}

/**
 * 修改当前用户密码
 */
export const changePersonalPasswordApi = (params: ChangePasswordParams): Promise<void> => {
  const formData = new URLSearchParams()
  formData.append('currentPassword', params.currentPassword)
  formData.append('newPassword', params.newPassword)
  formData.append('confirmPassword', params.confirmPassword)
  
  return http.put('/personal/password', formData, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 系统访问记录控制器 ==========

/**
 * 根据ID查询登录信息
 */
export const getLoginInfoByIdApi = (id: number): Promise<LoginInfo> => {
  return http.get(`/loginInfo/getLoginInfoById?id=${id}`)
}

/**
 * 分页查询登录信息
 */
export const getLoginInfoListApi = (params: LoginInfoListParams): Promise<PageResult<LoginInfo>> => {
  return http.get('/loginInfo/list', { params })
}

// ========== 后台用户 ==========

/**
 * 根据id查询用户详情
 */
export const getUserByIdApi = (id: number): Promise<User> => {
  return http.get(`/user/getUserById?id=${id}`)
}

/**
 * 新增用户
 */
export const addUserApi = (user: Partial<User>): Promise<void> => {
  return http.post('/user/addUser', user)
}

/**
 * 修改用户
 */
export const updateUserApi = (user: Partial<User>): Promise<void> => {
  return http.put('/user/updateUser', user)
}

/**
 * 重置密码
 */
export const resetPasswordApi = (params: ResetPasswordParams): Promise<void> => {
  const formData = new URLSearchParams()
  formData.append('userId', params.userId.toString())
  formData.append('newPassword', params.newPassword)
  
  return http.put('/user/resetPassword', formData, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 修改用户状态
 */
export const changeUserStatusApi = (params: ChangeUserStatusParams): Promise<void> => {
  const formData = new URLSearchParams()
  formData.append('userId', params.userId.toString())
  formData.append('status', params.status)
  
  return http.put('/user/changeStatus', formData, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 删除用户
 */
export const deleteUserApi = (ids: string): Promise<void> => {
  return http.delete(`/user/deleteUser?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 分页查询用户
 */
export const getUserListApi = (params: UserListParams): Promise<PageResult<User>> => {
  return http.get('/user/list', { params })
}

/**
 * 给用户分配角色
 */
export const assignRolesToUserApi = (params: AssignRolesParams): Promise<void> => {
  const formData = new URLSearchParams()
  formData.append('userId', params.userId.toString())
  if (params.roleIds) {
    formData.append('roleIds', params.roleIds)
  }
  
  return http.put('/user/assignRoles', formData, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 获取登录管理员名
 */
export const getUsernameApi = (): Promise<string> => {
  return http.get('/user/getUsername')
}

// ========== 字典类型管理控制器 ==========

/**
 * 分页查询字典类型列表
 */
export const getDictTypeListApi = (params: DictTypeListParams): Promise<PageResult<DictType>> => {
  return http.get('/dictType/list', { params })
}

/**
 * 根据ID查询字典类型详情
 */
export const getDictTypeByIdApi = (dictId: number): Promise<DictType> => {
  return http.get(`/dictType/getDictTypeById?dictId=${dictId}`)
}

/**
 * 新增字典类型
 */
export const addDictTypeApi = (dictType: Partial<DictType>): Promise<void> => {
  return http.post('/dictType/addDictType', dictType)
}

/**
 * 修改字典类型
 */
export const updateDictTypeApi = (dictType: Partial<DictType>): Promise<void> => {
  return http.put('/dictType/updateDictType', dictType)
}

/**
 * 批量删除字典类型
 */
export const deleteDictTypeApi = (dictIds: string): Promise<void> => {
  return http.delete(`/dictType/deleteDictType?dictIds=${dictIds}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 查询所有有效的字典类型
 */
export const getAllDictTypesApi = (): Promise<DictType[]> => {
  return http.get('/dictType/getAllDictTypes')
}

// ========== 后台权限 ==========

/**
 * 分页查询权限
 */
export const getPermissionListApi = (params: PermissionListParams): Promise<PageResult<Permission>> => {
  return http.get('/permission/list', { params })
}

/**
 * 根据ID查询权限
 */
export const getPermissionByIdApi = (id: number): Promise<Permission> => {
  return http.get(`/permission/getPermissionById?id=${id}`)
}

/**
 * 获取权限下拉列表
 */
export const getPermissionSelectListApi = (): Promise<Permission[]> => {
  return http.get('/permission/selectList')
}

/**
 * 新增权限
 */
export const addPermissionApi = (permission: Partial<Permission>): Promise<void> => {
  return http.post('/permission/addPermission', permission)
}

/**
 * 修改权限
 */
export const updatePermissionApi = (permission: Partial<Permission>): Promise<void> => {
  return http.put('/permission/updatePermission', permission)
}

/**
 * 删除权限
 */
export const deletePermissionApi = (ids: string): Promise<void> => {
  return http.delete(`/permission/deletePermission?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 获取所有权限
 */
export const getAllPermissionsApi = (): Promise<Permission[]> => {
  return http.get('/permission/all')
}

// ========== 操作日志信息 ==========

/**
 * 分页查询操作日志列表
 */
export const getOperLogListApi = (params: OperLogListParams): Promise<OperLogPageResult> => {
  return http.get('/operlog/list', { params })
}

/**
 * 根据操作日志ID查询详细信息
 */
export const getOperLogByIdApi = (operId: number): Promise<OperLog> => {
  return http.get(`/operlog/getOperLogById?operId=${operId}`)
}

// ========== 字典数据管理控制器 ==========

/**
 * 分页查询字典数据列表
 */
export const getDictDataListApi = (params: DictDataListParams): Promise<PageResult<DictData>> => {
  return http.get('/dictData/list', { params })
}

/**
 * 根据ID查询字典数据详情
 */
export const getDictDataByIdApi = (dictCode: number): Promise<DictData> => {
  return http.get(`/dictData/getDictDataById?dictCode=${dictCode}`)
}

/**
 * 根据字典类型查询字典数据列表
 * @param dictType 字典类型编码或ID
 */
export const getDictDataByTypeApi = (dictType: string | number): Promise<DictData[]> => {
  return http.get(`/dictData/getDictDataByType?dictType=${dictType}`)
}

/**
 * 新增字典数据
 */
export const addDictDataApi = (dictData: Partial<DictData>): Promise<void> => {
  return http.post('/dictData/addDictData', dictData)
}

/**
 * 修改字典数据
 */
export const updateDictDataApi = (dictData: Partial<DictData>): Promise<void> => {
  return http.put('/dictData/updateDictData', dictData)
}

/**
 * 批量删除字典数据
 */
export const deleteDictDataApi = (dictCodes: string): Promise<void> => {
  return http.delete(`/dictData/deleteDictData?dictCodes=${dictCodes}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// ========== 验证码控制器 ==========

/**
 * 生成验证码
 */
export const generateCaptchaApi = (): Promise<string> => {
  return http.get('/captcha/generate')
}

// ========== 后台角色 ==========

/**
 * 分页查询角色
 */
export const getRoleListApi = (params: RoleListParams): Promise<PageResult<Role>> => {
  return http.get('/role/list', { params })
}

/**
 * 根据ID查询角色
 */
export const getRoleByIdApi = (id: number): Promise<Role> => {
  return http.get(`/role/getRoleById?id=${id}`)
}

/**
 * 新增角色
 */
export const addRoleApi = (role: Partial<Role>): Promise<void> => {
  return http.post('/role/addRole', role)
}

/**
 * 修改角色
 */
export const updateRoleApi = (role: Partial<Role>): Promise<void> => {
  return http.put('/role/updateRole', role)
}

/**
 * 删除角色
 */
export const deleteRoleApi = (ids: string): Promise<void> => {
  return http.delete(`/role/deleteRole?ids=${ids}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 获取角色选择框列表
 */
export const getRoleSelectListApi = (): Promise<Role[]> => {
  return http.get('/role/selectList')
}

/**
 * 修改角色状态
 */
export const changeRoleStatusApi = (params: ChangeRoleStatusParams): Promise<void> => {
  const formData = new URLSearchParams()
  formData.append('roleId', params.roleId.toString())
  formData.append('status', params.status)
  
  return http.put('/role/changeStatus', formData, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 查询已分配该角色的用户列表
 */
export const getAssignedUsersApi = (params: AssignedUsersParams): Promise<PageResult<User>> => {
  return http.get('/role/getAssignUsers', { params })
}

/**
 * 查询未分配该角色的用户列表
 */
export const getUnassignedUsersApi = (params: UnassignedUsersParams): Promise<PageResult<User>> => {
  return http.get('/role/getUnassignUsers', { params })
}

/**
 * 批量选择用户取消角色
 */
export const cancelRoleFromUsersApi = (params: CancelRoleFromUsersParams): Promise<void> => {
  const formData = new URLSearchParams()
  formData.append('roleId', params.roleId.toString())
  formData.append('userIds', params.userIds)
  
  return http.delete('/role/cancelRoleFromUsers', {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    data: formData
  })
}

/**
 * 批量选择用户分配角色
 */
export const assignRoleToUsersApi = (params: AssignRoleToUsersParams): Promise<void> => {
  const formData = new URLSearchParams()
  formData.append('roleId', params.roleId.toString())
  formData.append('userIds', params.userIds)
  
  return http.post('/role/assignRoleToUsers', formData, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 给角色分配权限
 */
export const assignPermissionsToRoleApi = (params: AssignPermissionsParams): Promise<void> => {
  const formData = new URLSearchParams()
  formData.append('roleId', params.roleId.toString())
  formData.append('permissionIds', params.permissionIds)
  
  return http.put('/role/assignPermissions', formData, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 获取角色的权限列表
 */
export const getRolePermissionsApi = (roleId: number): Promise<Permission[]> => {
  return http.get(`/role/getRolePermissions?roleId=${roleId}`)
}
