import request from '@/utils/request'

export function listRecord(params) {
  return request({ url: '/api/mes/qc/records', method: 'get', params })
}
export function getRecord(id) {
  return request({ url: `/api/mes/qc/records/${id}`, method: 'get' })
}
export function createRecord(data) {
  return request({ url: '/api/mes/qc/records', method: 'post', data })
}
export function updateRecord(id, data) {
  return request({ url: `/api/mes/qc/records/${id}`, method: 'put', data })
}
export function deleteRecord(id) {
  return request({ url: `/api/mes/qc/records/${id}`, method: 'delete' })
}
