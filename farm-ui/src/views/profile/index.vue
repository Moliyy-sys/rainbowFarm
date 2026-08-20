<template>
  <div class="profile-container p-8" v-loading="loading">

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- 左侧：用户信息卡片 -->
      <div class="lg:col-span-1">
        <div class="bg-white rounded-xl shadow-lg border border-gray-100 p-8 hover:shadow-xl transition-shadow duration-300">
          <div class="text-center">
            <!-- 头像 -->
            <div class="relative inline-block mb-6">
              <el-avatar
                :size="120"
                :src="userStore.avatar"
                class="border-4 border-white shadow-xl ring-4 ring-blue-50"
              >
                <el-icon size="50"><User /></el-icon>
              </el-avatar>
              <el-button
                type="primary"
                size="small"
                circle
                class="absolute bottom-0 right-0 shadow-lg hover:scale-110 transition-transform duration-200"
                @click="handleAvatarUpload"
              >
                <el-icon size="16"><Camera /></el-icon>
              </el-button>
            </div>

            <!-- 用户基本信息 -->
            <h2 class="text-2xl font-bold text-gray-800 mb-2">
              {{ userInfo?.nickName || userInfo?.userName }}
            </h2>
            <p class="text-gray-600 mb-4 text-sm">{{ userInfo?.email }}</p>
            
            <!-- 角色标签 -->
            <div class="flex flex-wrap justify-center gap-2 mb-6">
              <el-tag
                v-for="role in userInfo?.roles"
                :key="role.roleId"
                type="primary"
                size="small"
                effect="light"
                class="hover:shadow-sm transition-shadow"
              >
                {{ role.roleName }}
              </el-tag>
            </div>

            <!-- 账户状态 -->
            <div class="flex items-center justify-center bg-gray-50 rounded-lg p-3">
              <el-icon 
                :class="userInfo?.status === '0' ? 'text-green-500' : 'text-red-500'"
                class="mr-2"
                size="18"
              >
                <CircleCheck v-if="userInfo?.status === '0'" />
                <CircleClose v-else />
              </el-icon>
              <span 
                :class="userInfo?.status === '0' ? 'text-green-600' : 'text-red-600'"
                class="text-sm font-semibold"
              >
                {{ userInfo?.status === '0' ? '账户正常' : '账户停用' }}
              </span>
            </div>
          </div>
        </div>

        <!-- 快速操作 -->
        <div class="bg-white rounded-xl shadow-lg border border-gray-100 p-6 mt-8 hover:shadow-xl transition-shadow duration-300">
          <h3 class="text-lg font-semibold text-gray-800 mb-6 flex items-center">
            <el-icon class="mr-2 text-blue-500"><Setting /></el-icon>
            快速操作
          </h3>
          <div class="space-y-4">
            <el-button
              type="primary"
              class="w-full h-12 text-base font-medium hover:shadow-lg transition-all duration-200 quick-action-btn"
              @click="showEditDialog = true"
            >
              <el-icon class="mr-3"><Edit /></el-icon>
              编辑个人信息
            </el-button>
            <el-button
              type="warning"
              class="w-full h-12 text-base font-medium hover:shadow-lg transition-all duration-200 quick-action-btn"
              @click="showPasswordDialog = true"
            >
              <el-icon class="mr-3"><Lock /></el-icon>
              修改密码
            </el-button>
          </div>
        </div>
      </div>

      <!-- 右侧：详细信息 -->
      <div class="lg:col-span-2">
        <div class="bg-white rounded-xl shadow-lg border border-gray-100 hover:shadow-xl transition-shadow duration-300">
          <!-- 标签页 -->
          <el-tabs v-model="activeTab" class="profile-tabs">
            <!-- 基本信息 -->
            <el-tab-pane label="基本信息" name="basic">
              <div class="p-8 pl-16">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
                  <div class="info-item">
                    <label class="info-label">用户名</label>
                    <div class="info-value">{{ userInfo?.userName }}</div>
                  </div>
                  <div class="info-item">
                    <label class="info-label">昵称</label>
                    <div class="info-value">{{ userInfo?.nickName }}</div>
                  </div>
                  <div class="info-item">
                    <label class="info-label">邮箱</label>
                    <div class="info-value">{{ userInfo?.email }}</div>
                  </div>
                  <div class="info-item">
                    <label class="info-label">手机号</label>
                    <div class="info-value">{{ userInfo?.phonenumber }}</div>
                  </div>
                  <div class="info-item">
                    <label class="info-label">性别</label>
                    <div class="info-value">
                      {{ userInfo?.sex === '0' ? '男' : userInfo?.sex === '1' ? '女' : '未知' }}
                    </div>
                  </div>
                  <div class="info-item">
                    <label class="info-label">状态</label>
                    <div class="info-value">
                      <el-tag :type="userInfo?.status === '0' ? 'success' : 'danger'" size="small">
                        {{ userInfo?.status === '0' ? '正常' : '停用' }}
                      </el-tag>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>

            <!-- 角色权限 -->
            <el-tab-pane label="角色权限" name="roles">
              <div class="p-8 pl-16">
                <div class="space-y-6">
                  <div
                    v-for="role in userInfo?.roles"
                    :key="role.roleId"
                    class="role-card bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl p-6 border border-gray-200 hover:shadow-md transition-all duration-300"
                  >
                    <div class="flex items-center justify-between mb-4">
                      <h4 class="text-xl font-semibold text-gray-800">{{ role.roleName }}</h4>
                      <el-tag type="primary" size="small" effect="dark">角色</el-tag>
                    </div>
                    <p v-if="role.remark" class="text-gray-600 text-sm mb-4 leading-relaxed">{{ role.remark }}</p>
                    
                    <!-- 权限列表 -->
                    <div v-if="role.permissions && role.permissions.length > 0">
                      <h5 class="text-sm font-semibold text-gray-700 mb-3 flex items-center">
                        <el-icon class="mr-1"><Key /></el-icon>
                        权限列表
                      </h5>
                      <div class="flex flex-wrap gap-2">
                        <el-tag
                          v-for="permission in role.permissions"
                          :key="permission.permissionId"
                          size="small"
                          effect="light"
                          class="hover:shadow-sm transition-shadow"
                        >
                          {{ permission.permissionName }}
                        </el-tag>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>

          </el-tabs>
        </div>
      </div>
    </div>

    <!-- 编辑个人信息对话框 -->
    <ProfileEditDialog
      v-model="showEditDialog"
      :user-info="userInfo"
      @success="handleEditSuccess"
    />

    <!-- 修改密码对话框 -->
    <PasswordChangeDialog
      v-model="showPasswordDialog"
      @success="handlePasswordSuccess"
    />

    <!-- 头像上传 -->
    <input
      ref="avatarInput"
      type="file"
      accept="image/*"
      style="display: none"
      @change="handleAvatarChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { uploadAvatarApi } from '@/api/auth'
import ProfileEditDialog from './components/ProfileEditDialog.vue'
import PasswordChangeDialog from './components/PasswordChangeDialog.vue'
import type { UserInfo } from '@/types/api'

const userStore = useUserStore()

// 响应式状态
const activeTab = ref('basic')
const showEditDialog = ref(false)
const showPasswordDialog = ref(false)
const avatarInput = ref<HTMLInputElement>()
const loading = ref(false)

// 计算属性
const userInfo = computed(() => userStore.userInfo)

// 头像上传
const handleAvatarUpload = () => {
  avatarInput.value?.click()
}

const handleAvatarChange = async (event: Event) => {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  // 验证文件大小（2MB）
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }

  try {
    ElMessage.info('正在上传头像...')
    
    // 调用上传API
    const avatarUrl = await uploadAvatarApi(file)
    
    // 更新用户信息中的头像
    if (userInfo.value) {
      userInfo.value.avatar = avatarUrl
      // 重新获取用户信息以确保数据同步
      await userStore.fetchUserInfo()
    }
    
    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败，请稍后重试')
  } finally {
    // 清空文件选择，允许重复选择同一文件
    if (avatarInput.value) {
      avatarInput.value.value = ''
    }
  }
}

// 编辑成功回调
const handleEditSuccess = () => {
  ElMessage.success('个人信息更新成功')
  userStore.fetchUserInfo() // 重新获取用户信息
}

// 密码修改成功回调
const handlePasswordSuccess = () => {
  ElMessage.success('密码修改成功')
}

// 组件挂载时获取最新用户信息
onMounted(async () => {
  console.log('个人中心挂载，当前用户信息:', userInfo.value)
  if (!userInfo.value) {
    console.log('用户信息为空，尝试获取')
    loading.value = true
    try {
      await userStore.fetchUserInfo()
      console.log('获取用户信息成功:', userStore.userInfo)
    } catch (error) {
      console.error('获取用户信息失败:', error)
      ElMessage.error('获取用户信息失败，请刷新页面重试')
    } finally {
      loading.value = false
    }
  }
})
</script>

<style scoped>
.profile-container {
  @apply min-h-screen bg-gradient-to-br from-gray-50 via-blue-50 to-indigo-50;
}

.page-header {
  @apply bg-white rounded-xl shadow-lg border border-gray-100 p-6;
}

.info-item {
  @apply space-y-3 p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors duration-200;
}

.info-label {
  @apply block text-sm font-semibold text-gray-600 uppercase tracking-wide;
}

.info-value {
  @apply text-gray-900 font-semibold text-lg;
}

.role-card {
  @apply transition-all duration-300 hover:shadow-lg hover:-translate-y-1;
}

.profile-tabs :deep(.el-tabs__header) {
  @apply px-8 mb-0 bg-gray-50 rounded-t-xl;
}

.profile-tabs :deep(.el-tabs__nav-wrap::after) {
  @apply bg-gray-200;
}

.profile-tabs :deep(.el-tabs__item) {
  @apply text-gray-600 font-medium text-base px-6 py-4;
}

.profile-tabs :deep(.el-tabs__item.is-active) {
  @apply text-blue-600 font-semibold;
}

.profile-tabs :deep(.el-tabs__active-bar) {
  @apply bg-blue-600 h-1;
}

.profile-tabs :deep(.el-tab-pane) {
  @apply pt-0;
}

/* 头像悬浮效果 */
.el-avatar {
  @apply transition-all duration-300 hover:scale-110 hover:shadow-2xl;
}

/* 快速操作按钮样式 - 强制内容居中 */
.quick-action-btn {
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  text-align: center !important;
}

.quick-action-btn .el-icon {
  margin-right: 12px !important;
}

/* 确保按钮内容完全居中 */
.space-y-4 .el-button {
  @apply justify-center font-semibold;
}

/* 美化滚动条 */
.profile-container::-webkit-scrollbar {
  width: 6px;
}

.profile-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.profile-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.profile-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 卡片悬浮效果 */
.bg-white {
  @apply transition-all duration-300;
}

.bg-white:hover {
  @apply transform hover:-translate-y-1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-container {
    @apply p-4;
  }
  
  .grid {
    @apply grid-cols-1 gap-4;
  }
  
  .page-header {
    @apply p-4;
  }
  
  .page-header h1 {
    @apply text-xl;
  }
  
  .info-item {
    @apply p-3;
  }
  
  .info-value {
    @apply text-base;
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.profile-container > div {
  animation: fadeInUp 0.6s ease-out;
}

/* 美化标签页 */
.profile-tabs :deep(.el-tabs__item:hover) {
  @apply text-blue-500;
}

/* 美化按钮 */
.el-button {
  @apply transition-all duration-200;
}

.el-button:hover {
  @apply transform hover:scale-105;
}
</style>
