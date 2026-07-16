import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const data = error.response?.data
    const message = data?.message || error.message || '请求失败'
    const status = error.response?.status

    if (status === 403) {
      ElMessage.error('权限不足：' + message)
    } else if (status === 404) {
      ElMessage.error('资源未找到：' + message)
    } else if (status === 409) {
      ElMessage.error(message)
    } else if (status === 400) {
      ElMessage.error(message)
    } else if (error.code === 'ECONNABORTED' || !error.response) {
      ElMessage.error('网络连接失败，请检查网络后重试')
    } else {
      ElMessage.error('服务器错误：' + message)
    }

    return Promise.reject(error)
  }
)

export default api
