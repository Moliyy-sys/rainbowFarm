<template>
  <div class="amap-drawer-container">
    <div class="map-toolbar">
      <el-button 
        type="primary" 
        size="small" 
        @click="startDraw"
        :disabled="isDrawing"
      >
        {{ isDrawing ? '绘制中...' : '开始绘制' }}
      </el-button>
      <el-button 
        type="warning" 
        size="small" 
        @click="clearDraw"
        :disabled="!hasPolygon"
      >
        清除
      </el-button>
      <el-button 
        type="success" 
        size="small" 
        @click="finishDraw"
        :disabled="!hasPolygon"
      >
        完成绘制
      </el-button>
    </div>
    <div 
      ref="mapContainer" 
      class="map-container"
      :style="{ height: mapHeight + 'px' }"
    ></div>
    <div v-if="coordinates.length > 0" class="coordinates-info">
      <el-text size="small" type="info">
        已绘制 {{ coordinates.length }} 个坐标点
      </el-text>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import { ElMessage } from 'element-plus'

interface Props {
  modelValue?: string // 坐标字符串
  height?: number
  center?: [number, number]
  zoom?: number
  apiKey?: string
  strokeColor?: string // 边框颜色
  strokeWeight?: number // 边框宽度
  strokeOpacity?: number // 边框透明度
  fillColor?: string // 填充颜色
  fillOpacity?: number // 填充透明度
}

interface Emits {
  (e: 'update:modelValue', value: string): void
  (e: 'change', coordinates: Array<[number, number]>): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  height: 400,
  center: () => [116.397428, 39.90923], // 北京天安门
  zoom: 13,
  apiKey: import.meta.env.VITE_AMAP_API_KEY || 'demo-key', // 从环境变量读取API Key
  strokeColor: '#FF0000',
  strokeWeight: 2,
  strokeOpacity: 1,
  fillColor: '#00FF00',
  fillOpacity: 0.3
})

const emit = defineEmits<Emits>()

const mapContainer = ref<HTMLDivElement>()
const mapHeight = ref(props.height)
const isDrawing = ref(false)
const hasPolygon = ref(false)
const coordinates = ref<Array<[number, number]>>([])

let map: any = null
let mouseTool: any = null
let currentPolygon: any = null

// 初始化地图
const initMap = async () => {
  try {
    const AMap = await AMapLoader.load({
      key: props.apiKey,
      version: '2.0',
      plugins: ['AMap.MouseTool', 'AMap.ToolBar', 'AMap.Scale']
    })

    if (!mapContainer.value) return

    // 创建地图实例
    map = new AMap.Map(mapContainer.value, {
      center: props.center,
      zoom: props.zoom,
      mapStyle: 'amap://styles/normal',
      viewMode: '2D'
    })

    // 添加工具栏
    map.addControl(new AMap.ToolBar({
      position: {
        top: '10px',
        right: '10px'
      }
    }))

    // 添加比例尺
    map.addControl(new AMap.Scale({
      position: {
        bottom: '10px',
        left: '10px'
      }
    }))

    // 初始化绘制工具
    mouseTool = new AMap.MouseTool(map)

    // 监听绘制完成事件
    mouseTool.on('draw', (event: any) => {
      currentPolygon = event.obj
      hasPolygon.value = true
      isDrawing.value = false
      
      // 获取多边形路径
      const path = currentPolygon.getPath()
      const coords = path.map((point: any) => [point.lng, point.lat])
      coordinates.value = coords
      
      // 计算并显示面积
      const area = AMap.GeometryUtil.ringArea(path)
      const areaInMu = (area / 666.67).toFixed(2) // 转换为亩
      
      ElMessage.success(`绘制完成！面积约 ${areaInMu} 亩`)
      
      // 发射事件
      const coordsString = JSON.stringify(coords)
      emit('update:modelValue', coordsString)
      emit('change', coords)
    })

    // 如果有初始坐标值，显示在地图上
    if (props.modelValue) {
      displayInitialPolygon()
    }

  } catch (error) {
    console.error('地图初始化失败:', error)
    ElMessage.error('地图加载失败，请检查网络连接')
  }
}

// 显示初始多边形
const displayInitialPolygon = () => {
  try {
    const coords = JSON.parse(props.modelValue)
    if (Array.isArray(coords) && coords.length > 0) {
      coordinates.value = coords
      
      // 创建多边形
      const path = coords.map(([lng, lat]: [number, number]) => [lng, lat])
      currentPolygon = new (window as any).AMap.Polygon({
        path: path,
        strokeColor: props.strokeColor,
        strokeWeight: props.strokeWeight,
        strokeOpacity: props.strokeOpacity,
        fillColor: props.fillColor,
        fillOpacity: props.fillOpacity,
        strokeStyle: 'solid'
      })
      
      map.add(currentPolygon)
      hasPolygon.value = true
      
      // 调整地图视野以适应多边形
      map.setFitView([currentPolygon])
    }
  } catch (error) {
    console.warn('初始坐标解析失败:', error)
  }
}

// 开始绘制
const startDraw = () => {
  if (!mouseTool) {
    ElMessage.error('地图工具未初始化')
    return
  }
  
  // 清除之前的多边形
  if (currentPolygon) {
    map.remove(currentPolygon)
    currentPolygon = null
    hasPolygon.value = false
    coordinates.value = []
  }
  
  isDrawing.value = true
  
  // 开始绘制多边形
  mouseTool.polygon({
    strokeColor: props.strokeColor,
    strokeWeight: props.strokeWeight,
    strokeOpacity: props.strokeOpacity,
    fillColor: props.fillColor,
    fillOpacity: props.fillOpacity,
    strokeStyle: 'solid'
  })
  
  ElMessage.info('请在地图上点击绘制地块边界，双击完成绘制')
}

// 清除绘制
const clearDraw = () => {
  if (currentPolygon) {
    map.remove(currentPolygon)
    currentPolygon = null
  }
  
  hasPolygon.value = false
  isDrawing.value = false
  coordinates.value = []
  
  // 停止绘制工具
  if (mouseTool) {
    mouseTool.close(true)
  }
  
  // 清空表单值
  emit('update:modelValue', '')
  emit('change', [])
  
  ElMessage.success('已清除绘制内容')
}

// 完成绘制
const finishDraw = () => {
  if (!hasPolygon.value || coordinates.value.length === 0) {
    ElMessage.warning('请先绘制地块边界')
    return
  }
  
  // 停止绘制工具
  if (mouseTool) {
    mouseTool.close(true)
  }
  
  isDrawing.value = false
  
  const coordsString = JSON.stringify(coordinates.value)
  emit('update:modelValue', coordsString)
  emit('change', coordinates.value)
  
  ElMessage.success('地块边界设置完成')
}

// 监听props变化
watch(() => props.modelValue, (newValue) => {
  if (newValue && map && !hasPolygon.value) {
    displayInitialPolygon()
  }
})

// 监听颜色和透明度变化，更新多边形样式
watch(
  () => [props.strokeColor, props.strokeWeight, props.strokeOpacity, props.fillColor, props.fillOpacity],
  () => {
    if (currentPolygon && map) {
      currentPolygon.setOptions({
        strokeColor: props.strokeColor,
        strokeWeight: props.strokeWeight,
        strokeOpacity: props.strokeOpacity,
        fillColor: props.fillColor,
        fillOpacity: props.fillOpacity
      })
    }
  }
)

onMounted(() => {
  initMap()
})

onUnmounted(() => {
  if (map) {
    map.destroy()
  }
})
</script>

<style scoped>
.amap-drawer-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.map-toolbar {
  padding: 10px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  gap: 8px;
  align-items: center;
}

.map-container {
  width: 100%;
  position: relative;
}

.coordinates-info {
  padding: 8px 10px;
  background-color: #f5f7fa;
  border-top: 1px solid #dcdfe6;
  text-align: center;
}

/* 地图控件样式调整 */
:deep(.amap-toolbar) {
  background: rgba(255, 255, 255, 0.8) !important;
}

:deep(.amap-scale) {
  background: rgba(255, 255, 255, 0.8) !important;
}
</style>
