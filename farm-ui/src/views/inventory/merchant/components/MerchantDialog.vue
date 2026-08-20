<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑往来单位' : '新增往来单位'"
    width="650px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      class="merchant-form"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="单位编码" prop="merchantCode">
            <el-input
              v-model="formData.merchantCode"
              placeholder="请输入往来单位编码"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="单位名称" prop="merchantName">
            <el-input
              v-model="formData.merchantName"
              placeholder="请输入往来单位名称"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="单位类型" prop="merchantType">
            <el-select
              v-model="formData.merchantType"
              placeholder="请选择单位类型"
              style="width: 100%"
            >
              <el-option label="供应商" :value="1" />
              <el-option label="客户" :value="2" />
              <el-option label="供应商客户" :value="3" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系人" prop="contactPerson">
            <el-input
              v-model="formData.contactPerson"
              placeholder="请输入联系人"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="手机号" prop="mobile">
            <el-input
              v-model="formData.mobile"
              placeholder="请输入手机号"
              maxlength="11"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="座机号" prop="tel">
            <el-input
              v-model="formData.tel"
              placeholder="请输入座机号"
              maxlength="20"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="地址" prop="address">
        <el-input
          v-model="formData.address"
          placeholder="请输入地址"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input
          v-model="formData.email"
          placeholder="请输入邮箱地址"
          maxlength="100"
        />
      </el-form-item>

      <el-form-item label="备注">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注信息"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ isEdit ? '保存' : '新增' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, nextTick } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { addMerchantApi, updateMerchantApi } from '@/api/inventory'
import type { Merchant } from '@/types/inventory'

// Props定义
interface Props {
  modelValue: boolean
  merchantData?: Merchant | null
  isEdit: boolean
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  merchantData: null,
  isEdit: false
})

// Emits定义
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  success: []
}>()

// 响应式数据
const visible = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

// 表单数据
const formData = reactive<Partial<Merchant>>({
  merchantCode: '',
  merchantName: '',
  merchantType: 1,
  contactPerson: '',
  mobile: '',
  tel: '',
  address: '',
  email: '',
  remark: ''
})

// 表单验证规则
const formRules: FormRules = {
  merchantCode: [
    { required: true, message: '请输入往来单位编码', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_-]+$/, message: '编码只能包含字母、数字、下划线和横线', trigger: 'blur' }
  ],
  merchantName: [
    { required: true, message: '请输入往来单位名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  merchantType: [
    { required: true, message: '请选择单位类型', trigger: 'change' }
  ],
  contactPerson: [
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  mobile: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  tel: [
    { pattern: /^(\d{3,4}-?)?\d{7,8}$/, message: '请输入正确的座机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 监听弹窗显示状态
watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    nextTick(() => {
      resetForm()
      if (props.isEdit && props.merchantData) {
        Object.assign(formData, props.merchantData)
      }
    })
  }
})

// 监听visible变化，同步给父组件
watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.clearValidate()
  }
  Object.assign(formData, {
    merchantCode: '',
    merchantName: '',
    merchantType: 1,
    contactPerson: '',
    mobile: '',
    tel: '',
    address: '',
    email: '',
    remark: ''
  })
}

// 关闭弹窗
const handleClose = () => {
  visible.value = false
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    submitLoading.value = true

    if (props.isEdit) {
      // 编辑
      await updateMerchantApi({
        ...formData,
        id: props.merchantData?.id
      })
      ElMessage.success('修改成功')
    } else {
      // 新增
      await addMerchantApi(formData)
      ElMessage.success('新增成功')
    }

    emit('success')
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(props.isEdit ? '修改失败' : '新增失败')
  } finally {
    submitLoading.value = false
  }
}
</script>

<style scoped>
.merchant-form {
  @apply mt-4;
}

.dialog-footer {
  @apply flex justify-end gap-3;
}
</style>
