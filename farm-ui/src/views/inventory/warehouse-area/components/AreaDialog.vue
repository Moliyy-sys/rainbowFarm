<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑库区' : '新增库区'"
    width="600px"
    :close-on-click-modal="false"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="库区名称" prop="areaName">
        <el-input
          v-model="form.areaName"
          placeholder="请输入库区名称"
          clearable
        />
      </el-form-item>
      
      <el-form-item label="库区编码" prop="areaCode">
        <el-input
          v-model="form.areaCode"
          placeholder="请输入库区编码"
          clearable
        />
      </el-form-item>
      
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注"
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :loading="loading">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { addAreaApi, updateAreaApi } from '@/api/inventory'
import type { WarehouseArea } from '@/types/inventory'

interface Props {
  visible: boolean
  area?: Partial<WarehouseArea>
  warehouseId?: number | null
}

interface Emits {
  (e: 'update:visible', visible: boolean): void
  (e: 'success'): void
}

const props = withDefaults(defineProps<Props>(), {
  area: () => ({}),
  warehouseId: null
})

const emit = defineEmits<Emits>()

const formRef = ref<FormInstance>()
const loading = ref(false)

const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

const isEdit = computed(() => !!(props.area?.id))

const form = reactive<Partial<WarehouseArea>>({
  areaName: '',
  areaCode: '',
  remark: '',
  warehouseId: undefined
})

const rules: FormRules = {
  areaName: [
    { required: true, message: '请输入库区名称', trigger: 'blur' }
  ],
  areaCode: [
    { required: true, message: '请输入库区编码', trigger: 'blur' }
  ]
}

// 监听area变化，更新表单
watch(() => [props.area, props.warehouseId], ([newArea, newWarehouseId]) => {
  if (newArea) {
    Object.assign(form, {
      id: newArea.id,
      areaName: newArea.areaName || '',
      areaCode: newArea.areaCode || '',
      remark: newArea.remark || '',
      warehouseId: newArea.warehouseId || newWarehouseId
    })
  } else {
    // 新增时设置仓库ID
    form.warehouseId = newWarehouseId
  }
}, { immediate: true, deep: true })

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: undefined,
    areaName: '',
    areaCode: '',
    remark: '',
    warehouseId: props.warehouseId
  })
  formRef.value?.resetFields()
}

// 取消
const handleCancel = () => {
  dialogVisible.value = false
  resetForm()
}

// 确认
const handleConfirm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    if (isEdit.value) {
      await updateAreaApi(form)
      ElMessage.success('更新成功')
    } else {
      await addAreaApi(form)
      ElMessage.success('新增成功')
    }
    
    emit('success')
    resetForm()
    
  } catch (error) {
    console.error('保存库区失败:', error)
    ElMessage.error('保存失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>
