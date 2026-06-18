import request from '@/utils/request'

// ==================== 接口配置 ====================

export function listInterfaceConfigs(params) {
  return request({ url: '/system/interface/config/list', method: 'get', params })
}

export function getInterfaceConfig(id) {
  return request({ url: `/system/interface/config/${id}`, method: 'get' })
}

export function saveInterfaceConfig(data) {
  return request({ url: '/system/interface/config', method: 'post', data })
}

export function deleteInterfaceConfig(id) {
  return request({ url: `/system/interface/config/${id}`, method: 'delete' })
}

export function toggleInterfaceStatus(data) {
  return request({ url: '/system/interface/config/toggle-status', method: 'put', data })
}

// ==================== 接口调用 ====================

export function callInterface(interfaceCode, params, bizNo, sourceSystem) {
  return request({
    url: `/system/interface/call/${interfaceCode}`,
    method: 'post',
    data: params || {},
    params: { bizNo, sourceSystem }
  })
}

export function retryInterface(logId) {
  return request({ url: `/system/interface/retry/${logId}`, method: 'post' })
}

export function batchRetryInterface(logIds) {
  return request({ url: '/system/interface/retry/batch', method: 'post', data: logIds })
}

// ==================== 调用日志 ====================

export function listInterfaceLogs(params) {
  return request({ url: '/system/interface/log/list', method: 'get', params })
}

export function getInterfaceLog(id) {
  return request({ url: `/system/interface/log/${id}`, method: 'get' })
}

export function listErrorLogs() {
  return request({ url: '/system/interface/log/errors', method: 'get' })
}

export function processInterfaceLog(data) {
  return request({ url: '/system/interface/log/process', method: 'post', data })
}

export function getInterfaceStatistics(startDate, endDate) {
  return request({ url: '/system/interface/statistics', method: 'get', params: { startDate, endDate } })
}
