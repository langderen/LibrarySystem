import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import App from './App.vue'
import VueCookies from 'vue3-cookies'
import 'element-plus/dist/index.css'
<<<<<<< HEAD
import ElementPlus from 'element-plus'
import router from './router';


=======
import { createRouter, createWebHistory } from 'vue-router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'


//定义路由
const routes = [
  {path:"/",redirect:"/home"},
]

//创建路由
const router = createRouter({
  history:createWebHistory(),
  routes
})

>>>>>>> fb8621dc81e769fd5d8a486352ce2f7498e03556
//加载路由
const app = createApp(App)

//Pinia配置

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate);
app.use(pinia)
app.use(router)
app.use(VueCookies)
app.use(ElementPlus)

app.mount('#app')