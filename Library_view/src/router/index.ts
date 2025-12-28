// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
// 假设你已经将各个页面拆分为单独的组件
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import BooksView from '../views/BooksView.vue'
import ProfileView from '../views/ProfileView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
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
    next({ name: 'login' });
  } else {
    next();
  }
});

export default router