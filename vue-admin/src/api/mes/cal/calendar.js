import request from '@/utils/request'

export function listCalendar(params) {
  return request({ url: '/api/mes/cal/calendars', method: 'get', params })
}
export function getCalendar(id) {
  return request({ url: `/api/mes/cal/calendars/${id}`, method: 'get' })
}
export function createCalendar(data) {
  return request({ url: '/api/mes/cal/calendars', method: 'post', data })
}
export function updateCalendar(id, data) {
  return request({ url: `/api/mes/cal/calendars/${id}`, method: 'put', data })
}
export function deleteCalendar(id) {
  return request({ url: `/api/mes/cal/calendars/${id}`, method: 'delete' })
}
