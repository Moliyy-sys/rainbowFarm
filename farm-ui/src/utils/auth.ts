const TOKEN_KEY = 'smart_agriculture_token'
const USER_INFO_KEY = 'smart_agriculture_user_info'

/**
 * 获取token
 */
export const getToken = (): string | null => {
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * 设置token
 */
export const setToken = (token: string): void => {
  localStorage.setItem(TOKEN_KEY, token)
}

/**
 * 移除token
 */
export const removeToken = (): void => {
  localStorage.removeItem(TOKEN_KEY)
}

/**
 * 获取用户信息
 */
export const getUserInfo = (): any => {
  try {
    const userInfo = localStorage.getItem(USER_INFO_KEY)
    return userInfo ? JSON.parse(userInfo) : null
  } catch (error) {
    console.error('解析用户信息失败:', error)
    removeUserInfo() // 清理损坏的数据
    return null
  }
}

/**
 * 设置用户信息
 */
export const setUserInfo = (userInfo: any): void => {
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo))
}

/**
 * 移除用户信息
 */
export const removeUserInfo = (): void => {
  localStorage.removeItem(USER_INFO_KEY)
}

/**
 * 清除所有认证信息
 */
export const clearAuth = (): void => {
  removeToken()
  removeUserInfo()
}
