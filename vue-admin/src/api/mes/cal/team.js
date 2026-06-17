import request from '@/utils/request'

export function listTeam(params) {
  return request({ url: '/api/mes/cal/teams', method: 'get', params })
}
export function getTeam(id) {
  return request({ url: `/api/mes/cal/teams/${id}`, method: 'get' })
}
export function createTeam(data) {
  return request({ url: '/api/mes/cal/teams', method: 'post', data })
}
export function updateTeam(id, data) {
  return request({ url: `/api/mes/cal/teams/${id}`, method: 'put', data })
}
export function deleteTeam(id) {
  return request({ url: `/api/mes/cal/teams/${id}`, method: 'delete' })
}
