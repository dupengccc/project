import request from '@/utils/request'

export function listSchedule(params) {
  return request({ url: '/api/mes/pro/schedules', method: 'get', params })
}
export function getSchedule(id) {
  return request({ url: `/api/mes/pro/schedules/${id}`, method: 'get' })
}
export function createSchedule(data) {
  return request({ url: '/api/mes/pro/schedules', method: 'post', data })
}
export function updateSchedule(id, data) {
  return request({ url: `/api/mes/pro/schedules/${id}`, method: 'put', data })
}
export function deleteSchedule(id) {
  return request({ url: `/api/mes/pro/schedules/${id}`, method: 'delete' })
}
