import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: 'http://localhost:8088',   // 走 Gateway
  timeout: 30000
})

// 请求拦截器：自动带 Token
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.set('token', token)
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器：统一处理错误
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    }
    // 401 未登录
    if (res.code === 401) {
      ElMessage.error('请先登录')
      localStorage.removeItem('token')
      window.location.href = '/login'
      return Promise.reject(new Error(res.message))
    }
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message))
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request