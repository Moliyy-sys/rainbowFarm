<template>
  <div v-if="!item.meta?.hidden" class="modern-sidebar-item">
    <!-- 单级菜单项 -->
    <el-menu-item 
      v-if="!hasChildren"
      :index="resolvePath"
      class="premium-menu-item"
      @click="handleClick"
    >
      <div class="menu-content">
        <div v-if="item.meta?.icon" class="icon-container">
          <div class="icon-bg">
            <el-icon class="menu-icon">
              <component :is="item.meta.icon" />
            </el-icon>
          </div>
        </div>
        <span class="menu-label">{{ item.meta?.title }}</span>
        <div class="active-indicator"></div>
      </div>
    </el-menu-item>

    <!-- 多级子菜单 -->
    <el-sub-menu 
      v-else
      :index="item.path"
      class="premium-sub-menu"
    >
      <template #title>
        <div class="submenu-header">
          <div v-if="item.meta?.icon" class="icon-container">
            <div class="icon-bg">
              <el-icon class="menu-icon">
                <component :is="item.meta.icon" />
              </el-icon>
            </div>
          </div>
          <span class="menu-label">{{ item.meta?.title }}</span>
        </div>
      </template>
      
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :base-path="resolvePath"
      />
    </el-sub-menu>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

interface MenuItem {
  path: string
  name?: string
  meta?: {
    title: string
    icon?: string
    hidden?: boolean
  }
  children?: MenuItem[]
}

interface Props {
  item: MenuItem
  basePath?: string
}

const props = defineProps<Props>()
const router = useRouter()

// 是否有子菜单
const hasChildren = computed(() => {
  return props.item.children && props.item.children.length > 0
})

// 解析完整路径
const resolvePath = computed(() => {
  if (props.basePath && !props.item.path.startsWith('/')) {
    return `${props.basePath}/${props.item.path}`
  }
  return props.item.path
})

// 处理菜单点击
const handleClick = () => {
  if (resolvePath.value) {
    router.push(resolvePath.value)
  }
}

</script>

<style scoped>
/* 现代化菜单项容器 */
.modern-sidebar-item {
  margin: 3px 0;
  position: relative;
}

/* 高级菜单项设计 */
.premium-menu-item {
  position: relative;
  margin: 0 12px 6px 12px !important;
  border-radius: 12px !important;
  background: transparent !important;
  border: none !important;
  padding: 0 !important;
  height: 48px !important;
  line-height: 48px !important;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1) !important;
  cursor: pointer;
}

/* 菜单项背景和悬浮效果 */
.premium-menu-item::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.06) 0%, rgba(255, 255, 255, 0.02) 100%);
  border-radius: 12px;
  opacity: 0;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  z-index: 1;
}

.premium-menu-item:hover::before {
  opacity: 1;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.12) 0%, rgba(255, 255, 255, 0.06) 100%);
}

.premium-menu-item.is-active::before {
  opacity: 1;
  background: linear-gradient(135deg, rgba(22, 160, 133, 0.25) 0%, rgba(39, 174, 96, 0.2) 50%, rgba(46, 204, 113, 0.18) 100%);
  box-shadow: 
    0 4px 16px rgba(22, 160, 133, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

/* 菜单内容布局 */
.menu-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  padding: 0 16px;
  height: 100%;
  width: 100%;
  border-radius: 12px;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

.premium-menu-item:hover .menu-content {
  transform: translateX(4px);
}

.premium-menu-item.is-active .menu-content {
  transform: translateX(6px);
}

/* 图标容器设计 */
.icon-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 14px;
  position: relative;
}

.icon-bg {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: transparent;
  border: none;
  border-radius: 8px;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  position: relative;
  overflow: hidden;
}

.premium-menu-item:hover .icon-bg {
  transform: scale(1.1);
}

.premium-menu-item.is-active .icon-bg {
  transform: scale(1.1);
}

.menu-icon {
  font-size: 16px !important;
  color: rgba(255, 255, 255, 0.85) !important;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  position: relative;
  z-index: 2;
}

.premium-menu-item:hover .menu-icon {
  color: #ffffff !important;
  transform: scale(1.15);
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.premium-menu-item.is-active .menu-icon {
  color: #ffffff !important;
  transform: scale(1.2);
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.3));
}

/* 菜单标签样式 */
.menu-label {
  flex: 1;
  font-size: 15px !important;
  font-weight: 500 !important;
  color: rgba(255, 255, 255, 0.9) !important;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1) !important;
  letter-spacing: 0.3px;
  line-height: 1.4;
}

.premium-menu-item:hover .menu-label {
  color: #ffffff !important;
  font-weight: 600 !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.premium-menu-item.is-active .menu-label {
  color: #ffffff !important;
  font-weight: 700 !important;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* 活跃状态指示器 */
.active-indicator {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: linear-gradient(180deg, #ffffff 0%, rgba(255, 255, 255, 0.8) 100%);
  border-radius: 2px;
  opacity: 0;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

.premium-menu-item.is-active .active-indicator {
  opacity: 1;
  height: 28px;
  box-shadow: 
    0 0 12px rgba(255, 255, 255, 0.6),
    0 0 24px rgba(22, 160, 133, 0.35);
}

/* 高级子菜单设计 */
.premium-sub-menu {
  margin: 0 12px 8px 12px !important;
  border-radius: 12px !important;
  background: transparent !important;
  border: none !important;
  overflow: hidden;
  position: relative;
}

/* 子菜单标题样式 */
.premium-sub-menu :deep(.el-sub-menu__title) {
  height: 48px !important;
  line-height: 48px !important;
  padding: 0 !important;
  border-radius: 12px;
  background: transparent !important;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1) !important;
  border: none !important;
  color: rgba(255, 255, 255, 0.9) !important;
  position: relative;
}

.premium-sub-menu :deep(.el-sub-menu__title)::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.06) 0%, rgba(255, 255, 255, 0.02) 100%);
  border-radius: 12px;
  opacity: 0;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

.premium-sub-menu :deep(.el-sub-menu__title):hover::before {
  opacity: 1;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%);
}

.premium-sub-menu :deep(.el-sub-menu__title):hover {
  background: transparent !important;
  color: #ffffff !important;
  transform: translateX(4px);
}

/* 子菜单头部内容 */
.submenu-header {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 0 16px;
  position: relative;
  z-index: 2;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

/* 子菜单列表容器 */
.premium-sub-menu :deep(.el-menu) {
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.12) 0%, rgba(0, 0, 0, 0.08) 100%) !important;
  backdrop-filter: blur(16px);
  border: none !important;
  padding: 12px 0;
  margin: 8px 12px 0 12px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  box-shadow: 
    inset 0 1px 0 rgba(255, 255, 255, 0.1),
    0 4px 16px rgba(0, 0, 0, 0.15);
  position: relative;
  overflow: hidden;
}

/* 折叠状态下的子菜单样式 */
.el-menu--collapse .premium-sub-menu :deep(.el-menu) {
  position: absolute;
  left: 100%;
  top: 0;
  min-width: 200px;
  z-index: 1000;
  margin: 0;
  background: linear-gradient(135deg, rgba(15, 32, 39, 0.95) 0%, rgba(32, 58, 67, 0.95) 100%) !important;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.3),
    0 4px 16px rgba(0, 0, 0, 0.2);
  opacity: 0;
  visibility: hidden;
  transform: translateX(-10px);
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}

/* 折叠状态下子菜单悬浮时显示 */
.el-menu--collapse .premium-sub-menu:hover :deep(.el-menu) {
  opacity: 1;
  visibility: visible;
  transform: translateX(0);
}

/* 确保子菜单内容在折叠状态下可见 */
.el-menu--collapse .premium-sub-menu :deep(.el-menu .el-menu-item) {
  display: block !important;
  opacity: 1 !important;
  visibility: visible !important;
}

.premium-sub-menu :deep(.el-menu .el-menu-item) {
  margin: 3px 12px;
  padding: 0 16px !important;
  height: 40px !important;
  line-height: 40px !important;
  border-radius: 10px !important;
  background: transparent !important;
  color: rgba(255, 255, 255, 0.8) !important;
  font-size: 14px !important;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1) !important;
  border: none !important;
  position: relative;
  font-weight: 500;
  overflow: hidden;
  text-align: left !important;
  display: flex !important;
  align-items: center !important;
  justify-content: flex-start !important;
}

.premium-sub-menu :deep(.el-menu .premium-menu-item .icon-container) {
  margin-right: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.premium-sub-menu :deep(.el-menu .premium-menu-item .icon-bg) {
  width: 24px;
  height: 24px;
}

.premium-sub-menu :deep(.el-menu .premium-menu-item .menu-icon) {
  font-size: 14px !important;
}

.premium-sub-menu :deep(.el-menu .menu-label) {
  font-size: 13px !important;
}


.premium-sub-menu :deep(.el-menu .el-menu-item):hover {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.12) 0%, rgba(255, 255, 255, 0.06) 100%) !important;
  color: rgba(255, 255, 255, 0.98) !important;
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(255, 255, 255, 0.1);
}


.premium-sub-menu :deep(.el-menu .el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(22, 160, 133, 0.25) 0%, rgba(39, 174, 96, 0.2) 50%, rgba(46, 204, 113, 0.18) 100%) !important;
  color: #ffffff !important;
  font-weight: 600 !important;
  box-shadow: 
    0 4px 12px rgba(22, 160, 133, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transform: translateX(6px);
}


.premium-sub-menu :deep(.el-menu .el-menu-item.is-active)::after {
  content: '';
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  background: linear-gradient(180deg, #ffffff 0%, rgba(255, 255, 255, 0.85) 100%);
  border-radius: 2px;
  box-shadow: 
    0 0 8px rgba(255, 255, 255, 0.5),
    0 0 16px rgba(22, 160, 133, 0.35);
}

/* 折叠状态下子菜单项样式 */
.el-menu--collapse .premium-sub-menu :deep(.el-menu .el-menu-item) {
  margin: 3px 8px !important;
  padding: 0 12px !important;
  height: 36px !important;
  line-height: 36px !important;
  border-radius: 8px !important;
  background: transparent !important;
  color: rgba(255, 255, 255, 0.85) !important;
  font-size: 13px !important;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1) !important;
  border: none !important;
  position: relative;
  font-weight: 500;
  overflow: hidden;
  text-align: left !important;
  display: flex !important;
  align-items: center !important;
  justify-content: flex-start !important;
  opacity: 1 !important;
  visibility: visible !important;
}

.el-menu--collapse .premium-sub-menu :deep(.el-menu .el-menu-item):hover {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.15) 0%, rgba(255, 255, 255, 0.08) 100%) !important;
  color: rgba(255, 255, 255, 0.98) !important;
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(255, 255, 255, 0.1);
}

.el-menu--collapse .premium-sub-menu :deep(.el-menu .el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(22, 160, 133, 0.3) 0%, rgba(39, 174, 96, 0.25) 50%, rgba(46, 204, 113, 0.22) 100%) !important;
  color: #ffffff !important;
  font-weight: 600 !important;
  box-shadow: 
    0 4px 12px rgba(22, 160, 133, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transform: translateX(6px);
}

/* 折叠状态下的高级样式 */
.el-menu--collapse .premium-menu-item {
  margin: 2px 8px 6px 8px !important;
  width: auto !important;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
}

.el-menu--collapse .premium-menu-item .menu-content {
  justify-content: center !important;
  align-items: center !important;
  padding: 0 !important;
  width: 100% !important;
  display: flex !important;
}

.el-menu--collapse .premium-menu-item .icon-container {
  margin-right: 0 !important;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
  width: 100% !important;
}

.el-menu--collapse .premium-menu-item .icon-bg {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: transparent;
  border: none;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
  box-shadow: none;
}

/* 折叠状态下隐藏文字 */
.el-menu--collapse .premium-menu-item .menu-label {
  display: none !important;
}

.el-menu--collapse .premium-menu-item:hover .icon-bg {
  transform: scale(1.15);
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
}

.el-menu--collapse .premium-menu-item.is-active .icon-bg {
  transform: scale(1.2);
  background: rgba(22, 160, 133, 0.2);
  border-radius: 8px;
}

/* 折叠状态下子菜单悬浮时显示图标框 */
.el-menu--collapse .premium-sub-menu:hover .icon-bg {
  transform: scale(1.15);
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
}

.el-menu--collapse .premium-sub-menu.is-opened .icon-bg {
  transform: scale(1.2);
  background: rgba(22, 160, 133, 0.2);
  border-radius: 8px;
}

.el-menu--collapse .premium-menu-item .menu-icon {
  font-size: 18px !important;
}

.el-menu--collapse .premium-sub-menu {
  margin: 2px 8px 6px 8px !important;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
}

.el-menu--collapse .premium-sub-menu :deep(.el-sub-menu__title) {
  padding: 0 !important;
  justify-content: center !important;
  align-items: center !important;
  display: flex !important;
  width: 100% !important;
}

.el-menu--collapse .premium-sub-menu .icon-bg {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: transparent;
  border: none;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
  box-shadow: none;
}

.el-menu--collapse .premium-sub-menu .submenu-header {
  justify-content: center !important;
  align-items: center !important;
  padding: 0 !important;
  width: 100% !important;
  display: flex !important;
}

.el-menu--collapse .premium-sub-menu .icon-container {
  margin-right: 0 !important;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
  width: 100% !important;
}

/* 折叠状态下隐藏子菜单文字 */
.el-menu--collapse .premium-sub-menu .menu-label {
  display: none !important;
}

/* 折叠状态下隐藏子菜单箭头 */
.el-menu--collapse .premium-sub-menu :deep(.el-sub-menu__icon-arrow) {
  display: none !important;
}



/* 响应式设计优化 */
@media (max-width: 768px) {
  .premium-menu-item,
  .premium-sub-menu {
    margin: 2px 8px 4px 8px;
  }
  
  .menu-content,
  .submenu-header {
    padding: 0 12px;
  }
  
  .icon-bg {
    width: 24px;
    height: 24px;
  }
  
  .menu-icon {
    font-size: 14px !important;
  }
  
  .menu-label {
    font-size: 13px !important;
  }
}

@media (max-width: 480px) {
  .premium-menu-item,
  .premium-sub-menu {
    margin: 1px 6px 3px 6px;
  }
  
  .menu-content,
  .submenu-header {
    padding: 0 10px;
  }
  
  .icon-bg {
    width: 20px;
    height: 20px;
  }
  
  .menu-icon {
    font-size: 12px !important;
  }
  
  .menu-label {
    font-size: 12px !important;
  }
}
</style>
