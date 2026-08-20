<template>
  <el-dialog
    v-model="visible"
    title="修改密码"
    width="450px"
    :before-close="handleClose"
    destroy-on-close
  >
    <div class="password-change-form">
      <!-- 安全提示 -->
      <el-alert
        title="安全提示"
        type="info"
        effect="light"
        :closable="false"
        class="mb-6"
      >
        <template #default>
          <div class="text-sm">
            <p>• 密码长度应为8-20位</p>
            <p>• 建议包含大小写字母、数字和特殊字符</p>
            <p>• 修改密码后需要重新登录</p>
          </div>
        </template>
      </el-alert>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
        label-position="left"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="formData.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
            autocomplete="current-password"
            class="input-with-icon"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="formData.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
            autocomplete="new-password"
            class="input-with-icon"
            @input="checkPasswordStrength"
          >
            <template #prefix>
              <el-icon><Key /></el-icon>
            </template>
          </el-input>
          
          <!-- 密码强度指示器 -->
          <div v-if="formData.newPassword" class="password-strength mt-2">
            <div class="strength-bar flex gap-1">
              <div
                v-for="i in 4"
                :key="i"
                class="strength-item"
                :class="getStrengthItemClass(i)"
              ></div>
            </div>
            <div class="strength-text text-xs mt-1" :class="strengthTextClass">
              {{ strengthText }}
            </div>
          </div>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="formData.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
            autocomplete="new-password"
            class="input-with-icon"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button 
          type="primary" 
          :loading="loading"
          @click="handleSubmit"
        >
          确认修改
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { changePasswordApi } from '@/api/auth'
import { useUserStore } from '@/store/modules/user'
import { useRouter } from 'vue-router'

// Props
interface Props {
  modelValue: boolean
}

// Emits
interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const userStore = useUserStore()
const router = useRouter()

// 响应式状态
const formRef = ref<FormInstance>()
const loading = ref(false)
const visible = ref(false)
const passwordStrength = ref(0)

// 表单数据
const formData = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const rules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码长度应为8-20位', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value === formData.oldPassword) {
          callback(new Error('新密码不能与原密码相同'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== formData.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

// 密码强度计算
const checkPasswordStrength = () => {
  const password = formData.newPassword
  let strength = 0
  
  // 长度检查
  if (password.length >= 8) strength++
  
  // 包含小写字母
  if (/[a-z]/.test(password)) strength++
  
  // 包含大写字母
  if (/[A-Z]/.test(password)) strength++
  
  // 包含数字
  if (/\d/.test(password)) strength++
  
  // 包含特殊字符
  if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) strength++
  
  // 最大强度为4
  passwordStrength.value = Math.min(strength, 4)
}

// 密码强度文本
const strengthText = computed(() => {
  const texts = ['很弱', '较弱', '一般', '较强', '很强']
  return texts[passwordStrength.value] || '很弱'
})

// 密码强度文本样式
const strengthTextClass = computed(() => {
  const classes = ['text-red-500', 'text-orange-500', 'text-yellow-500', 'text-blue-500', 'text-green-500']
  return classes[passwordStrength.value] || 'text-red-500'
})

// 强度指示器样式
const getStrengthItemClass = (index: number) => {
  const baseClass = 'w-full h-1 rounded-full transition-colors'
  if (index <= passwordStrength.value) {
    const activeClasses = ['bg-red-400', 'bg-orange-400', 'bg-yellow-400', 'bg-blue-400', 'bg-green-400']
    return `${baseClass} ${activeClasses[passwordStrength.value] || 'bg-red-400'}`
  }
  return `${baseClass} bg-gray-200`
}

// 监听弹窗显示状态
watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    nextTick(() => {
      resetForm()
    })
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  formData.oldPassword = ''
  formData.newPassword = ''
  formData.confirmPassword = ''
  passwordStrength.value = 0
}

// 关闭弹窗
const handleClose = () => {
  visible.value = false
  resetForm()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    // 检查密码强度
    if (passwordStrength.value < 2) {
      ElMessage.warning('建议使用更强的密码组合')
      return
    }

    loading.value = true
    
    // 调用API修改密码
    await changePasswordApi({
      currentPassword: formData.oldPassword,
      newPassword: formData.newPassword,
      confirmPassword: formData.confirmPassword
    })

    ElMessage.success('密码修改成功，即将重新登录')
    emit('success')
    handleClose()

    // 延迟登出，给用户时间看到成功消息
    setTimeout(() => {
      ElMessageBox.alert(
        '密码已修改成功，请使用新密码重新登录',
        '修改成功',
        {
          confirmButtonText: '确定',
          type: 'success'
        }
      ).then(() => {
        userStore.logout()
        router.push('/login')
      })
    }, 1000)

  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('修改失败，请检查原密码是否正确')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.password-change-form {
  @apply max-w-md mx-auto;
}

.input-with-icon :deep(.el-input__prefix) {
  @apply text-gray-400;
}

.input-with-icon :deep(.el-input__inner) {
  @apply pl-10;
}

.dialog-footer {
  @apply text-right;
}

.password-strength {
  @apply w-full;
}

.strength-bar {
  @apply w-full;
}

.strength-item {
  @apply flex-1 h-1 rounded-full transition-all duration-300;
}

.strength-text {
  @apply font-medium;
}

/* 表单项间距优化 */
.el-form-item {
  @apply mb-6;
}

.el-form-item:last-child {
  @apply mb-0;
}

/* 安全提示样式 */
.el-alert :deep(.el-alert__content) {
  @apply text-sm;
}

.el-alert p {
  @apply mb-1;
}

.el-alert p:last-child {
  @apply mb-0;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .password-change-form {
    @apply px-0;
  }
}
</style>
