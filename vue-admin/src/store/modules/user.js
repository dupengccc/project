import { defineStore } from 'pinia'
import { login, logout, getInfo } from '@/api/login'
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
    }
  }
})
