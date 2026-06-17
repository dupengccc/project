import request from '@/utils/request'

export function listDefect(params) {
  return request({ url: '/api/mes/qc/defects', method: 'get', params })
}
export function getDefect(id) {
  return request({ url: `/api/mes/qc/defects/${id}`, method: 'get' })
}
export function createDefect(data) {
  return request({ url: '/api/mes/qc/defects', method: 'post', data })
}
export function updateDefect(id, data) {
  return request({ url: `/api/mes/qc/defects/${id}`, method: 'put', data })
}
export function deleteDefect(id) {
  return request({ url: `/api/mes/qc/defects/${id}`, method: 'delete' })
}
