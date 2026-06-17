import request from '@/utils/request'

export function listVendor(params) {
  return request({ url: '/api/mes/md/vendors', method: 'get', params })
}
export function getVendor(id) {
  return request({ url: `/api/mes/md/vendors/${id}`, method: 'get' })
}
export function createVendor(data) {
  return request({ url: '/api/mes/md/vendors', method: 'post', data })
}
export function updateVendor(id, data) {
  return request({ url: `/api/mes/md/vendors/${id}`, method: 'put', data })
}
export function deleteVendor(id) {
  return request({ url: `/api/mes/md/vendors/${id}`, method: 'delete' })
}
