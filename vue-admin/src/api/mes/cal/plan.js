import request from '@/utils/request'

export function listPlan(params) {
  return request({ url: '/api/mes/cal/plans', method: 'get', params })
}
export function getPlan(id) {
  return request({ url: `/api/mes/cal/plans/${id}`, method: 'get' })
}
export function createPlan(data) {
  return request({ url: '/api/mes/cal/plans', method: 'post', data })
}
export function updatePlan(id, data) {
  return request({ url: `/api/mes/cal/plans/${id}`, method: 'put', data })
}
export function deletePlan(id) {
  return request({ url: `/api/mes/cal/plans/${id}`, method: 'delete' })
}
