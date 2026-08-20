import { defineStore } from 'pinia'
import type { UserInfo, LoginParams, LoginResult } from '@/types/api'
import { getToken, setToken, removeToken, getUserInfo, setUserInfo, clearAuth } from '@/utils/auth'
import { loginApi, getUserInfoApi } from '@/api/auth'
import { getUserByIdApi } from '@/api/system'

interface UserState {
  token: string | null
  userInfo: UserInfo | null
  permissions: string[]
  roles: string[]
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: getToken(),
    userInfo: getUserInfo(),
    permissions: [],
    roles: []
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    userName: (state) => state.userInfo?.userName || '',
    nickName: (state) => state.userInfo?.nickName || '',
    avatar: (state) => state.userInfo?.avatar || '',
    hasPermission: (state) => (permission: string) => state.permissions.includes(permission),
    hasRole: (state) => (role: string) => state.roles.includes(role)
  },

  actions: {
    // 登录
    async login(loginParams: LoginParams): Promise<void> {
      try {
        const result: string = await loginApi(loginParams)
        console.log("登录API返回结果:", result)

        // 保存token到localStorage和store
        setToken(result)
        this.token = result
        
        // 尝试获取用户信息（如果失败不影响登录）
        try {
        await this.fetchUserInfo()
        } catch (error) {
          console.warn('获取用户信息失败，将在后续访问时重试:', error)
          // 不抛出错误，允许登录继续
        }
      } catch (error) {
        console.error('登录失败:', error)
        // 清理可能的残留状态
        this.logout()
        throw error
      }
    },

    // 获取用户信息
    async fetchUserInfo(): Promise<void> {
      try {
        // 先获取基本信息以获取userId
        const basicInfo: UserInfo = await getUserInfoApi()
        console.log("获取到的基本信息:", basicInfo)
        
        if (!basicInfo.userId) {
          console.error('无法获取userId')
          throw new Error('无法获取用户ID')
        }
        
        // 使用userId获取完整用户信息（包含角色和权限）
        const userInfo = await getUserByIdApi(basicInfo.userId)
        console.log("获取到的完整用户信息:", userInfo)
        
        this.userInfo = userInfo as UserInfo
        setUserInfo(userInfo)
        
        // 提取权限和角色 - 安全处理null/undefined情况
        this.permissions = (userInfo.roles || []).flatMap(role => 
          role.permissions?.map(permission => permission.permissionName) || []
        )
        this.roles = (userInfo.roles || []).map(role => role.roleName)
        
        console.log("提取的权限:", this.permissions)
        console.log("提取的角色:", this.roles)
      } catch (error) {
        console.error('获取用户信息失败:', error)
        throw error
      }
    },

    // 登出
    logout(): void {
      this.token = null
      this.userInfo = null
      this.permissions = []
      this.roles = []
      clearAuth()
    },

    // 重置用户信息
    resetUserInfo(): void {
      this.userInfo = null
      this.permissions = []
      this.roles = []
    }
  }
})
