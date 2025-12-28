<template>
  <div style="padding: 20px;">
    <h1>图书借阅系统</h1>
    <div v-if="loading">加载中...</div>
    
    <div v-else class="book-list">
      <div v-for="book in books" :key="book.id" class="card">
        <h3>{{ book.title }}</h3>
        <p>剩余库存: <span :style="{color: book.availableStock > 0 ? 'green' : 'red'}">{{ book.availableStock }}</span></p>
        <button @click="handleBorrow(book.id)" :disabled="book.availableStock <= 0">
          {{ book.availableStock > 0 ? '立即借阅' : '缺货' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios' // 
import { userStore } from './stores/user'


const books = ref([])
const loading = ref(false)
// 后端运行在 8080 端口
const API_URL = 'http://localhost:8080/api/books'

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

onMounted(() => {
  fetchBooks()
})
</script>

<style>
.card { border: 1px solid #ddd; padding: 15px; margin-bottom: 10px; border-radius: 8px; }
button { background: #409EFF; color: white; border: none; padding: 5px 15px; cursor: pointer; }
button:disabled { background: #ccc; cursor: not-allowed; }
</style>