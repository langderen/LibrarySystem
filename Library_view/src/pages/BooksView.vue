<template>
  <div class="books-page">
    <div class="page-header">
      <h2>图书借阅列表</h2>
      <div class="search-group">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入书名/作者/ISBN模糊搜索"
          clearable  
          @keyup.enter="fetchBooks" 
          style="width: 300px; margin-right: 8px;"
        />
        <el-button type="primary" @click="fetchBooks">搜索</el-button>
        <el-button type="default" @click="fetchBooks">刷新列表</el-button>
      </div>
    </div>
    
    <el-table :data="books" style="width: 100%" v-loading="loading" stripe>
      
      <el-table-column prop="title" label="书名" min-width="180" show-overflow-tooltip />
      <el-table-column prop="author" label="作者" min-width="150" />
      <el-table-column prop="publisher" label="出版社" min-width="150" />
      <el-table-column prop="isbn" label="ISBN" width="200" />
      <el-table-column prop="availableStock" label="库存" width="100">
        <template #default="{ row }">
          <el-tag :type="row.availableStock > 0 ? 'success' : 'danger'">
            {{ row.availableStock }} 本
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            :disabled="row.availableStock <= 0"
            @click="handleBorrow(row)"
          >
            {{ row.availableStock > 0 ? '借阅' : '缺货' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import apiService from '../service/api'; // ✅ 必须使用封装好的 API
import { useUserStore } from '@/stores/user';
const userStore = useUserStore();
const books = ref([]);
const loading = ref(false);

// 新增：搜索关键词响应式变量
const searchKeyword = ref('');

const fetchBooks = async () => {
  loading.value = true;
  try {
    const res = await apiService.getBooks(1,100, searchKeyword.value.trim()); 
    books.value = res.records || res; 
  } catch (error) {
    console.error('Fetch books failed', error);
  } finally {
    loading.value = false;
  }
};
const handleBorrow = async (book) => {
  try {
    await apiService.borrowBook(userStore.userId, book.id);
    ElMessage.success(`成功借阅《${book.title}》`);
    await fetchBooks(); // 借阅成功后刷新数据
  } catch (error) {
    // 忽略错误，拦截器已处理
  }
};

onMounted(fetchBooks);
</script>

<style scoped>
.books-page {
  background-color: #fff;
  padding: 24px;
  border-radius: 8px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>