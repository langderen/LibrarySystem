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
              <el-avatar size="small" :src=" undefined">{{ userStore.userName?.[0] || 'U' }}</el-avatar>
              <span class="username">{{ userStore.userName || '用户' }}</span>
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
import { useUserStore } from './stores/user'; // 建议 store 导出改为 useUserStore 命名规范
import { ElMessage } from 'element-plus';
import apiService from './service/api';
const route = useRoute();
const router = useRouter();
const userStore = useUserStore(); // 确保 store 名字匹配
const currentRoute = computed(() => route);

const handleLogout = () => {
  userStore.clearUser(); // 使用 Pinia action 清理状态
  localStorage.removeItem('token'); // 确保 token 被移除
  ElMessage.success('已成功退出登录');
  router.push('/login');
};
onMounted(async () => {
  // Pinia 的持久化插件会自动从 localStorage 恢复 token 到 userStore
  // 我们检查如果 token 存在，说明用户之前登录过且未过期
  if (userStore.token) {
    try {
      // 调用后端接口获取最新用户信息
      const userInfo = await apiService.getProfile();
      console.log('自动刷新用户信息:', userInfo.data.profile);
      userStore.setUser(userInfo.data.profile); 
    } catch (error) {
      console.error('自动刷新用户信息失败:', error);
      // 如果是 Token 过期 (401)，api.ts 中的拦截器通常会自动处理跳转登录
      // 所以这里不需要写太多逻辑
    }
  }
});
</script>

<style scoped>
/* 样式简化，使用 Flex 布局处理对齐 */
#app-container {
  display: flex;
  flex-direction: column;
  height: 96vh;
}
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #ebeef5;
  padding: 0 25px;
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
}
.app-footer {
  text-align: center;
  line-height: 60px;
  color: #909399;
}
</style>