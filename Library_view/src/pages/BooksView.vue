<template>
  <div class="books-page">
    <div class="page-header">
      <h2>图书借阅列表</h2>
      <div class="header-actions">
        <el-button type="primary" @click="fetchBooks">刷新列表</el-button>
      </div>
    </div>

    <div class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="书名">
          <el-input v-model="searchForm.title" placeholder="请输入书名" clearable style="width: 160px;" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="searchForm.author" placeholder="请输入作者" clearable style="width: 140px;" />
        </el-form-item>
        <el-form-item label="ISBN">
          <el-input v-model="searchForm.isbn" placeholder="请输入ISBN" clearable style="width: 160px;" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="请选择分类" clearable style="width: 120px;">
            <el-option label="计算机" value="计算机" />
            <el-option label="文学" value="文学" />
            <el-option label="历史" value="历史" />
            <el-option label="科学" value="科学" />
            <el-option label="艺术" value="艺术" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="searchForm.publisher" placeholder="请输入出版社" clearable style="width: 160px;" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="books" style="width: 100%" v-loading="loading" stripe>
      <el-table-column prop="title" label="书名" min-width="180" show-overflow-tooltip />
      <el-table-column prop="author" label="作者" min-width="120" />
      <el-table-column prop="isbn" label="ISBN" width="160" />
      <el-table-column prop="publisher" label="出版社" min-width="150" />
      <el-table-column prop="category" label="分类" width="100" align="center" />
      <el-table-column prop="publishDate" label="出版日期" width="120" align="center" />
      <el-table-column prop="totalStock" label="总库存" width="100" align="center" />
      <el-table-column prop="availableStock" label="可借" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.availableStock > 0 ? 'success' : 'danger'" size="small">
            {{ scope.row.availableStock }} 本
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right" align="center">
        <template #default="scope">
          <el-button
            type="primary"
            size="small"
            :disabled="scope.row.availableStock <= 0"
            @click="handleBorrow(scope.row)"
          >
            {{ scope.row.availableStock > 0 ? '借阅' : '缺货' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="fetchBooks"
      @current-change="fetchBooks"
      style="margin-top: 20px; justify-content: flex-end;"
    />

    <el-dialog v-model="borrowDialogVisible" title="确认借阅" width="400px">
      <div class="borrow-info">
        <p><strong>书名：</strong>{{ selectedBook?.title }}</p>
        <p><strong>作者：</strong>{{ selectedBook?.author }}</p>
        <p><strong>ISBN：</strong>{{ selectedBook?.isbn }}</p>
        <p><strong>出版社：</strong>{{ selectedBook?.publisher }}</p>
        <p><strong>可借数量：</strong>{{ selectedBook?.availableStock }} 本</p>
      </div>
      <template #footer>
        <el-button @click="borrowDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBorrow">确认借阅</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import apiService from '../service/api';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();
const books = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const borrowDialogVisible = ref(false);
const selectedBook = ref(null);

const searchForm = reactive({
  title: '',
  author: '',
  isbn: '',
  category: '',
  publisher: ''
});

const fetchBooks = async () => {
  loading.value = true;
  try {
    const params = {
      title: searchForm.title || undefined,
      author: searchForm.author || undefined,
      isbn: searchForm.isbn || undefined,
      category: searchForm.category || undefined,
      publisher: searchForm.publisher || undefined
    };
    const res = await apiService.searchBooks(currentPage.value, pageSize.value, params);
    books.value = res.records || [];
    total.value = res.total || 0;
  } catch (error) {
    console.error('Fetch books failed', error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchBooks();
};

const handleReset = () => {
  searchForm.title = '';
  searchForm.author = '';
  searchForm.isbn = '';
  searchForm.category = '';
  searchForm.publisher = '';
  currentPage.value = 1;
  fetchBooks();
};

const handleBorrow = (book) => {
  selectedBook.value = book;
  borrowDialogVisible.value = true;
};

const confirmBorrow = async () => {
  if (!selectedBook.value) return;

  try {
    await apiService.borrowBook(userStore.userId, selectedBook.value.id);
    ElMessage.success(`成功借阅《${selectedBook.value.title}》`);
    borrowDialogVisible.value = false;
    await fetchBooks();
  } catch (error) {
    console.error('Borrow failed', error);
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

.search-section {
  background-color: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.borrow-info p {
  margin: 8px 0;
  font-size: 14px;
}

.borrow-info strong {
  color: #606266;
}
</style>
