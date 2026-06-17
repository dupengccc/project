import request from '@/utils/request'

export function listTool(params) {
  return request({ url: '/api/mes/tm/tools', method: 'get', params })
}
export function getTool(id) {
  return request({ url: `/api/mes/tm/tools/${id}`, method: 'get' })
}
export function createTool(data) {
  return request({ url: '/api/mes/tm/tools', method: 'post', data })
}
export function updateTool(id, data) {
  return request({ url: `/api/mes/tm/tools/${id}`, method: 'put', data })
}
export function deleteTool(id) {
  return request({ url: `/api/mes/tm/tools/${id}`, method: 'delete' })
}
