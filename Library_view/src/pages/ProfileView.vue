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
            <el-table-column prop="bookTitle" label="书名" min-width="150" show-overflow-tooltip />
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
                <div class="header-actions">
                  <el-button type="primary" size="small" @click="openEditProfileDialog">编辑信息</el-button>
                  <el-button type="danger" size="small" @click="handleLogout">注销</el-button>
                </div>
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
                <el-col :span="12">
                  <el-form-item label="性别">
                    <el-input :value="userStore.sex || '未知'" disabled />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="角色">
                    <el-tag :type="userStore.isAdmin ? 'danger' : 'primary'" size="small">
                      {{ userStore.isAdmin ? '管理员' : '普通用户' }}
                    </el-tag>
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

          <div class="search-section">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <el-form-item label="书名">
                <el-input v-model="searchForm.title" placeholder="请输入书名" clearable style="width: 150px;" />
              </el-form-item>
              <el-form-item label="作者">
                <el-input v-model="searchForm.author" placeholder="请输入作者" clearable style="width: 130px;" />
              </el-form-item>
              <el-form-item label="ISBN">
                <el-input v-model="searchForm.isbn" placeholder="请输入ISBN" clearable style="width: 150px;" />
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
                <el-input v-model="searchForm.publisher" placeholder="请输入出版社" clearable style="width: 150px;" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">搜索</el-button>
                <el-button @click="handleReset">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-table :data="allBooks" border stripe style="width: 100%" v-loading="loadingBooks">
            <el-table-column prop="id" label="ID" width="60" align="center" />
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
                  {{ scope.row.availableStock }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right" align="center">
              <template #default="scope">
                <el-button type="primary" link size="small" @click="openBookDialog(scope.row)">编辑</el-button>
                <el-button type="info" link size="small" @click="viewBorrowRecords(scope.row)">查看借阅</el-button>
                <el-button type="danger" link size="small" @click="handleDeleteBook(scope.row)">删除</el-button>
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
        </el-tab-pane>

        <el-tab-pane label="用户管理" name="userManage" v-if="userStore.isAdmin">
          <div class="admin-toolbar">
            <el-button type="primary" icon="Plus" @click="openUserDialog()">新增用户</el-button>
            <el-button icon="Refresh" @click="fetchUsers">刷新</el-button>
          </div>

          <div class="search-section">
            <el-form :inline="true" :model="userSearchForm" class="search-form">
              <el-form-item label="用户名">
                <el-input v-model="userSearchForm.username" placeholder="请输入用户名" clearable style="width: 140px;" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userSearchForm.email" placeholder="请输入邮箱" clearable style="width: 180px;" />
              </el-form-item>
              <el-form-item label="角色">
                <el-select v-model="userSearchForm.role" placeholder="请选择角色" clearable style="width: 120px;">
                  <el-option label="管理员" value="ADMIN" />
                  <el-option label="普通用户" value="USER" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUserSearch">搜索</el-button>
                <el-button @click="handleUserReset">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-table :data="allUsers" border stripe style="width: 100%" v-loading="loadingUsers">
            <el-table-column prop="id" label="ID" width="60" align="center" />
            <el-table-column prop="username" label="用户名" min-width="120" />
            <el-table-column prop="email" label="邮箱" min-width="180" />
            <el-table-column prop="phone" label="手机号" width="150" />
            <el-table-column prop="sex" label="性别" width="80" align="center">
              <template #default="scope">
                <el-tag v-if="scope.row.sex === '男'" type="primary" size="small">男</el-tag>
                <el-tag v-else-if="scope.row.sex === '女'" type="danger" size="small">女</el-tag>
                <el-tag v-else type="info" size="small">未知</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="role" label="角色" width="100" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'primary'" size="small">
                  {{ scope.row.role === 'ADMIN' ? '管理员' : '普通用户' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="registerDate" label="注册日期" width="120" align="center" />
            <el-table-column label="操作" width="150" fixed="right" align="center">
              <template #default="scope">
                <el-button type="primary" link size="small" @click="openUserDialog(scope.row)">编辑</el-button>
                <el-button type="danger" link size="small" @click="handleDeleteUser(scope.row)" :disabled="scope.row.role === 'ADMIN'">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="userCurrentPage"
            v-model:page-size="userPageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="userTotal"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchUsers"
            @current-change="fetchUsers"
            style="margin-top: 20px; justify-content: flex-end;"
          />
        </el-tab-pane>

      </el-tabs>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? '编辑图书' : '新增图书'"
      width="550px"
    >
      <el-form :model="bookForm" label-width="100px" :rules="bookRules" ref="bookFormRef">
        <el-form-item label="书名" prop="title">
          <el-input v-model="bookForm.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="bookForm.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="bookForm.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="出版日期" prop="publishDate">
          <el-date-picker
            v-model="bookForm.publishDate"
            type="date"
            placeholder="选择出版日期"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="bookForm.category" placeholder="请选择分类" style="width: 100%;">
            <el-option label="计算机" value="计算机" />
            <el-option label="文学" value="文学" />
            <el-option label="历史" value="历史" />
            <el-option label="科学" value="科学" />
            <el-option label="艺术" value="艺术" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="总库存" prop="totalStock">
          <el-input-number v-model="bookForm.totalStock" :min="0" :max="10000" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBook">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
      v-model="userDialogVisible"
      :title="isUserEditMode ? '编辑用户' : '新增用户'"
      width="500px"
    >
      <el-form :model="userForm" label-width="100px" :rules="userRules" ref="userFormRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" :disabled="isUserEditMode" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="userForm.sex">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%;">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="普通用户" value="USER" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="userDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUser">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
      v-model="profileDialogVisible"
      title="编辑个人信息"
      width="450px"
    >
      <el-form :model="profileForm" label-width="100px" :rules="profileRules" ref="profileFormRef">
        <el-form-item label="用户名">
          <el-input :value="userStore.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="profileForm.sex">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="profileDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitProfile">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
      v-model="borrowRecordDialogVisible"
      :title="`借阅记录 - 《${currentBookTitle}》`"
      width="700px"
    >
      <el-table :data="bookBorrowRecords" border stripe style="width: 100%" v-loading="loadingBookBorrowRecords">
        <el-table-column prop="id" label="记录ID" width="80" align="center" />
        <el-table-column prop="userId" label="用户ID" width="100" align="center" />
        <el-table-column prop="userName" label="用户名" width="120" />
        <el-table-column prop="borrowTime" label="借阅日期" width="180" />
        <el-table-column prop="returnTime" label="归还日期" width="180">
          <template #default="scope">
            {{ scope.row.returnTime ? scope.row.returnTime : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'warning' : 'success'" size="small">
              {{ scope.row.status === 0 ? '未归还' : '已归还' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loadingBookBorrowRecords && bookBorrowRecords.length === 0" description="暂无借阅记录" />
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import apiService from '../service/api';
import { useUserStore } from '../stores/user';

const userStore = useUserStore();
const activeTab = ref('borrows');
const bookFormRef = ref(null);
const userFormRef = ref(null);
const profileFormRef = ref(null);

// 借阅记录查看相关
const borrowRecordDialogVisible = ref(false);
const bookBorrowRecords = ref([]);
const loadingBookBorrowRecords = ref(false);
const currentBookTitle = ref('');
const currentBookId = ref(null);

const viewBorrowRecords = async (row) => {
  currentBookTitle.value = row.title;
  currentBookId.value = row.id;
  borrowRecordDialogVisible.value = true;
  loadingBookBorrowRecords.value = true;
  
  try {
    const res = await apiService.getBorrowRecordsByBookId(row.id);
    bookBorrowRecords.value = res || [];
  } catch (error) {
    console.error(error);
    ElMessage.error('获取借阅记录失败');
  } finally {
    loadingBookBorrowRecords.value = false;
  }
};

// 个人编辑相关
const profileDialogVisible = ref(false);
const profileForm = reactive({
  email: '',
  phone: '',
  sex: ''
});

const profileRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
};

const openEditProfileDialog = () => {
  profileForm.email = userStore.userEmail || '';
  profileForm.phone = userStore.userPhone || '';
  profileForm.sex = userStore.sex || '';
  profileDialogVisible.value = true;
};

const submitProfile = async () => {
  if (!profileFormRef.value) return;

  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await apiService.updateUser({
          id: userStore.userId,
          username: userStore.username,
          email: profileForm.email,
          phone: profileForm.phone,
          sex: profileForm.sex
        });
        userStore.setUser({
          userEmail: profileForm.email,
          userPhone: profileForm.phone,
          sex: profileForm.sex
        });
        ElMessage.success('信息更新成功');
        profileDialogVisible.value = false;
      } catch (error) {
        console.error(error);
        ElMessage.error('更新失败');
      }
    }
  });
};

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要注销登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    userStore.clearUser();
    window.location.href = '/login';
  } catch {}
};

// 借阅列表逻辑
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
    await apiService.returnBook(book.id);
    ElMessage.success(`归还成功`);
    fetchBorrowedBooks();
  } catch (error) {}
};

// 管理员图书管理逻辑
const allBooks = ref([]);
const loadingBooks = ref(false);
const dialogVisible = ref(false);
const isEditMode = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const searchForm = reactive({
  title: '',
  author: '',
  isbn: '',
  category: '',
  publisher: ''
});

const bookForm = reactive({
  id: null,
  title: '',
  author: '',
  isbn: '',
  publisher: '',
  publishDate: '',
  category: '',
  totalStock: 0,
  availableStock: 0
});

const bookRules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
  publisher: [{ required: true, message: '请输入出版社', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  totalStock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }]
};

const fetchBooks = async () => {
  if (!userStore.isAdmin) return;
  loadingBooks.value = true;
  try {
    const params = {
      title: searchForm.title || undefined,
      author: searchForm.author || undefined,
      isbn: searchForm.isbn || undefined,
      category: searchForm.category || undefined,
      publisher: searchForm.publisher || undefined
    };
    const res = await apiService.searchBooks(currentPage.value, pageSize.value, params);
    allBooks.value = res.records || [];
    total.value = res.total || 0;
  } catch (error) {
    console.error(error);
  } finally {
    loadingBooks.value = false;
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

const openBookDialog = (row = null) => {
  if (row) {
    isEditMode.value = true;
    Object.assign(bookForm, {
      id: row.id,
      title: row.title,
      author: row.author,
      isbn: row.isbn,
      publisher: row.publisher,
      publishDate: row.publishDate,
      category: row.category,
      totalStock: row.totalStock,
      availableStock: row.availableStock
    });
  } else {
    isEditMode.value = false;
    Object.assign(bookForm, {
      id: null,
      title: '',
      author: '',
      isbn: '',
      publisher: '',
      publishDate: '',
      category: '',
      totalStock: 0,
      availableStock: 0
    });
  }
  dialogVisible.value = true;
};

const submitBook = async () => {
  if (!bookFormRef.value) return;

  await bookFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEditMode.value) {
          await apiService.updateBook(bookForm);
          ElMessage.success('更新成功');
        } else {
          bookForm.availableStock = bookForm.totalStock;
          await apiService.addBook(bookForm);
          ElMessage.success('添加成功');
        }
        dialogVisible.value = false;
        fetchBooks();
      } catch (error) {
        console.error(error);
      }
    }
  });
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

// 管理员借阅管理逻辑
const allBorrowRecords = ref([]);
const loadingBorrowManage = ref(false);

const fetchAllBorrowRecords = async () => {
  if (!userStore.isAdmin) return;
  loadingBorrowManage.value = true;
  try {
    const res = await apiService.getAllBorrowRecords();
    allBorrowRecords.value = res || [];
  } catch (error) {
    console.error(error);
  } finally {
    loadingBorrowManage.value = false;
  }
};

const remindReturnBook = async (record) => {
  ElMessageBox.confirm(
    `确定要向用户 ${record.userId} 发送《${record.bookTitle}》的催还通知吗?`,
    '确认',
    { confirmButtonText: '发送催还', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await apiService.remindReturn(record.id);
      ElMessage.success('催还通知已发送');
    } catch (error) {}
  });
};

// 管理员用户管理逻辑
const allUsers = ref([]);
const loadingUsers = ref(false);
const userDialogVisible = ref(false);
const isUserEditMode = ref(false);
const userCurrentPage = ref(1);
const userPageSize = ref(10);
const userTotal = ref(0);

const userSearchForm = reactive({
  username: '',
  email: '',
  role: ''
});

const userForm = reactive({
  id: null,
  username: '',
  password: '',
  email: '',
  phone: '',
  sex: '',
  role: 'USER'
});

const userRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
};

const fetchUsers = async () => {
  if (!userStore.isAdmin) return;
  loadingUsers.value = true;
  try {
    const params = {
      username: userSearchForm.username || undefined,
      email: userSearchForm.email || undefined,
      role: userSearchForm.role || undefined
    };
    const res = await apiService.getAllUsers(userCurrentPage.value, userPageSize.value, params);
    allUsers.value = res.records || [];
    userTotal.value = res.records?.length || 0;
  } catch (error) {
    console.error(error);
  } finally {
    loadingUsers.value = false;
  }
};

const handleUserSearch = () => {
  userCurrentPage.value = 1;
  fetchUsers();
};

const handleUserReset = () => {
  userSearchForm.username = '';
  userSearchForm.email = '';
  userSearchForm.role = '';
  userCurrentPage.value = 1;
  fetchUsers();
};

const openUserDialog = (row = null) => {
  if (row) {
    isUserEditMode.value = true;
    Object.assign(userForm, {
      id: row.id,
      username: row.username,
      password: '',
      email: row.email,
      phone: row.phone || '',
      sex: row.sex || '',
      role: row.role
    });
  } else {
    isUserEditMode.value = false;
    Object.assign(userForm, {
      id: null,
      username: '',
      password: '',
      email: '',
      phone: '',
      sex: '',
      role: 'USER'
    });
  }
  userDialogVisible.value = true;
};

const submitUser = async () => {
  if (!userFormRef.value) return;

  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isUserEditMode.value) {
          if (userForm.password) {
            await apiService.updateUser(userForm);
          } else {
            const updateData = { ...userForm };
            delete updateData.password;
            await apiService.updateUser(updateData);
          }
          ElMessage.success('更新成功');
        } else {
          await apiService.register({
            username: userForm.username,
            password: userForm.password,
            email: userForm.email,
            phone: userForm.phone,
            sex: userForm.sex,
            role: userForm.role
          });
          ElMessage.success('添加成功');
        }
        userDialogVisible.value = false;
        fetchUsers();
      } catch (error) {
        console.error(error);
      }
    }
  });
};

const handleDeleteUser = (row) => {
  ElMessageBox.confirm(
    `确定要删除用户「${row.username}」吗?`,
    '警告',
    { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await apiService.deleteUser(row.id);
      ElMessage.success('删除成功');
      fetchUsers();
    } catch (error) {}
  });
};

onMounted(() => {
  fetchBorrowedBooks();
  if (userStore.isAdmin) {
    fetchBooks();
    fetchUsers();
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
.search-section {
  background-color: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 15px;
}
.search-form {
  margin-bottom: 0;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-actions {
  display: flex;
  gap: 10px;
}
</style>
