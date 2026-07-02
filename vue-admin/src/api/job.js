import request from '@/utils/request'

// ==================== 定时任务 ====================

export function listJobs(params) {
  return request({ url: '/system/job/list', method: 'get', params })
}

export function getJob(id) {
  return request({ url: `/system/job/${id}`, method: 'get' })
}

export function addJob(data) {
  return request({ url: '/system/job', method: 'post', data })
}

export function updateJob(data) {
  return request({ url: '/system/job', method: 'put', data })
}

export function deleteJob(id) {
  return request({ url: `/system/job/${id}`, method: 'delete' })
}

export function deleteJobs(ids) {
  return request({ url: '/system/job/batch', method: 'delete', data: ids })
}

export function changeJobStatus(id, status) {
  return request({ url: '/system/job/change-status', method: 'put', params: { id, status } })
}

export function runJob(id) {
  return request({ url: '/system/job/run', method: 'put', params: { id } })
}

// ==================== 任务日志 ====================

export function listJobLogs(params) {
  return request({ url: '/system/job/log/list', method: 'get', params })
}

export function deleteJobLog(id) {
  return request({ url: `/system/job/log/${id}`, method: 'delete' })
}

export function cleanJobLogs() {
  return request({ url: '/system/job/log/clean', method: 'delete' })
}
