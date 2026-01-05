<template>
  <div id="app-container">
    <el-header class="app-header">
      <div class="logo">图书管理系统</div>
      <el-menu :default-active="currentRoute.path" mode="horizontal" :ellipsis="false" router>
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/books">图书借阅</el-menu-item>
        
        <div class="flex-grow" />

        <div v-if="userStore.isFinited" class="user-actions">
          <el-dropdown>
            <span class="el-dropdown-link">
              <el-avatar size="medial" >{{ userStore.username?.[0] || 'U' }}</el-avatar>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-menu-item index="/profile" @click="router.push('/profile')">个人中心</el-menu-item>
                <el-menu-item divided @click="handleLogout">退出登录</el-menu-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <div v-else>
          <el-menu-item index="/login">登录</el-menu-item>
        </div>
      </el-menu>
    </el-header>

    <el-main>
      <router-view />
    </el-main>

    <el-footer class="app-footer">
      <span>© 2025 图书管理系统 | Powered by Vue 3 & Element Plus</span>
    </el-footer>
  </div>
</template>

<script setup>
import { computed , onMounted} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from './stores/user';
import { ElMessage } from 'element-plus';
import apiService from './service/api';
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const currentRoute = computed(() => route);

const handleLogout = () => {
  userStore.clearUser();
  localStorage.removeItem('token');
  ElMessage.success('已成功退出登录');
  router.push('/login');
};
onMounted(async () => {
  if (localStorage.getItem('token')) {
    try {
      const userInfo = await apiService.getProfile();
      userStore.setUser(userInfo.data.profile);
    } catch (error) {
      console.error('自动刷新用户信息失败:', error);
    }
  }
});
</script>

<style scoped>
#app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #ebeef5;
  padding: 0 25px;
  flex-shrink: 0;
}
.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  margin-right: 20px;
  width: 180px;
}
.flex-grow {
  flex-grow: 1;
}
.user-actions {
  display: flex;
  align-items: center;
  height: 100%;
}
.el-menu {
  border-bottom: none;
  width: 100%;
}
.el-main {
  background-color: #f5f7fa;
  flex: 1;
}
.app-footer {
  text-align: center;
  line-height: 60px;
  color: #909399;
  background-color: #fff;
}
</style>
