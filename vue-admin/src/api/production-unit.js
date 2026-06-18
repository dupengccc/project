import request from '@/utils/request'

export function listProductionUnits(params) {
  return request({ url: '/system/production-unit/list', method: 'get', params })
}

export function getProductionUnit(id) {
  return request({ url: `/system/production-unit/${id}`, method: 'get' })
}

export function createProductionUnit(data) {
  return request({ url: '/system/production-unit', method: 'post', data })
}

export function updateProductionUnit(data) {
  return request({ url: '/system/production-unit', method: 'put', data })
}

export function deleteProductionUnit(id) {
  return request({ url: `/system/production-unit/${id}`, method: 'delete' })
}

export function deleteProductionUnitBatch(ids) {
  return request({ url: '/system/production-unit/batch', method: 'delete', data: ids })
}
