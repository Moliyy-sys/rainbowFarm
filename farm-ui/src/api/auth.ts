import { http } from '@/utils/request'
import type { LoginParams, LoginResult, UserInfo } from '@/types/api'

/**
 * 登录
 */
export const loginApi = (params: LoginParams): Promise<string> => {
  const { username, password, captchaCode } = params
  
  // 构建form-data格式的数据
  const formData = new URLSearchParams()
  formData.append('username', username)
  formData.append('password', password)
  
  // 构建查询参数
  const queryParams: any = {}
  if (captchaCode) {
    queryParams.captchaCode = captchaCode
  }
  
  return http.post('/user/login', formData.toString(), {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    params: queryParams
  })
}

/**
 * 登出
 */
export const logoutApi = (): Promise<void> => {
  return http.post('/user/logout')
}

/**
 * 获取验证码
 */
export const getCaptchaApi = (): Promise<{ img: string, uuid: string }> => {
  return http.get('/captcha/generate').then((data: string) => {
    // API返回base64字符串，后端使用sessionId管理验证码，前端不需要传uuid
    // 这里保持接口一致性，返回空的uuid
    return {
      img: data,
      uuid: ''
    }
  })
}

/**
 * 获取当前用户信息
 */
export const getUserInfoApi = (): Promise<UserInfo> => {
  return http.get('/personal/info')
}

/**
 * 修改当前用户信息
 */
export const updateUserInfoApi = (userInfo: Partial<UserInfo>): Promise<void> => {
  return http.put('/personal/update', userInfo)
}

/**
 * 修改当前用户密码
 */
export const changePasswordApi = (params: {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}): Promise<void> => {
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

/**
 * 上传头像
 */
export const uploadAvatarApi = (file: File): Promise<string> => {
  const formData = new FormData()
  formData.append('avatar', file)
  
  return http.post('/personal/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
