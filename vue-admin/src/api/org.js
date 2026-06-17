import request from '@/utils/request'

export function getOrgTree() {
  return request({
    url: '/system/org/tree',
    method: 'get'
  })
}

export function getOrg(id) {
  return request({
    url: '/system/org/' + id,
    method: 'get'
  })
}

export function createOrg(data) {
  return request({
    url: '/system/org',
    method: 'post',
    data
  })
}

export function updateOrg(data) {
  return request({
    url: '/system/org',
    method: 'put',
    data
  })
}

export function deleteOrg(id) {
  return request({
    url: '/system/org/' + id,
    method: 'delete'
  })
}
