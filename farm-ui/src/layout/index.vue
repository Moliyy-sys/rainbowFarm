<template>
  <div class="app-layout">
    <div class="layout-container">
      <!-- 移动端遮罩层 -->
      <div 
        v-if="appStore.isMobile && !appStore.collapsed"
        class="mobile-overlay"
        @click="toggleCollapse"
      ></div>
      
      <!-- 现代化侧边栏 -->
      <aside 
        :class="[
          'modern-sidebar',
          { 
            'sidebar-collapsed': appStore.collapsed,
            'mobile-sidebar': appStore.isMobile,
            'mobile-sidebar-hidden': appStore.isMobile && appStore.collapsed
          }
        ]"
      >
        <!-- 精美Logo区域 -->
        <div class="logo-section">
          <div class="logo-container">
            <div class="logo-icon-bg">
              <div class="logo-icon">
                <el-icon :size="appStore.collapsed ? 24 : 28" class="logo-svg">
                  <Cherry />
                </el-icon>
              </div>
            </div>
            <transition name="logo-text-fade">
              <div v-if="!appStore.collapsed" class="logo-content">
                <h1 class="brand-name">智慧农业</h1>
                <p class="brand-tagline">Smart Agriculture</p>
              </div>
            </transition>
          </div>
        </div>
        
        <!-- 导航菜单区域 -->
        <nav class="navigation-section">
          <el-scrollbar class="nav-scrollbar">
            <el-menu
              :default-active="activeMenu"
              :collapse="appStore.collapsed"
              :unique-opened="false"
              class="modern-nav-menu"
              background-color="transparent"
              text-color="rgba(255, 255, 255, 0.9)"
              active-text-color="#ffffff"
              router
            >
              <sidebar-item
                v-for="route in routes"
                :key="route.path"
                :item="route"
                :base-path="route.path"
              />
            </el-menu>
          </el-scrollbar>
        </nav>

      </aside>

      <!-- 主体内容区域 -->
      <main class="main-content">
        <!-- 现代化顶部导航 -->
        <header class="modern-header">
          <navbar />
        </header>

        <!-- 页签导航区 -->
        <div class="tabs-navigation">
          <tags-view />
        </div>

        <!-- 页面内容区 -->
        <section class="page-content">
          <router-view v-slot="{ Component }">
            <transition name="page-transition" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/store/modules/app'
import Navbar from './components/Navbar.vue'
import SidebarItem from './components/SidebarItem.vue'
import TagsView from './components/TagsView.vue'

// 响应式状态
const route = useRoute()
const appStore = useAppStore()

// 设备检测和响应式处理
const handleResize = () => {
  const width = window.innerWidth
  if (width <= 768) {
    appStore.setDevice('mobile')
    // 移动端默认收起侧边栏
    if (!appStore.collapsed) {
      appStore.toggleSidebar()
    }
  } else {
    appStore.setDevice('desktop')
    // 桌面端默认展开侧边栏
    if (appStore.collapsed) {
      appStore.toggleSidebar()
    }
  }
}

onMounted(() => {
  handleResize()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

// 路由配置
const routes = computed(() => {
  return [
    {
      path: '/dashboard',
      name: 'Dashboard',
      meta: {
        title: '首页',
        icon: 'House'
      }
    },
    {
      path: '/inventory',
      meta: {
        title: '库存管理',
        icon: 'Box'
      },
      children: [
        {
          path: '/inventory/statistics',
          name: 'InventoryStatistics',
          meta: {
            title: '库存统计',
            icon: 'DataAnalysis'
          },
          children: [
            {
              path: '/inventory/statistics/count',
              name: 'InventoryStatisticsCount',
              meta: {
                title: '库存数量'
              }
            },
            {
              path: '/inventory/statistics/detail',
              name: 'InventoryStatisticsDetail',
              meta: {
                title: '库存详情'
              }
            },
            {
              path: '/inventory/statistics/flow',
              name: 'InventoryStatisticsFlow',
              meta: {
                title: '库存流水'
              }
            }
          ]
        },
        {
          path: '/inventory/warehouse-area',
          name: 'WarehouseArea',
          meta: {
            title: '仓库/库区',
            icon: 'Grid'
          }
        },
        {
          path: '/inventory/item',
          name: 'Item',
          meta: {
            title: '物料管理',
            icon: 'Goods'
          }
        },
        {
          path: '/inventory/merchant',
          name: 'Merchant',
          meta: {
            title: '往来单位',
            icon: 'UserFilled'
          }
        },
        {
          path: '/inventory/brand',
          name: 'Brand',
          meta: {
            title: '商品品牌',
            icon: 'Medal'
          }
        },
        {
          path: '/inventory/inbound',
          name: 'Inbound',
          meta: {
            title: '入库管理',
            icon: 'Download'
          }
        },
        {
          path: '/inventory/outbound',
          name: 'Outbound',
          meta: {
            title: '出库管理',
            icon: 'Upload'
          }
        },
        {
          path: '/inventory/transfer',
          name: 'InventoryTransfer',
          meta: {
            title: '移库管理',
            icon: 'Switch'
          }
        },
        {
          path: '/inventory/stocktaking',
          name: 'Stocktaking',
          meta: {
            title: '盘库管理',
            icon: 'DataAnalysis'
          }
        }
      ]
    },
    {
      path: '/plant',
      meta: {
        title: '种植管理',
        icon: 'Cherry'
      },
      children: [
        {
          path: '/plant/task',
          name: 'PlantTask',
          meta: {
            title: '我的批次',
            icon: 'List'
          }
        },
        {
          path: '/plant/batch',
          name: 'TaskBatch',
          meta: {
            title: '种植批次',
            icon: 'Collection'
          }
        },
        {
          path: '/plant/germplasm',
          name: 'Germplasm',
          meta: {
            title: '种质管理',
            icon: 'Apple'
          }
        },
        {
          path: '/plant/land',
          name: 'Land',
          meta: {
            title: '地块管理',
            icon: 'MapLocation'
          }
        },
        {
          path: '/plant/material',
          name: 'Material',
          meta: {
            title: '农资管理',
            icon: 'Box'
          }
        },
        {
          path: '/plant/machine',
          name: 'Machine',
          meta: {
            title: '机械管理',
            icon: 'Tools'
          }
        },
        {
          path: '/plant/employee',
          name: 'Employee',
          meta: {
            title: '雇员管理',
            icon: 'Avatar'
          }
        }
      ]
    },
    {
      path: '/system',
      meta: {
        title: '系统管理',
        icon: 'Setting'
      },
      children: [
        {
          path: '/system/user',
          name: 'SystemUser',
          meta: {
            title: '用户管理',
            icon: 'User'
          }
        },
        {
          path: '/system/role',
          name: 'SystemRole',
          meta: {
            title: '角色管理',
            icon: 'Avatar'
          }
        },
        {
          path: '/system/permission',
          name: 'SystemPermission',
          meta: {
            title: '权限管理',
            icon: 'Key'
          }
        },
        {
          path: '/system/dict',
          name: 'SystemDict',
          meta: {
            title: '字典管理',
            icon: 'Collection'
          }
        },
        {
          path: '/system/login-log',
          name: 'LoginLog',
          meta: {
            title: '登录日志',
            icon: 'Document'
          }
        },
        {
          path: '/system/operation-log',
          name: 'OperationLog',
          meta: {
            title: '操作日志',
            icon: 'DocumentCopy'
          }
        }
      ]
    }
  ]
})

// 当前活动菜单
const activeMenu = computed(() => route.path)

</script>

<style scoped>
/* 全局应用布局 */
.app-layout {
  height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 50%, #e2e8f0 100%);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  overflow: hidden;
  margin: 0;
  padding: 0;
  position: relative;
}

.layout-container {
  display: flex;
  height: 100vh;
  width: 100vw;
  margin: 0;
  padding: 0;
  overflow: hidden;
}

/* 现代化侧边栏设计 - 绿色渐变主题 */
.modern-sidebar {
  position: relative;
  width: 280px;
  height: 100vh;
  background: linear-gradient(145deg, 
    #0f2027 0%, 
    #203a43 20%, 
    #2c5530 45%, 
    #16a085 70%, 
    #27ae60 100%);
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  overflow: hidden;
  box-shadow: 
    8px 0 32px rgba(15, 32, 39, 0.15),
    4px 0 16px rgba(22, 160, 133, 0.1),
    inset -1px 0 0 rgba(255, 255, 255, 0.08);
  z-index: 1000;
  backdrop-filter: blur(20px);
}

/* 侧边栏折叠状态 */
.sidebar-collapsed {
  width: 80px;
}

/* 侧边栏装饰层和纹理 - 绿色主题 */
.modern-sidebar::before {
  content: '';
  position: absolute;
  inset: 0;
  background: 
    radial-gradient(circle at 15% 15%, rgba(22, 160, 133, 0.12) 0%, transparent 45%),
    radial-gradient(circle at 85% 85%, rgba(39, 174, 96, 0.08) 0%, transparent 45%),
    radial-gradient(circle at 50% 50%, rgba(46, 204, 113, 0.06) 0%, transparent 60%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.03) 0%, transparent 65%),
    linear-gradient(45deg, transparent 0%, rgba(255, 255, 255, 0.015) 50%, transparent 100%);
  pointer-events: none;
  z-index: 1;
}

.modern-sidebar::after {
  content: '';
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    90deg,
    transparent,
    transparent 2px,
    rgba(255, 255, 255, 0.005) 2px,
    rgba(255, 255, 255, 0.005) 4px
  );
  pointer-events: none;
  z-index: 2;
}

/* 精美Logo区域设计 */
.logo-section {
  position: relative;
  z-index: 10;
  height: 80px;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding: 0 24px;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

.logo-icon-bg {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, rgba(22, 160, 133, 0.2) 0%, rgba(39, 174, 96, 0.18) 50%, rgba(46, 204, 113, 0.15) 100%);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 8px 24px rgba(22, 160, 133, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.25),
    0 0 0 1px rgba(46, 204, 113, 0.1);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  overflow: hidden;
}

.logo-icon-bg::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.1) 50%, transparent 70%);
  transform: translateX(-100%);
  transition: transform 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}

.logo-icon-bg:hover::before {
  transform: translateX(100%);
}

.logo-icon-bg:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 
    0 12px 32px rgba(22, 160, 133, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.35),
    0 0 0 1px rgba(46, 204, 113, 0.15);
  background: linear-gradient(135deg, rgba(22, 160, 133, 0.25) 0%, rgba(39, 174, 96, 0.22) 50%, rgba(46, 204, 113, 0.2) 100%);
}

.logo-icon {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.logo-svg {
  color: #ffffff;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.2));
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}

.logo-icon-bg:hover .logo-svg {
  filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.3));
  transform: scale(1.1);
}

.logo-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-name {
  margin: 0;
  color: #ffffff;
  font-size: 20px;
  font-weight: 700;
  line-height: 1.2;
  letter-spacing: 0.5px;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
  background: linear-gradient(135deg, #ffffff 0%, rgba(255, 255, 255, 0.9) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-tagline {
  margin: 0;
  color: rgba(255, 255, 255, 0.7);
  font-size: 11px;
  font-weight: 500;
  line-height: 1.2;
  letter-spacing: 1px;
  text-transform: uppercase;
  opacity: 0.9;
  font-family: 'SF Pro Text', -apple-system, sans-serif;
}

/* 导航菜单区域 */
.navigation-section {
  position: relative;
  z-index: 5;
  flex: 1;
  padding: 24px 0 0 0;
  overflow: hidden;
}

.nav-scrollbar {
  height: calc(100vh - 80px);
  padding: 0 20px;
}

.nav-scrollbar :deep(.el-scrollbar__bar.is-vertical) {
  right: 8px;
  width: 6px;
  border-radius: 3px;
}

.nav-scrollbar :deep(.el-scrollbar__thumb) {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 3px;
  transition: all 0.3s ease;
}

.nav-scrollbar :deep(.el-scrollbar__thumb:hover) {
  background: rgba(255, 255, 255, 0.25);
}

.modern-nav-menu {
  border: none !important;
  background-color: transparent !important;
  padding: 0;
}


/* 主体内容区域设计 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  margin: 0;
  padding: 0;
  overflow: hidden;
}

/* 现代化顶部导航 */
.modern-header {
  height: 70px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(24px);
  border-bottom: 1px solid rgba(148, 163, 184, 0.1);
  box-shadow: 
    0 4px 24px rgba(148, 163, 184, 0.08),
    0 1px 6px rgba(148, 163, 184, 0.04);
  position: relative;
  z-index: 100;
  transition: all 0.3s ease;
}

.modern-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, rgba(22, 160, 133, 0.12) 30%, rgba(39, 174, 96, 0.1) 70%, transparent 100%);
}

/* 页签导航区 */
.tabs-navigation {
  height: 44px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(148, 163, 184, 0.08);
  display: flex;
  align-items: center;
  padding: 0;
  position: relative;
  z-index: 90;
  width: 100%;
  margin: 0;
}

/* 页面内容区 */
.page-content {
  flex: 1;
  padding: 24px 0;
  height: calc(100vh - 70px - 44px);
  overflow: auto;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  position: relative;
  margin: 0;
  width: 100%;
}

/* 高级动画效果 */
.logo-text-fade-enter-active,
.logo-text-fade-leave-active {
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

.logo-text-fade-enter-from,
.logo-text-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px) scale(0.95);
}


/* 页面过渡动画 */
.page-transition-enter-active,
.page-transition-leave-active {
  transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}

.page-transition-enter-from {
  opacity: 0;
  transform: translateY(24px) scale(0.96);
  filter: blur(6px);
}

.page-transition-leave-to {
  opacity: 0;
  transform: translateY(-24px) scale(1.04);
  filter: blur(6px);
}

/* 折叠状态适配 */
.sidebar-collapsed .logo-section {
  padding: 0 16px;
}

.sidebar-collapsed .logo-container {
  justify-content: center;
}

.sidebar-collapsed .nav-scrollbar {
  padding: 0 12px;
}


/* 响应式设计优化 */
@media (max-width: 1024px) {
  .page-content {
    padding: 20px 0;
  }
  
  .modern-sidebar {
    width: 260px;
  }
  
  .sidebar-collapsed {
    width: 72px;
  }
}

/* 移动端遮罩层 */
.mobile-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1400;
  backdrop-filter: blur(4px);
  transition: all 0.3s ease;
}

/* 移动端侧边栏样式 */
.mobile-sidebar {
  position: fixed !important;
  left: 0;
  top: 0;
  z-index: 1500;
  transform: translateX(0);
  transition: transform 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}

.mobile-sidebar-hidden {
  transform: translateX(-100%);
}

@media (max-width: 768px) {
  .layout-container {
    position: relative;
  }
  
  .main-content {
    width: 100%;
    margin-left: 0;
  }
  
  .modern-sidebar {
    position: fixed;
    z-index: 1500;
    height: 100vh;
    box-shadow: 16px 0 48px rgba(30, 41, 59, 0.2);
  }
  
  .logo-section {
    padding: 0 16px;
  }
  
  
  .page-content {
    padding: 16px 0;
  }
  
  .modern-header {
    border-radius: 0;
  }
  
  .tabs-navigation {
    padding: 0;
  }
}

@media (max-width: 480px) {
  .page-content {
    padding: 12px 0;
  }
  
  .tabs-navigation {
    padding: 0;
  }
  
  .logo-section {
    height: 64px;
  }
  
  .brand-name {
    font-size: 16px;
  }
  
  .brand-tagline {
    font-size: 10px;
  }
}

/* 高性能动画优化 */
@media (prefers-reduced-motion: reduce) {
  .modern-sidebar,
  .logo-icon-bg,
  .control-icon,
  .page-transition-enter-active,
  .page-transition-leave-active {
    transition: none;
  }
}
</style>
