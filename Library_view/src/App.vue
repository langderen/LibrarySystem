<template>
  <div id="app-container">
    <!-- 顶部导航栏 -->
    <el-header v-if="currentRoute.name !== 'login'" style="text-align: right; font-size: 12px">
      <div style="float: left; font-size: 20px; font-weight: bold; color: #409EFF;">
        图书管理系统
      </div>
      <el-menu :default-active="currentRoute.path" class="el-menu-demo" mode="horizontal" :ellipsis="false">
        <el-menu-item index="/" @click="goTo('/')">首页</el-menu-item>
        <el-menu-item index="/books" @click="goTo('/books')">图书借阅</el-menu-item>
          <!-- 登录或用户按钮 -->
            <div v-if="useUserStore.isFinited">
                <el-dropdown >
                  <span class="el-dropdown-link" style="display:flex; align-items:center; gap:8px;">
                    <el-avatar style="background-color: #4a6bff"> {{useUserStore.userId}} </el-avatar>
                  </span>
                    <el-dropdown-menu>
                      <el-menu-item index="/profile" @click="goTo('/profile')">个人中心</el-menu-item>
                      <el-menu-item index="logout" @click="logout">退出登录</el-menu-item>
                    </el-dropdown-menu>
                </el-dropdown>
              </div>
              <div v-else >
                <el-menu-item index="/login" @click="goTo('/login')">登录</el-menu-item>
              </div>
      </el-menu>
    </el-header>

    <!-- 主内容区域 -->
    <el-main>
      <router-view />
    </el-main>

    <!-- 页脚 -->
    <el-footer style="text-align: center">
      <span>© 2025 图书管理系统 | Powered by Vue 3 & Element Plus</span>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMenuItem, ElMessage } from 'element-plus';
import { userStore } from './stores/user';

const router = useRouter();
const route = useRoute();
const currentRoute = computed(() => route);
const useUserStore = userStore();

const goTo = (path) => {
  router.push(path);
};

const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  isLoggedIn.value = false;
  ElMessage.success('已成功退出登录');
  router.push('/login');
};

onMounted(() => {
  isLoggedIn.value = localStorage.getItem('token') !== null;
});
</script>

<style scoped>
#app-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}
.el-header, .el-footer {
  background-color: #fff;
  color: #333;
  line-height: 60px;
  border-bottom: 1px solid #ebeef5;
}
.el-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f9fafc;
}
.el-menu-demo:not(.el-menu--collapse) {
  border-bottom: none;
}
</style>