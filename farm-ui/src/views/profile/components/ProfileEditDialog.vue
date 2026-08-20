<template>
  <el-dialog
    v-model="visible"
    title="编辑个人信息"
    width="500px"
    :before-close="handleClose"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      label-position="left"
    >
      <el-form-item label="用户名" prop="userName">
        <el-input
          v-model="formData.userName"
          placeholder="请输入用户名"
          disabled
          class="input-with-icon"
        >
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
        <div class="text-xs text-gray-500 mt-1">用户名不可修改</div>
      </el-form-item>

      <el-form-item label="昵称" prop="nickName">
        <el-input
          v-model="formData.nickName"
          placeholder="请输入昵称"
          maxlength="20"
          show-word-limit
          class="input-with-icon"
        >
          <template #prefix>
            <el-icon><Avatar /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input
          v-model="formData.email"
          placeholder="请输入邮箱地址"
          type="email"
          class="input-with-icon"
        >
          <template #prefix>
            <el-icon><Message /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item label="手机号" prop="phonenumber">
        <el-input
          v-model="formData.phonenumber"
          placeholder="请输入手机号"
          maxlength="11"
          class="input-with-icon"
        >
          <template #prefix>
            <el-icon><Phone /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item label="性别" prop="sex">
        <el-radio-group v-model="formData.sex">
          <el-radio label="0">
            <el-icon class="mr-1 text-blue-500"><Male /></el-icon>
            男
          </el-radio>
          <el-radio label="1">
            <el-icon class="mr-1 text-pink-500"><Female /></el-icon>
            女
          </el-radio>
          <el-radio label="2">
            <el-icon class="mr-1 text-gray-500"><QuestionFilled /></el-icon>
            保密
          </el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button 
          type="primary" 
          :loading="loading"
          @click="handleSubmit"
        >
          保存
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, nextTick } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { updateUserInfoApi } from '@/api/auth'
import type { UserInfo } from '@/types/api'

// Props
interface Props {
  modelValue: boolean
  userInfo?: UserInfo | null
}

// Emits
interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 响应式状态
const formRef = ref<FormInstance>()
const loading = ref(false)
const visible = ref(false)

// 表单数据
const formData = reactive({
  userId: 0,
  userName: '',
  nickName: '',
  email: '',
  phonenumber: '',
  sex: '2'
})

// 表单验证规则
const rules: FormRules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  nickName: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在2到20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phonenumber: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  sex: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ]
}

// 监听弹窗显示状态
watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.userInfo) {
    // 重置表单并填充数据
    nextTick(() => {
      resetForm()
      formData.userId = props.userInfo!.userId
      formData.userName = props.userInfo!.userName
      formData.nickName = props.userInfo!.nickName
      formData.email = props.userInfo!.email
      formData.phonenumber = props.userInfo!.phonenumber
      formData.sex = props.userInfo!.sex || '2'
    })
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
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

    loading.value = true
    
    // 调用API更新用户信息
    await updateUserInfoApi({
      userId: formData.userId,
      nickName: formData.nickName,
      email: formData.email,
      phonenumber: formData.phonenumber,
      sex: formData.sex
    })

    ElMessage.success('个人信息更新成功')
    emit('success')
    handleClose()
  } catch (error) {
    console.error('更新个人信息失败:', error)
    ElMessage.error('更新失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.input-with-icon :deep(.el-input__prefix) {
  @apply text-gray-400;
}

.input-with-icon :deep(.el-input__inner) {
  @apply pl-10;
}

.dialog-footer {
  @apply text-right;
}

.el-radio {
  @apply flex items-center mr-6;
}

.el-radio :deep(.el-radio__label) {
  @apply flex items-center;
}

/* 性别选项样式优化 */
.el-radio-group {
  @apply flex flex-wrap gap-4;
}

.el-radio {
  @apply border border-gray-200 rounded-lg px-4 py-2 transition-all;
  @apply hover:border-primary-300 hover:bg-primary-50;
}

.el-radio.is-checked {
  @apply border-primary-500 bg-primary-50;
}

.el-radio :deep(.el-radio__input.is-checked .el-radio__inner) {
  @apply border-primary-500 bg-primary-500;
}

.el-radio :deep(.el-radio__input.is-checked + .el-radio__label) {
  @apply text-primary-600 font-medium;
}

/* 表单项间距优化 */
.el-form-item {
  @apply mb-6;
}

.el-form-item:last-child {
  @apply mb-0;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .el-radio-group {
    @apply flex-col gap-2;
  }
  
  .el-radio {
    @apply w-full justify-start;
  }
}
</style>
