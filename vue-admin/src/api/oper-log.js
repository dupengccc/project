import request from '@/utils/request'

export function listOperLog(params) {
  return request({
    url: '/system/oper-log/list',
    method: 'get',
    params
  })
}

export function getOperLog(id) {
  return request({
    url: `/system/oper-log/${id}`,
    method: 'get'
  })
}

export function deleteOperLog(id) {
  return request({
    url: `/system/oper-log/${id}`,
    method: 'delete'
  })
}

export function deleteOperLogBatch(ids) {
  return request({
    url: '/system/oper-log/batch',
    method: 'delete',
    data: { ids }
  })
}

export function cleanOperLog(days) {
  return request({
    url: '/system/oper-log/clean',
    method: 'post',
    data: { days }
  })
}
