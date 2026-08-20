<template>
  <div class="inventory-record-container">
    <!-- 页面标题 -->
    <div class="page-header mb-6">
      <h1 class="text-2xl font-bold text-gray-800">库存记录</h1>
      <p class="text-gray-600 mt-1">查看商品库存变动历史记录</p>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="content-card mb-6">
      <el-form
        :model="searchForm"
        inline
        label-width="80px"
        class="search-form"
      >
        <el-form-item label="操作类型">
          <el-select
            v-model="searchForm.operationType"
            placeholder="请选择操作类型"
            clearable
            style="width: 150px"
          >
            <el-option label="入库" value="INBOUND" />
            <el-option label="出库" value="OUTBOUND" />
            <el-option label="移库" value="TRANSFER" />
            <el-option label="盘库" value="STOCKTAKING" />
            <el-option label="调整" value="ADJUSTMENT" />
          </el-select>
        </el-form-item>

        <el-form-item label="商品名称">
          <el-input
            v-model="searchForm.itemName"
            placeholder="请输入商品名称"
            clearable
            style="width: 180px"
          />
        </el-form-item>

        <el-form-item label="商品编号">
          <el-input
            v-model="searchForm.itemNo"
            placeholder="请输入商品编号"
            clearable
            style="width: 150px"
          />
        </el-form-item>

        <el-form-item label="仓库">
          <el-select
            v-model="searchForm.warehouseId"
            placeholder="请选择仓库"
            clearable
            style="width: 150px"
            @change="handleWarehouseChange"
          >
            <el-option
              v-for="warehouse in warehouseOptions"
              :key="warehouse.id"
              :label="warehouse.warehouseName"
              :value="warehouse.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="库区">
          <el-select
            v-model="searchForm.areaId"
            placeholder="请选择库区"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="area in areaOptions"
              :key="area.id"
              :label="area.areaName"
              :value="area.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="操作时间">
          <el-date-picker
            v-model="searchForm.operationDate"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon class="mr-1"><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon class="mr-1"><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="success" @click="handleExport">
            <el-icon class="mr-1"><Download /></el-icon>
            导出
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div class="stat-card bg-blue-50 border border-blue-200 rounded-lg p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-blue-600 text-sm font-medium">今日入库</p>
            <p class="text-2xl font-bold text-blue-700">{{ statistics.todayInbound }}</p>
          </div>
          <el-icon class="text-blue-500 text-2xl"><Download /></el-icon>
        </div>
      </div>
      
      <div class="stat-card bg-red-50 border border-red-200 rounded-lg p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-red-600 text-sm font-medium">今日出库</p>
            <p class="text-2xl font-bold text-red-700">{{ statistics.todayOutbound }}</p>
          </div>
          <el-icon class="text-red-500 text-2xl"><Upload /></el-icon>
        </div>
      </div>
      
      <div class="stat-card bg-green-50 border border-green-200 rounded-lg p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-green-600 text-sm font-medium">今日移库</p>
            <p class="text-2xl font-bold text-green-700">{{ statistics.todayTransfer }}</p>
          </div>
          <el-icon class="text-green-500 text-2xl"><Switch /></el-icon>
        </div>
      </div>
      
      <div class="stat-card bg-purple-50 border border-purple-200 rounded-lg p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-purple-600 text-sm font-medium">今日调整</p>
            <p class="text-2xl font-bold text-purple-700">{{ statistics.todayAdjustment }}</p>
          </div>
          <el-icon class="text-purple-500 text-2xl"><Edit /></el-icon>
        </div>
      </div>
    </div>

    <!-- 库存记录表格 -->
    <div class="content-card">
      <div class="table-header flex-between mb-4">
        <div class="table-title">
          <h3 class="text-lg font-semibold text-gray-800">库存变动记录</h3>
          <p class="text-sm text-gray-500 mt-1">共 {{ pagination.total }} 条记录</p>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="recordList"
        stripe
        class="w-full"
      >
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="p-4 bg-gray-50">
              <el-row :gutter="20">
                <el-col :span="12">
                  <div class="detail-info">
                    <h4 class="font-medium mb-3 text-gray-800">操作详情</h4>
                    <div class="info-item">
                      <span class="label">操作人:</span>
                      <span class="value">{{ row.operatorName }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">操作时间:</span>
                      <span class="value">{{ formatDate(row.operationTime) }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">单据号:</span>
                      <span class="value">{{ row.documentNo }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">业务类型:</span>
                      <span class="value">{{ row.businessType }}</span>
                    </div>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="detail-info">
                    <h4 class="font-medium mb-3 text-gray-800">库存变化</h4>
                    <div class="info-item">
                      <span class="label">变动前:</span>
                      <span class="value">{{ row.beforeQuantity }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">变动数量:</span>
                      <span class="value" :class="getQuantityChangeClass(row.quantityChange)">
                        {{ formatQuantityChange(row.quantityChange) }}
                      </span>
                    </div>
                    <div class="info-item">
                      <span class="label">变动后:</span>
                      <span class="value">{{ row.afterQuantity }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">备注:</span>
                      <span class="value">{{ row.remark || '-' }}</span>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="operationTime" label="操作时间" width="160">
          <template #default="{ row }">
            <span class="text-sm">{{ formatDate(row.operationTime, 'MM-DD HH:mm:ss') }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="operationType" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag
              :type="getOperationTypeColor(row.operationType)"
              size="small"
            >
              {{ getOperationTypeText(row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="itemName" label="商品信息" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="flex items-center">
              <el-avatar
                :size="32"
                :src="row.itemImage"
                class="mr-3"
              >
                <el-icon><Goods /></el-icon>
              </el-avatar>
              <div>
                <div class="font-medium text-gray-800">{{ row.itemName }}</div>
                <div class="text-xs text-gray-500">{{ row.itemNo }} | {{ row.skuName }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="warehouseName" label="仓库" width="100" />

        <el-table-column prop="areaName" label="库区" width="100" />

        <el-table-column prop="quantityChange" label="变动数量" width="120" align="center">
          <template #default="{ row }">
            <span :class="getQuantityChangeClass(row.quantityChange)" class="font-medium">
              {{ formatQuantityChange(row.quantityChange) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="beforeQuantity" label="变动前" width="100" align="center" />

        <el-table-column prop="afterQuantity" label="变动后" width="100" align="center" />

        <el-table-column prop="operatorName" label="操作人" width="100" />

        <el-table-column prop="documentNo" label="单据号" width="150" show-overflow-tooltip />

        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button
              type="text"
              size="small"
              @click="handleViewDocument(row)"
            >
              查看单据
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container mt-6">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { formatDate } from '@/utils/common'
import { 
  getWarehouseOptionsApi,
  getWarehouseAreaOptionsApi
} from '@/api/inventory'

// 定义库存记录类型
interface InventoryRecord {
  id: number
  operationType: string
  itemName: string
  itemNo: string
  itemImage?: string
  skuName: string
  warehouseName: string
  areaName: string
  quantityChange: number
  beforeQuantity: number
  afterQuantity: number
  operatorName: string
  operationTime: string
  documentNo: string
  businessType?: string
  remark?: string
}

// 统计数据类型
interface Statistics {
  todayInbound: number
  todayOutbound: number
  todayTransfer: number
  todayAdjustment: number
}

// 响应式数据
const loading = ref(false)
const recordList = ref<InventoryRecord[]>([])
const warehouseOptions = ref<Array<{
  id: number
  warehouseName: string
}>>([])
const areaOptions = ref<Array<{
  id: number
  areaName: string
  warehouseId: number
}>>([])

// 统计数据
const statistics = reactive<Statistics>({
  todayInbound: 0,
  todayOutbound: 0,
  todayTransfer: 0,
  todayAdjustment: 0
})

// 搜索表单
const searchForm = reactive({
  operationType: '',
  itemName: '',
  itemNo: '',
  warehouseId: '',
  areaId: '',
  operationDate: null as [string, string] | null
})

// 分页
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 获取操作类型颜色
const getOperationTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    INBOUND: 'success',
    OUTBOUND: 'danger',
    TRANSFER: 'warning',
    STOCKTAKING: 'info',
    ADJUSTMENT: 'primary'
  }
  return colorMap[type] || ''
}

// 获取操作类型文本
const getOperationTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    INBOUND: '入库',
    OUTBOUND: '出库',
    TRANSFER: '移库',
    STOCKTAKING: '盘库',
    ADJUSTMENT: '调整'
  }
  return textMap[type] || type
}

// 获取数量变化样式类
const getQuantityChangeClass = (change: number) => {
  if (change > 0) return 'text-green-600'
  if (change < 0) return 'text-red-600'
  return 'text-gray-600'
}

// 格式化数量变化
const formatQuantityChange = (change: number) => {
  if (change > 0) return `+${change}`
  return change.toString()
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadRecordList()
}

// 重置
const handleReset = () => {
  (Object.keys(searchForm) as Array<keyof typeof searchForm>).forEach((key) => {
    (searchForm as any)[key] = key === 'operationDate' ? null : ''
  })
  pagination.current = 1
  loadRecordList()
}

// 导出
const handleExport = () => {
  ElMessage.success('导出功能开发中...')
}

// 查看单据
const handleViewDocument = (record: InventoryRecord) => {
  ElMessage.info(`查看单据: ${record.documentNo}`)
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadRecordList()
}

// 当前页变化
const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadRecordList()
}

// 加载库存记录列表
const loadRecordList = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟数据
    const mockData: InventoryRecord[] = [
      {
        id: 1,
        operationType: 'INBOUND',
        itemName: '有机苹果',
        itemNo: 'APPLE001',
        skuName: '500g/袋',
        warehouseName: '主仓库',
        areaName: 'A区',
        quantityChange: 100,
        beforeQuantity: 50,
        afterQuantity: 150,
        operatorName: '张三',
        operationTime: '2024-01-15 09:30:00',
        documentNo: 'IN20240115001',
        businessType: '采购入库',
        remark: '新鲜有机苹果入库'
      },
      {
        id: 2,
        operationType: 'OUTBOUND',
        itemName: '有机苹果',
        itemNo: 'APPLE001',
        skuName: '500g/袋',
        warehouseName: '主仓库',
        areaName: 'A区',
        quantityChange: -30,
        beforeQuantity: 150,
        afterQuantity: 120,
        operatorName: '李四',
        operationTime: '2024-01-15 14:20:00',
        documentNo: 'OUT20240115001',
        businessType: '销售出库',
        remark: '门店补货出库'
      },
      {
        id: 3,
        operationType: 'TRANSFER',
        itemName: '有机草莓',
        itemNo: 'STRAWBERRY001',
        skuName: '250g/盒',
        warehouseName: '主仓库',
        areaName: 'B区',
        quantityChange: 20,
        beforeQuantity: 80,
        afterQuantity: 100,
        operatorName: '王五',
        operationTime: '2024-01-15 16:45:00',
        documentNo: 'TF20240115001',
        businessType: '移库调拨',
        remark: '从冷库移至常温库'
      }
    ]
    
    recordList.value = mockData
    pagination.total = mockData.length
    
    // 更新统计数据
    statistics.todayInbound = 2
    statistics.todayOutbound = 1
    statistics.todayTransfer = 1
    statistics.todayAdjustment = 0
    
  } catch (error) {
    console.error('加载库存记录失败:', error)
    ElMessage.error('加载库存记录失败')
  } finally {
    loading.value = false
  }
}

// 加载仓库选项
const loadWarehouseOptions = async () => {
  try {
    const response = await getWarehouseOptionsApi()
    warehouseOptions.value = response || []
  } catch (error) {
    console.error('加载仓库选项失败:', error)
    ElMessage.error('加载仓库选项失败')
  }
}

// 加载库区选项
const loadAreaOptions = async (warehouseId?: number) => {
  try {
    const response = await getWarehouseAreaOptionsApi(warehouseId)
    areaOptions.value = response || []
  } catch (error) {
    console.error('加载库区选项失败:', error)
    ElMessage.error('加载库区选项失败')
  }
}

// 监听仓库变化，重新加载库区选项
const handleWarehouseChange = () => {
  searchForm.areaId = '' // 清空库区选择
  
  if (searchForm.warehouseId && searchForm.warehouseId !== '') {
    loadAreaOptions(Number(searchForm.warehouseId))
  } else {
    loadAreaOptions() // 不传参数，加载所有库区
  }
}

onMounted(() => {
  loadRecordList()
  loadWarehouseOptions()
  loadAreaOptions()
})
</script>

<style scoped>
.inventory-record-container {
  @apply min-h-full;
}

.search-form {
  @apply flex flex-wrap items-center gap-4;
}

.stats-cards .stat-card {
  @apply transition-all duration-300 hover:shadow-md;
}

.table-header {
  @apply border-b border-gray-200 pb-4;
}

.detail-info .info-item {
  @apply flex justify-between items-center py-1;
}

.detail-info .label {
  @apply text-gray-600 text-sm;
}

.detail-info .value {
  @apply font-medium text-gray-800;
}

.pagination-container {
  @apply flex justify-center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-form {
    @apply flex-col items-stretch;
  }
  
  .search-form .el-form-item {
    @apply w-full;
  }
  
  .stats-cards {
    @apply grid-cols-2;
  }
}
</style>
