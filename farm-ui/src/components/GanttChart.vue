<template>
  <div class="gantt-chart">
    <div class="gantt-header">
      <div class="task-list-header">
        <div class="task-name-column">任务名称</div>
        <div class="task-status-column">状态</div>
      </div>
      <div class="timeline-header" ref="timelineHeaderRef">
        <div
          v-for="date in timelineDates"
          :key="date.key"
          :class="['timeline-date', { 'is-today': date.isToday }]"
          :style="{ width: dayWidth + 'px' }"
        >
          <div class="date-label">{{ date.label }}</div>
          <div class="date-day">{{ date.day }}</div>
        </div>
      </div>
    </div>
    
    <div class="gantt-body" @scroll="handleScroll">
      <div class="task-list">
        <div
          v-for="(task, index) in tasks"
          :key="task.taskId || index"
          class="task-row"
          :style="{ height: rowHeight + 'px' }"
        >
          <div class="task-name-column">
            <div class="task-name" :title="task.taskName">
              {{ task.taskName }}
            </div>
          </div>
          <div class="task-status-column">
            <el-tag
              :type="getStatusTagType(task.status)"
              size="small"
            >
              {{ getStatusText(task.status) }}
            </el-tag>
          </div>
        </div>
      </div>
      
      <div class="timeline-content" ref="timelineContentRef">
        <div class="timeline-grid">
          <!-- 垂直网格线 -->
          <div
            v-for="(date, index) in timelineDates"
            :key="'grid-' + date.key"
            class="grid-line vertical"
            :style="{
              left: index * dayWidth + 'px',
              height: tasks.length * rowHeight + 'px'
            }"
          />
          
          <!-- 水平网格线 -->
          <div
            v-for="index in tasks.length + 1"
            :key="'h-grid-' + index"
            class="grid-line horizontal"
            :style="{
              top: (index - 1) * rowHeight + 'px',
              width: timelineDates.length * dayWidth + 'px'
            }"
          />
          
          <!-- 今日线 -->
          <div
            v-if="todayPosition >= 0"
            class="today-line"
            :style="{
              left: todayPosition + 'px',
              height: tasks.length * rowHeight + 'px'
            }"
          />
        </div>
        
        <!-- 任务条 -->
        <div class="task-bars">
          <div
            v-for="(task, index) in tasks"
            :key="'bar-' + (task.taskId || index)"
            class="task-bar-row"
            :style="{ top: index * rowHeight + 'px', height: rowHeight + 'px' }"
          >
            <div
              v-if="getTaskBarStyle(task)"
              :class="['task-bar', `status-${task.status}`]"
              :style="getTaskBarStyle(task)"
              :title="`${task.taskName} (${formatDate(task.planStart)} - ${formatDate(task.planFinish)})`"
            >
              <div class="task-bar-content">
                <span class="task-bar-text">{{ task.taskName }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import dayjs from 'dayjs'

interface Task {
  taskId?: number
  taskName: string
  planStart: string
  planFinish: string
  status: string
  taskDetail?: string
  remark?: string
}

interface Props {
  tasks: Task[]
  startDate?: string
  endDate?: string
}

const props = withDefaults(defineProps<Props>(), {
  tasks: () => [],
  startDate: '',
  endDate: ''
})

// 配置
const dayWidth = 60 // 每天的宽度
const rowHeight = 50 // 每行的高度

// 引用
const timelineHeaderRef = ref<HTMLElement>()
const timelineContentRef = ref<HTMLElement>()

// 计算时间范围
const dateRange = computed(() => {
  if (!props.tasks.length) {
    const today = dayjs()
    return {
      start: today.subtract(7, 'day'),
      end: today.add(30, 'day')
    }
  }

  let minDate = dayjs()
  let maxDate = dayjs()

  props.tasks.forEach(task => {
    if (task.planStart) {
      const start = dayjs(task.planStart)
      if (start.isBefore(minDate)) minDate = start
    }
    if (task.planFinish) {
      const end = dayjs(task.planFinish)
      if (end.isAfter(maxDate)) maxDate = end
    }
  })

  // 添加缓冲区
  return {
    start: minDate.subtract(7, 'day').startOf('day'),
    end: maxDate.add(7, 'day').endOf('day')
  }
})

// 生成时间轴日期
const timelineDates = computed(() => {
  const dates = []
  let current = dateRange.value.start
  const end = dateRange.value.end
  const today = dayjs().format('YYYY-MM-DD')

  while (current.isBefore(end) || current.isSame(end, 'day')) {
    const dateStr = current.format('YYYY-MM-DD')
    dates.push({
      key: dateStr,
      label: current.format('MM/DD'),
      day: current.format('DD'),
      date: current,
      isToday: dateStr === today
    })
    current = current.add(1, 'day')
  }

  return dates
})

// 今日位置
const todayPosition = computed(() => {
  const today = dayjs().format('YYYY-MM-DD')
  const todayIndex = timelineDates.value.findIndex(d => d.key === today)
  return todayIndex >= 0 ? todayIndex * dayWidth + dayWidth / 2 : -1
})

// 获取任务条样式
const getTaskBarStyle = (task: Task) => {
  if (!task.planStart || !task.planFinish) return null

  const startDate = dayjs(task.planStart)
  const endDate = dayjs(task.planFinish)
  
  // 计算开始位置
  const startIndex = timelineDates.value.findIndex(d => d.date.isSame(startDate, 'day'))
  if (startIndex === -1) return null

  // 计算结束位置
  const endIndex = timelineDates.value.findIndex(d => d.date.isSame(endDate, 'day'))
  const actualEndIndex = endIndex === -1 ? timelineDates.value.length - 1 : endIndex

  const left = startIndex * dayWidth
  const width = (actualEndIndex - startIndex + 1) * dayWidth - 2 // 减去边距

  return {
    left: left + 'px',
    width: width + 'px',
    top: '8px',
    height: (rowHeight - 16) + 'px'
  }
}

// 状态相关方法
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    '0': '待执行',
    '1': '进行中',
    '2': '已暂停',
    '3': '已完成'
  }
  return statusMap[status] || '未知'
}

const getStatusTagType = (status: string) => {
  const typeMap: Record<string, string> = {
    '0': 'info',
    '1': 'warning',
    '2': 'danger',
    '3': 'success'
  }
  return typeMap[status] || 'info'
}

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  return dayjs(dateStr).format('MM/DD')
}

// 同步滚动
const handleScroll = (event: Event) => {
  const target = event.target as HTMLElement
  if (timelineHeaderRef.value) {
    timelineHeaderRef.value.scrollLeft = target.scrollLeft
  }
}

onMounted(() => {
  nextTick(() => {
    // 滚动到今日位置
    if (todayPosition.value > 0 && timelineContentRef.value) {
      const scrollLeft = Math.max(0, todayPosition.value - 300)
      timelineContentRef.value.scrollLeft = scrollLeft
      if (timelineHeaderRef.value) {
        timelineHeaderRef.value.scrollLeft = scrollLeft
      }
    }
  })
})
</script>

<style scoped>
.gantt-chart {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background: #fff;
  font-size: 14px;
}

.gantt-header {
  display: flex;
  border-bottom: 2px solid #e4e7ed;
  background: #f5f7fa;
}

.task-list-header {
  display: flex;
  width: 350px;
  flex-shrink: 0;
  border-right: 2px solid #e4e7ed;
}

.task-name-column {
  width: 200px;
  padding: 12px 16px;
  font-weight: 600;
  border-right: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
}

.task-status-column {
  width: 150px;
  padding: 12px 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.timeline-header {
  flex: 1;
  display: flex;
  overflow-x: hidden;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.timeline-header::-webkit-scrollbar {
  display: none;
}

.timeline-date {
  flex-shrink: 0;
  border-right: 1px solid #e4e7ed;
  padding: 8px 4px;
  text-align: center;
  transition: background-color 0.2s;
}

.timeline-date.is-today {
  background: #e1f3d8;
  color: #67c23a;
  font-weight: 600;
}

.date-label {
  font-size: 12px;
  color: #606266;
  margin-bottom: 2px;
}

.date-day {
  font-size: 14px;
  font-weight: 600;
}

.gantt-body {
  display: flex;
  max-height: 400px;
  overflow: auto;
}

.task-list {
  width: 350px;
  flex-shrink: 0;
  border-right: 2px solid #e4e7ed;
  background: #fafafa;
}

.task-row {
  display: flex;
  border-bottom: 1px solid #e4e7ed;
  align-items: center;
}

.task-row .task-name-column {
  border-right: 1px solid #e4e7ed;
  font-weight: normal;
}

.task-row .task-status-column {
  font-weight: normal;
}

.task-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

.timeline-content {
  flex: 1;
  position: relative;
  overflow-x: auto;
  overflow-y: hidden;
}

.timeline-grid {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
}

.grid-line {
  position: absolute;
  pointer-events: none;
}

.grid-line.vertical {
  width: 1px;
  background: #e4e7ed;
}

.grid-line.horizontal {
  height: 1px;
  background: #e4e7ed;
}

.today-line {
  position: absolute;
  width: 2px;
  background: #67c23a;
  z-index: 2;
  pointer-events: none;
}

.task-bars {
  position: relative;
  z-index: 3;
  height: 100%;
}

.task-bar-row {
  position: absolute;
  width: 100%;
}

.task-bar {
  position: absolute;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.task-bar:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.task-bar.status-0 {
  background: linear-gradient(90deg, #909399 0%, #c0c4cc 100%);
  color: #fff;
}

.task-bar.status-1 {
  background: linear-gradient(90deg, #e6a23c 0%, #f0a020 100%);
  color: #fff;
}

.task-bar.status-2 {
  background: linear-gradient(90deg, #f56c6c 0%, #f78989 100%);
  color: #fff;
}

.task-bar.status-3 {
  background: linear-gradient(90deg, #67c23a 0%, #85ce61 100%);
  color: #fff;
}

.task-bar-content {
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 8px;
}

.task-bar-text {
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

/* 滚动条样式 */
.gantt-body::-webkit-scrollbar,
.timeline-content::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.gantt-body::-webkit-scrollbar-track,
.timeline-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.gantt-body::-webkit-scrollbar-thumb,
.timeline-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.gantt-body::-webkit-scrollbar-thumb:hover,
.timeline-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式 */
@media (max-width: 768px) {
  .task-name-column {
    width: 150px;
  }
  
  .task-status-column {
    width: 100px;
  }
  
  .task-list {
    width: 250px;
  }
}
</style>
