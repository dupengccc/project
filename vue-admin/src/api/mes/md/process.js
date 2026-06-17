import request from '@/utils/request'

export function listProcess(params) {
  return request({ url: '/api/mes/md/processes', method: 'get', params })
}
export function getProcess(id) {
  return request({ url: `/api/mes/md/processes/${id}`, method: 'get' })
}
export function createProcess(data) {
  return request({ url: '/api/mes/md/processes', method: 'post', data })
}
export function updateProcess(id, data) {
  return request({ url: `/api/mes/md/processes/${id}`, method: 'put', data })
}
export function deleteProcess(id) {
  return request({ url: `/api/mes/md/processes/${id}`, method: 'delete' })
}
