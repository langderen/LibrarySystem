<template>
  <div class="profile-page">
    <h2>个人中心</h2>
    <el-card shadow="never">
      <h3>我的借阅</h3>
      <el-table :data="borrowedBooks" border style="width: 100%" v-loading="loading">
        <el-table-column prop="title" label="书名" min-width="200" />
        <el-table-column prop="author" label="作者" min-width="150" />
        <el-table-column prop="borrowDate" label="借阅日期" width="180" />
        <el-table-column prop="dueDate" label="应还日期" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              icon="el-icon-circle-check"
              @click="returnBook(scope.row)"
            >
              归还
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="!loading && borrowedBooks.length === 0" class="empty-tip">
        暂无借阅记录
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import apiService from '../service/api';

const borrowedBooks = ref([]);
const loading = ref(true);

const fetchBorrowedBooks = async () => {
  try {
    loading.value = true;
    borrowedBooks.value = await apiService.getBorrowedBooks();
  } catch (error) {
    console.error('获取借阅列表失败:', error);
  } finally {
    loading.value = false;
  }
};

const returnBook = async (book) => {
  try {
    await apiService.returnBook(book.id);
    ElMessage.success(`已归还《${book.title}》`);
    fetchBorrowedBooks(); // 归还成功后刷新列表
  } catch (error) {
    console.error(`归还图书 ${book.id} 失败:`, error);
  }
};

onMounted(() => {
  fetchBorrowedBooks();
});
</script>

<style scoped>
.profile-page {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
.empty-tip {
  text-align: center;
  padding: 40px;
  color: #9ca3af;
  font-size: 1rem;
}
</style>