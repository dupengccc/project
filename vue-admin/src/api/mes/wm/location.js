import request from '@/utils/request'

export function listLocation(params) {
  return request({ url: '/api/mes/wm/locations', method: 'get', params })
}
export function getLocation(id) {
  return request({ url: `/api/mes/wm/locations/${id}`, method: 'get' })
}
export function createLocation(data) {
  return request({ url: '/api/mes/wm/locations', method: 'post', data })
}
export function updateLocation(id, data) {
  return request({ url: `/api/mes/wm/locations/${id}`, method: 'put', data })
}
export function deleteLocation(id) {
  return request({ url: `/api/mes/wm/locations/${id}`, method: 'delete' })
}
