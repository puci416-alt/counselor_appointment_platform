import request from '@/utils/request'

export function createAppointment(data) {
  return request.post('/api/appointment/create', data)
}

export function cancelAppointment(id) {
  return request.put(`/api/appointment/${id}/cancel`)
}

export function getMyAppointments(params) {
  return request.get('/api/appointment/page', { params })
}