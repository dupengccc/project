import request from '@/utils/request'

export function listBom(params) {
  return request({ url: '/api/mes/md/boms', method: 'get', params })
}
export function getBom(id) {
  return request({ url: `/api/mes/md/boms/${id}`, method: 'get' })
}
export function createBom(data) {
  return request({ url: '/api/mes/md/boms', method: 'post', data })
}
export function updateBom(id, data) {
  return request({ url: `/api/mes/md/boms/${id}`, method: 'put', data })
}
export function deleteBom(id) {
  return request({ url: `/api/mes/md/boms/${id}`, method: 'delete' })
}
