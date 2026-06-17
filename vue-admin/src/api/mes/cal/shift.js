import request from '@/utils/request'

export function listShift(params) {
  return request({ url: '/api/mes/cal/shifts', method: 'get', params })
}
export function getShift(id) {
  return request({ url: `/api/mes/cal/shifts/${id}`, method: 'get' })
}
export function createShift(data) {
  return request({ url: '/api/mes/cal/shifts', method: 'post', data })
}
export function updateShift(id, data) {
  return request({ url: `/api/mes/cal/shifts/${id}`, method: 'put', data })
}
export function deleteShift(id) {
  return request({ url: `/api/mes/cal/shifts/${id}`, method: 'delete' })
}
