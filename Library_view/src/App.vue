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
<<<<<<< HEAD
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMenuItem, ElMessage } from 'element-plus';
import { userStore } from './stores/user';
=======
import { ref, onMounted } from 'vue'
import axios from 'axios' // 
import { userStore } from './stores/user'

>>>>>>> fb8621dc81e769fd5d8a486352ce2f7498e03556

const router = useRouter();
const route = useRoute();
const currentRoute = computed(() => route);
const useUserStore = userStore();

<<<<<<< HEAD
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
=======
// 获取图书列表
const fetchBooks = async () => {
  loading.value = true
  try {
    const res = await axios.get(API_URL+'?page=1&size=10')
    books.value = res.data.records
  } catch (err) {
    alert('连接后端失败')
  } finally {
    loading.value = false
  }
}

// 借阅操作
const handleBorrow = async (bookId) => {
  try {
    // 模拟用户 ID = 1001
    const res = await axios.post(`/borrows?userId=${userStore.userId}&bookId=${bookId}`)
    if (res.data === '借阅成功') {
      alert('成功！')
      fetchBooks() // 借阅成功后刷新列表，更新库存显示
    } else {
      alert('失败：' + res.data)
    }
  } catch (err) {
    alert('请求出错')
  }
}
>>>>>>> fb8621dc81e769fd5d8a486352ce2f7498e03556

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