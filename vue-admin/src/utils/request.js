import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/store/modules/user'

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 15000
})

service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  (error) => Promise.reject(error)
)

service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 0 && res.code !== 200 && res.code !== undefined) {
      ElMessage({
        message: res.msg || '请求失败',
        type: 'error',
        duration: 3000
      })
      if (res.code === 401) {
        ElMessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const userStore = useUserStore()
          userStore.logout().then(() => {
            location.reload()
          })
        }).catch(() => {})
      }
      return Promise.reject(new Error(res.msg || 'Error'))
    }
    return res
  },
  (error) => {
    ElMessage({
      message: error.response?.data?.msg || error.message || '网络错误',
      type: 'error',
      duration: 3000
    })
    return Promise.reject(error)
  }
)

export default service
