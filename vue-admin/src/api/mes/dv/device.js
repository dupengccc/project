import request from '@/utils/request'

export function listDevice(params) {
  return request({ url: '/api/mes/dv/devices', method: 'get', params })
}
export function getDevice(id) {
  return request({ url: `/api/mes/dv/devices/${id}`, method: 'get' })
}
export function createDevice(data) {
  return request({ url: '/api/mes/dv/devices', method: 'post', data })
}
export function updateDevice(id, data) {
  return request({ url: `/api/mes/dv/devices/${id}`, method: 'put', data })
}
export function deleteDevice(id) {
  return request({ url: `/api/mes/dv/devices/${id}`, method: 'delete' })
}
