<template>
  <div class="stocktaking-container">

  <!-- 盘库任务列表 -->
  <el-card>
    <template #header>
      <div class="card-header">
        <span>盘库任务列表</span>
        <el-button type="primary" @click="handleCreateStocktaking">
          <el-icon class="mr-1"><Plus /></el-icon>
          新建盘库任务
        </el-button>
      </div>
    </template>

    <div class="search-area">
      <el-form
        :model="searchForm"
        inline
        label-width="80px"
        class="search-form-inline"
      >
        <el-form-item label="盘库状态">
          <el-select
            v-model="searchForm.checkOrderStatus"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="未盘库" :value="0" />
            <el-option label="已盘库" :value="1" />
            <el-option label="作废" :value="-1" />
          </el-select>
        </el-form-item>

        <el-form-item label="盘库单号">
          <el-input
            v-model="searchForm.checkOrderNo"
            placeholder="请输入盘库单号"
            clearable
            style="width: 220px"
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
        </el-form-item>
      </el-form>
    </div>

      <el-table
        v-loading="loading"
        :data="stocktakingList"
        stripe
        class="w-full"
        row-key="id"
      >
        <el-table-column prop="checkOrderNo" label="盘库单号" width="160" show-overflow-tooltip />

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.checkOrderStatus)"
              size="small"
            >
              {{ getStatusText(row.checkOrderStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="盈亏数" width="100">
          <template #default="{ row }">
            <span :class="row.checkOrderTotal && row.checkOrderTotal >= 0 ? 'text-green-600' : 'text-red-600'">
              {{ row.checkOrderTotal || 0 }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="warehouseName" label="仓库" width="120" show-overflow-tooltip>
          <template #default="{ row }">
            <span>{{ row.warehouseName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="areaName" label="库区" width="120" show-overflow-tooltip>
          <template #default="{ row }">
            <span>{{ row.areaName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="createBy" label="创建人" width="100" />


        <el-table-column prop="createTime" label="创建时间" width="140">
          <template #default="{ row }">
            <span class="text-sm">{{ formatDate(row.createTime, 'YYYY-MM-DD HH:mm') }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="remark" label="备注" width="200" show-overflow-tooltip />

        <el-table-column label="操作" width="360" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleView(row)">
                查看
              </el-button>
              <el-button
                v-if="row.checkOrderStatus === 0"
                type="warning"
                size="small"
                @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button
                v-if="row.checkOrderStatus === 0"
                type="success"
                size="small"
                @click="handleComplete(row)"
              >
                完成盘库
              </el-button>
              <el-button
                v-if="row.checkOrderStatus === 0"
                type="danger"
                size="small"
                @click="handleCancel(row)"
              >
                删除
              </el-button>
            </div>
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
  </el-card>

  <!-- 新建盘库任务对话框 -->
  <el-dialog
    v-model="stocktakingDialogVisible"
    :title="stocktakingDialogTitle"
    width="1250px"
    :close-on-click-modal="false"
    @close="handleDialogClose"
  >
    <div v-loading="stocktakingDialogLoading">
      <el-form
        ref="stocktakingFormRef"
        :model="stocktakingForm"
        :rules="stocktakingFormRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="仓库" prop="warehouseId" required>
              <el-select
                v-model="stocktakingForm.warehouseId"
                placeholder="请选择仓库"
                style="width: 100%"
                :disabled="isViewMode || stocktakingDialogMode === 'edit'"
                @change="handleFormWarehouseChange"
              >
                <el-option
                  v-for="warehouse in warehouseOptions"
                  :key="warehouse.id"
                  :label="warehouse.warehouseName"
                  :value="warehouse.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库区" prop="areaId" required>
              <el-select
                v-model="stocktakingForm.areaId"
                placeholder="请选择库区"
                style="width: 100%"
                :disabled="isViewMode || stocktakingDialogMode === 'edit'"
              >
                <el-option
                  v-for="area in formAreaOptions"
                  :key="area.id"
                  :label="area.areaName"
                  :value="area.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input
            v-model="stocktakingForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入盘库任务备注信息"
            maxlength="200"
            show-word-limit
            :disabled="isViewMode"
          />
        </el-form-item>

        <el-form-item label="盘库物料" required>
          <div class="w-full space-y-4">
            <div v-if="!isViewMode" class="flex items-center gap-3">
              <el-button
                type="primary"
                size="small"
                @click="handleOpenSelectItems"
                :disabled="!canSelectItems"
              >
                <el-icon class="mr-1"><Plus /></el-icon>
                选择盘库物料
              </el-button>
              <span v-if="!canSelectItems" class="text-xs text-gray-500">
                请先选择仓库和库区后再添加盘库物料
              </span>
            </div>

            <el-table
              v-if="stocktakingForm.detailList.length > 0"
              :data="stocktakingForm.detailList"
              size="small"
              border
              style="width: 100%"
            >
              <el-table-column prop="itemName" label="物料名称" min-width="140">
                <template #default="{ row }">
                  <div class="flex flex-col">
                    <span class="font-medium text-sm">{{ row.itemName || '未知物料' }}</span>
                    <span class="text-xs text-gray-500">{{ row.skuName || '-' }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="systemQuantity" label="系统库存" width="100" align="center">
                <template #default="{ row }">
                  <span class="font-medium text-green-600">{{ row.systemQuantity }}</span>
                </template>
              </el-table-column>
              <el-table-column label="盘点数量" width="120" align="center">
                <template #default="{ row }">
                  <el-input-number
                    v-model="row.actualQuantity"
                    :precision="0"
                    :step="1"
                    :min="0"
                    :controls="false"
                    style="width: 100px"
                    :disabled="isViewMode"
                    @change="handleActualQuantityChange(row)"
                  />
                </template>
              </el-table-column>
              <el-table-column label="盈亏数量" width="120" align="center">
                <template #default="{ row }">
                  <span :class="row.differenceQuantity >= 0 ? 'text-green-600' : 'text-red-500'">
                    {{ formatDifference(row.differenceQuantity) }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="unit" label="单位" width="80" align="center" />
              <el-table-column prop="orderNo" label="来源单号" min-width="140" />
              <el-table-column label="生产日期" width="120" align="center">
                <template #default="{ row }">
                  {{ formatDetailDate(row.productionDate) }}
                </template>
              </el-table-column>
              <el-table-column label="失效日期" width="120" align="center">
                <template #default="{ row }">
                  {{ formatDetailDate(row.expirationDate) }}
                </template>
              </el-table-column>
              <el-table-column v-if="!isViewMode" label="操作" width="80" align="center">
                <template #default="{ $index }">
                  <el-button
                    type="text"
                    size="small"
                    class="text-red-500"
                    @click="handleRemoveItem($index)"
                  >
                    移除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <div
              v-else
              class="text-center text-gray-500 py-8 border border-dashed border-gray-300 rounded-lg"
            >
              暂未选择盘库物料
            </div>
          </div>
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="stocktakingDialogVisible = false">{{ isViewMode ? '关闭' : '取消' }}</el-button>
        <el-button
          v-if="!isViewMode"
          type="primary"
          @click="handleSubmitStocktaking"
        >
          {{ stocktakingDialogSubmitText }}
        </el-button>
      </div>
    </template>
  </el-dialog>

  <SelectCheckItemsDialog
    v-model="selectItemDialogVisible"
    :warehouse-id="stocktakingForm.warehouseId"
    :area-id="stocktakingForm.areaId"
    :selected-items="selectedItemsForDialog"
    @confirm="handleSelectItems"
  />
</div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDate } from '@/utils/common'
import { 
  getCheckOrderListApi,
  addCheckOrderApi,
  updateCheckOrderApi,
  deleteCheckOrderApi,
  completeCheckApi,
  addCheckOrderDetailApi,
  updateCheckOrderDetailApi,
  deleteCheckOrderDetailApi,
  getWarehouseOptionsApi,
  getWarehouseAreaOptionsApi,
  getCheckOrderByIdApi,
  getItemSkuByIdApi,
  getItemByIdApi,
  getInventoryDetailByIdApi,
  getCheckOrderDetailListApi
} from '@/api/inventory'
import type { CheckOrder, CheckOrderDetail } from '@/types/inventory'
import SelectCheckItemsDialog from './components/SelectCheckItemsDialog.vue'
import type { SelectedCheckItem } from './components/SelectCheckItemsDialog.vue'

// 扩展的盘库任务类型（基于后端CheckOrder）
interface StocktakingTask extends CheckOrder {
  // 扩展字段用于前端显示
  warehouseName?: string
  areaName?: string
  totalItems?: number
  countedItems?: number
  differenceItems?: number
  profitLossAmount?: number
  progress?: number
  assigneeName?: string
  checkOrderTotal?: number // 盈亏数总和
}

// 统计数据类型
interface Statistics {
  pending: number
  processing: number
  completed: number
  differences: number
}

// 响应式数据
const loading = ref(false)
const stocktakingList = ref<StocktakingTask[]>([])
const stocktakingDialogVisible = ref(false)
const stocktakingDialogMode = ref<'create' | 'edit' | 'view'>('create')
const stocktakingDialogLoading = ref(false)
const currentCheckOrderId = ref<number | null>(null)
const currentCheckOrderStatus = ref<number | null>(null)
const initializingForm = ref(false)
const selectItemDialogVisible = ref(false)
const originalDetailList = ref<StocktakingDetailItem[]>([])

const warehouseOptions = ref<Array<{
  id: number
  warehouseName: string
}>>([])

const allAreasMap = ref<Map<number, string>>(new Map())

const formAreaOptions = ref<Array<{
  id: number
  areaName: string
  warehouseId: number
}>>([])

// 统计数据
const statistics = reactive<Statistics>({
  pending: 0,
  processing: 0,
  completed: 0,
  differences: 0
})

// 搜索表单
const searchForm = reactive({
  checkOrderStatus: null as number | null,
  checkOrderNo: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 盘库表单
type StocktakingDetailItem = (Partial<CheckOrderDetail> & {
  inventoryDetailId: number
  systemQuantity: number
  actualQuantity: number
  differenceQuantity: number
}) & {
  itemName?: string
  skuName?: string
  unit?: string
  orderNo?: string
  itemId?: number
}

const stocktakingForm = reactive({
  warehouseId: null as number | null,
  areaId: null as number | null,
  remark: '',
  detailList: [] as StocktakingDetailItem[]
})

const isViewMode = computed(() => stocktakingDialogMode.value === 'view')
const stocktakingDialogTitle = computed(() => {
  if (stocktakingDialogMode.value === 'edit') {
    return '编辑盘库任务'
  }
  if (stocktakingDialogMode.value === 'view') {
    return '盘库任务详情'
  }
  return '新建盘库任务'
})
const stocktakingDialogSubmitText = computed(() =>
  stocktakingDialogMode.value === 'edit' ? '保存修改' : '确认创建'
)

const canSelectItems = computed(
  () => !isViewMode.value && stocktakingForm.warehouseId !== null && stocktakingForm.areaId !== null
)
const selectedItemsForDialog = computed<SelectedCheckItem[]>(() =>
  stocktakingForm.detailList.map(item => ({
    inventoryDetailId: item.inventoryDetailId,
    skuId: item.skuId,
    itemId: item.itemId,
    itemName: item.itemName,
    skuName: item.skuName,
    unit: item.unit,
    systemQuantity: item.systemQuantity,
    availableQuantity: item.systemQuantity,
    orderNo: item.orderNo,
    productionDate: item.productionDate,
    expirationDate: item.expirationDate
  }))
)

// 表单验证规则
const stocktakingFormRules = {
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  areaId: [{ required: true, message: '请选择库区', trigger: 'change' }]
}

// 获取状态类型
const getStatusType = (status: number) => {
  const typeMap: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    0: 'info',    // 未盘库
    1: 'success', // 已盘库
    [-1]: 'danger' // 作废
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const textMap: Record<number, string> = {
    0: '未盘库',
    1: '已盘库',
    [-1]: '作废'
  }
  return textMap[status] || '未知'
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadStocktakingList()
}

// 重置
const handleReset = () => {
  searchForm.checkOrderStatus = null
  searchForm.checkOrderNo = ''
  pagination.current = 1
  loadStocktakingList()
}

// 新建盘库任务
const handleCreateStocktaking = () => {
  stocktakingDialogMode.value = 'create'
  currentCheckOrderId.value = null
  resetStocktakingForm()
  stocktakingDialogVisible.value = true
}

// 查看盘库任务
const handleView = (row: StocktakingTask) => {
  openStocktakingDialog('view', row.id)
}

// 编辑盘库任务
const handleEdit = (row: StocktakingTask) => {
  if (row.checkOrderStatus !== 0) {
    ElMessage.warning('仅未盘库的任务支持编辑')
    return
  }
  openStocktakingDialog('edit', row.id)
}

/**
 * 重置盘库表单
 */
const resetStocktakingForm = () => {
  stocktakingForm.warehouseId = null
  stocktakingForm.areaId = null
  stocktakingForm.remark = ''
  stocktakingForm.detailList = []
  formAreaOptions.value = []
  originalDetailList.value = []
}

/**
 * 根据盘库单ID加载详情
 */
const openStocktakingDialog = async (mode: 'edit' | 'view', id: number) => {
  resetStocktakingForm()
  stocktakingDialogMode.value = mode
  currentCheckOrderId.value = id
  stocktakingDialogVisible.value = true
  stocktakingDialogLoading.value = true
  try {
    const orderDetail = await getCheckOrderByIdApi(id)
    await fillStocktakingForm(orderDetail)
  } catch (error) {
    console.error('加载盘库详情失败:', error)
    ElMessage.error('加载盘库详情失败')
    stocktakingDialogVisible.value = false
  } finally {
    stocktakingDialogLoading.value = false
  }
}

/**
 * 丰富盘库单详情数据（获取物料名称、单位、来源单号等）
 */
const enrichCheckOrderDetails = async (detailList: CheckOrderDetail[]): Promise<(CheckOrderDetail & { itemName?: string; skuName?: string; unit?: string; orderNo?: string; itemId?: number })[]> => {
  if (!detailList || detailList.length === 0) {
    return []
  }
  const enrichedList: (CheckOrderDetail & { itemName?: string; skuName?: string; unit?: string; orderNo?: string; itemId?: number })[] = []
  for (const detail of detailList) {
    const enrichedDetail: CheckOrderDetail & { itemName?: string; skuName?: string; unit?: string; orderNo?: string; itemId?: number } = { ...detail }
    
    // 如果物料名称、规格名称或单位缺失，则尝试获取物料信息
    const needsEnrichment = !enrichedDetail.itemName || 
                           !enrichedDetail.skuName || 
                           !enrichedDetail.unit
    
    if (needsEnrichment && detail.skuId) {
      try {
        const skuInfo = await getItemSkuByIdApi(detail.skuId)
        if (skuInfo) {
          if (!enrichedDetail.itemName) {
            enrichedDetail.itemName = skuInfo.itemName || ''
          }
          if (!enrichedDetail.skuName) {
            enrichedDetail.skuName = skuInfo.skuName || '未知规格'
          }
          if (!enrichedDetail.unit) {
            enrichedDetail.unit = skuInfo.unit || '件'
          }
          
          // 如果 skuInfo 中有 itemId，尝试获取更完整的物料信息
          if (skuInfo.itemId && !enrichedDetail.itemName) {
            try {
              const itemInfo = await getItemByIdApi(skuInfo.itemId)
              if (itemInfo && itemInfo.itemName) {
                enrichedDetail.itemName = itemInfo.itemName
              }
              if (itemInfo && itemInfo.unit && !enrichedDetail.unit) {
                enrichedDetail.unit = itemInfo.unit
              }
            } catch (innerError) {
              // 忽略补充物料信息异常
            }
          }
        }
      } catch (error) {
        // 忽略补充规格信息异常
      }
    }
    
    // 获取来源单号（从库存详情中获取）
    if (!enrichedDetail.orderNo && detail.inventoryDetailId) {
      try {
        const inventoryDetail = await getInventoryDetailByIdApi(detail.inventoryDetailId)
        if (inventoryDetail && inventoryDetail.orderNo) {
          enrichedDetail.orderNo = inventoryDetail.orderNo
        }
      } catch (error) {
        // 忽略获取库存详情异常
      }
    }
    
    // 设置默认值
    if (!enrichedDetail.itemName) {
      enrichedDetail.itemName = `物料ID-${detail.skuId}`
    }
    if (!enrichedDetail.skuName) {
      enrichedDetail.skuName = '未知规格'
    }
    if (!enrichedDetail.unit) {
      enrichedDetail.unit = '件'
    }
    
    enrichedList.push(enrichedDetail)
  }
  return enrichedList
}

/**
 * 将详情数据填充进表单
 */
const fillStocktakingForm = async (order: CheckOrder) => {
  initializingForm.value = true
  try {
    stocktakingForm.warehouseId = order.warehouseId ?? null
    if (order.warehouseId) {
      const areas = await loadAreaOptions(order.warehouseId)
      formAreaOptions.value = areas
    } else {
      formAreaOptions.value = []
    }
    stocktakingForm.areaId = order.areaId ?? null
    stocktakingForm.remark = order.remark || ''
    // 保存原始的 checkOrderStatus，用于编辑时提交
    currentCheckOrderStatus.value = order.checkOrderStatus ?? null
    
    // 丰富详情数据
    const enrichedDetails = await enrichCheckOrderDetails(order.detailList || [])
    
    const detailList = enrichedDetails.map(detail => {
      // 支持后端返回 quantity 和 checkQuantity 字段（优先使用），如果没有则使用 systemQuantity 和 actualQuantity
      const detailAny = detail as any
      const systemQuantity = detailAny.quantity ?? detail.systemQuantity ?? 0
      const actualQuantity = detailAny.checkQuantity ?? detail.actualQuantity ?? systemQuantity
      const differenceQuantity =
        detail.differenceQuantity ??
        Number((actualQuantity - systemQuantity).toFixed(2))
      const enrichedDetail = detail as CheckOrderDetail & { itemName?: string; skuName?: string; unit?: string; orderNo?: string; itemId?: number }
      return {
        id: detail.id,
        checkOrderId: detail.checkOrderId,
        inventoryDetailId: detail.inventoryDetailId,
        skuId: detail.skuId,
        itemId: enrichedDetail.itemId,
        itemName: enrichedDetail.itemName,
        skuName: enrichedDetail.skuName,
        unit: enrichedDetail.unit,
        orderNo: enrichedDetail.orderNo,
        systemQuantity,
        actualQuantity,
        differenceQuantity,
        productionDate: detail.productionDate,
        expirationDate: detail.expirationDate,
        warehouseId: detail.warehouseId,
        areaId: detail.areaId
      } as StocktakingDetailItem
    })
    stocktakingForm.detailList = detailList
    originalDetailList.value = detailList.map(item => ({ ...item }))
  } finally {
    initializingForm.value = false
  }
}

// 开始盘库（后端暂无此接口，使用更新状态）
const handleStart = async (row: StocktakingTask) => {
  try {
    await ElMessageBox.confirm(
      `确认开始盘库任务 ${row.checkOrderNo} 吗？`,
      '开始盘库',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // 这里可能需要更新状态或者调用其他接口
    ElMessage.info('开始盘库功能待实现...')
    loadStocktakingList()
  } catch {
    // 用户取消
  }
}

// 盘库操作
const handleCount = (row: StocktakingTask) => {
  ElMessage.info(`开始盘库: ${row.checkOrderNo}`)
}

// 完成盘库
const handleComplete = async (row: StocktakingTask) => {
  try {
    await ElMessageBox.confirm(
      `确认完成盘库任务 ${row.checkOrderNo} 吗？`,
      '完成盘库',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await completeCheckApi(row.id)
    ElMessage.success('盘库任务完成成功')
    loadStocktakingList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('完成盘库失败:', error)
      ElMessage.error('完成盘库失败')
    }
  }
}

// 库存调整
const handleAdjust = (row: StocktakingTask) => {
  ElMessage.info(`库存调整: ${row.checkOrderNo}`)
}

// 取消盘库（删除盘库单）
const handleCancel = async (row: StocktakingTask) => {
  try {
    await ElMessageBox.confirm(
      `确认删除盘库任务 ${row.checkOrderNo} 吗？删除后无法恢复。`,
      '删除盘库',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await deleteCheckOrderApi(row.id)
    ElMessage.success('盘库任务删除成功')
    loadStocktakingList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除盘库失败:', error)
      ElMessage.error('删除盘库失败')
    }
  }
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadStocktakingList()
}

// 当前页变化
const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadStocktakingList()
}

// 根据ID获取仓库名称
const getWarehouseName = (warehouseId?: number) => {
  if (!warehouseId) {
    return '未知仓库'
  }
  const warehouse = warehouseOptions.value.find(item => item.id === warehouseId)
  return warehouse?.warehouseName || '未知仓库'
}

// 根据ID获取库区名称
const getAreaName = (areaId?: number) => {
  if (!areaId) {
    return '未知库区'
  }
  return allAreasMap.value.get(areaId) || '未知库区'
}

// 加载盘库任务列表
const loadStocktakingList = async () => {
  loading.value = true
  try {
    const params = {
      pageNo: pagination.current,
      pageSize: pagination.size,
      checkOrderNo: searchForm.checkOrderNo || undefined,
      checkOrderStatus: searchForm.checkOrderStatus !== null ? searchForm.checkOrderStatus : undefined
    }
    
    const response = await getCheckOrderListApi(params)
    const records = (response.records || []) as CheckOrder[]
    stocktakingList.value = records.map(item => {
      const extendedItem: StocktakingTask = {
        ...item,
        warehouseName: getWarehouseName(item.warehouseId),
        areaName: getAreaName(item.areaId),
        checkOrderTotal: item.checkOrderTotal ?? undefined // 使用后端返回的值，如果没有则后续计算
      }
      return extendedItem
    })
    pagination.total = response.total || 0
    
    // 计算所有订单的盈亏数总和（优先使用后端返回的值，如果没有则从详情计算）
    await Promise.all(
      stocktakingList.value.map(async (order) => {
        // 如果后端已经返回了盈亏数，则使用后端的值
        if (order.checkOrderTotal !== undefined && order.checkOrderTotal !== null) {
          return
        }
        try {
          const detailResponse = await getCheckOrderDetailListApi({
            pageNum: 1,
            pageSize: 1000, // 获取所有详情
            checkOrderId: order.id
          })
          const details = detailResponse.records || []
          if (details.length === 0) {
            order.checkOrderTotal = 0
            return
          }
          // 计算所有物料的盈亏数之和：actualQuantity - systemQuantity
          const totalDifference = details.reduce((sum, detail) => {
            const systemQuantity = Number(detail.systemQuantity ?? 0)
            const actualQuantity = Number(detail.actualQuantity ?? systemQuantity)
            const diff = actualQuantity - systemQuantity
            return sum + (isNaN(diff) ? 0 : diff)
          }, 0)
          order.checkOrderTotal = Number(totalDifference.toFixed(2))
        } catch (error) {
          console.error(`计算盘库单 ${order.checkOrderNo} 盈亏数失败:`, error)
          order.checkOrderTotal = 0
        }
      })
    )
    
    // 更新统计数据
    const allData = stocktakingList.value
    statistics.pending = allData.filter(item => item.checkOrderStatus === 0).length
    statistics.processing = 0 // 后端没有进行中状态，设为0
    statistics.completed = allData.filter(item => item.checkOrderStatus === 1).length
    statistics.differences = 0 // 需要根据实际业务逻辑计算
    
  } catch (error) {
    console.error('加载盘库任务列表失败:', error)
    ElMessage.error('加载盘库任务列表失败')
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
    return response || []
  } catch (error) {
    console.error('加载库区选项失败:', error)
    ElMessage.error('加载库区选项失败')
    return []
  }
}

// 加载所有库区并建立映射
const loadAllAreas = async () => {
  try {
    const allAreas = await getWarehouseAreaOptionsApi()
    const list = allAreas || []
    allAreasMap.value.clear()
    list.forEach(area => {
      allAreasMap.value.set(area.id, area.areaName)
    })
  } catch (error) {
    console.error('加载全部库区失败:', error)
  }
}

// 表单仓库变化
const handleFormWarehouseChange = () => {
  stocktakingForm.areaId = null
  stocktakingForm.detailList = []
  if (stocktakingForm.warehouseId) {
    loadAreaOptions(stocktakingForm.warehouseId).then(areas => {
      formAreaOptions.value = areas
    })
  } else {
    formAreaOptions.value = []
  }
}

// 提交盘库任务
const handleSubmitStocktaking = async () => {
  if (isViewMode.value) {
    stocktakingDialogVisible.value = false
    return
  }
  if (stocktakingForm.warehouseId === null || stocktakingForm.areaId === null) {
    ElMessage.warning('请选择仓库和库区')
    return
  }
  try {
    if (stocktakingForm.detailList.length === 0) {
      ElMessage.warning('请至少选择一个盘库物料')
      return
    }
    stocktakingDialogLoading.value = true
    if (stocktakingDialogMode.value === 'edit' && currentCheckOrderId.value) {
      await submitEditStocktaking()
      ElMessage.success('盘库任务修改成功')
    } else {
      await submitCreateStocktaking()
      ElMessage.success('盘库任务创建成功')
    }
    stocktakingDialogVisible.value = false
    loadStocktakingList()
  } catch (error) {
    console.error('保存盘库任务失败:', error)
    ElMessage.error('保存盘库任务失败')
  } finally {
    stocktakingDialogLoading.value = false
  }
}

// 对话框关闭
const handleDialogClose = () => {
  resetStocktakingForm()
  currentCheckOrderId.value = null
  currentCheckOrderStatus.value = null
  stocktakingDialogMode.value = 'create'
  stocktakingDialogLoading.value = false
}

/**
 * 打开选择物料弹窗
 */
const handleOpenSelectItems = () => {
  if (isViewMode.value) {
    return
  }
  if (!canSelectItems.value) {
    ElMessage.warning('请选择仓库和库区')
    return
  }
  selectItemDialogVisible.value = true
}

/**
 * 处理物料选择
 */
const handleSelectItems = (items: SelectedCheckItem[]) => {
  items.forEach(item => {
    const detailPayload: StocktakingDetailItem = {
      inventoryDetailId: item.inventoryDetailId,
      skuId: item.skuId,
      itemId: item.itemId,
      itemName: item.itemName,
      skuName: item.skuName,
      unit: item.unit,
      systemQuantity: item.systemQuantity,
      actualQuantity: item.systemQuantity,
      differenceQuantity: 0,
      orderNo: item.orderNo,
      productionDate: item.productionDate,
      expirationDate: item.expirationDate,
      warehouseId: stocktakingForm.warehouseId || undefined,
      areaId: stocktakingForm.areaId || undefined
    }
    const existIndex = stocktakingForm.detailList.findIndex(detail => detail.inventoryDetailId === item.inventoryDetailId)
    if (existIndex >= 0) {
      stocktakingForm.detailList[existIndex] = detailPayload
    } else {
      stocktakingForm.detailList.push(detailPayload)
    }
  })
  ElMessage.success(`已添加 ${items.length} 个物料`)
}

/**
 * 处理盘点数量变化
 */
const handleActualQuantityChange = (row: StocktakingDetailItem) => {
  const actualValue = Number(row.actualQuantity ?? 0)
  const actual = Math.max(0, Math.round(actualValue))
  const system = Number(row.systemQuantity ?? 0)
  row.actualQuantity = actual
  row.differenceQuantity = Number((actual - system).toFixed(2))
}

/**
 * 移除盘库物料
 */
const handleRemoveItem = (index: number) => {
  stocktakingForm.detailList.splice(index, 1)
}

/**
 * 格式化盈亏数量
 */
const formatDifference = (value?: number) => {
  if (value === undefined || value === null) {
    return '-'
  }
  return Number(value).toFixed(2)
}

/**
 * 格式化明细日期
 */
const formatDetailDate = (value?: string) => {
  if (!value) {
    return '-'
  }
  return formatDate(value, 'YYYY-MM-DD')
}

/**
 * 统一格式化提交给后端的日期
 */
const formatDateTimePayload = (value?: string) => {
  if (!value) {
    return undefined
  }
  try {
    const date = new Date(value)
    if (!isNaN(date.getTime())) {
      return date.toISOString().slice(0, 19)
    }
  } catch {
    // 忽略解析异常
  }
  if (value.includes('T')) {
    return value.length >= 19 ? value.slice(0, 19) : `${value.split('T')[0]}T00:00:00`
  }
  if (/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    return `${value}T00:00:00`
  }
  return value
}

/**
 * 计算盘库明细的总数量
 */
const calcTotalQuantity = () => {
  return stocktakingForm.detailList.reduce((sum, item) => {
    const actual = Number(item.actualQuantity ?? item.systemQuantity ?? 0)
    return sum + (isNaN(actual) ? 0 : actual)
  }, 0)
}

/**
 * 计算盘库单的总盈亏数（所有详情的盈亏数之和）
 * 盈亏数 = checkQuantity - quantity = actualQuantity - systemQuantity
 */
const calcCheckOrderTotal = () => {
  return stocktakingForm.detailList.reduce((sum, item) => {
    const systemQuantity = Number(item.systemQuantity ?? 0)
    const actualQuantity = Number(item.actualQuantity ?? systemQuantity)
    const difference = actualQuantity - systemQuantity
    return sum + (isNaN(difference) ? 0 : difference)
  }, 0)
}

/**
 * 组装盘库详情提交数据
 */
const buildDetailPayload = (item: StocktakingDetailItem, checkOrderId: number): Partial<CheckOrderDetail> & { quantity?: number; checkQuantity?: number } => {
  const systemQuantity = Number(item.systemQuantity ?? 0)
  const actualQuantity = Number(item.actualQuantity ?? systemQuantity)
  const differenceQuantity =
    item.differenceQuantity !== undefined
      ? Number(item.differenceQuantity.toFixed(2))
      : Number((actualQuantity - systemQuantity).toFixed(2))
  return {
    checkOrderId,
    skuId: item.skuId,
    quantity: systemQuantity, // 系统库存
    checkQuantity: actualQuantity, // 盘点数量
    differenceQuantity,
    productionDate: formatDateTimePayload(item.productionDate),
    expirationDate: formatDateTimePayload(item.expirationDate),
    warehouseId: stocktakingForm.warehouseId || undefined,
    areaId: stocktakingForm.areaId || undefined,
    inventoryDetailId: item.inventoryDetailId
  } as any
}

/**
 * 新建盘库单并保存详情
 */
const submitCreateStocktaking = async () => {
  const orderPayload: Partial<CheckOrder> = {
    warehouseId: stocktakingForm.warehouseId || undefined,
    areaId: stocktakingForm.areaId || undefined,
    remark: stocktakingForm.remark,
    totalQuantity: calcTotalQuantity(),
    checkOrderTotal: calcCheckOrderTotal()
  }
  const checkOrderNo = await addCheckOrderApi(orderPayload)
  if (!checkOrderNo) {
    throw new Error('后端未返回盘库单号')
  }
  const listResponse = await getCheckOrderListApi({
    pageNo: 1,
    pageSize: 1,
    checkOrderNo
  })
  const createdOrder = listResponse?.records?.[0]
  if (!createdOrder?.id) {
    throw new Error('创建盘库单后未找到盘库单ID')
  }
  await Promise.all(
    stocktakingForm.detailList.map(item => addCheckOrderDetailApi(buildDetailPayload(item, createdOrder.id)))
  )
}

/**
 * 编辑盘库单并同步详情
 */
const submitEditStocktaking = async () => {
  if (!currentCheckOrderId.value) {
    throw new Error('当前盘库单ID缺失')
  }
  const checkOrderId = currentCheckOrderId.value
  const originalIds = new Set(
    originalDetailList.value.map(item => item.id).filter((id): id is number => typeof id === 'number')
  )
  const currentIds = new Set(
    stocktakingForm.detailList.map(item => item.id).filter((id): id is number => typeof id === 'number')
  )
  const deletedIds: number[] = []
  originalIds.forEach(id => {
    if (!currentIds.has(id)) {
      deletedIds.push(id)
    }
  })
  if (deletedIds.length > 0) {
    await Promise.all(deletedIds.map(id => deleteCheckOrderDetailApi(id.toString())))
  }
  const addPromises: Promise<void>[] = []
  const updatePromises: Promise<void>[] = []
  stocktakingForm.detailList.forEach(item => {
    if (!item.id) {
      addPromises.push(addCheckOrderDetailApi(buildDetailPayload(item, checkOrderId)))
    } else {
      updatePromises.push(
        updateCheckOrderDetailApi({
          ...buildDetailPayload(item, checkOrderId),
          id: item.id
        })
      )
    }
  })
  await Promise.all([...addPromises, ...updatePromises])
  await updateCheckOrderApi({
    id: checkOrderId,
    warehouseId: stocktakingForm.warehouseId || undefined,
    areaId: stocktakingForm.areaId || undefined,
    remark: stocktakingForm.remark,
    totalQuantity: calcTotalQuantity(),
    checkOrderTotal: calcCheckOrderTotal(),
    checkOrderStatus: currentCheckOrderStatus.value ?? undefined
  })
}

watch(
  () => stocktakingForm.areaId,
  (newVal, oldVal) => {
    if (initializingForm.value) {
      return
    }
    if (oldVal !== null && newVal !== oldVal) {
      stocktakingForm.detailList = []
    }
  }
)

onMounted(async () => {
  await Promise.all([loadWarehouseOptions(), loadAllAreas()])
  await loadStocktakingList()
})
</script>

<style scoped>
.stocktaking-container {
  @apply min-h-full;
}

.stats-cards .stat-card {
  @apply transition-all duration-300 hover:shadow-md;
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-area {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.search-form-inline {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0;
}

.search-form-inline .el-form-item {
  margin-bottom: 0;
  margin-right: 16px;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-form-inline {
    @apply flex-col items-stretch;
  }
  
  .stats-cards {
    @apply grid-cols-2;
  }
}
</style>
