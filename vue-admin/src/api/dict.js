import request from '@/utils/request'

export function getDictTree() {
  return request({
    url: '/system/dict/tree',
    method: 'get'
  })
}

export function listDict(params) {
  return request({
    url: '/system/dict/list',
    method: 'get',
    params
  })
}

export function getDictChildren(parentCode) {
  return request({
    url: '/system/dict/children',
    method: 'get',
    params: { parentCode }
  })
}

export function getDict(id) {
  return request({
    url: `/system/dict/${id}`,
    method: 'get'
  })
}

export function createDict(data) {
  return request({
    url: '/system/dict',
    method: 'post',
    data
  })
}

export function updateDict(data) {
  return request({
    url: '/system/dict',
    method: 'put',
    data
  })
}

export function deleteDict(id) {
  return request({
    url: `/system/dict/${id}`,
    method: 'delete'
  })
}
