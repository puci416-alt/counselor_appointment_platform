import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    children: [
      {
        path: '',
        redirect: '/counselor'
      },
      {
        path: 'counselor',
        name: 'CounselorList',
        component: () => import('@/views/CounselorList.vue'),
        meta: { title: '咨询师列表' }
      },
      {
        path: 'counselor/:id',
        name: 'CounselorDetail',
        component: () => import('@/views/CounselorDetail.vue'),
        meta: { title: '咨询师详情' }
      },
      {
        path: 'my-appointments',
        name: 'MyAppointments',
        component: () => import('@/views/MyAppointments.vue'),
        meta: { title: '我的预约' }
      },
      {
        path: 'appointment/create',
        name: 'AppointmentCreate',
        component: () => import('@/views/AppointmentCreate.vue'),
        meta: { title: '预约确认' }
      },
      {
        path: 'appointment/create',
        name: 'AppointmentCreate',
        component: () => import('@/views/AppointmentCreate.vue'),
        meta: { title: '预约确认' }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 心理咨询预约平台` : '心理咨询预约平台'
  next()
})

export default router
