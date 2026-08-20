<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑仓库' : '新增仓库'"
    width="600px"
    :close-on-click-modal="false"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="仓库名称" prop="warehouseName">
        <el-input
          v-model="form.warehouseName"
          placeholder="请输入仓库名称"
          clearable
        />
      </el-form-item>
      
      <el-form-item label="仓库编码" prop="warehouseCode">
        <el-input
          v-model="form.warehouseCode"
          placeholder="请输入仓库编码"
          clearable
        />
      </el-form-item>
      
      <el-form-item label="排序" prop="orderNum">
        <el-input-number
          v-model="form.orderNum"
          :min="0"
          :max="999"
          placeholder="排序"
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
import { addWarehouseApi, updateWarehouseApi } from '@/api/inventory'
import type { Warehouse } from '@/types/inventory'

interface Props {
  visible: boolean
  warehouse?: Partial<Warehouse>
}

interface Emits {
  (e: 'update:visible', visible: boolean): void
  (e: 'success'): void
}

const props = withDefaults(defineProps<Props>(), {
  warehouse: () => ({})
})

const emit = defineEmits<Emits>()

const formRef = ref<FormInstance>()
const loading = ref(false)

const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

const isEdit = computed(() => !!(props.warehouse?.id))

const form = reactive<Partial<Warehouse>>({
  warehouseName: '',
  warehouseCode: '',
  orderNum: 0,
  remark: ''
})

const rules: FormRules = {
  warehouseName: [
    { required: true, message: '请输入仓库名称', trigger: 'blur' }
  ],
  warehouseCode: [
    { required: true, message: '请输入仓库编码', trigger: 'blur' }
  ]
}

// 监听warehouse变化，更新表单
watch(() => props.warehouse, (newWarehouse) => {
  if (newWarehouse) {
    Object.assign(form, {
      id: newWarehouse.id,
      warehouseName: newWarehouse.warehouseName || '',
      warehouseCode: newWarehouse.warehouseCode || '',
      orderNum: newWarehouse.orderNum || 0,
      remark: newWarehouse.remark || ''
    })
  }
}, { immediate: true, deep: true })

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: undefined,
    warehouseName: '',
    warehouseCode: '',
    orderNum: 0,
    remark: ''
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
      await updateWarehouseApi(form)
      ElMessage.success('更新成功')
    } else {
      await addWarehouseApi(form)
      ElMessage.success('新增成功')
    }
    
    emit('success')
    resetForm()
    
  } catch (error) {
    console.error('保存仓库失败:', error)
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
