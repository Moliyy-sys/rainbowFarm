// 通用API响应类型
export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

// 分页请求参数
export interface PageParams {
  pageNum: number
  pageSize: number
}

// 分页响应数据
export interface PageResult<T = any> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 登录相关
export interface LoginParams {
  username: string
  password: string
  captchaCode?: string
  uuid?: string
}

export interface LoginResult {
  code: number
  data: string
  msg: string
}

// 用户信息
export interface UserInfo {
  userId: number
  userName: string
  nickName: string
  email: string
  phonenumber: string
  sex: string
  avatar: string
  status: string
  roles: Role[]
}

// 角色信息
export interface Role {
  roleId: number
  roleName: string
  roleSort: number
  status: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  remark?: string
  permissions?: Permission[]
}

// 权限信息
export interface Permission {
  permissionId: number
  permissionName: string
  component: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  remark?: string
}

// 菜单项
export interface MenuItem {
  path: string
  name: string
  component?: string
  redirect?: string
  meta: {
    title: string
    icon?: string
    hidden?: boolean
    roles?: string[]
  }
  children?: MenuItem[]
}
