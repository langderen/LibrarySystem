import axios from 'axios';
import { ElMessage } from 'element-plus';

// 创建 axios 实例
const api = axios.create({
  // ！！！请将此处的 URL 替换为你的后端服务地址
  baseURL: 'http://localhost:8080/api', 
  timeout: 15000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
});

// 请求拦截器：在发送请求前做些什么
api.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token 并添加到请求头
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['satoken'] = token; 
    }
    return config;
  },
  error => {
    console.error('Request Error:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器：对响应数据做点什么
api.interceptors.response.use(
  response => {
    // 假设后端返回的数据结构为 { code: 200, message: 'success', data: ... }
    const res = response.data;
    if (response.status !== 200) {
      ElMessage.error(res.message || '请求失败');
      return Promise.reject(new Error(res.message || 'Error'));
    } else {
      return res; // 只返回成功的数据部分
    }
  },
  error => {
    console.error('Response Error:', error.response);
    if (error.response && error.response.status === 401) {
      // 401 未授权，通常是 token 过期或无效，清除本地存储并重定向到登录页
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
      ElMessage.warning('登录已过期，请重新登录');
    } else {
      ElMessage.error(error.message || '服务器错误');
    }
    return Promise.reject(error);
  }
);

// 封装 API 方法
const apiService = {
  // --- 用户相关 ---
  login(username: any, password: any) {
    return api.post('/users/login', { username, password });
  },
  register(user: any) {
    return api.post('/users/register', user);
  },
  getProfile() {
    return api.get(`/users/profile`);
  },

  // --- 管理员用户管理接口 ---
  getAllUsers(page: any, size: any, params?: { username?: string; email?: string; role?: string }) {
    let url = `/users/admin/all?page=${page}&size=${size}`;
    if (params) {
      if (params.username) url += `&username=${encodeURIComponent(params.username)}`;
      if (params.email) url += `&email=${encodeURIComponent(params.email)}`;
      if (params.role) url += `&role=${encodeURIComponent(params.role)}`;
    }
    return api.get(url);
  },
  getUserById(userId: any) {
    return api.get(`/users/admin/${userId}`);
  },
  updateUser(userData: any) {
    return api.put('/users/admin/update', userData);
  },
  deleteUser(userId: any) {
    return api.delete(`/users/admin/${userId}`);
  },

  // --- 图书相关 ---
  getBooks(page:any,size:any, keyword?:string) {
    let url = `/books?page=${page}&size=${size}`;
    if (keyword) {
      url += `&keyword=${encodeURIComponent(keyword)}`;
    }
    return api.get(url);
  },
  searchBooks(page:any, size:any, params?: {title?:string, author?:string, isbn?:string, category?:string, publisher?:string}) {
    let url = `/books/search?page=${page}&size=${size}`;
    if (params) {
      if (params.title) url += `&title=${encodeURIComponent(params.title)}`;
      if (params.author) url += `&author=${encodeURIComponent(params.author)}`;
      if (params.isbn) url += `&isbn=${encodeURIComponent(params.isbn)}`;
      if (params.category) url += `&category=${encodeURIComponent(params.category)}`;
      if (params.publisher) url += `&publisher=${encodeURIComponent(params.publisher)}`;
    }
    return api.get(url);
  },
  getBookById(bookId: any) {
    return api.get(`/books/${bookId}`);
  },
  borrowBook(userId: any, bookId: any) {
    return api.post(`/borrows?userId=${userId}&bookId=${bookId} `);
  },
  returnBook(recordId: any) {
    return api.put(`/borrows/return?recordId=${recordId}`);
  },
  getBorrowedBooks(userId: any) {
    return api.get(`/borrows/getBrrowByUserId?userId=${userId}`);
  },
  checkOverdueBooks(userId: any) {
    return api.get(`/borrows/overdue?userId=${userId}`);
  },
  getAllBorrowRecords() {
    return api.get(`/borrows/admin/all`);
  },
  getBorrowRecordsByBookId(bookId: any) {
    return api.get(`/borrows/admin/byBookId/${bookId}`);
  },
  remindReturn(recordId: any) {
    return api.post(`/borrows/admin/remind?recordId=${recordId}`);
  },


  // --- 新增：管理员图书管理接口 ---
  addBook(bookData: any) {
    return api.post('/books', bookData);
  },
  updateBook(bookData: any) {
    return api.put('/books', bookData);
  },
  deleteBook(bookId: any) {
    return api.delete(`/books/${bookId}`);
  },

  // --- AI 推荐接口 ---
  aiRecommend(question: string) {
    return api.post('/ai/recommend', { question });
  }
};

export default apiService;