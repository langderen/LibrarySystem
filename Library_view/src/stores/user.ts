// src/stores/user.ts
import { defineStore } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
// 1. 定义状态接口
export interface UserState {
  userId: string
  username: string
  userEmail: string
  isFinited: boolean
  userPhone: string
  token: string
  role: string 
}

// 2. 使用对象式 state（而非函数），并显式标注类型
export const useUserStore = defineStore('user', {
  // ✅ 启用持久化（v3 写法）
  persist: true, // 最简：整个 state 持久化到 localStorage


  // ✅ 使用对象字面量 + 断言，确保类型推导
  state: (): UserState => ({
    userId: '',
    username: '',
    userEmail: '',
    isFinited: false,
    token: '',
    userPhone: '',
    role: ''
  }),

  getters: {
    getUserName(state): string {
      return state.username || state.userId.toUpperCase()
    },
    isAdmin(state): boolean {
      return state.role === 'admin'; 
    }
  },

  actions: {
    setUserId(value: string) {
      if (value?.trim()) {
        this.userId = value.trim()
      }
    },
    setUser(data: Partial<UserState>) {
      this.userId = data.userId || this.userId
      this.username = data.username || this.username
      this.userEmail = data.userEmail || this.userEmail
      this.token = data.token || this.token
      this.role = data.role || this.role
      this.userPhone = data.userPhone || this.userPhone
      this.isFinited = true
    },
    clearUser() {
      this.$reset() // 👈 Pinia 内置方法，重置为初始 state
    }
  }
})
