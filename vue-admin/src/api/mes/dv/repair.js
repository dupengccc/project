import request from '@/utils/request'

export function listRepair(params) {
  return request({ url: '/api/mes/dv/repairs', method: 'get', params })
}
export function getRepair(id) {
  return request({ url: `/api/mes/dv/repairs/${id}`, method: 'get' })
}
export function createRepair(data) {
  return request({ url: '/api/mes/dv/repairs', method: 'post', data })
}
export function updateRepair(id, data) {
  return request({ url: `/api/mes/dv/repairs/${id}`, method: 'put', data })
}
export function deleteRepair(id) {
  return request({ url: `/api/mes/dv/repairs/${id}`, method: 'delete' })
}
