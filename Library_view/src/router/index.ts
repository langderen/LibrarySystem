<<<<<<< HEAD
import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../pages/HomeView.vue';
=======
// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
// 假设你已经将各个页面拆分为单独的组件
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import BooksView from '../views/BooksView.vue'
import ProfileView from '../views/ProfileView.vue'
>>>>>>> fb8621dc81e769fd5d8a486352ce2f7498e03556

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
<<<<<<< HEAD
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
=======
    { path: '/', name: 'home', component: HomeView },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/books', name: 'books', component: BooksView },
    { path: '/profile', name: 'profile', component: ProfileView },
    { path: '/:pathMatch(.*)*', redirect: '/' }
  ]
})

// 路由守卫也放在这里
router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('user') !== null;
  const requiresAuth = to.name !== 'login' && to.name !== 'register';
  if (requiresAuth && !isLoggedIn) {
>>>>>>> fb8621dc81e769fd5d8a486352ce2f7498e03556
    next({ name: 'login' });
  } else {
    next();
  }
<<<<<<< HEAD
}); */

export default router;
=======
});

export default router
>>>>>>> fb8621dc81e769fd5d8a486352ce2f7498e03556
