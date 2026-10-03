import request from '@/utils/request'

export function getCounselorPage(params) {
  return request.get('/api/counselor/page', { params })
}

export function getCounselorDetail(id) {
  return request.get(`/api/counselor/${id}`)
}

export function getSchedules(counselorId) {
  return request.get(`/api/schedule/counselor/${counselorId}`)
}