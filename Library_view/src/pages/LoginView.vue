<template>
  <div class="login-page">
    <el-card shadow="never" class="login-card">
      <h2 class="login-title">系统登录</h2>
      <!-- 移除 label-width，避免影响按钮布局；增加 class 统一控制表单 -->
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username" class="form-item">
          <!-- 手动添加必填星标，避免 Element 表单 label 布局干扰 -->
          <label class="form-label">用户名</label>
          <el-input v-model="loginForm.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item prop="password" class="form-item">
          <label class="form-label">密码</label>
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password size="large" />
        </el-form-item>
        <!-- 按钮专属表单项，清除默认内边距/外边距 -->
        <el-form-item class="btn-item">
          <el-button type="primary" @click="handleLogin" :loading="loading" class="login-btn">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="auth-footer">
        <span>还没有账号？</span>
        <router-link to="/register" class="link-btn">注册账号</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import apiService from '../service/api';
import { useUserStore } from '@/stores/user';
const router = useRouter();
const loginFormRef = ref(null);
const loading = ref(false);
const userStore = useUserStore();
const loginForm = reactive({
  username: '',
  password: '',
});
const loginRules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
});
const handleLogin = async () => {
  try {
    await loginFormRef.value.validate();
    loading.value = true;
    const response = await apiService.login(loginForm.username, loginForm.password);
    localStorage.setItem('token', response.data.tokenValue);
    const userInfo = await apiService.getProfile();
    userStore.setUser(userInfo.data.profile);
    ElMessage.success('登录成功');
    router.push('/');
  } catch (error) {
    console.error('登录失败:', error);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
  background-color: #f5f7fa;
  padding: 20px;
}
.login-card {
  width: 100%;
  max-width: 420px;
  padding: 40px 30px; /* 调整卡片内边距，适配按钮布局 */
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: none;
}
.login-title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
}
/* 统一表单项样式 */
.login-form {
  margin-bottom: 20px;
}
.form-item {
  margin-bottom: 18px; /* 输入框之间的垂直间距 */
}
/* 自定义标签样式，替代 Element 默认 label，避免布局干扰 */
.form-label {
  display: inline-block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
}
.form-label::before {
  content: '*';
  color: red;
  margin-right: 4px;
}
/* 按钮容器：清除默认内边距，保证按钮对齐 */
.btn-item {
  margin: 0;
  padding: 0;
}
/* 登录按钮：精准匹配截图样式 */
.login-btn {
  width: 100%; /* 与输入框等宽 */
  height: 44px; /* 匹配截图按钮高度 */
  line-height: 44px;
  background-color: #0066ff; /* 精准匹配截图蓝色 */
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  color: #fff;
  margin-top: 10px; /* 与密码框的垂直间距 */
}
.login-btn:hover {
  background-color: #0052cc;
}
/* 底部区域 */
.auth-footer {
  text-align: center;
  font-size: 14px;
  color: #666;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
.link-btn {
  color: #0066ff;
  text-decoration: none;
  font-weight: 500;
  margin-left: 4px;
}
.link-btn:hover {
  text-decoration: underline;
}
</style>