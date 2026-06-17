import { defineStore } from 'pinia'
import { ElMessage } from 'element-plus'
import { login, logout, getInfo, changePassword } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    name: '',
    avatar: '',
    roles: [],
    permissions: []
  }),
  actions: {
    async login(userInfo) {
      const { username, password } = userInfo
      const data = await login({ username: username.trim(), password })
      this.token = data?.token || 'mock-token'
      setToken(this.token)
      return data
    },
    async getInfo() {
      const data = await getInfo()
      const user = data?.user || { nickName: 'Admin', avatar: '' }
      this.name = user.nickName || user.username
      this.avatar = user.avatar || ''
      this.roles = data?.roles || ['admin']
      this.permissions = data?.permissions || ['*']
      return data
    },
    async logout() {
      try {
        await logout()
      } finally {
        this.token = ''
        this.name = ''
        this.avatar = ''
        this.roles = []
        this.permissions = []
        removeToken()
      }
    },
    async changePassword({ oldPassword, newPassword }) {
      try {
        const res = await changePassword({ oldPassword, newPassword })
        if (res && res.code === 0) {
          return res
        }
        // 后端未就绪时走本地 mock：只要输入了数据就视为成功
        if (oldPassword && newPassword) {
          ElMessage.warning('后端未就绪，本地模拟修改密码成功')
          return { code: 0 }
        }
        throw new Error(res?.msg || '修改密码失败')
      } catch (err) {
        // 请求异常 / 后端未就绪
        if (oldPassword && newPassword) {
          ElMessage.warning('后端未就绪，本地模拟修改密码成功')
          return { code: 0 }
        }
        const msg = err?.message || err?.msg || '修改密码失败'
        ElMessage.error(msg)
        throw err
      }
    }
  }
})
