import { defineStore } from 'pinia'

interface AppState {
  collapsed: boolean
  device: 'desktop' | 'mobile'
  theme: 'light' | 'dark'
  size: 'large' | 'default' | 'small'
  language: 'zh-cn' | 'en'
  loading: boolean
}

export const useAppStore = defineStore('app', {
  state: (): AppState => ({
    collapsed: false,
    device: 'desktop',
    theme: 'light',
    size: 'default',
    language: 'zh-cn',
    loading: false
  }),

  getters: {
    isMobile: (state) => state.device === 'mobile',
    isDark: (state) => state.theme === 'dark'
  },

  actions: {
    // 切换侧边栏
    toggleSidebar(): void {
      this.collapsed = !this.collapsed
    },

    // 设置设备类型
    setDevice(device: 'desktop' | 'mobile'): void {
      this.device = device
    },

    // 切换主题
    toggleTheme(): void {
      this.theme = this.theme === 'light' ? 'dark' : 'light'
    },

    // 设置组件尺寸
    setSize(size: 'large' | 'default' | 'small'): void {
      this.size = size
    },

    // 设置语言
    setLanguage(language: 'zh-cn' | 'en'): void {
      this.language = language
    },

    // 设置全局加载状态
    setLoading(loading: boolean): void {
      this.loading = loading
    }
  },

  persist: {
    key: 'app-settings',
    storage: localStorage,
    paths: ['collapsed', 'theme', 'size', 'language']
  }
})
