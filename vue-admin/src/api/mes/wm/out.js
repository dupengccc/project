import request from '@/utils/request'

export function listOut(params) {
  return request({ url: '/api/mes/wm/outs', method: 'get', params })
}
export function getOut(id) {
  return request({ url: `/api/mes/wm/outs/${id}`, method: 'get' })
}
export function createOut(data) {
  return request({ url: '/api/mes/wm/outs', method: 'post', data })
}
export function updateOut(id, data) {
  return request({ url: `/api/mes/wm/outs/${id}`, method: 'put', data })
}
export function deleteOut(id) {
  return request({ url: `/api/mes/wm/outs/${id}`, method: 'delete' })
}
