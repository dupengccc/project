import request from '@/utils/request'

export function listMaterial(params) {
  return request({ url: '/api/mes/md/materials', method: 'get', params })
}
export function getMaterial(id) {
  return request({ url: `/api/mes/md/materials/${id}`, method: 'get' })
}
export function createMaterial(data) {
  return request({ url: '/api/mes/md/materials', method: 'post', data })
}
export function updateMaterial(id, data) {
  return request({ url: `/api/mes/md/materials/${id}`, method: 'put', data })
}
export function deleteMaterial(id) {
  return request({ url: `/api/mes/md/materials/${id}`, method: 'delete' })
}
