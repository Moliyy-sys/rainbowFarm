<template>
  <el-dialog
    :model-value="visible"
    title="地块详情"
    width="700px"
    @update:model-value="$emit('update:visible', $event)"
  >
    <div class="land-detail">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="地块ID">
          {{ land.landId || '-' }}
        </el-descriptions-item>
        
        <el-descriptions-item label="地块名称">
          {{ land.landName || '-' }}
        </el-descriptions-item>
        
        <el-descriptions-item label="地块类型">
          {{ land.landType || '-' }}
        </el-descriptions-item>
        
        <el-descriptions-item label="地块面积">
          {{ land.landArea ? `${land.landArea} 亩` : '-' }}
        </el-descriptions-item>
        
        <el-descriptions-item label="边框宽度">
          {{ land.strokeWeight ? `${land.strokeWeight}px` : '-' }}
        </el-descriptions-item>
        
        <el-descriptions-item label="边框颜色">
          <div class="color-display" v-if="land.strokeColor">
            <div 
              class="color-box" 
              :style="{ backgroundColor: land.strokeColor }"
            ></div>
            <span>{{ land.strokeColor }}</span>
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
        
        <el-descriptions-item label="边框透明度">
          {{ land.strokeOpacity !== undefined ? `${(land.strokeOpacity * 100).toFixed(0)}%` : '-' }}
        </el-descriptions-item>
        
        <el-descriptions-item label="填充颜色">
          <div class="color-display" v-if="land.fillColor">
            <div 
              class="color-box" 
              :style="{ backgroundColor: land.fillColor }"
            ></div>
            <span>{{ land.fillColor }}</span>
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
        
        <el-descriptions-item label="填充透明度">
          {{ land.fillOpacity !== undefined ? `${(land.fillOpacity * 100).toFixed(0)}%` : '-' }}
        </el-descriptions-item>
        
        <el-descriptions-item label="当前批次">
          {{ land.currentBatch || '-' }}
        </el-descriptions-item>
        
        <el-descriptions-item label="创建时间" :span="2">
          {{ formatDateTime(land.createTime) }}
        </el-descriptions-item>
        
        <el-descriptions-item label="更新时间" :span="2">
          {{ formatDateTime(land.updateTime) }}
        </el-descriptions-item>
        
        <el-descriptions-item label="地块路径" :span="2">
          <div class="path-content">
            {{ land.landPath || '-' }}
          </div>
        </el-descriptions-item>
        
        <el-descriptions-item label="备注" :span="2">
          <div class="remark-content">
            {{ land.remark || '-' }}
          </div>
        </el-descriptions-item>
      </el-descriptions>
      
      <!-- 地块预览区域 -->
      <div class="preview-section" v-if="land.fillColor || land.strokeColor">
        <h4>地块样式预览</h4>
        <div class="preview-container">
          <svg width="200" height="150" viewBox="0 0 200 150">
            <rect
              x="20"
              y="20"
              width="160"
              height="110"
              :fill="land.fillColor || '#00ff00'"
              :fill-opacity="land.fillOpacity || 0.3"
              :stroke="land.strokeColor || '#000000'"
              :stroke-width="land.strokeWeight || 2"
              :stroke-opacity="land.strokeOpacity || 1"
            />
            <text x="100" y="80" text-anchor="middle" fill="#333" font-size="12">
              {{ land.landName }}
            </text>
          </svg>
        </div>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="$emit('update:visible', false)">
          关闭
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import type { Land } from '@/types/plant'

interface Props {
  visible: boolean
  land: Partial<Land>
}

interface Emits {
  (e: 'update:visible', value: boolean): void
}

defineProps<Props>()
defineEmits<Emits>()

const formatDateTime = (dateTime: string | undefined) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}
</script>

<style scoped>
.land-detail {
  padding: 10px 0;
}

.color-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.color-box {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  display: inline-block;
}

.path-content,
.remark-content {
  max-height: 100px;
  overflow-y: auto;
  word-break: break-all;
  line-height: 1.5;
}

.preview-section {
  margin-top: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 6px;
}

.preview-section h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
}

.preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #ffffff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>
