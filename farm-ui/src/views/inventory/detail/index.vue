<template>
  <el-dialog
    v-model="visible"
    title="库存详情"
    width="90%"
    max-width="1200px"
    top="5vh"
    :before-close="handleClose"
    class="inventory-detail-dialog"
  >
    <div class="detail-container">
      <!-- 商品概览 -->
      <div class="product-overview">
        <div class="product-header">
          <div class="product-image">
            <el-avatar :size="80" :src="(baseInfo as any).itemImage">
              <el-icon size="40"><Goods /></el-icon>
            </el-avatar>
          </div>
          <div class="product-info">
            <h2 class="product-name">{{ (baseInfo as any).itemName || '未知商品' }}</h2>
            <div class="product-meta">
              <span class="meta-item">
                <el-icon><Document /></el-icon>
                {{ (baseInfo as any).itemNo || '-' }}
              </span>
              <span class="meta-item">
                <el-icon><Box /></el-icon>
                {{ (baseInfo as any).skuName || '-' }}
              </span>
              <span class="meta-item">
                <el-icon><Location /></el-icon>
                {{ (baseInfo as any).warehouseName }} - {{ (baseInfo as any).areaName }}
              </span>
            </div>
          </div>
          <div class="product-stats">
            <div class="stat-item">
              <div class="stat-value">{{ (baseInfo as any).quantity || 0 }}</div>
              <div class="stat-label">总库存</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ (baseInfo as any).availableQuantity || 0 }}</div>
              <div class="stat-label">可用</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ (baseInfo as any).lockedQuantity || 0 }}</div>
              <div class="stat-label">锁定</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">¥{{ totalValue.toFixed(2) }}</div>
              <div class="stat-label">总价值</div>
            </div>
          </div>
        </div>
      </div>


      <!-- 库存明细表格 -->
      <div class="table-section">
        <div class="section-header">
          <h3>库存明细</h3>
        </div>
        
        <!-- 搜索区域 -->
        <div class="search-section">
          <el-form class="search-form" :model="searchForm" label-width="60px">
            <el-row :gutter="16">
              <el-col :xs="24" :sm="12" :md="6" :lg="6">
                <el-form-item label="仓库" prop="warehouseId">
                  <el-select 
                    v-model="searchForm.warehouseId" 
                    placeholder="选择仓库"
                    clearable
                    style="width: 100%;"
                  >
                    <el-option
                      v-for="item in warehouseOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12" :md="6" :lg="6">
                <el-form-item label="库区" prop="areaId">
                  <el-select 
                    v-model="searchForm.areaId" 
                    placeholder="选择库区"
                    clearable
                    style="width: 100%;"
                  >
                    <el-option
                      v-for="item in warehouseAreaOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12" :md="6" :lg="6">
                <el-form-item label="批次号" prop="batchNo">
                  <el-input 
                    v-model="searchForm.batchNo" 
                    placeholder="输入批次号"
                    clearable
                    style="width: 100%;"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12" :md="6" :lg="6">
                <el-form-item label=" " class="button-group">
                  <el-button type="primary" @click="handleSearch" :icon="Search">
                    搜索
                  </el-button>
                  <el-button @click="handleReset" :icon="Refresh">
                    重置
                  </el-button>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>

        <div class="table-container">
          <el-table 
            :data="tableData" 
            v-loading="loading"
            :row-class-name="getRowClass"
            :span-method="spanMethod"
            stripe
            border
            height="400"
          >
            <el-table-column prop="warehouseName" label="仓库" width="100" align="center" />
            <el-table-column prop="areaName" label="库区" width="100" align="center" />
            <el-table-column prop="locationNo" label="库位" width="100" align="center" />
            <el-table-column prop="batchNo" label="批次号" width="120" align="center" />
            <el-table-column label="数量" width="80" align="center">
              <template #default="{ row }">
                <span class="quantity-tag">{{ row.quantity || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="可用数量" width="80" align="center">
              <template #default="{ row }">
                <span class="quantity-tag text-green-600">{{ row.availableQuantity || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="锁定数量" width="80" align="center">
              <template #default="{ row }">
                <span class="quantity-tag text-orange-600">{{ row.lockedQuantity || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="单价" width="90" align="right">
              <template #default="{ row }">
                <span>¥{{ row.unitPrice || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="总价值" width="100" align="right">
              <template #default="{ row }">
                <span class="font-medium text-purple-600">
                  ¥{{ ((row.quantity || 0) * (row.unitPrice || 0)).toFixed(2) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="manufacturingDate" label="生产日期" width="100" align="center">
              <template #default="{ row }">
                {{ row.manufacturingDate ? formatDate(row.manufacturingDate, 'YYYY-MM-DD') : '-' }}
              </template>
            </el-table-column>
            <el-table-column label="过期日期" width="100" align="center">
              <template #default="{ row }">
                <div class="expiration-cell">
                  <span :class="getExpirationClass(row.expirationDate)">
                    {{ row.expirationDate ? formatDate(row.expirationDate, 'YYYY-MM-DD') : '-' }}
                  </span>
                  <el-tooltip 
                    v-if="row.expirationDate && getExpirationTooltip(row.expirationDate)"
                    :content="getExpirationTooltip(row.expirationDate)"
                    placement="top"
                  >
                    <el-icon class="ml-1 text-orange-500"><Warning /></el-icon>
                  </el-tooltip>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="createdTime" label="入库时间" width="140" align="center">
              <template #default="{ row }">
                {{ row.createdTime ? formatDate(row.createdTime) : '-' }}
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.page"
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
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Goods, 
  Picture, 
  Document, 
  Box, 
  Location, 
  Check, 
  Lock, 
  Money, 
  List, 
  Search, 
  Refresh, 
  House, 
  Collection, 
  Calendar, 
  Warning 
} from '@element-plus/icons-vue'
import { formatDate } from '@/utils/common'
import type { InventoryItem, InventoryDetail } from '@/types/inventory'
import { 
  getInventoryDetailListApi,
  getWarehouseOptionsApi,
  getWarehouseAreaOptionsApi
} from '@/api/inventory'

interface Props {
  modelValue: boolean
  inventoryItem?: InventoryItem | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
}

const props = withDefaults(defineProps<Props>(), {
  inventoryItem: null
})

const emit = defineEmits<Emits>()

// 对话框显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 基本信息
const baseInfo = computed(() => props.inventoryItem || {} as any)

// 总价值计算
const totalValue = computed(() => {
  const item = baseInfo.value as any
  const quantity = item.quantity || 0
  const unitPrice = item.unitPrice || 0
  return quantity * unitPrice
})

// 表格数据
const tableData = ref<InventoryDetail[]>([])
const loading = ref(false)

// 合并单元格信息
const spanInfo = ref<any[]>([])
const mergeColumns = ['warehouseName', 'areaName'] // 需要合并的列

// 搜索表单
const searchForm = reactive({
  warehouseId: '',
  areaId: '',
  batchNo: ''
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 选项数据
const warehouseOptions = ref<Array<{ label: string; value: any }>>([])
const warehouseAreaOptions = ref<Array<{ label: string; value: any }>>([])

// 获取表格数据
const getTableData = async () => {
  const item = props.inventoryItem as any
  if (!item?.itemId && !item?.id) return
  
  loading.value = true
  try {
    const params = {
      pageNo: pagination.page,
      pageSize: pagination.size,
      warehouseId: searchForm.warehouseId ? Number(searchForm.warehouseId) : undefined,
      areaId: searchForm.areaId ? Number(searchForm.areaId) : undefined,
      itemName: item.itemName,
      itemNo: item.itemNo,
      skuName: item.skuName,
      skuNo: item.skuNo
    }
    
    const res = await getInventoryDetailListApi(params)
    if (res && res.records) {
      tableData.value = res.records || []
      pagination.total = res.total || 0
      
      // 计算合并单元格信息
      calculateSpanInfo(tableData.value)
    }
  } catch (error) {
    console.error('获取库存详情失败:', error)
    ElMessage.error('获取库存详情失败')
  } finally {
    loading.value = false
  }
}

// 获取仓库选项
const getWarehouseOptions = async () => {
  try {
    const res = await getWarehouseOptionsApi()
    if (res && Array.isArray(res)) {
      warehouseOptions.value = res.map(item => ({
        label: item.warehouseName,
        value: item.id
      }))
    }
  } catch (error) {
    console.error('获取仓库选项失败:', error)
  }
}

// 获取库区选项
const getWarehouseAreaOptions = async () => {
  if (!searchForm.warehouseId) {
    warehouseAreaOptions.value = []
    return
  }
  
  try {
    const res = await getWarehouseAreaOptionsApi(Number(searchForm.warehouseId))
    if (res && Array.isArray(res)) {
      warehouseAreaOptions.value = res.map(item => ({
        label: item.areaName,
        value: item.id
      }))
    }
  } catch (error) {
    console.error('获取库区选项失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  getTableData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    warehouseId: '',
    areaId: '',
    batchNo: ''
  })
  pagination.page = 1
  getTableData()
}

// 分页处理
const handleSizeChange = (val: number) => {
  pagination.size = val
  pagination.page = 1
  getTableData()
}

const handleCurrentChange = (val: number) => {
  pagination.page = val
  getTableData()
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
}

// 检查是否即将过期（7天内）
const isExpiringSoon = (expirationDate: string) => {
  if (!expirationDate) return false
  const expDate = new Date(expirationDate)
  const now = new Date()
  const daysDiff = Math.ceil((expDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
  return daysDiff <= 7 && daysDiff >= 0
}

// 检查是否即将过期警告（30天内）
const isExpiringWarning = (expirationDate: string) => {
  if (!expirationDate) return false
  const expDate = new Date(expirationDate)
  const now = new Date()
  const daysDiff = Math.ceil((expDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
  return daysDiff <= 30 && daysDiff > 7
}

// 获取过期提示文本
const getExpirationTooltip = (expirationDate: string): string => {
  if (!expirationDate) return ''
  const expDate = new Date(expirationDate)
  const now = new Date()
  const daysDiff = Math.ceil((expDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
  
  if (daysDiff < 0) return '已过期'
  if (daysDiff <= 7) return `${daysDiff}天后过期`
  if (daysDiff <= 30) return `${daysDiff}天后过期，请注意`
  return ''
}

// 获取过期日期样式类
const getExpirationClass = (expirationDate: string) => {
  if (!expirationDate) return ''
  if (isExpiringSoon(expirationDate)) return 'text-red-600 font-medium'
  if (isExpiringWarning(expirationDate)) return 'text-orange-600'
  return ''
}

// 获取表格行样式类名
const getRowClass = ({ row }: { row: any }) => {
  if (row.expirationDate) {
    if (isExpiringSoon(row.expirationDate)) {
      return 'expiring-soon-row'
    }
    if (isExpiringWarning(row.expirationDate)) {
      return 'expiring-warning-row'
    }
  }
  return ''
}

// 计算合并单元格信息
const calculateSpanInfo = (data: InventoryDetail[]) => {
  if (!data || data.length === 0) {
    spanInfo.value = []
    return
  }

  const spans: any[] = []
  
  // 为每一行初始化合并信息
  data.forEach((_, index) => {
    spans[index] = {}
    mergeColumns.forEach(column => {
      spans[index][column] = { rowspan: 1, colspan: 1 }
    })
  })

  // 计算合并信息
  mergeColumns.forEach(column => {
    let startIndex = 0
    
    for (let i = 1; i <= data.length; i++) {
      // 检查是否需要合并
      const shouldMerge = i < data.length && 
        data[i][column as keyof InventoryDetail] === data[startIndex][column as keyof InventoryDetail] &&
        // 仓库和库区都要相同才合并
        (column === 'areaName' ? 
          data[i].warehouseName === data[startIndex].warehouseName && 
          data[i].areaName === data[startIndex].areaName :
          data[i].warehouseName === data[startIndex].warehouseName)
      
      if (!shouldMerge) {
        // 设置合并行数
        const mergeCount = i - startIndex
        if (mergeCount > 1) {
          spans[startIndex][column].rowspan = mergeCount
          
          // 将后续行的rowspan设为0（隐藏）
          for (let j = startIndex + 1; j < i; j++) {
            spans[j][column].rowspan = 0
            spans[j][column].colspan = 0
          }
        }
        startIndex = i
      }
    }
  })

  spanInfo.value = spans
}

// 合并单元格方法
const spanMethod = ({ rowIndex, columnIndex }: { rowIndex: number; columnIndex: number }) => {
  const columns = ['warehouseName', 'areaName', 'locationNo', 'batchNo', 'quantity', 'availableQuantity', 'lockedQuantity', 'unitPrice', 'totalValue', 'manufacturingDate', 'expirationDate', 'createdTime']
  const currentColumn = columns[columnIndex]
  
  if (spanInfo.value[rowIndex] && spanInfo.value[rowIndex][currentColumn]) {
    const span = spanInfo.value[rowIndex][currentColumn]
    return [span.rowspan, span.colspan]
  }
  
  return [1, 1]
}

// 监听对话框显示状态
watch(visible, (val) => {
  if (val && props.inventoryItem) {
    getTableData()
    getWarehouseOptions()
  }
})

// 监听仓库变化
watch(() => searchForm.warehouseId, () => {
  searchForm.areaId = ''
  getWarehouseAreaOptions()
})
</script>

<style scoped>
.inventory-detail-dialog {
  .detail-container {
    padding: 0;
  }
  
  /* 商品概览样式 */
  .product-overview {
    background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 20px;
    border: 1px solid #e2e8f0;
  }
  
  .product-header {
    display: flex;
    align-items: center;
    gap: 20px;
  }
  
  .product-image {
    flex-shrink: 0;
  }
  
  .product-info {
    flex: 1;
    min-width: 0;
  }
  
  .product-name {
    font-size: 18px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 8px 0;
  }
  
  .product-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    font-size: 14px;
    color: #6b7280;
  }
  
  .meta-item {
    display: flex;
    align-items: center;
    gap: 4px;
  }
  
  .product-stats {
    display: flex;
    gap: 24px;
    flex-shrink: 0;
  }
  
  .stat-item {
    text-align: center;
  }
  
  .stat-value {
    font-size: 20px;
    font-weight: 700;
    color: #1f2937;
    line-height: 1;
  }
  
  .stat-label {
    font-size: 12px;
    color: #6b7280;
    margin-top: 4px;
    line-height: 1;
  }
  
  /* 详细信息样式 */
  .detail-info {
    background: white;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 20px;
    border: 1px solid #e5e7eb;
  }
  
  .info-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 12px;
  }
  
  .info-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
    border-bottom: 1px solid #f3f4f6;
  }
  
  .info-item:last-child {
    border-bottom: none;
  }
  
  .info-item .label {
    font-size: 14px;
    color: #6b7280;
    font-weight: 500;
  }
  
  .info-item .value {
    font-size: 14px;
    color: #1f2937;
    font-weight: 600;
  }
  
  /* 表格区域样式 */
  .table-section {
    background: white;
    border-radius: 8px;
    border: 1px solid #e5e7eb;
  }
  
  .section-header {
    padding: 16px 20px;
    border-bottom: 1px solid #e5e7eb;
    background: #f9fafb;
    border-radius: 8px 8px 0 0;
  }
  
  .section-header h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    color: #1f2937;
  }
  
  .search-section {
    padding: 20px;
    background: #ffffff;
    border-bottom: 1px solid #e5e7eb;
  }
  
  .search-form {
    margin: 0;
  }
  
  .search-form .el-form-item {
    margin-bottom: 16px;
  }
  
  .search-form .button-group .el-form-item__content {
    display: flex;
    gap: 8px;
  }
  
  .search-form .button-group .el-button {
    flex: 1;
    min-width: auto;
  }
  
  .table-container {
    padding: 0;
  }
  
  .quantity-tag {
    font-weight: 600;
    font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Fira Code', monospace;
  }
  
  .expiration-cell {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  /* 表格行样式 */
  :deep(.expiring-soon-row) {
    background-color: #fef2f2 !important;
  }

  :deep(.expiring-warning-row) {
    background-color: #fffbeb !important;
  }

  /* 合并单元格样式优化 */
  :deep(.el-table) {
    .el-table__cell {
      vertical-align: middle;
    }
    
    /* 合并的单元格添加特殊样式 */
    .cell {
      position: relative;
    }
    
    /* 仓库和库区列的合并单元格样式 */
    .el-table__body tr td:first-child,
    .el-table__body tr td:nth-child(2) {
      border-right: 1px solid #ebeef5;
      background-color: #fafafa;
    }
    
    /* 合并行的边框优化 */
    .el-table__body tr:not(:last-child) td {
      border-bottom: 1px solid #ebeef5;
    }
  }
  
  /* 分页样式 */
  .pagination-container {
    padding: 16px 20px;
    display: flex;
    justify-content: center;
    border-top: 1px solid #e5e7eb;
    background: #f9fafb;
    border-radius: 0 0 8px 8px;
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    padding-top: 16px;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .inventory-detail-dialog {
    .product-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }
    
    .product-stats {
      align-self: stretch;
      justify-content: space-between;
    }
    
    .search-section {
      padding: 16px;
    }
    
    .search-form {
      .el-form-item {
        margin-bottom: 12px;
      }
      
      .button-group .el-form-item__content {
        flex-direction: column;
        gap: 8px;
      }
      
      .button-group .el-button {
        width: 100%;
      }
    }
    
    .info-grid {
      grid-template-columns: 1fr;
    }
  }
}

@media (max-width: 480px) {
  .inventory-detail-dialog {
    .product-stats {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
    }
    
    .product-meta {
      flex-direction: column;
      align-items: flex-start;
      gap: 8px;
    }
  }
}
</style>