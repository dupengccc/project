import request from '@/utils/request'

/**
 * 登录日志列表
 * @param params { empNo, name, loginIp, status, page, pageSize }
 */
export function listLoginLog(params) {
  return request({
    url: '/system/login-log/list',
    method: 'get',
    params
  })
}

/**
 * 登录日志详情
 */
export function getLoginLog(id) {
  return request({
    url: `/system/login-log/${id}`,
    method: 'get'
  })
}

/**
 * 单条删除
 */
export function deleteLoginLog(id) {
  return request({
    url: `/system/login-log/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除
 */
export function deleteLoginLogBatch(ids) {
  return request({
    url: '/system/login-log/batch',
    method: 'delete',
    data: { ids }
  })
}

/**
 * 清理 n 天前的日志
 */
export function cleanLoginLog(days) {
  return request({
    url: '/system/login-log/clean',
    method: 'post',
    data: { days }
  })
}
