import request from '@/utils/request'

export function listWarehouse(params) {
  return request({ url: '/api/mes/wm/warehouses', method: 'get', params })
}
export function getWarehouse(id) {
  return request({ url: `/api/mes/wm/warehouses/${id}`, method: 'get' })
}
export function createWarehouse(data) {
  return request({ url: '/api/mes/wm/warehouses', method: 'post', data })
}
export function updateWarehouse(id, data) {
  return request({ url: `/api/mes/wm/warehouses/${id}`, method: 'put', data })
}
export function deleteWarehouse(id) {
  return request({ url: `/api/mes/wm/warehouses/${id}`, method: 'delete' })
}
