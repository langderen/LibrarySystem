import axios from 'axios';
import { ElMessage } from 'element-plus';

// 创建 axios 实例
const api = axios.create({
  // ！！！请将此处的 URL 替换为你的后端服务地址
  baseURL: 'http://localhost:8080/api', 
  timeout: 5000, // 请求超时时间
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

  // --- 图书相关 ---
  getBooks(page:any,size:any, keyword?:string) {
    let url = `/books?page=${page}&size=${size}`;
    if (keyword) {
      url += `&keyword=${encodeURIComponent(keyword)}`;
    }
    return api.get(url);
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


  // --- 新增：管理员图书管理接口 ---
  addBook(bookData: any) {
    return api.post('/books', bookData);
  },
  updateBook(bookData: any) {
    return api.put('/books', bookData);
  },
  deleteBook(bookId: any) {
    return api.delete(`/books/${bookId}`);
  }
};

export default apiService;