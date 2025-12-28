<template>
  <div class="books-page">
    <h2>图书借阅</h2>
    <el-table :data="books"  style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="书名" min-width="180" />
      <el-table-column prop="author" label="作者" min-width="150" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.availableStock > 0 ? 'success' : 'danger'">
            {{ scope.row.availableStock }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button
            type="success"
            size="small"
            icon="el-icon-circle-plus"
            @click="borrowBook(scope.row)"
            :disabled="scope.row.availableStock > 0 ? false : true"
          >
            借阅
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import apiService from '../service/api';
import axios from 'axios';

const router = useRouter();
const books = ref([]);
const loading = ref(true);

const fetchBooks = async () => {
  try {
    loading.value = true;
    //books.value = await apiService.getBooks();
    const booksRes = await axios({
      url: `http://localhost:8080/api/books?page=1&size=10`,
      method: 'GET',
    }); 
    console.log(booksRes.data.records);
    books.value = booksRes.data.records;
    
  } catch (error) {
    console.error('获取图书列表失败:', error);
  } finally {
    loading.value = false;
  }
};

const borrowBook = async (book) => {
  try {
    await apiService.borrowBook(book.id);
    ElMessage.success(`成功借阅《${book.title}》`);
    fetchBooks(); // 借阅成功后刷新列表
  } catch (error) {
    console.error(`借阅图书 ${book.id} 失败:`, error);
  }
};

onMounted(() => {
  fetchBooks();
});
</script>

<style scoped>
.books-page {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
</style>