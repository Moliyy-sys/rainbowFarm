<template>
  <div class="modern-navbar">
    <!-- 左侧操作区 -->
    <div class="navbar-left">
      <!-- 侧边栏切换按钮 -->
      <div class="sidebar-toggle-btn" @click="toggleSidebar">
        <el-icon class="toggle-icon">
          <Fold v-if="!appStore.collapsed" />
          <Expand v-else />
        </el-icon>
      </div>

      <!-- 智能面包屑导航 -->
      <nav class="smart-breadcrumb" aria-label="页面导航">
        <div class="breadcrumb-wrapper">
          <template v-for="(item, index) in breadcrumbs" :key="item.path">
            <div 
            :class="[
              'breadcrumb-item',
                { 'is-active': index === breadcrumbs.length - 1 }
              ]"
              @click="navigateToPath(item, index)"
            >
              <div class="breadcrumb-inner">
                <el-icon v-if="item.meta?.icon && index === 0" class="breadcrumb-icon">
                  <component :is="item.meta.icon" />
                </el-icon>
                <span class="breadcrumb-label">{{ item.meta?.title }}</span>
              </div>
            </div>
            <div 
              v-if="index < breadcrumbs.length - 1" 
              class="breadcrumb-separator"
            >
              <el-icon class="separator-icon">
                <ArrowRight />
              </el-icon>
            </div>
          </template>
        </div>
      </nav>
    </div>

    <!-- 右侧工具栏 -->
    <div class="navbar-right">
      <!-- 搜索按钮 -->
      <div class="action-btn search-btn" @click="handleSearch">
        <el-icon class="btn-icon">
          <Search />
        </el-icon>
      </div>

      <!-- 全屏切换 -->
      <div class="action-btn fullscreen-btn" @click="toggleFullscreen">
        <el-icon class="btn-icon">
            <FullScreen />
          </el-icon>
      </div>

      <!-- 主题切换 -->
      <div class="action-btn theme-btn" @click="toggleTheme">
        <el-icon class="btn-icon">
            <Sunny v-if="appStore.theme === 'light'" />
            <Moon v-else />
          </el-icon>
      </div>

      <!-- 消息通知 -->
      <div class="action-btn notification-btn" @click="handleNotifications">
        <el-icon class="btn-icon">
          <Bell />
        </el-icon>
      </div>

      <!-- 分隔线 -->
      <div class="navbar-divider"></div>

      <!-- 用户信息下拉菜单 -->
      <el-dropdown 
        trigger="click" 
        @command="handleCommand"
        placement="bottom-end"
        class="user-dropdown"
      >
        <div class="user-profile">
          <div class="user-avatar-wrapper">
            <el-avatar :size="38" :src="userStore.avatar" class="user-avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
            <div class="online-indicator"></div>
          </div>
          <div class="user-info">
            <span class="user-name">{{ userStore.nickName || userStore.userName }}</span>
            <span class="user-role">管理员</span>
          </div>
          <el-icon class="dropdown-arrow">
            <ArrowDown />
          </el-icon>
        </div>
        
        <template #dropdown>
          <el-dropdown-menu class="user-dropdown-menu">
            <el-dropdown-item command="profile" class="dropdown-item">
              <el-icon class="item-icon"><User /></el-icon>
              <span>个人中心</span>
            </el-dropdown-item>
            <el-dropdown-item command="settings" class="dropdown-item">
              <el-icon class="item-icon"><Setting /></el-icon>
              <span>系统设置</span>
            </el-dropdown-item>
            <el-dropdown-item command="help" class="dropdown-item">
              <el-icon class="item-icon"><QuestionFilled /></el-icon>
              <span>帮助中心</span>
            </el-dropdown-item>
            <el-dropdown-item divided command="logout" class="dropdown-item logout-item">
              <el-icon class="item-icon"><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useAppStore } from '@/store/modules/app'
import { useUserStore } from '@/store/modules/user'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

// 智能面包屑导航
const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta?.title)
  return matched
})

// 切换侧边栏
const toggleSidebar = () => {
  appStore.toggleSidebar()
}

// 切换主题
const toggleTheme = () => {
  appStore.toggleTheme()
  document.documentElement.classList.toggle('dark', appStore.theme === 'dark')
  ElMessage.success(`已切换到${appStore.theme === 'dark' ? '深色' : '浅色'}主题`)
}

// 全屏切换
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    ElMessage.success('已进入全屏模式')
  } else {
    document.exitFullscreen()
    ElMessage.success('已退出全屏模式')
  }
}

// 面包屑导航点击
const navigateToPath = (item: any, index: number) => {
  if (index < breadcrumbs.value.length - 1) {
    router.push(item.path)
  }
}

// 搜索功能
const handleSearch = () => {
  ElMessage.info('搜索功能开发中...')
}

// 消息通知
const handleNotifications = () => {
  ElMessage.info('消息通知功能开发中...')
}

// 用户菜单命令处理
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'help':
      ElMessage.info('帮助中心功能开发中...')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '退出确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      customClass: 'modern-message-box'
    }
  ).then(() => {
    userStore.logout()
    router.push('/login')
    ElMessage.success('退出登录成功')
  }).catch(() => {
    // 用户取消
  })
}
</script>

<style scoped>
/* 现代化导航栏主体 */
.modern-navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(24px);
  border-bottom: 1px solid rgba(148, 163, 184, 0.1);
  position: relative;
  z-index: 100;
}

/* 左侧操作区域 */
.navbar-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* 侧边栏切换按钮 */
.sidebar-toggle-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(34, 197, 94, 0.08) 100%);
  border: 1px solid rgba(148, 163, 184, 0.15);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  position: relative;
  overflow: hidden;
}

.sidebar-toggle-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.2) 50%, transparent 70%);
  transform: translateX(-100%);
  transition: transform 0.5s ease;
}

.sidebar-toggle-btn:hover::before {
  transform: translateX(100%);
}

.sidebar-toggle-btn:hover {
  transform: translateY(-1px) scale(1.02);
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.15) 0%, rgba(34, 197, 94, 0.12) 100%);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.15);
}

.toggle-icon {
  color: #475569;
  font-size: 16px;
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

.sidebar-toggle-btn:hover .toggle-icon {
  color: #1e293b;
  transform: scale(1.1);
}

/* 智能面包屑导航 */
.smart-breadcrumb {
  display: flex;
  align-items: center;
}

.breadcrumb-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.breadcrumb-item {
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}

.breadcrumb-item:not(.is-active):hover {
  transform: translateY(-1px);
}

.breadcrumb-inner {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  background: transparent;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  position: relative;
  overflow: hidden;
}

.breadcrumb-item:not(.is-active) .breadcrumb-inner:hover {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.08) 0%, rgba(34, 197, 94, 0.06) 100%);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.1);
}

.breadcrumb-item.is-active .breadcrumb-inner {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.12) 0%, rgba(34, 197, 94, 0.1) 100%);
  border: 1px solid rgba(59, 130, 246, 0.2);
  box-shadow: 0 2px 12px rgba(59, 130, 246, 0.15);
}

.breadcrumb-icon {
  color: #6366f1;
  font-size: 14px;
}

.breadcrumb-label {
  font-size: 14px;
  font-weight: 500;
  color: #334155;
  transition: all 0.3s ease;
}

.breadcrumb-item.is-active .breadcrumb-label {
  color: #1e40af;
  font-weight: 600;
}

.breadcrumb-separator {
  display: flex;
  align-items: center;
  margin: 0 4px;
}

.separator-icon {
  color: #94a3b8;
  font-size: 12px;
  transition: all 0.3s ease;
}

/* 右侧工具栏 */
.navbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 操作按钮通用样式 */
.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: rgba(148, 163, 184, 0.08);
  border: 1px solid rgba(148, 163, 184, 0.12);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  position: relative;
  overflow: hidden;
}

.action-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(34, 197, 94, 0.08) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.action-btn:hover::before {
  opacity: 1;
}

.action-btn:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.12);
  border-color: rgba(59, 130, 246, 0.2);
}

.btn-icon {
  color: #475569;
  font-size: 16px;
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

.action-btn:hover .btn-icon {
  color: #1e40af;
  transform: scale(1.1);
}

/* 特定按钮样式 */
.search-btn:hover {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(79, 70, 229, 0.08) 100%);
}

.fullscreen-btn:hover {
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.1) 0%, rgba(22, 163, 74, 0.08) 100%);
}

.theme-btn:hover {
  background: linear-gradient(135deg, rgba(251, 191, 36, 0.1) 0%, rgba(245, 158, 11, 0.08) 100%);
}

.notification-btn:hover {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.1) 0%, rgba(220, 38, 38, 0.08) 100%);
}

/* 通知徽章 */
.notification-badge :deep(.el-badge__content) {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  border: 2px solid #ffffff;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
  font-size: 11px;
  font-weight: 600;
  min-width: 18px;
  height: 18px;
  line-height: 14px;
}

/* 分隔线 */
.navbar-divider {
  width: 1px;
  height: 24px;
  background: linear-gradient(180deg, transparent 0%, rgba(148, 163, 184, 0.3) 50%, transparent 100%);
  margin: 0 8px;
}

/* 用户下拉菜单 */
.user-dropdown {
  cursor: pointer;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  background: linear-gradient(135deg, rgba(148, 163, 184, 0.05) 0%, rgba(148, 163, 184, 0.02) 100%);
  border: 1px solid rgba(148, 163, 184, 0.15);
  border-radius: 16px;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  position: relative;
  overflow: hidden;
}

.user-profile::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.08) 0%, rgba(34, 197, 94, 0.06) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.user-profile:hover::before {
  opacity: 1;
}

.user-profile:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.12);
  border-color: rgba(59, 130, 246, 0.2);
}

.user-avatar-wrapper {
  position: relative;
}

.user-avatar {
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

.online-indicator {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 10px;
  height: 10px;
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  border: 2px solid #ffffff;
  border-radius: 50%;
  box-shadow: 0 0 6px rgba(34, 197, 94, 0.4);
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  position: relative;
  z-index: 2;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.2;
}

.user-role {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.dropdown-arrow {
  color: #64748b;
  font-size: 12px;
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

.user-profile:hover .dropdown-arrow {
  color: #1e40af;
  transform: rotate(180deg);
}

/* 下拉菜单样式 */
.user-dropdown-menu {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(148, 163, 184, 0.15);
  border-radius: 12px;
  box-shadow: 
    0 20px 40px rgba(59, 130, 246, 0.15),
    0 4px 16px rgba(59, 130, 246, 0.1);
  padding: 8px;
  min-width: 200px;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #334155;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  position: relative;
  overflow: hidden;
}

.dropdown-item::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.08) 0%, rgba(34, 197, 94, 0.06) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.dropdown-item:hover::before {
  opacity: 1;
}

.dropdown-item:hover {
  color: #1e40af;
  transform: translateX(4px);
}

.logout-item:hover {
  color: #dc2626;
}

.logout-item:hover::before {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.08) 0%, rgba(220, 38, 38, 0.06) 100%);
}

.item-icon {
  font-size: 16px;
  position: relative;
  z-index: 2;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .modern-navbar {
    padding: 0 16px;
    gap: 12px;
  }
  
  .navbar-left {
    gap: 12px;
  }
  
  .navbar-right {
    gap: 6px;
  }
  
  .action-btn {
    width: 36px;
    height: 36px;
  }
  
  .user-profile {
    padding: 6px 12px;
  }
  
  .user-info {
    display: none;
  }
  
  .breadcrumb-inner {
    padding: 6px 8px;
  }
  
  .breadcrumb-label {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .smart-breadcrumb {
    display: none;
  }
  
  .modern-navbar {
    padding: 0 12px;
  }
}
</style>
