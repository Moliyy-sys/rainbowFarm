<template>
  <el-dialog
    :model-value="visible"
    :title="mode === 'add' ? '添加地块' : '编辑地块'"
    width="800px"
    @update:model-value="$emit('update:visible', $event)"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="地块名称" prop="landName">
        <el-input 
          v-model="formData.landName" 
          placeholder="请输入地块名称"
          clearable
        />
      </el-form-item>
      
      <el-form-item label="地块类型" prop="landType">
        <el-select 
          v-model="formData.landType" 
          placeholder="请选择地块类型"
          clearable
          style="width: 100%"
        >
          <el-option label="耕地" value="0" />
          <el-option label="大棚" value="1" />
          <el-option label="果园" value="2" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="地块面积" prop="landArea">
        <el-input-number 
          v-model="formData.landArea" 
          :min="0"
          :precision="2"
          placeholder="请输入地块面积"
          style="width: 100%"
        />
        <span class="ml-2 text-gray-500">亩</span>
      </el-form-item>
      
      <el-form-item label="边框宽度">
        <el-input-number 
          v-model="formData.strokeWeight" 
          :min="0"
          placeholder="请输入边框宽度"
          style="width: 100%"
        />
        <span class="ml-2 text-gray-500">像素</span>
      </el-form-item>
      
      <el-form-item label="边框颜色">
        <el-color-picker 
          v-model="formData.strokeColor"
          show-alpha
          :predefine="predefineColors"
        />
      </el-form-item>
      
      <el-form-item label="边框透明度">
        <el-slider 
          v-model="formData.strokeOpacity" 
          :min="0"
          :max="1"
          :step="0.1"
          show-input
        />
      </el-form-item>
      
      <el-form-item label="地块边界" prop="landPath">
        <div class="map-section">
          <el-text size="small" type="info" class="mb-2">
            请在地图上绘制地块边界，系统将自动计算面积
          </el-text>
          <AMapDrawer 
            v-model="formData.landPath"
            :height="350"
            api-key="a1144a3e8922b298683254b91aedae13"
            :stroke-color="formData.strokeColor"
            :stroke-weight="formData.strokeWeight"
            :stroke-opacity="formData.strokeOpacity"
            :fill-color="formData.fillColor"
            :fill-opacity="formData.fillOpacity"
            @change="handleBoundaryChange"
          />
        </div>
      </el-form-item>
      
      <el-form-item label="填充颜色">
        <el-color-picker 
          v-model="formData.fillColor"
          show-alpha
          :predefine="predefineColors"
        />
      </el-form-item>
      
      <el-form-item label="填充透明度">
        <el-slider 
          v-model="formData.fillOpacity" 
          :min="0"
          :max="1"
          :step="0.1"
          show-input
        />
      </el-form-item>
      
      <el-form-item label="备注">
        <el-input 
          v-model="formData.remark" 
          type="textarea"
          :rows="3"
          placeholder="请输入备注信息"
          clearable
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :loading="loading">
          {{ mode === 'add' ? '添加' : '更新' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, nextTick } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { Land } from '@/types/plant'
import AMapDrawer from '@/components/AMapDrawer.vue'

interface Props {
  visible: boolean
  land: Partial<Land>
  mode: 'add' | 'edit'
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'confirm', land: Land): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const formRef = ref<FormInstance>()
const loading = ref(false)

const formData = reactive<Partial<Land>>({
  landName: '',
  landType: '',
  landArea: 0,
  strokeWeight: 2,
  strokeColor: '#000000',
  strokeOpacity: 1,
  landPath: '',
  fillColor: '#00ff00',
  fillOpacity: 0.3,
  remark: ''
})

const predefineColors = [
  '#ff4500',
  '#ff8c00',
  '#ffd700',
  '#90ee90',
  '#00ced1',
  '#1e90ff',
  '#c71585',
  '#rgba(255, 69, 0, 0.68)',
  '#rgb(255, 120, 0)',
  '#hsv(51, 100, 98)',
  '#hsva(120, 40, 94, 0.5)',
  '#hsl(181, 100%, 37%)',
  '#hsla(209, 100%, 56%, 0.73)',
  '#000000',
  '#ffffff'
]

const rules: FormRules = {
  landName: [
    { required: true, message: '请输入地块名称', trigger: 'blur' }
  ],
  landType: [
    { required: true, message: '请输入地块类型', trigger: 'blur' }
  ],
  landArea: [
    { required: true, message: '请输入地块面积', trigger: 'blur' },
    { type: 'number', min: 0, message: '面积必须大于等于0', trigger: 'blur' }
  ],
  landPath: [
    { required: true, message: '请在地图上绘制地块边界', trigger: 'change' }
  ]
}

// 监听 props 变化，更新表单数据
watch(
  () => props.land,
  (newLand) => {
    if (newLand && Object.keys(newLand).length > 0) {
      Object.assign(formData, {
        landId: newLand.landId,
        landName: newLand.landName || '',
        landType: newLand.landType || '',
        landArea: newLand.landArea || 0,
        strokeWeight: newLand.strokeWeight || 2,
        strokeColor: newLand.strokeColor || '#000000',
        strokeOpacity: newLand.strokeOpacity || 1,
        landPath: newLand.landPath || '',
        fillColor: newLand.fillColor || '#00ff00',
        fillOpacity: newLand.fillOpacity || 0.3,
        remark: newLand.remark || ''
      })
    } else {
      // 重置表单
      Object.assign(formData, {
        landId: undefined,
        landName: '',
        landType: '',
        landArea: 0,
        strokeWeight: 2,
        strokeColor: '#000000',
        strokeOpacity: 1,
        landPath: '',
        fillColor: '#00ff00',
        fillOpacity: 0.3,
        remark: ''
      })
    }
  },
  { immediate: true, deep: true }
)

const handleClose = () => {
  emit('update:visible', false)
  nextTick(() => {
    formRef.value?.resetFields()
  })
}

// 处理边界坐标变化
const handleBoundaryChange = (coordinates: Array<[number, number]>) => {
  if (coordinates.length > 2) {
    // 计算面积（简单的多边形面积计算）
    const area = calculatePolygonArea(coordinates)
    const areaInMu = (area / 666.67) // 转换为亩
    
    // 自动填充面积字段
    if (areaInMu > 0) {
      formData.landArea = Math.round(areaInMu * 100) / 100 // 保礁2位小数
    }
  }
}

// 计算多边形面积（使用Shoelace公式）
const calculatePolygonArea = (coordinates: Array<[number, number]>): number => {
  if (coordinates.length < 3) return 0
  
  let area = 0
  const n = coordinates.length
  
  for (let i = 0; i < n; i++) {
    const j = (i + 1) % n
    area += coordinates[i][0] * coordinates[j][1]
    area -= coordinates[j][0] * coordinates[i][1]
  }
  
  return Math.abs(area) / 2 * 111319.9 * 111319.9 // 转换为平方米
}

const handleConfirm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    emit('confirm', formData as Land)
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}

.ml-2 {
  margin-left: 8px;
}

.text-gray-500 {
  color: #6b7280;
}

.map-section {
  width: 100%;
}

.mb-2 {
  margin-bottom: 8px;
  display: block;
}
</style>
