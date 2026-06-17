import request from '@/utils/request'

export function listStock(params) {
  return request({ url: '/api/mes/wm/stocks', method: 'get', params })
}
export function getStock(id) {
  return request({ url: `/api/mes/wm/stocks/${id}`, method: 'get' })
}
export function createStock(data) {
  return request({ url: '/api/mes/wm/stocks', method: 'post', data })
}
export function updateStock(id, data) {
  return request({ url: `/api/mes/wm/stocks/${id}`, method: 'put', data })
}
export function deleteStock(id) {
  return request({ url: `/api/mes/wm/stocks/${id}`, method: 'delete' })
}
