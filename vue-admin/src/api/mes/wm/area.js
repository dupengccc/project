import request from '@/utils/request'

export function listArea(params) {
  return request({ url: '/api/mes/wm/areas', method: 'get', params })
}
export function getArea(id) {
  return request({ url: `/api/mes/wm/areas/${id}`, method: 'get' })
}
export function createArea(data) {
  return request({ url: '/api/mes/wm/areas', method: 'post', data })
}
export function updateArea(id, data) {
  return request({ url: `/api/mes/wm/areas/${id}`, method: 'put', data })
}
export function deleteArea(id) {
  return request({ url: `/api/mes/wm/areas/${id}`, method: 'delete' })
}
