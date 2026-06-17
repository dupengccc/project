import request from '@/utils/request'

export function listIn(params) {
  return request({ url: '/api/mes/wm/ins', method: 'get', params })
}
export function getIn(id) {
  return request({ url: `/api/mes/wm/ins/${id}`, method: 'get' })
}
export function createIn(data) {
  return request({ url: '/api/mes/wm/ins', method: 'post', data })
}
export function updateIn(id, data) {
  return request({ url: `/api/mes/wm/ins/${id}`, method: 'put', data })
}
export function deleteIn(id) {
  return request({ url: `/api/mes/wm/ins/${id}`, method: 'delete' })
}
