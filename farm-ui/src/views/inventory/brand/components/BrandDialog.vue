<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑品牌' : '新增品牌'"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      class="brand-form"
    >
      <el-form-item label="品牌名称" prop="brandName">
        <el-input
          v-model="formData.brandName"
          placeholder="请输入品牌名称"
          maxlength="100"
          show-word-limit
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
import { addItemBrandApi, updateItemBrandApi } from '@/api/inventory'
import type { ItemBrand } from '@/types/inventory'

// Props定义
interface Props {
  modelValue: boolean
  brandData?: ItemBrand | null
  isEdit: boolean
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  brandData: null,
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
const formData = reactive<Partial<ItemBrand>>({
  brandName: '',
  remark: ''
})

// 表单验证规则
const formRules: FormRules = {
  brandName: [
    { required: true, message: '请输入品牌名称', trigger: 'blur' },
    { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
  ]
}

// 监听弹窗显示状态
watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    nextTick(() => {
      resetForm()
      if (props.isEdit && props.brandData) {
        Object.assign(formData, props.brandData)
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
    brandName: '',
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

    // 准备提交数据，只包含必要的字段
    const submitData: any = {
      brandName: formData.brandName,
      remark: formData.remark || ''
    }

    if (props.isEdit) {
      // 编辑
      submitData.id = props.brandData?.id
      await updateItemBrandApi(submitData)
      ElMessage.success('修改成功')
    } else {
      // 新增
      await addItemBrandApi(submitData)
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
.brand-form {
  @apply mt-4;
}

.dialog-footer {
  @apply flex justify-end gap-3;
}
</style>

