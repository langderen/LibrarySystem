import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../pages/HomeView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../pages/LoginView.vue') // 懒加载
    },
    {
      path: '/books',
      name: 'books',
      component: () => import('../pages/BooksView.vue') // 懒加载
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../pages/ProfileView.vue') // 懒加载
    }
  ]
});

/* // 全局前置路由守卫：检查用户是否登录
router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('token') !== null;
  
  // 如果目标路由不是登录页，且用户未登录，则重定向到登录页
  if (to.name !== 'login' && !isLoggedIn) {
    next({ name: 'login' });
  } else {
    next();
  }
}); */

export default router;
