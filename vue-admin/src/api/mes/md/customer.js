import request from '@/utils/request'

export function listCustomer(params) {
  return request({ url: '/api/mes/md/customers', method: 'get', params })
}
export function getCustomer(id) {
  return request({ url: `/api/mes/md/customers/${id}`, method: 'get' })
}
export function createCustomer(data) {
  return request({ url: '/api/mes/md/customers', method: 'post', data })
}
export function updateCustomer(id, data) {
  return request({ url: `/api/mes/md/customers/${id}`, method: 'put', data })
}
export function deleteCustomer(id) {
  return request({ url: `/api/mes/md/customers/${id}`, method: 'delete' })
}
