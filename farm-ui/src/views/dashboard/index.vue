<template>
  <div class="dashboard-container">
    <!-- 欢迎横幅 - 重新设计的卡片样式 -->
    <div class="welcome-banner-container">
      <!-- 主欢迎卡片 -->
      <div class="welcome-main-card glass-card">
        <div class="card-background">
          <div class="gradient-orb orb-1"></div>
          <div class="gradient-orb orb-2"></div>
          <div class="gradient-orb orb-3"></div>
        </div>
        
        <div class="welcome-content">
          <div class="welcome-header">
            <div class="welcome-icon-wrapper">
              <el-icon class="welcome-icon"><Sunrise /></el-icon>
              <div class="icon-pulse"></div>
            </div>
            <h1 class="welcome-title">
              欢迎回到
              <span class="highlight-text">智慧农业</span>
              管理系统
            </h1>
            <p class="welcome-subtitle">今天是个适合耕耘的好日子，让我们一起创造丰收的未来</p>
          </div>
          
          <div class="welcome-actions">
            <button class="action-btn primary-btn" @click="$router.push('/plant/task')">
              <el-icon><Lightning /></el-icon>
              开始工作
            </button>
            <button class="action-btn secondary-btn" @click="$router.push('/dashboard')">
              <el-icon><DataAnalysis /></el-icon>
              查看分析
            </button>
          </div>
        </div>
      </div>
      
      <!-- 信息卡片组 -->
      <div class="info-cards-grid">
        <!-- 日期卡片 -->
        <div class="info-card glass-card date-card">
          <div class="info-card-icon">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="info-card-content">
            <h3 class="info-card-title">今日日期</h3>
            <p class="info-card-value">{{ currentDate }}</p>
          </div>
        </div>
        
        <!-- 天气卡片 -->
        <div class="info-card glass-card weather-card">
          <div class="info-card-icon">
            <el-icon><Sunny /></el-icon>
          </div>
          <div class="info-card-content">
            <h3 class="info-card-title">天气状况</h3>
            <p class="info-card-value">{{ weather }}</p>
          </div>
        </div>
        
        <!-- 活跃批次卡片 -->
        <div class="info-card glass-card batch-card">
          <div class="info-card-icon">
            <el-icon><Cherry /></el-icon>
          </div>
          <div class="info-card-content">
            <h3 class="info-card-title">活跃批次</h3>
            <p class="info-card-value">{{ plantStats.activeBatches }} 个</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计概览卡片 -->
    <div class="stats-grid">
      <div class="stat-card card card-interactive card-fade-in">
        <div class="stat-content">
          <div class="stat-icon inventory">
            <el-icon><Box /></el-icon>
          </div>
          <div class="stat-info">
            <h3 class="stat-number">{{ inventoryStats.total }}</h3>
            <p class="stat-label">库存物料总数</p>
            <span class="stat-trend positive">
              <el-icon><ArrowUp /></el-icon>
              +{{ inventoryStats.growth }}%
            </span>
          </div>
        </div>
      </div>

      <div class="stat-card card card-interactive card-fade-in">
        <div class="stat-content">
          <div class="stat-icon plant">
            <el-icon><Cherry /></el-icon>
          </div>
          <div class="stat-info">
            <h3 class="stat-number">{{ plantStats.activeBatches }}</h3>
            <p class="stat-label">活跃种植批次</p>
            <span class="stat-trend positive">
              <el-icon><ArrowUp /></el-icon>
              +{{ plantStats.growth }}%
            </span>
          </div>
        </div>
      </div>

      <div class="stat-card card card-interactive card-fade-in">
        <div class="stat-content">
          <div class="stat-icon land">
            <el-icon><MapLocation /></el-icon>
          </div>
          <div class="stat-info">
            <h3 class="stat-number">{{ landStats.total }}</h3>
            <p class="stat-label">管理地块数量</p>
            <span class="stat-unit">亩</span>
          </div>
        </div>
      </div>

      <div class="stat-card card card-interactive card-fade-in">
        <div class="stat-content">
          <div class="stat-icon revenue">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <h3 class="stat-number">{{ revenueStats.thisMonth }}</h3>
            <p class="stat-label">本月预估收益</p>
            <span class="stat-trend positive">
              <el-icon><ArrowUp /></el-icon>
              +{{ revenueStats.growth }}%
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧内容 -->
      <div class="left-content">
        <!-- 快捷操作 -->
        <div class="quick-actions card card-lg">
          <div class="card-header">
            <h2 class="section-title">
              <el-icon><Lightning /></el-icon>
              快捷操作
            </h2>
          </div>
          <div class="actions-grid">
            <div 
              v-for="action in quickActions" 
              :key="action.name"
              class="action-item card-interactive"
              @click="handleQuickAction(action.route)"
            >
              <div class="action-icon" :class="action.color">
                <el-icon><component :is="action.icon" /></el-icon>
              </div>
              <span class="action-label">{{ action.name }}</span>
            </div>
          </div>
        </div>

        <!-- 最近任务 -->
        <div class="recent-tasks card card-lg">
          <div class="card-header">
            <h2 class="section-title">
              <el-icon><List /></el-icon>
              最近任务
            </h2>
            <el-button text type="primary" @click="$router.push('/plant/task')">
              查看全部
            </el-button>
          </div>
          <div class="tasks-list">
            <div 
              v-for="task in recentTasks" 
              :key="task.id"
              class="task-item"
            >
              <div class="task-status" :class="task.status"></div>
              <div class="task-content">
                <h4 class="task-title">{{ task.title }}</h4>
                <p class="task-meta">
                  <span class="task-date">{{ task.date }}</span>
                  <span class="task-location">{{ task.location }}</span>
                </p>
              </div>
              <div class="task-progress">
                <el-progress 
                  :percentage="task.progress" 
                  :stroke-width="6"
                  :show-text="false"
                />
                <span class="progress-text">{{ task.progress }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧内容 -->
      <div class="right-content">
        <!-- 天气信息 -->
        <div class="weather-card card card-lg">
          <div class="card-header">
            <h2 class="section-title">
              <el-icon><Cloudy /></el-icon>
              天气预报
            </h2>
          </div>
          <div class="weather-content">
            <div class="today-weather">
              <div class="weather-main">
                <div class="weather-icon">
                  <el-icon size="48"><Sunny /></el-icon>
                </div>
                <div class="weather-info">
                  <h3 class="temperature">{{ todayWeather.temperature }}°C</h3>
                  <p class="weather-desc">{{ todayWeather.description }}</p>
                </div>
              </div>
              <div class="weather-details">
                <div class="weather-item">
                  <span class="label">湿度</span>
                  <span class="value">{{ todayWeather.humidity }}%</span>
                </div>
                <div class="weather-item">
                  <span class="label">风速</span>
                  <span class="value">{{ todayWeather.windSpeed }}km/h</span>
                </div>
              </div>
            </div>
            <div class="forecast-list">
              <div 
                v-for="day in forecast" 
                :key="day.date"
                class="forecast-item"
              >
                <span class="forecast-date">{{ day.date }}</span>
                <el-icon class="forecast-icon"><component :is="day.icon" /></el-icon>
                <span class="forecast-temp">{{ day.temp }}°C</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 产量分析 -->
        <div class="yield-analysis card card-lg">
          <div class="card-header">
            <h2 class="section-title">
              <el-icon><DataAnalysis /></el-icon>
              产量分析
            </h2>
          </div>
          <div class="chart-container" ref="yieldChartRef"></div>
        </div>
      </div>
    </div>

    <!-- 系统状态 -->
    <div class="system-status card card-lg">
      <div class="card-header">
        <h2 class="section-title">
          <el-icon><Monitor /></el-icon>
          系统状态
        </h2>
      </div>
      <div class="status-grid">
        <div class="status-item">
          <div class="status-indicator online"></div>
          <span class="status-label">数据库连接</span>
          <span class="status-value">正常</span>
        </div>
        <div class="status-item">
          <div class="status-indicator online"></div>
          <span class="status-label">传感器网络</span>
          <span class="status-value">{{ sensorStatus.online }}/{{ sensorStatus.total }}</span>
        </div>
        <div class="status-item">
          <div class="status-indicator warning"></div>
          <span class="status-label">存储空间</span>
          <span class="status-value">{{ storageUsage }}%</span>
        </div>
        <div class="status-item">
          <div class="status-indicator online"></div>
          <span class="status-label">自动化设备</span>
          <span class="status-value">运行中</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Sunrise, Calendar, Sunny, Box, Cherry, MapLocation, TrendCharts,
  Lightning, List, Cloudy, DataAnalysis, Monitor, ArrowUp,
  Download, Upload, Switch, Goods, UserFilled, Avatar, Tools
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import dayjs from 'dayjs'

const router = useRouter()
const yieldChartRef = ref<HTMLElement>()

// 当前日期和天气
const currentDate = computed(() => dayjs().format('YYYY年MM月DD日 dddd'))
const weather = ref('晴朗 25°C')

// 统计数据
const inventoryStats = ref({
  total: 2847,
  growth: 12.5
})

const plantStats = ref({
  activeBatches: 15,
  growth: 8.3
})

const landStats = ref({
  total: 1250
})

const revenueStats = ref({
  thisMonth: '¥186,420',
  growth: 15.7
})

// 快捷操作
const quickActions = ref([
  { name: '库存入库', icon: 'Download', route: '/inventory/inbound', color: 'success' },
  { name: '库存出库', icon: 'Upload', route: '/inventory/outbound', color: 'warning' },
  { name: '库存移库', icon: 'Switch', route: '/inventory/transfer', color: 'info' },
  { name: '物料管理', icon: 'Goods', route: '/inventory/item', color: 'primary' },
  { name: '种植批次', icon: 'Cherry', route: '/plant/batch', color: 'success' },
  { name: '地块管理', icon: 'MapLocation', route: '/plant/land', color: 'warning' },
  { name: '雇员管理', icon: 'Avatar', route: '/plant/employee', color: 'info' },
  { name: '机械管理', icon: 'Tools', route: '/plant/machine', color: 'primary' }
])

// 最近任务
const recentTasks = ref([
  {
    id: 1,
    title: '玉米田春播任务',
    date: '今天 09:00',
    location: '东区A地块',
    progress: 75,
    status: 'in-progress'
  },
  {
    id: 2,
    title: '温室番茄采摘',
    date: '昨天 14:30',
    location: '温室B区',
    progress: 100,
    status: 'completed'
  },
  {
    id: 3,
    title: '水稻田施肥',
    date: '明天 08:00',
    location: '南区C地块',
    progress: 0,
    status: 'pending'
  }
])

// 天气信息
const todayWeather = ref({
  temperature: 25,
  description: '晴朗',
  humidity: 65,
  windSpeed: 12
})

const forecast = ref([
  { date: '明天', icon: 'Sunny', temp: 27 },
  { date: '后天', icon: 'Cloudy', temp: 23 },
  { date: '周四', icon: 'Sunny', temp: 26 },
  { date: '周五', icon: 'Cloudy', temp: 24 }
])

// 系统状态
const sensorStatus = ref({
  online: 18,
  total: 20
})

const storageUsage = ref(67)

// 快捷操作处理
const handleQuickAction = (route: string) => {
  router.push(route)
}

// 初始化图表
const initYieldChart = () => {
  if (!yieldChartRef.value) return

  const chart = echarts.init(yieldChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    grid: {
      left: '10%',
      right: '10%',
      bottom: '15%',
      top: '10%'
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月'],
      axisLine: {
        lineStyle: {
          color: '#e5e7eb'
        }
      },
      axisLabel: {
        color: '#6b7280'
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        lineStyle: {
          color: '#e5e7eb'
        }
      },
      axisLabel: {
        color: '#6b7280'
      },
      splitLine: {
        lineStyle: {
          color: '#f3f4f6'
        }
      }
    },
    series: [
      {
        name: '产量(吨)',
        type: 'line',
        smooth: true,
        data: [42, 38, 45, 52, 48, 55],
        lineStyle: {
          color: '#10b981',
          width: 3
        },
        itemStyle: {
          color: '#10b981'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(16, 185, 129, 0.3)' },
              { offset: 1, color: 'rgba(16, 185, 129, 0.05)' }
            ]
          }
        }
      }
    ]
  }

  chart.setOption(option)
  
  // 响应式调整
  const resizeChart = () => {
    chart.resize()
  }
  
  window.addEventListener('resize', resizeChart)
}

onMounted(() => {
  initYieldChart()
})
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
}

/* 欢迎横幅 - 重新设计的卡片样式 */
.welcome-banner-container {
  margin-bottom: 32px;
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  align-items: start;
}

/* 玻璃态卡片基础样式 */
.glass-card {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
}

.glass-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  border-color: rgba(255, 255, 255, 0.3);
}

/* 主欢迎卡片 */
.welcome-main-card {
  padding: 40px;
  min-height: 280px;
  position: relative;
  background: linear-gradient(135deg, 
    rgba(16, 185, 129, 0.15) 0%, 
    rgba(5, 150, 105, 0.15) 50%,
    rgba(34, 197, 94, 0.15) 100%);
}

/* 背景装饰球体 */
.card-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 1;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.6;
  animation: float 6s ease-in-out infinite;
}

.orb-1 {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #10b981, #059669);
  top: -60px;
  right: -60px;
  animation-delay: 0s;
}

.orb-2 {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  bottom: -40px;
  left: -40px;
  animation-delay: 2s;
}

.orb-3 {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  top: 50%;
  right: 20%;
  animation-delay: 4s;
}

/* 欢迎内容 */
.welcome-content {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: space-between;
}

.welcome-header {
  margin-bottom: 32px;
}

.welcome-icon-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;
}

.welcome-icon {
  font-size: 3rem;
  color: #fbbf24;
  position: relative;
  z-index: 2;
}

.icon-pulse {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60px;
  height: 60px;
  background: rgba(251, 191, 36, 0.2);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

.welcome-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 16px 0;
  color: #1f2937;
  line-height: 1.2;
}

.highlight-text {
  background: linear-gradient(135deg, #10b981, #059669);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-subtitle {
  font-size: 1.1rem;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
}

/* 欢迎操作按钮 */
.welcome-actions {
  display: flex;
  gap: 16px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.primary-btn {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  box-shadow: 0 4px 20px rgba(16, 185, 129, 0.3);
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(16, 185, 129, 0.4);
}

.secondary-btn {
  background: rgba(255, 255, 255, 0.8);
  color: #374151;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.secondary-btn:hover {
  background: rgba(255, 255, 255, 0.95);
  transform: translateY(-2px);
}

/* 信息卡片网格 */
.info-cards-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}

.info-card {
  padding: 14px;
  display: flex;
  align-items: center;
  gap: 16px;
  min-height: 80px;
  background: rgba(255, 255, 255, 0.2);
}

.info-card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  flex-shrink: 0;
}

.date-card .info-card-icon {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
}

.weather-card .info-card-icon {
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
}

.batch-card .info-card-icon {
  background: linear-gradient(135deg, #10b981, #059669);
}

.status-card .info-card-icon {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
}

.info-card-content {
  flex: 1;
  min-width: 0;
}

.info-card-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: #6b7280;
  margin: 0 0 4px 0;
}

.info-card-value {
  font-size: 1rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 动画定义 */
@keyframes float {
  0%, 100% { 
    transform: translateY(0px) rotate(0deg); 
  }
  50% { 
    transform: translateY(-10px) rotate(5deg); 
  }
}

@keyframes pulse {
  0% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.6;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.2);
    opacity: 0.3;
  }
  100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.6;
  }
}

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  padding: 20px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--card-shadow-3);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.inventory { background: linear-gradient(135deg, #3b82f6, #1d4ed8); }
.stat-icon.plant { background: linear-gradient(135deg, #10b981, #059669); }
.stat-icon.land { background: linear-gradient(135deg, #f59e0b, #d97706); }
.stat-icon.revenue { background: linear-gradient(135deg, #8b5cf6, #7c3aed); }

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  margin: 0 0 4px 0;
  color: #1f2937;
}

.stat-label {
  color: #6b7280;
  margin: 0 0 8px 0;
  font-size: 0.9rem;
}

.stat-trend {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 0.8rem;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 4px;
}

.stat-trend.positive {
  color: #10b981;
  background: rgba(16, 185, 129, 0.1);
}

.stat-unit {
  font-size: 0.8rem;
  color: #6b7280;
  margin-left: 4px;
}

/* 主要内容区域 */
.main-content {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 24px;
  margin-bottom: 24px;
}

.left-content, .right-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 快捷操作 */
.quick-actions .card-header {
  display: flex;
  align-items: center;
  justify-content: between;
  margin-bottom: 20px;
}

.section-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.action-item:hover {
  background: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.action-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.action-icon.primary { background: linear-gradient(135deg, #3b82f6, #1d4ed8); }
.action-icon.success { background: linear-gradient(135deg, #10b981, #059669); }
.action-icon.warning { background: linear-gradient(135deg, #f59e0b, #d97706); }
.action-icon.info { background: linear-gradient(135deg, #6366f1, #4f46e5); }

.action-label {
  font-size: 0.8rem;
  color: #374151;
  text-align: center;
  font-weight: 500;
}

/* 最近任务 */
.recent-tasks .card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.tasks-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.task-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.task-item:hover {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.task-status {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.task-status.completed { background: #10b981; }
.task-status.in-progress { background: #f59e0b; }
.task-status.pending { background: #6b7280; }

.task-content {
  flex: 1;
  min-width: 0;
}

.task-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 4px 0;
}

.task-meta {
  font-size: 0.8rem;
  color: #6b7280;
  margin: 0;
  display: flex;
  gap: 12px;
}

.task-progress {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  min-width: 80px;
}

.progress-text {
  font-size: 0.8rem;
  color: #6b7280;
  font-weight: 600;
}

/* 天气卡片 */
.weather-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.today-weather {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.weather-main {
  display: flex;
  align-items: center;
  gap: 16px;
}

.weather-icon {
  color: #fbbf24;
}

.temperature {
  font-size: 2rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
}

.weather-desc {
  color: #6b7280;
  margin: 4px 0 0 0;
}

.weather-details {
  display: flex;
  gap: 20px;
}

.weather-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.weather-item .label {
  font-size: 0.8rem;
  color: #6b7280;
}

.weather-item .value {
  font-weight: 600;
  color: #1f2937;
}

.forecast-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.forecast-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
}

.forecast-date {
  font-size: 0.8rem;
  color: #6b7280;
  flex: 1;
}

.forecast-icon {
  color: #fbbf24;
  margin: 0 12px;
}

.forecast-temp {
  font-weight: 600;
  color: #1f2937;
  font-size: 0.9rem;
}

/* 图表容器 */
.chart-container {
  height: 200px;
  width: 100%;
}

/* 系统状态 */
.system-status {
  background: #f8fafc;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-indicator.online { background: #10b981; }
.status-indicator.warning { background: #f59e0b; }
.status-indicator.offline { background: #ef4444; }

.status-label {
  flex: 1;
  font-size: 0.9rem;
  color: #374151;
}

.status-value {
  font-weight: 600;
  font-size: 0.9rem;
  color: #1f2937;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .actions-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .welcome-banner-container {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .info-cards-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .welcome-banner-container {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .welcome-main-card {
    padding: 24px;
    min-height: 240px;
  }
  
  .welcome-title {
    font-size: 2rem;
  }
  
  .welcome-subtitle {
    font-size: 1rem;
  }
  
  .welcome-actions {
    flex-direction: column;
    gap: 12px;
  }
  
  .action-btn {
    justify-content: center;
    padding: 14px 20px;
  }
  
  .info-cards-grid {
    grid-template-columns: 1fr;
  }
  
  .info-card {
    padding: 16px;
    min-height: 70px;
  }
  
  .info-card-icon {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
  
  .status-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .welcome-main-card {
    padding: 20px;
  }
  
  .welcome-icon {
    font-size: 2.5rem;
  }
  
  .welcome-title {
    font-size: 1.8rem;
  }
  
  .orb-1, .orb-2, .orb-3 {
    display: none; /* 在小屏幕上隐藏装饰球体以提高性能 */
  }
}

/* 动画 */
.card-fade-in {
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .dashboard-container {
    background: linear-gradient(135deg, #111827 0%, #1f2937 100%);
  }
  
  .glass-card {
    background: rgba(31, 41, 55, 0.25);
    border: 1px solid rgba(156, 163, 175, 0.18);
  }
  
  .glass-card:hover {
    border-color: rgba(156, 163, 175, 0.3);
  }
  
  .welcome-main-card {
    background: linear-gradient(135deg, 
      rgba(16, 185, 129, 0.1) 0%, 
      rgba(5, 150, 105, 0.1) 50%,
      rgba(34, 197, 94, 0.1) 100%);
  }
  
  .welcome-title,
  .stat-number,
  .section-title,
  .temperature,
  .weather-item .value,
  .forecast-temp,
  .status-value,
  .info-card-value {
    color: #f9fafb;
  }
  
  .welcome-subtitle,
  .stat-label,
  .weather-desc,
  .task-meta,
  .progress-text,
  .weather-item .label,
  .forecast-date,
  .status-label,
  .info-card-title {
    color: #d1d5db;
  }
  
  .action-item,
  .task-item,
  .status-item,
  .info-card {
    background: rgba(55, 65, 81, 0.4);
    border-color: #4b5563;
  }
  
  .action-item:hover,
  .task-item:hover,
  .info-card:hover {
    background: rgba(75, 85, 99, 0.5);
  }
  
  .secondary-btn {
    background: rgba(55, 65, 81, 0.8);
    color: #f9fafb;
    border: 1px solid rgba(156, 163, 175, 0.3);
  }
  
  .secondary-btn:hover {
    background: rgba(75, 85, 99, 0.95);
  }
}
</style>



