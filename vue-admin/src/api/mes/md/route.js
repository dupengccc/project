import request from '@/utils/request'

export function listRoute(params) {
  return request({ url: '/api/mes/md/routes', method: 'get', params })
}
export function getRoute(id) {
  return request({ url: `/api/mes/md/routes/${id}`, method: 'get' })
}
export function createRoute(data) {
  return request({ url: '/api/mes/md/routes', method: 'post', data })
}
export function updateRoute(id, data) {
  return request({ url: `/api/mes/md/routes/${id}`, method: 'put', data })
}
export function deleteRoute(id) {
  return request({ url: `/api/mes/md/routes/${id}`, method: 'delete' })
}
