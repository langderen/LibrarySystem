<template>
  <div class="login-page">
    <el-card shadow="never" class="login-card">
      <h2 class="login-title">系统登录</h2>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import apiService from '../service/api';
import { userStore } from '@/stores/user';
const router = useRouter();
const loginFormRef = ref(null);
const loading = ref(false);
const useUserStore = userStore();
const loginForm = reactive({
  username: '', // 预设值方便测试
  password: '', // 预设值方便测试
});

const loginRules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
});

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate();
    loading.value = true;
    
    // 调用登录 API
    const response = await apiService.login(loginForm.username, loginForm.password);
    useUserStore.userId = response.data.loginId;
    useUserStore.isFinited = true;
    useUserStore.token = response.data.tokenValue;
    // 登录成功，保存 token 和用户信息到本地存储
    localStorage.setItem('token', response.data.tokenValue);
    localStorage.setItem('user', JSON.stringify(response.data.user));
    
    ElMessage.success('登录成功');
    router.push('/');

  } catch (error) {
    console.error('登录失败:', error);
    // 如果是 API 响应错误，axios 拦截器已经处理了消息提示
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
  min-height: 100%;
  background-color: #f5f7fa;
}
.login-card {
  width: 400px;
  padding: 20px 40px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.login-title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 1.5rem;
  color: #1f2937;
}
</style>