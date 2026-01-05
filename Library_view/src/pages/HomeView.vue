<template>
  <div class="home-page">
    <div class="welcome-section">
      <el-card shadow="hover">
        <div class="welcome-content">
          <h1>欢迎来到图书管理系统</h1>
          <p>探索知识的海洋，借阅您喜爱的图书，开启阅读之旅</p>
          <el-button type="primary" size="large" @click="router.push('/books')">
            <el-icon><Search /></el-icon>
            开始探索图书
          </el-button>
        </div>
      </el-card>
    </div>

    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.bookCount }}</div>
              <div class="stat-label">图书总数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <el-icon :size="28"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.borrowCount }}</div>
              <div class="stat-label">借阅中</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">注册用户</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
              <el-icon :size="28"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.returnedCount }}</div>
              <div class="stat-label">已归还</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="features-section">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span><el-icon><Star /></el-icon> 热门图书</span>
              </div>
            </template>
            <div class="book-list">
              <div v-for="book in hotBooks" :key="book.id" class="book-item" @click="goToBookDetail(book)">
                <div class="book-info">
                  <div class="book-title">{{ book.title }}</div>
                  <div class="book-author">{{ book.author }}</div>
                  <div class="book-meta">
                    <el-tag size="small" :type="book.availableStock > 0 ? 'success' : 'info'">
                      {{ book.availableStock > 0 ? '可借阅' : '已借完' }}
                    </el-tag>
                  </div>
                </div>
              </div>
              <el-empty v-if="hotBooks.length === 0" description="暂无热门图书" :image-size="80" />
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span><el-icon><Clock /></el-icon> 最新上架</span>
              </div>
            </template>
            <div class="book-list">
              <div v-for="book in newBooks" :key="book.id" class="book-item" @click="goToBookDetail(book)">
                <div class="book-info">
                  <div class="book-title">{{ book.title }}</div>
                  <div class="book-author">{{ book.author }}</div>
                  <div class="book-meta">
                    <el-tag size="small" type="primary">新书上架</el-tag>
                  </div>
                </div>
              </div>
              <el-empty v-if="newBooks.length === 0" description="暂无新书" :image-size="80" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="quick-actions">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span><el-icon><Grid /></el-icon> 快捷功能</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="action-item" @click="router.push('/books')">
              <el-icon :size="32"><Search /></el-icon>
              <span>图书查询</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="action-item" @click="router.push('/profile')">
              <el-icon :size="32"><UserFilled /></el-icon>
              <span>个人中心</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="action-item" @click="router.push('/books')">
              <el-icon :size="32"><ShoppingCart /></el-icon>
              <span>我的借阅</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="action-item" @click="handleHelp">
              <el-icon :size="32"><QuestionFilled /></el-icon>
              <span>帮助中心</span>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Search, Document, Reading, User, CircleCheck, Star, Clock, Grid, UserFilled, ShoppingCart, QuestionFilled } from '@element-plus/icons-vue';
import apiService from '../service/api';

const router = useRouter();

const stats = ref({
  bookCount: 0,
  borrowCount: 0,
  userCount: 0,
  returnedCount: 0
});

const hotBooks = ref([]);
const newBooks = ref([]);

const fetchData = async () => {
  try {
    const booksRes = await apiService.getBooks(1, 100);
    const books = booksRes.records || booksRes || [];
    
    stats.value.bookCount = books.length;
    stats.value.borrowCount = books.filter(b => b.availableStock < b.totalStock).length;
    stats.value.returnedCount = books.filter(b => b.availableStock === b.totalStock).length;
    stats.value.userCount = 128;
    
    hotBooks.value = books.slice(0, 4);
    newBooks.value = books.slice(-4).reverse();
  } catch (error) {
    console.error('获取数据失败', error);
  }
};

const goToBookDetail = (book) => {
  router.push('/books');
};

const handleHelp = () => {
  router.push('/books');
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.home-page {
  padding: 20px 0;
}

.welcome-section {
  margin-bottom: 30px;
}

.welcome-content {
  text-align: center;
  padding: 20px;
}

.welcome-content h1 {
  font-size: 2.5rem;
  color: #1a1a2e;
  margin-bottom: 16px;
}

.welcome-content p {
  font-size: 1.2rem;
  color: #6b7280;
  margin-bottom: 30px;
}

.stats-section {
  margin-bottom: 30px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: bold;
  color: #1a1a2e;
}

.stat-label {
  font-size: 0.9rem;
  color: #6b7280;
  margin-top: 4px;
}

.features-section {
  margin-bottom: 30px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.1rem;
  font-weight: 500;
}

.book-list {
  max-height: 320px;
  overflow-y: auto;
}

.book-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 8px;
}

.book-item:hover {
  background: #f5f7fa;
}

.book-cover {
  width: 50px;
  height: 70px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 16px;
  background: #e5e7eb;
}

.book-info {
  flex: 1;
}

.book-title {
  font-size: 1rem;
  font-weight: 500;
  color: #1a1a2e;
  margin-bottom: 4px;
}

.book-author {
  font-size: 0.85rem;
  color: #6b7280;
  margin-bottom: 8px;
}

.book-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quick-actions .action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  color: #6b7280;
}

.quick-actions .action-item:hover {
  background: #f5f7fa;
  color: #409eff;
}

.quick-actions .action-item span {
  margin-top: 8px;
  font-size: 0.9rem;
}
</style>