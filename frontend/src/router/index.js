import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Restoration from '@/views/Restoration.vue'
import Profile from '@/views/Profile.vue'

// 路由守卫：未登录拦截
const requireAuth = (to, from, next) => {
  const token = localStorage.getItem('token')
  if (token) {
    next()
  } else {
    next('/login')
  }
}

const routes = [
  { path: '/', redirect: '/restoration' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/restoration', component: Restoration, beforeEnter: requireAuth },
  { path: '/profile', component: Profile, beforeEnter: requireAuth }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router