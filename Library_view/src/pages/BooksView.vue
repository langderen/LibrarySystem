<template>
  <div class="books-page">
    <div class="page-header">
      <h2>图书借阅列表</h2>
      <div class="header-actions">
        <el-button type="primary" @click="fetchBooks">刷新列表</el-button>
        <el-button type="success" @click="aiRecommendDialogVisible = true">AI推荐</el-button>
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

    <el-dialog v-model="aiRecommendDialogVisible" title="AI图书推荐" width="700px">
      <div class="ai-recommend-container">
        <div class="ai-messages" ref="aiMessagesRef">
          <div v-for="(msg, index) in aiMessages" :key="index" 
               :class="['message', msg.role === 'user' ? 'user-message' : 'ai-message']">
            <div class="message-content">
              <el-tag v-if="msg.role === 'user'" type="primary" size="small">你</el-tag>
              <el-tag v-else type="success" size="small">AI</el-tag>
              <div class="message-text" v-html="msg.content"></div>
            </div>
          </div>
          <div v-if="aiLoading" class="message ai-message">
            <div class="message-content">
              <el-tag type="success" size="small">AI</el-tag>
              <div class="message-text"><el-icon class="is-loading"><Loading /></el-icon> 正在思考...</div>
            </div>
          </div>
        </div>
        <div class="ai-input-area">
          <el-input
            v-model="aiQuestion"
            placeholder="描述你想要什么类型的书，例如：'我想看一些关于人工智能的书'"
            @keyup.enter="sendAiQuestion"
            :disabled="aiLoading"
            size="large"
          >
            <template #append>
              <el-button type="primary" @click="sendAiQuestion" :loading="aiLoading">
                <el-icon><Promotion /></el-icon> 提问
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { Loading, Promotion } from '@element-plus/icons-vue';
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

// AI推荐相关
const aiRecommendDialogVisible = ref(false);
const aiMessages = ref([
  { role: 'ai', content: '你好！我是图书推荐助手。请告诉我你想看什么类型的书，我会为你推荐合适的图书。' }
]);
const aiQuestion = ref('');
const aiLoading = ref(false);
const aiMessagesRef = ref(null);

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

const sendAiQuestion = async () => {
  if (!aiQuestion.value.trim() || aiLoading.value) return;

  const userQuestion = aiQuestion.value.trim();
  aiQuestion.value = '';

  aiMessages.value.push({ role: 'user', content: userQuestion });
  aiLoading.value = true;

  await nextTick();
  scrollToBottom();

  try {
    const res = await apiService.aiRecommend(userQuestion);
    const recommendedBooks = res.recommendedBooks || [];
    const explanation = res.explanation || '';

    let content = explanation;
    if (recommendedBooks.length > 0) {
      content += '<div class="recommended-books"><h4>📚 推荐图书：</h4><ul>';
      recommendedBooks.forEach(book => {
        content += `<li><strong>《${book.title}》</strong> - ${book.author} (${book.publisher}) - 分类：${book.category}</li>`;
      });
      content += '</ul></div>';
    } else {
      content += '<p>没有找到符合您要求的图书。</p>';
    }

    aiMessages.value.push({ role: 'ai', content });
  } catch (error) {
    console.error('AI推荐失败', error);
    aiMessages.value.push({ role: 'ai', content: '抱歉，推荐失败了。请换个问题试试。' });
  } finally {
    aiLoading.value = false;
    await nextTick();
    scrollToBottom();
  }
};

const scrollToBottom = () => {
  if (aiMessagesRef.value) {
    aiMessagesRef.value.scrollTop = aiMessagesRef.value.scrollHeight;
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

.ai-recommend-container {
  display: flex;
  flex-direction: column;
  height: 500px;
}

.ai-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 16px;
}

.message {
  margin-bottom: 16px;
  display: flex;
}

.user-message {
  justify-content: flex-end;
}

.ai-message {
  justify-content: flex-start;
}

.message-content {
  max-width: 80%;
  padding: 12px 16px;
  border-radius: 12px;
}

.user-message .message-content {
  background-color: #409eff;
  color: #fff;
}

.ai-message .message-content {
  background-color: #fff;
  border: 1px solid #e4e7ed;
}

.message-text {
  margin-top: 8px;
  line-height: 1.6;
}

.ai-input-area {
  display: flex;
  gap: 8px;
}

.recommended-books {
  margin-top: 12px;
}

.recommended-books h4 {
  margin: 8px 0;
  color: #67c23a;
}

.recommended-books ul {
  padding-left: 20px;
  margin: 8px 0;
}

.recommended-books li {
  margin: 6px 0;
}
</style>
