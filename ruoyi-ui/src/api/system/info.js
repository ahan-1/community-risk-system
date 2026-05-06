import request from '@/utils/request'

// 查询风险信息主列表
export function listInfo(query) {
  return request({
    url: '/system/info/list',
    method: 'get',
    params: query
  })
}

export function listInfoTypeOptions() {
  return request({
    url: '/system/info/type-options',
    method: 'get'
  })
}

export function auditInfo(data) {
  return request({
    url: '/system/info/audit',
    method: 'post',
    data: data
  })
}

export function statInfoByType() {
  return request({
    url: '/system/info/stat/type',
    method: 'get'
  })
}

export function statInfoByLevel() {
  return request({
    url: '/system/info/stat/level',
    method: 'get'
  })
}

export function statInfoByMonth() {
  return request({
    url: '/system/info/stat/month',
    method: 'get'
  })
}

// 查询风险信息主详细
export function getInfo(id) {
  return request({
    url: '/system/info/' + id,
    method: 'get'
  })
}

// 新增风险信息主
export function addInfo(data) {
  return request({
    url: '/system/info',
    method: 'post',
    data: data
  })
}

// 修改风险信息主
export function updateInfo(data) {
  return request({
    url: '/system/info',
    method: 'put',
    data: data
  })
}

// 删除风险信息主
export function delInfo(id) {
  return request({
    url: '/system/info/' + id,
    method: 'delete'
  })
}
