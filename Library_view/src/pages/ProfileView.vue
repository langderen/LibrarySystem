<template>
  <div class="profile-page">
    <div class="header-section">
      <h2>个人中心</h2>
      <el-tag v-if="userStore.isAdmin" type="danger" effect="dark">管理员模式</el-tag>
    </div>

    <el-card shadow="never">
      <el-tabs v-model="activeTab">

        <el-tab-pane label="我的借阅" name="borrows">
          <el-table :data="borrowedBooks" border style="width: 100%" v-loading="loadingBorrow">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="bookTitle" label="书名" min-width="150" />
            <el-table-column prop="isbn" label="ISBN" width="140" />
            <el-table-column prop="bookAuthor" label="作者" min-width="100" />
            <el-table-column prop="borrowTime" label="借阅日期" width="180" />
            <el-table-column prop="returnTime" label="归还日期" width="180" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="scope">
                <el-button v-if="scope.row.status === 0" type="primary" size="small" @click="returnBook(scope.row)">归还</el-button>
                 <span v-else class="returned-text">已归还</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

<el-tab-pane label="我的信息" name="info">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>用户基本信息</span>
        </div>
      </template>

      <el-form :model="userStore" label-width="100px" style="width: 100%">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户ID">
              <el-input :value="userStore.userId || 'N/A'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名">
              <el-input :value="userStore.username || 'N/A'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input :value="userStore.userEmail || 'N/A'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input :value="userStore.userPhone || 'N/A'" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </el-tab-pane>


        <el-tab-pane label="图书管理" name="admin" v-if="userStore.isAdmin">
          <div class="admin-toolbar">
            <el-button type="primary" icon="Plus" @click="openBookDialog()">新增图书</el-button>
            <el-button icon="Refresh" @click="fetchBooks">刷新</el-button>
          </div>
          
          <el-table :data="allBooks" border stripe style="width: 100%" v-loading="loadingBooks">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="title" label="书名" min-width="150" />
            <el-table-column prop="author" label="作者" min-width="100" />
            <el-table-column prop="isbn" label="ISBN" width="140" />
            <el-table-column prop="publisher" label="出版社" min-width="120" />
            <el-table-column prop="availableStock" label="库存" width="100" />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="scope">
                <el-button type="primary" link size="small" @click="openBookDialog(scope.row)">编辑</el-button>
                <el-button type="danger" link size="small" @click="handleDeleteBook(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      
      
      </el-tabs>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? '编辑图书' : '新增图书'"
      width="500px"
    >
      <el-form :model="bookForm" label-width="80px">
        <el-form-item label="书名" required>
          <el-input v-model="bookForm.title" />
        </el-form-item>
        <el-form-item label="作者" required>
          <el-input v-model="bookForm.author" />
        </el-form-item>
        <el-form-item label="ISBN" required>
          <el-input v-model="bookForm.isbn" />
        </el-form-item>
        <el-form-item label="出版社" required>
          <el-input v-model="bookForm.publisher" />
        </el-form-item>
        <el-form-item label="库存" required>
          <el-input-number v-model="bookForm.totalStock" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBook">确定</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import apiService from '../service/api';
import { useUserStore } from '../stores/user';

const userStore = useUserStore();
const activeTab = ref('borrows');

// --- 借阅列表逻辑 ---
const borrowedBooks = ref([]);
const loadingBorrow = ref(false);

const fetchBorrowedBooks = async () => {
  loadingBorrow.value = true;
  try {
    const res = await apiService.getBorrowedBooks(userStore.userId);
    borrowedBooks.value = res || [];
  } catch (error) {
    console.error(error);
  } finally {
    loadingBorrow.value = false;
  }
};

const returnBook = async (book) => {
  try {
    await apiService.returnBook(book.id); // 假设借阅记录ID即为操作ID，或者应该是 book.bookId
    ElMessage.success(`归还成功`);
    fetchBorrowedBooks();
  } catch (error) {}
};

// --- 管理员图书管理逻辑 ---
const allBooks = ref([]);
const loadingBooks = ref(false);
const dialogVisible = ref(false);
const isEditMode = ref(false);

const bookForm = reactive({
  id: null,
  title: '',
  author: '',
  isbn: '',
  publisher: '',
  availableStock: 0,
  totalStock: 0
});

const fetchBooks = async () => {
  
  if (!userStore.isAdmin) return; // 只有管理员才拉取所有图书
  loadingBooks.value = true;
  try {
    const res = await apiService.getBooks(1, 10);
    allBooks.value = res.records || res; // 兼容分页返回
  } catch (error) {
    console.error(error);
  } finally {
    loadingBooks.value = false;
  }
};

const openBookDialog = (row = null) => {
  if (row) {
    isEditMode.value = true;
    Object.assign(bookForm, row); // 填充表单
  } else {
    isEditMode.value = false;
    // 重置表单
    bookForm.id = null;
    bookForm.title = '';
    bookForm.author = '';
    bookForm.isbn = '';
    bookForm.publisher = '';
    bookForm.availableStock = 0;
    bookForm.totalStock = 0;
  }
  dialogVisible.value = true;
};

const submitBook = async () => {
  try {
    if (isEditMode.value) {
      await apiService.updateBook(bookForm);
      ElMessage.success('更新成功');
    } else {
      bookForm.availableStock = bookForm.totalStock; // 新增时库存等于总库存
      await apiService.addBook(bookForm);
      ElMessage.success('添加成功');
    }
    dialogVisible.value = false;
    fetchBooks(); // 刷新列表
  } catch (error) {
    console.error(error);
  }
};

const handleDeleteBook = (row) => {
  ElMessageBox.confirm(
    `确定要删除图书《${row.title}》吗?`,
    '警告',
    { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await apiService.deleteBook(row.id);
      ElMessage.success('删除成功');
      fetchBooks();
    } catch (error) {}
  });
};

// 页面加载
onMounted(() => {
  fetchBorrowedBooks();
  if (userStore.isAdmin) {
    fetchBooks();
  }
});
</script>

<style scoped>
.profile-page {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
}
.header-section {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}
.admin-toolbar {
  margin-bottom: 15px;
  display: flex;
  gap: 10px;
}
</style>