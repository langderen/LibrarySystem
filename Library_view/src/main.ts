import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import App from './App.vue'
import VueCookies from 'vue3-cookies'
import 'element-plus/dist/index.css'
import ElementPlus from 'element-plus'
import router from './router';


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