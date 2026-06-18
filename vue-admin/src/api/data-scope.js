import request from '@/utils/request'

export function listRoles(params) {
  return request({ url: '/system/data-scope/role-list', method: 'get', params })
}

export function getRoleDataScope(roleId) {
  return request({ url: `/system/data-scope/role/${roleId}`, method: 'get' })
}

export function saveRole(data) {
  return request({ url: '/system/data-scope/role', method: 'post', data })
}

export function deleteRole(roleId) {
  return request({ url: `/system/data-scope/role/${roleId}`, method: 'delete' })
}

export function assignDataScope(data) {
  return request({ url: '/system/data-scope/assign', method: 'post', data })
}

export function getOrgTree() {
  return request({ url: '/system/data-scope/org-tree', method: 'get' })
}
