import request from '@/utils/request'

export function listCheck(params) {
  return request({ url: '/api/mes/dv/checks', method: 'get', params })
}
export function getCheck(id) {
  return request({ url: `/api/mes/dv/checks/${id}`, method: 'get' })
}
export function createCheck(data) {
  return request({ url: '/api/mes/dv/checks', method: 'post', data })
}
export function updateCheck(id, data) {
  return request({ url: `/api/mes/dv/checks/${id}`, method: 'put', data })
}
export function deleteCheck(id) {
  return request({ url: `/api/mes/dv/checks/${id}`, method: 'delete' })
}
