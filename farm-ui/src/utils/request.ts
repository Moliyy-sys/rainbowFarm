import axios, { type AxiosResponse, type AxiosRequestConfig } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    const token = userStore.token
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    // 打印响应数据用于调试
    console.log('API响应:', response.data)
    
    // 兼容 msg 和 message 两种字段
    const { code, msg, message, data } = response.data
    const msgText = msg || message
    
    // 成功响应
    if (code === 200 || code === 0) {
      return data
    }
    
    // 业务错误 - 显示更详细的错误信息
    const errorMsg = msgText || `请求失败 (code: ${code})`
    ElMessage.error(errorMsg)
    console.error('业务错误:', { code, msg: msgText, data, url: response.config.url })
    return Promise.reject(new Error(errorMsg))
  },
  (error) => {
    console.error('请求错误:', error)
    const { response } = error
    
    if (response) {
      console.error('响应错误详情:', { 
        status: response.status, 
        data: response.data,
        url: response.config.url 
      })
      
      const { status, data } = response
      
      switch (status) {
        case 401:
          ElMessageBox.confirm('登录状态已过期，请重新登录', '系统提示', {
            confirmButtonText: '重新登录',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            const userStore = useUserStore()
            userStore.logout()
            router.push('/login')
          })
          break
        case 403:
          ElMessage.error('没有权限访问该资源')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data?.msg || data?.message || `网络错误 (状态码: ${status})`)
      }
    } else {
      console.error('网络连接失败，错误详情:', error.message)
      ElMessage.error('网络连接失败，请检查后端服务是否启动（localhost:8080）')
    }
    
    return Promise.reject(error)
  }
)

// 封装常用请求方法
export const http = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return request.get(url, config)
  },
  
  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return request.post(url, data, config)
  },
  
  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return request.put(url, data, config)
  },
  
  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return request.delete(url, config)
  }
}

export default request
