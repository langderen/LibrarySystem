<template>
  <div class="home-page">
    <el-card shadow="hover">
      <h1>欢迎来到图书管理系统</h1>
      <p>探索知识的海洋，借阅您喜爱的图书。</p>
      <el-button type="primary" size="large" @click="router.push('/books')">
        <el-icon><Search /></el-icon>
        开始探索图书
      </el-button>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { Search } from '@element-plus/icons-vue';

const router = useRouter();
</script>

<style scoped>
.home-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}
.el-card {
  width: 60%;
  text-align: center;
  padding: 40px;
}
h1 {
  font-size: 2.5rem;
  margin-bottom: 20px;
}
p {
  font-size: 1.2rem;
  color: #6b7280;
  margin-bottom: 40px;
}
</style>