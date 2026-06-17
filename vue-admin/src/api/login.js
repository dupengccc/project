import request from '@/utils/request'
import axios from 'axios'
import { getToken } from '@/utils/auth'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

export function getInfo() {
  return request({
    url: '/auth/getInfo',
    method: 'get'
  })
}

export function changePassword(data) {
  // 走原生 axios，禁用通用 request 的自动错误提示，便于前端做本地兜底
  const token = getToken()
  return axios.post(
    (import.meta.env.VITE_APP_BASE_API || '') + '/auth/change-password',
    data,
    {
      headers: token ? { Authorization: 'Bearer ' + token } : {},
      timeout: 15000
    }
  ).then((res) => res.data)
}
