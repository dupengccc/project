import request from '@/utils/request'

export function listTemplate(params) {
  return request({ url: '/api/mes/qc/templates', method: 'get', params })
}
export function getTemplate(id) {
  return request({ url: `/api/mes/qc/templates/${id}`, method: 'get' })
}
export function createTemplate(data) {
  return request({ url: '/api/mes/qc/templates', method: 'post', data })
}
export function updateTemplate(id, data) {
  return request({ url: `/api/mes/qc/templates/${id}`, method: 'put', data })
}
export function deleteTemplate(id) {
  return request({ url: `/api/mes/qc/templates/${id}`, method: 'delete' })
}
