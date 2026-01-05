<template>
  <div class="auth-container">
    <div class="auth-card">
      <h2 class="auth-title">创建新账号</h2>
      <p class="auth-subtitle">请填写以下信息完成注册</p>
      
      <form @submit.prevent="handleRegister" class="auth-form">
        <div class="form-item">
          <label for="id">ID</label>
          <input
            type="text"
            id="id"
            v-model="form.id"
            placeholder="请输入学号"
            :class="{ 'input-error': errors.id }"
            required
          />
          <span class="error-msg">{{ errors.id }}</span>
        </div>

        <div class="form-item">
          <label for="username">用户名</label>
          <input
            type="text"
            id="username"
            v-model="form.username"
            placeholder="请输入用户名"
            :class="{ 'input-error': errors.username }"
            required
          />
          <span class="error-msg">{{ errors.username }}</span>
        </div>

        <div class="form-item">
          <label for="email">邮箱</label>
          <input
            type="email"
            id="email"
            v-model="form.email"
            placeholder="请输入您的邮箱"
            :class="{ 'input-error': errors.email }"
            required
          />
          <span class="error-msg">{{ errors.email }}</span>
        </div>
        
        <div class="form-item">
          <label for="phone">手机号</label>
          <input
            type="tel"
            id="phone"
            v-model="form.phone"
            placeholder="请输入手机号"
            :class="{ 'input-error': errors.phone }"
            required
          />
          <span class="error-msg">{{ errors.phone }}</span>
        </div>

        <div class="form-item">
          <label for="password">密码</label>
          <div class="password-wrapper">
            <input
              :type="showPassword ? 'text' : 'password'"
              id="password"
              v-model="form.password"
              placeholder="请输入密码（至少6位）"
              :class="{ 'input-error': errors.password }"
              required
            />
            <i class="icon" @click="showPassword = !showPassword">
              {{ showPassword ? '👁️' : '🙈' }}
            </i>
          </div>
          <span class="error-msg">{{ errors.password }}</span>
        </div>

        <div class="form-item">
          <label for="confirmPassword">确认密码</label>
          <input
            type="password"
            id="confirmPassword"
            v-model="form.confirmPassword"
            placeholder="请再次输入密码"
            :class="{ 'input-error': errors.confirmPassword }"
            required
          />
          <span class="error-msg">{{ errors.confirmPassword }}</span>
        </div>

        <button type="submit" class="auth-btn" :disabled="isSubmitting">
          {{ isSubmitting ? '注册中...' : '注册' }}
        </button>
      </form>

      <div class="auth-footer">
        <span>已有账号？</span>
        <router-link to="/login" class="link-btn">返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import apiService from '../service/api';
import { ElMessage } from 'element-plus';

const router = useRouter();

// 表单数据
const form = ref({
  id: '',
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
});

// 错误信息
const errors = ref({});

// 状态
const showPassword = ref(false);
const isSubmitting = ref(false);

// 表单验证
const validateForm = () => {
  const newErrors = {};
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  const phoneRegex = /^1[3-9]\d{9}$/;

  if (!form.value.username.trim()) {
    newErrors.username = '用户名不能为空';
  }
  if (!form.value.email.trim()) {
    newErrors.email = '邮箱不能为空';
  } else if (!emailRegex.test(form.value.email)) {
    newErrors.email = '邮箱格式不正确';
  }
  if (!form.value.phone.trim()) {
    newErrors.phone = '手机号不能为空';
  } else if (!phoneRegex.test(form.value.phone)) {
    newErrors.phone = '手机号格式不正确';
  }
  if (!form.value.password) {
    newErrors.password = '密码不能为空';
  } else if (form.value.password.length < 6) {
    newErrors.password = '密码长度不能少于6位';
  }
  if (form.value.confirmPassword !== form.value.password) {
    newErrors.confirmPassword = '两次输入的密码不一致';
  }

  errors.value = newErrors;
  // 如果 newErrors 对象为空，则验证通过
  return Object.keys(newErrors).length === 0;
};

// 处理注册
const handleRegister = async () => {
  // 1. 表单验证
  if (!validateForm()) {
    return;
  }

  isSubmitting.value = true;
  try {
    // 2. 发送注册请求
    const data = await apiService.register({
      id: form.value.id,
      username: form.value.username,
      email: form.value.email,
      phone: form.value.phone,
      password: form.value.password,
      registerTime: new Date().toISOString()
    });
    // 3. 处理响应
    if (data.code === 200) {
      ElMessage.success('注册成功，请登录！');
      router.push('/login'); // 注册成功后跳转到登录页
    } else {
      // 如果后端返回了具体的错误信息（如用户名已存在）
      ElMessage.error(data.msg || '注册失败，请稍后再试');
    }
  } catch (error) {
    console.error('注册失败:', error);
    ElMessage.error('网络错误，注册失败');
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
/* 基础布局样式，与登录页保持一致 */
.auth-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.auth-card {
  width: 100%;
  max-width: 400px;
  padding: 2.5rem;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.auth-title {
  font-size: 1.75rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
}

.auth-subtitle {
  color: #666;
  margin-bottom: 2rem;
}

/* 表单样式 */
.auth-form {
  margin-bottom: 1.5rem;
}

.form-item {
  margin-bottom: 1.25rem;
  text-align: left;
}

.form-item label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
}

.form-item input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.3s;
  box-sizing: border-box; /* 确保padding不会撑大宽度 */
}

.form-item input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
}

.password-wrapper {
  position: relative;
}

.icon {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  font-size: 1.2rem;
}

/* 错误提示 */
.input-error {
  border-color: #dc3545 !important;
}

.error-msg {
  display: block;
  height: 1.2rem;
  margin-top: 0.25rem;
  font-size: 0.875rem;
  color: #dc3545;
}

/* 按钮样式 */
.auth-btn {
  width: 100%;
  padding: 0.75rem;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.auth-btn:hover:not(:disabled) {
  background-color: #0056b3;
}

.auth-btn:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

/* 页脚链接 */
.auth-footer {
  font-size: 0.875rem;
  color: #666;
}

.link-btn {
  color: #007bff;
  text-decoration: none;
  font-weight: 500;
  margin-left: 0.25rem;
}

.link-btn:hover {
  text-decoration: underline;
}
</style>