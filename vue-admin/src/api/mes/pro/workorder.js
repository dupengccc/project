import request from '@/utils/request'

export function listWorkorder(params) {
  return request({ url: '/api/mes/pro/workorders', method: 'get', params })
}
export function getWorkorder(id) {
  return request({ url: `/api/mes/pro/workorders/${id}`, method: 'get' })
}
export function createWorkorder(data) {
  return request({ url: '/api/mes/pro/workorders', method: 'post', data })
}
export function updateWorkorder(id, data) {
  return request({ url: `/api/mes/pro/workorders/${id}`, method: 'put', data })
}
export function deleteWorkorder(id) {
  return request({ url: `/api/mes/pro/workorders/${id}`, method: 'delete' })
}
