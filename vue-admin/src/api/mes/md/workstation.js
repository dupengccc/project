import request from '@/utils/request'

export function listWorkstation(params) {
  return request({ url: '/api/mes/md/workstations', method: 'get', params })
}
export function getWorkstation(id) {
  return request({ url: `/api/mes/md/workstations/${id}`, method: 'get' })
}
export function createWorkstation(data) {
  return request({ url: '/api/mes/md/workstations', method: 'post', data })
}
export function updateWorkstation(id, data) {
  return request({ url: `/api/mes/md/workstations/${id}`, method: 'put', data })
}
export function deleteWorkstation(id) {
  return request({ url: `/api/mes/md/workstations/${id}`, method: 'delete' })
}
