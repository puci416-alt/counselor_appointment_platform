import request from '@/utils/request'

export function login(data) {
  return request.post('/api/user/login', data)
}

export function getUserInfo() {
  return request.get('/api/user/info')
}