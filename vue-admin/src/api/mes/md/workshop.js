import request from '@/utils/request'

export function listWorkshop(params) {
  return request({ url: '/api/mes/md/workshops', method: 'get', params })
}
export function getWorkshop(id) {
  return request({ url: `/api/mes/md/workshops/${id}`, method: 'get' })
}
export function createWorkshop(data) {
  return request({ url: '/api/mes/md/workshops', method: 'post', data })
}
export function updateWorkshop(id, data) {
  return request({ url: `/api/mes/md/workshops/${id}`, method: 'put', data })
}
export function deleteWorkshop(id) {
  return request({ url: `/api/mes/md/workshops/${id}`, method: 'delete' })
}
