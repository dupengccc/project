import request from '@/utils/request'

export function getDashboardStats(params) {
  return request({ url: '/api/mes/dashboard/stats', method: 'get', params })
}
