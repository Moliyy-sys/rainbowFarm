<template>
  <div class="error-container page-container flex-center">
    <div class="error-content text-center">
      <!-- 404图标 -->
      <div class="error-icon mb-8">
        <el-icon size="120" class="text-gray-300">
          <DocumentDelete />
        </el-icon>
      </div>

      <!-- 错误信息 -->
      <div class="error-info mb-8">
        <h1 class="text-6xl font-bold text-gray-400 mb-4">404</h1>
        <h2 class="text-2xl font-semibold text-gray-600 mb-2">页面未找到</h2>
        <p class="text-gray-500 max-w-md mx-auto">
          抱歉，您访问的页面不存在或已被移动。请检查网址是否正确，或返回首页。
        </p>
      </div>

      <!-- 操作按钮 -->
      <div class="error-actions space-x-4">
        <el-button type="primary" @click="goHome">
          <el-icon class="mr-1"><House /></el-icon>
          返回首页
        </el-button>
        <el-button @click="goBack">
          <el-icon class="mr-1"><Back /></el-icon>
          返回上页
        </el-button>
      </div>

      <!-- 建议链接 -->
      <div class="suggested-links mt-8">
        <p class="text-sm text-gray-500 mb-4">您可能想要访问：</p>
        <div class="flex-center space-x-6 text-sm">
          <router-link to="/dashboard" class="text-blue-500 hover:text-blue-600">
            系统首页
          </router-link>
          <router-link to="/inventory/list" class="text-blue-500 hover:text-blue-600">
            库存管理
          </router-link>
          <router-link to="/plant/task" class="text-blue-500 hover:text-blue-600">
            种植管理
          </router-link>
          <router-link to="/system/user" class="text-blue-500 hover:text-blue-600">
            系统管理
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { DocumentDelete, House, Back } from '@element-plus/icons-vue'

const router = useRouter()

// 返回首页
const goHome = () => {
  router.push('/')
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.error-container {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.error-content {
  max-width: 600px;
  padding: 2rem;
}

.error-icon {
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

.suggested-links a {
  @apply transition-colors duration-200;
}
</style>
