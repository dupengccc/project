import request from '@/utils/request'

export function listReport(params) {
  return request({ url: '/api/mes/pro/reports', method: 'get', params })
}
export function getReport(id) {
  return request({ url: `/api/mes/pro/reports/${id}`, method: 'get' })
}
export function createReport(data) {
  return request({ url: '/api/mes/pro/reports', method: 'post', data })
}
export function updateReport(id, data) {
  return request({ url: `/api/mes/pro/reports/${id}`, method: 'put', data })
}
export function deleteReport(id) {
  return request({ url: `/api/mes/pro/reports/${id}`, method: 'delete' })
}
