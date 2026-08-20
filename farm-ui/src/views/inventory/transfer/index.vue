<template>
  <div class="inventory-transfer-container">
    <!-- 移库单列表 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>移库单列表</span>
          <el-button type="primary" @click="handleCreateTransfer">
            <el-icon class="mr-1"><Plus /></el-icon>
            新建移库单
          </el-button>
        </div>
      </template>

      <!-- 筛选区域 -->
      <div class="search-area">
        <el-form
          :model="searchForm"
          inline
          label-width="80px"
          class="search-form-inline"
        >
          <el-form-item label="移库单号">
            <el-input
              v-model="searchForm.movementOrderNo"
              placeholder="请输入移库单号"
              clearable
              style="width: 200px"
            />
          </el-form-item>

          <el-form-item label="移库状态">
            <el-select
              v-model="searchForm.movementOrderStatus"
              placeholder="请选择状态"
              clearable
              style="width: 160px"
            >
              <el-option label="待移库" value="0" />
              <el-option label="已移库" value="1" />
            </el-select>
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
        :data="transferList"
        stripe
        class="w-full"
        row-key="id"
      >

        <el-table-column prop="movementOrderNo" label="移库单号" width="160" show-overflow-tooltip />

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.movementOrderStatus)"
              size="small"
            >
              {{ getStatusText(row.movementOrderStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="移库路径" width="350">
          <template #default="{ row }">
            <div class="flex items-center text-sm">
              <span>{{ row.sourceWarehouseName }}-{{ row.sourceAreaName }}</span>
              <el-icon class="mx-2 text-gray-400"><Right /></el-icon>
              <span>{{ row.targetWarehouseName }}-{{ row.targetAreaName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="totalQuantity" label="总数量" width="100" align="center">
          <template #default="{ row }">
            <span class="font-medium">{{ row.totalQuantity }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="creatorName" label="创建人" width="100" />

        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            <span class="text-sm">{{ formatDate(row.createTime, 'YYYY-MM-DD HH:mm') }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleView(row)">
                查看
              </el-button>
              <el-button
                v-if="row.movementOrderStatus === 0"
                type="warning"
                size="small"
                @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button
                v-if="row.movementOrderStatus === 0"
                type="success"
                size="small"
                @click="handleConfirm(row)"
              >
                完成移库
              </el-button>
              <el-button
                v-if="row.movementOrderStatus === 0"
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

    <el-dialog
      v-model="viewDialogVisible"
      title="移库单详情"
      width="720px"
      v-loading="viewDialogLoading"
      @close="handleViewDialogClose"
    >
      <div v-if="currentViewOrder" class="space-y-4">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="移库单号">
            {{ currentViewOrder.movementOrderNo }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            {{ getStatusText(currentViewOrder.movementOrderStatus) }}
          </el-descriptions-item>
          <el-descriptions-item label="源仓库">
            {{ currentViewOrder.sourceWarehouseName }}-{{ currentViewOrder.sourceAreaName }}
          </el-descriptions-item>
          <el-descriptions-item label="目标仓库">
            {{ currentViewOrder.targetWarehouseName }}-{{ currentViewOrder.targetAreaName }}
          </el-descriptions-item>
          <el-descriptions-item label="创建人">
            {{ currentViewOrder.createBy || '未知' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ currentViewOrder.createTime ? formatDate(currentViewOrder.createTime, 'YYYY-MM-DD HH:mm') : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            {{ currentViewOrder.remark || '无' }}
          </el-descriptions-item>
        </el-descriptions>

        <el-table
          v-if="viewDetailItems.length > 0"
          :data="viewDetailItems"
          size="small"
          border
        >
          <el-table-column prop="itemName" label="物料名称" min-width="160">
            <template #default="{ row }">
              <div class="flex items-center">
                <el-avatar :size="24" :src="row.itemImage" class="mr-2">
                  <el-icon><Goods /></el-icon>
                </el-avatar>
                <div>
                  <div class="text-sm font-medium">{{ row.itemName || '未知物料' }}</div>
                  <div class="text-xs text-gray-500">{{ row.skuName || '未知规格' }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="移库数量" width="100" align="center" />
          <el-table-column prop="unit" label="单位" width="80" align="center" />
          <el-table-column label="生产日期" width="140" align="center">
            <template #default="{ row }">
              {{ row.productionDate ? formatDate(row.productionDate, 'YYYY-MM-DD') : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="失效日期" width="140" align="center">
            <template #default="{ row }">
              {{ row.expirationDate ? formatDate(row.expirationDate, 'YYYY-MM-DD') : '-' }}
            </template>
          </el-table-column>
        </el-table>
        <div
          v-else
          class="py-8 text-center text-gray-500 border border-dashed border-gray-300 rounded-lg"
        >
          <div>暂无物料明细</div>
        </div>
      </div>
      <div v-else class="py-8 text-center text-gray-500">
        <div>暂无移库单详情</div>
      </div>
    </el-dialog>

    <!-- 新建移库单对话框 -->
    <el-dialog
      v-model="transferDialogVisible"
      :title="transferDialogTitle"
      width="960px"
      v-loading="transferDialogLoading"
      @close="handleDialogClose"
    >
      <el-form
        ref="transferFormRef"
        :model="transferForm"
        :rules="transferFormRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="源仓库" prop="sourceWarehouseId" required>
              <el-select
                v-model="transferForm.sourceWarehouseId"
                placeholder="请选择源仓库"
                style="width: 100%"
                :disabled="transferDialogMode === 'edit'"
                @change="handleFormFromWarehouseChange"
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
            <el-form-item label="源库区" prop="sourceAreaId" required>
              <el-select
                v-model="transferForm.sourceAreaId"
                placeholder="请选择源库区"
                style="width: 100%"
                :disabled="transferDialogMode === 'edit'"
              >
                <el-option
                  v-for="area in formFromAreaOptions"
                  :key="area.id"
                  :label="area.areaName"
                  :value="area.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="目标仓库" prop="targetWarehouseId" required>
              <el-select
                v-model="transferForm.targetWarehouseId"
                placeholder="请选择目标仓库"
                style="width: 100%"
                :disabled="transferDialogMode === 'edit'"
                @change="handleFormToWarehouseChange"
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
            <el-form-item label="目标库区" prop="targetAreaId" required>
              <el-select
                v-model="transferForm.targetAreaId"
                placeholder="请选择目标库区"
                style="width: 100%"
                :disabled="transferDialogMode === 'edit'"
              >
                <el-option
                  v-for="area in formToAreaOptions"
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
            v-model="transferForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入移库备注信息"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="移库物料" required>
          <div class="w-full space-y-4">
            <div class="flex items-center gap-3">
              <el-button
                type="primary"
                size="small"
                @click="handleOpenSelectItems"
                :disabled="!canSelectItems"
              >
                <el-icon class="mr-1"><Plus /></el-icon>
                选择移库物料
              </el-button>
              <span v-if="!canSelectItems" class="text-xs text-gray-500">
                请先选择源仓库和源库区后再选择移库物料
              </span>
            </div>

            <el-table
              v-if="transferForm.items.length > 0"
              :data="transferForm.items"
              size="small"
              border
              style="width: 100%"
            >
              <el-table-column prop="itemName" label="物料名称" min-width="160">
                <template #default="{ row }">
                  <div class="flex items-center">
                    <el-avatar :size="24" :src="row.itemImage" class="mr-2">
                      <el-icon><Goods /></el-icon>
                    </el-avatar>
                    <div>
                      <div class="text-sm font-medium">{{ row.itemName || '未知物料' }}</div>
                      <div class="text-xs text-gray-500">{{ row.skuName || '未知规格' }}</div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="availableQuantity" label="可用库存" width="110" align="center">
                <template #default="{ row }">
                  <span class="font-medium text-green-600">{{ row.availableQuantity }}</span>
                </template>
              </el-table-column>
              <el-table-column label="移库数量" width="150" align="center">
                <template #default="{ row, $index }">
                  <el-input-number
                    v-model="row.transferQuantity"
                    :min="1"
                    :max="row.availableQuantity"
                    size="small"
                    style="width: 120px"
                    @change="validateTransferQuantity($index)"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="unit" label="单位" width="80" align="center" />
              <el-table-column label="入库单号" prop="orderNo" min-width="160" />
              <el-table-column label="操作" width="80" align="center">
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
              <el-icon class="text-4xl text-gray-400 mb-2"><Box /></el-icon>
              <div>暂无选择物料</div>
              <div class="text-sm">点击“选择移库物料”按钮添加物料</div>
            </div>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="transferDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitTransfer">确认提交</el-button>
        </div>
      </template>
      <SelectTransferItemDialog
        v-model="selectItemDialogVisible"
        :source-warehouse-id="transferForm.sourceWarehouseId ?? undefined"
        :source-area-id="transferForm.sourceAreaId ?? undefined"
        @confirm="handleSelectItems"
      />
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { Plus, Goods, Box, Right, Search, Refresh } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/common'
import { 
  getWarehouseOptionsApi,
  getWarehouseAreaOptionsApi,
  getMovementOrderListApi,
  getMovementOrderByIdApi,
  addMovementOrderApi,
  addMovementOrderDetailApi,
  updateMovementOrderApi,
  updateMovementOrderDetailApi,
  deleteMovementOrderApi,
  deleteMovementOrderDetailApi,
  completeMovementApi,
  getItemSkuByIdApi,
  getItemByIdApi,
  getInventoryDetailByIdApi
} from '@/api/inventory'
import type { MovementOrder, MovementOrderDetail } from '@/types/inventory'
import SelectTransferItemDialog from './components/SelectTransferItemDialog.vue'

// 移库物料类型（用于表单）
interface TransferItem {
  detailId?: number
  inventoryDetailId: number
  skuId: number
  itemName?: string
  itemImage?: string
  skuName?: string
  availableQuantity: number
  transferQuantity: number
  unit?: string
  orderNo?: string
  productionDate?: string
  expirationDate?: string
}

interface SelectedTransferItemPayload {
  inventoryDetailId: number
  skuId: number
  quantity: number
  availableQuantity: number
  itemName?: string
  skuName?: string
  unit?: string
  orderNo?: string
  productionDate?: string
  expirationDate?: string
}

// 响应式数据
const loading = ref(false)
const transferFormRef = ref<FormInstance>()
const transferList = ref<MovementOrder[]>([])
const transferDialogVisible = ref(false)
const transferDialogLoading = ref(false)
const selectItemDialogVisible = ref(false)
const quickSearch = ref('')
const transferDialogMode = ref<'create' | 'edit'>('create')
const currentEditingOrder = ref<MovementOrder | null>(null)
const viewDialogVisible = ref(false)
const viewDialogLoading = ref(false)
const currentViewOrder = ref<MovementOrder | null>(null)

const warehouseOptions = ref<Array<{
  id: number
  warehouseName: string
}>>([])

const formFromAreaOptions = ref<Array<{
  id: number
  areaName: string
  warehouseId: number
}>>([])

const formToAreaOptions = ref<Array<{
  id: number
  areaName: string
  warehouseId: number
}>>([])

// 搜索表单
const searchForm = reactive({
  movementOrderNo: '',
  movementOrderStatus: '',
  createDate: null as [string, string] | null
})

// 分页
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 移库表单
const transferForm = reactive({
  sourceWarehouseId: null as number | null,
  sourceAreaId: null as number | null,
  targetWarehouseId: null as number | null,
  targetAreaId: null as number | null,
  remark: '',
  items: [] as TransferItem[]
})

const canSelectItems = computed(() => transferForm.sourceWarehouseId !== null && transferForm.sourceAreaId !== null)
const transferDialogTitle = computed(() => transferDialogMode.value === 'create' ? '新建移库单' : '编辑移库单')
const viewDetailItems = computed(() => currentViewOrder.value?.detailList ?? [])

// 表单验证规则
const transferFormRules = {
  sourceWarehouseId: [{ required: true, message: '请选择源仓库', trigger: 'change' }],
  sourceAreaId: [{ required: true, message: '请选择源库区', trigger: 'change' }],
  targetWarehouseId: [{ required: true, message: '请选择目标仓库', trigger: 'change' }],
  targetAreaId: [{ required: true, message: '请选择目标库区', trigger: 'change' }]
}

// 获取状态类型
const getStatusType = (status: number) => {
  const typeMap: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    0: 'warning',  // 待移库
    1: 'success'   // 已移库
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const textMap: Record<number, string> = {
    0: '待移库',
    1: '已移库'
  }
  return textMap[status] || '未知状态'
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadTransferList()
}

// 重置
const handleReset = () => {
  (Object.keys(searchForm) as Array<keyof typeof searchForm>).forEach((key) => {
    (searchForm as any)[key] = key === 'createDate' ? null : ''
  })
  pagination.current = 1
  loadTransferList()
}

// 快速搜索
const handleQuickSearch = () => {
  if (quickSearch.value.trim()) {
    // 实现快速搜索逻辑
    ElMessage.info(`搜索: ${quickSearch.value}`)
  }
}

// 重置移库单表单
const resetTransferForm = () => {
  transferFormRef.value?.resetFields?.()
  transferForm.sourceWarehouseId = null
  transferForm.sourceAreaId = null
  transferForm.targetWarehouseId = null
  transferForm.targetAreaId = null
  transferForm.remark = ''
  transferForm.items.splice(0, transferForm.items.length)
  formFromAreaOptions.value = []
  formToAreaOptions.value = []
}

// 新建移库单
const handleCreateTransfer = () => {
  transferDialogMode.value = 'create'
  transferDialogLoading.value = false
  resetTransferForm()
  transferDialogVisible.value = true
}

// 批量移库
const handleBatchTransfer = () => {
  ElMessage.info('批量移库功能开发中...')
}

// 构建移库单详情展示数据
const enrichMovementOrderDetails = async (detailList: MovementOrderDetail[]): Promise<MovementOrderDetail[]> => {
  if (!detailList || detailList.length === 0) {
    return []
  }
  const enrichedList: MovementOrderDetail[] = []
  for (const detail of detailList) {
    const enrichedDetail: MovementOrderDetail = { ...detail }
    // 如果物料名称、规格名称或单位缺失，或者显示的是物料ID格式，则尝试获取物料信息
    const needsEnrichment = !enrichedDetail.itemName || 
                           !enrichedDetail.skuName || 
                           !enrichedDetail.unit ||
                           (enrichedDetail.itemName && enrichedDetail.itemName.startsWith('物料ID-'))
    
    if (needsEnrichment && detail.skuId) {
      try {
        const skuInfo = await getItemSkuByIdApi(detail.skuId)
        if (skuInfo) {
          // 优先使用 skuInfo 中的 itemName，如果没有则通过 itemId 获取
          if (!enrichedDetail.itemName || enrichedDetail.itemName.startsWith('物料ID-')) {
            enrichedDetail.itemName = skuInfo.itemName || ''
          }
          if (!enrichedDetail.skuName) {
            enrichedDetail.skuName = skuInfo.skuName || '未知规格'
          }
          if (!enrichedDetail.unit) {
            enrichedDetail.unit = skuInfo.unit || '件'
          }
          
          // 如果 skuInfo 中有 itemId，尝试获取更完整的物料信息
          if (skuInfo.itemId && (!enrichedDetail.itemName || enrichedDetail.itemName.startsWith('物料ID-'))) {
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
    
    // 只有在确实无法获取物料名称时才使用默认值
    if (!enrichedDetail.itemName || enrichedDetail.itemName.startsWith('物料ID-')) {
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

// 查看移库单
const handleView = async (row: MovementOrder) => {
  viewDialogVisible.value = true
  viewDialogLoading.value = true
  try {
    const detail = await getMovementOrderByIdApi(row.id)
    const detailList = await enrichMovementOrderDetails(detail.detailList || [])
    currentViewOrder.value = {
      ...detail,
      sourceWarehouseName: getWarehouseName(detail.sourceWarehouseId),
      sourceAreaName: getAreaName(detail.sourceAreaId),
      targetWarehouseName: getWarehouseName(detail.targetWarehouseId),
      targetAreaName: getAreaName(detail.targetAreaId),
      totalItems: detailList.length,
      detailList
    }
  } catch (error) {
    currentViewOrder.value = null
    ElMessage.error('获取移库单详情失败')
  } finally {
    viewDialogLoading.value = false
  }
}

// 查看对话框关闭
const handleViewDialogClose = () => {
  currentViewOrder.value = null
}

// 编辑移库单
const handleEdit = async (row: MovementOrder) => {
  transferDialogMode.value = 'edit'
  transferDialogVisible.value = true
  transferDialogLoading.value = true
  resetTransferForm()
  try {
    const detail = await getMovementOrderByIdApi(row.id)
    const detailList = await enrichMovementOrderDetails(detail.detailList || [])
    currentEditingOrder.value = {
      ...detail,
      detailList
    }
    transferForm.sourceWarehouseId = detail.sourceWarehouseId
    transferForm.sourceAreaId = detail.sourceAreaId
    transferForm.targetWarehouseId = detail.targetWarehouseId
    transferForm.targetAreaId = detail.targetAreaId
    transferForm.remark = detail.remark || ''
    formFromAreaOptions.value = await loadAreaOptions(detail.sourceWarehouseId)
    formToAreaOptions.value = await loadAreaOptions(detail.targetWarehouseId)
    
    // 为每个物料查询当前的实际可用库存
    const items = await Promise.all(detailList.map(async (detailItem) => {
      let availableQuantity = detailItem.availableQuantity
      let orderNo = detailItem.remark
      
      // 如果可用库存不存在，通过 inventoryDetailId 查询当前实际可用库存
      if (!availableQuantity && detailItem.inventoryDetailId) {
        try {
          const inventoryDetail = await getInventoryDetailByIdApi(detailItem.inventoryDetailId)
          availableQuantity = inventoryDetail.availableQuantity ?? inventoryDetail.remainQuantity ?? 0
          if (!orderNo && inventoryDetail.orderNo) {
            orderNo = inventoryDetail.orderNo
          }
        } catch (error) {
          // 如果查询失败，使用移库数量作为备用值（虽然不准确，但至少不会报错）
          availableQuantity = detailItem.quantity
        }
      }
      
      // 如果仍然没有可用库存，使用移库数量作为备用值
      if (!availableQuantity) {
        availableQuantity = detailItem.quantity
      }
      
      return {
        detailId: detailItem.id,
        inventoryDetailId: detailItem.inventoryDetailId,
        skuId: detailItem.skuId,
        itemName: detailItem.itemName,
        itemImage: detailItem.itemImage,
        skuName: detailItem.skuName,
        availableQuantity: availableQuantity,
        transferQuantity: detailItem.quantity,
        unit: detailItem.unit,
        orderNo: orderNo || '未知入库单',
        productionDate: detailItem.productionDate,
        expirationDate: detailItem.expirationDate
      }
    }))
    
    transferForm.items.splice(0, transferForm.items.length, ...items)
    transferFormRef.value?.clearValidate?.()
  } catch (error) {
    ElMessage.error('加载移库单信息失败')
    transferDialogVisible.value = false
    transferDialogMode.value = 'create'
    currentEditingOrder.value = null
    resetTransferForm()
  } finally {
    transferDialogLoading.value = false
  }
}

// 确认移库单
const handleConfirm = async (row: MovementOrder) => {
  try {
    await ElMessageBox.confirm(
      `确认执行移库单 ${row.movementOrderNo} 吗？`,
      '确认移库',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await completeMovementApi(row.id)
    ElMessage.success('移库单确认成功')
    loadTransferList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('确认移库单失败:', error)
      ElMessage.error('确认移库单失败')
    }
  }
}

// 完成移库单
const handleComplete = async (row: MovementOrder) => {
  try {
    await ElMessageBox.confirm(
      `确认完成移库单 ${row.movementOrderNo} 吗？`,
      '完成移库',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await completeMovementApi(row.id)
    ElMessage.success('移库单完成成功')
    loadTransferList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('完成移库单失败:', error)
      ElMessage.error('完成移库单失败')
    }
  }
}

// 取消移库单
const handleCancel = async (row: MovementOrder) => {
  try {
    await ElMessageBox.confirm(
      `确认删除移库单 ${row.movementOrderNo} 吗？删除后无法恢复。`,
      '删除移库单',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteMovementOrderApi(row.id.toString())
    ElMessage.success('移库单删除成功')
    loadTransferList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除移库单失败:', error)
      ElMessage.error('删除移库单失败')
    }
  }
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadTransferList()
}

// 当前页变化
const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadTransferList()
}

// 根据ID获取仓库名称
const getWarehouseName = (warehouseId: number): string => {
  const warehouse = warehouseOptions.value.find(w => w.id === warehouseId)
  return warehouse?.warehouseName || '未知仓库'
}

// 存储所有库区数据的映射
const allAreasMap = ref<Map<number, string>>(new Map())

// 根据ID获取库区名称
const getAreaName = (areaId: number): string => {
  return allAreasMap.value.get(areaId) || '未知库区'
}

// 加载所有库区数据并建立映射
const loadAllAreas = async () => {
  try {
    // 获取所有库区数据（不限制仓库）
    const allAreas = await loadAreaOptions()
    
    // 建立ID到名称的映射
    allAreasMap.value.clear()
    allAreas.forEach(area => {
      allAreasMap.value.set(area.id, area.areaName)
    })
    
    return allAreas
  } catch (error) {
    console.error('加载所有库区失败:', error)
    return []
  }
}

// 加载移库单列表
const loadTransferList = async () => {
  loading.value = true
  try {
    // 构建查询参数
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      movementOrderNo: searchForm.movementOrderNo || undefined,
      movementOrderStatus: searchForm.movementOrderStatus ? Number(searchForm.movementOrderStatus) : undefined
    }
    
    // 调用后端API
    const response = await getMovementOrderListApi(params)
    
    // 处理响应数据
    if (response && response.records) {
      transferList.value = response.records.map(item => ({
        ...item,
        // 根据ID匹配仓库和库区名称
        sourceWarehouseName: getWarehouseName(item.sourceWarehouseId),
        sourceAreaName: getAreaName(item.sourceAreaId),
        targetWarehouseName: getWarehouseName(item.targetWarehouseId), 
        targetAreaName: getAreaName(item.targetAreaId),
        totalItems: (item.detailList?.length ?? item.totalItems) || 0,
        creatorName: item.createBy || '未知'
      }))
      
      pagination.total = response.total || 0
    } else {
      transferList.value = []
      pagination.total = 0
    }
    
  } catch (error) {
    console.error('加载移库单列表失败:', error)
    ElMessage.error('加载移库单列表失败')
    transferList.value = []
    pagination.total = 0
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

// 表单源仓库变化
const handleFormFromWarehouseChange = () => {
  transferForm.sourceAreaId = null
  if (transferForm.sourceWarehouseId) {
    loadAreaOptions(Number(transferForm.sourceWarehouseId)).then(areas => {
      formFromAreaOptions.value = areas
    })
  } else {
    formFromAreaOptions.value = []
  }
}

// 表单目标仓库变化
const handleFormToWarehouseChange = () => {
  transferForm.targetAreaId = null
  if (transferForm.targetWarehouseId) {
    loadAreaOptions(Number(transferForm.targetWarehouseId)).then(areas => {
      formToAreaOptions.value = areas
    })
  } else {
    formToAreaOptions.value = []
  }
}

const handleOpenSelectItems = () => {
  if (!canSelectItems.value) {
    ElMessage.warning('请先选择源仓库和源库区')
    return
  }
  selectItemDialogVisible.value = true
}

const handleSelectItems = (items: SelectedTransferItemPayload[]) => {
  items.forEach(item => {
    const existIndex = transferForm.items.findIndex(detail => detail.inventoryDetailId === item.inventoryDetailId)
    if (existIndex >= 0) {
      transferForm.items[existIndex].transferQuantity = item.quantity
      transferForm.items[existIndex].availableQuantity = item.availableQuantity
      transferForm.items[existIndex].unit = item.unit
      transferForm.items[existIndex].orderNo = item.orderNo
      transferForm.items[existIndex].productionDate = item.productionDate
      transferForm.items[existIndex].expirationDate = item.expirationDate
    } else {
      transferForm.items.push({
        inventoryDetailId: item.inventoryDetailId,
        skuId: item.skuId,
        itemName: item.itemName,
        skuName: item.skuName,
        availableQuantity: item.availableQuantity,
        transferQuantity: item.quantity,
        unit: item.unit,
        orderNo: item.orderNo,
        productionDate: item.productionDate,
        expirationDate: item.expirationDate
      })
    }
  })
  ElMessage.success(`已添加 ${items.length} 个物料`)
}

// 移除物料
const handleRemoveItem = (index: number) => {
  transferForm.items.splice(index, 1)
}

// 验证移库数量
const validateTransferQuantity = (index: number) => {
  const item = transferForm.items[index]
  if (item.transferQuantity > item.availableQuantity) {
    item.transferQuantity = item.availableQuantity
    ElMessage.warning('移库数量不能超过可用库存')
  }
  if (item.transferQuantity < 1) {
    item.transferQuantity = 1
  }
}

// 提交移库单
const handleSubmitTransfer = async () => {
  if (transferForm.items.length === 0) {
    ElMessage.warning('请至少选择一个物料')
    return
  }

  if (
    transferForm.sourceWarehouseId === null ||
    transferForm.sourceAreaId === null ||
    transferForm.targetWarehouseId === null ||
    transferForm.targetAreaId === null
  ) {
    ElMessage.warning('请完整选择源仓库、源库区、目标仓库和目标库区')
    return
  }

  const { 
    sourceWarehouseId, 
    sourceAreaId, 
    targetWarehouseId, 
    targetAreaId 
  } = transferForm
  
  // 验证移库数量
  const hasInvalidQuantity = transferForm.items.some(item => !item.transferQuantity || item.transferQuantity <= 0)
  if (hasInvalidQuantity) {
    ElMessage.warning('请输入有效的移库数量')
    return
  }
  
  try {
    transferDialogLoading.value = true
    
    if (transferDialogMode.value === 'create') {
      // 创建模式：先创建移库单，再创建明细
      const movementOrder = {
        sourceWarehouseId,
        sourceAreaId,
        targetWarehouseId,
        targetAreaId,
        remark: transferForm.remark,
        totalQuantity: transferForm.items.reduce((sum, item) => sum + (item.transferQuantity || 0), 0)
      }
      
      // 1. 先创建移库单，获取移库单号
      const movementOrderNo = await addMovementOrderApi(movementOrder as any)
      
      // 2. 根据移库单号查询移库单，获取移库单ID
      const orderListResponse = await getMovementOrderListApi({
        pageNum: 1,
        pageSize: 1,
        movementOrderNo
      })
      
      if (!orderListResponse.records || orderListResponse.records.length === 0) {
        throw new Error('创建移库单后无法找到移库单信息')
      }
      
      const createdOrder = orderListResponse.records[0]
      const movementOrderId = createdOrder.id
      
      if (!movementOrderId) {
        throw new Error('创建移库单后无法获取移库单ID')
      }
      
      // 3. 批量创建移库单明细
      if (transferForm.items.length === 0) {
        throw new Error('请至少添加一条移库详情')
      }
      
      const detailPayloads = transferForm.items.map(item => {
        // 格式化日期：统一转换为 YYYY-MM-DDTHH:mm:ss 格式（LocalDateTime）
        const formatDateStr = (dateStr?: string, defaultDate?: Date): string => {
          if (dateStr) {
            try {
              // 尝试解析日期字符串
              const date = new Date(dateStr)
              if (!isNaN(date.getTime())) {
                // 日期有效，格式化为 YYYY-MM-DDTHH:mm:ss（去掉末尾的Z）
                return date.toISOString().slice(0, 19)
              }
            } catch (e) {
              console.warn('日期格式解析失败:', dateStr, e)
            }
            // 如果包含 T，说明是 ISO 格式，提取日期时间部分
            if (dateStr.includes('T')) {
              // 如果已经是完整格式，直接返回前19个字符
              if (dateStr.length >= 19) {
                return dateStr.slice(0, 19)
              }
              // 如果只有日期部分，补充时间部分
              return `${dateStr.split('T')[0]}T00:00:00`
            }
            // 如果已经是 YYYY-MM-DD 格式，补充时间部分
            if (/^\d{4}-\d{2}-\d{2}$/.test(dateStr)) {
              return `${dateStr}T00:00:00`
            }
          }
          // 使用默认日期，格式化为 YYYY-MM-DDTHH:mm:ss
          const date = defaultDate || new Date()
          return date.toISOString().slice(0, 19)
        }
        
        return {
          movementOrderId,
          skuId: item.skuId,
          quantity: item.transferQuantity,
          sourceWarehouseId,
          sourceAreaId,
          targetWarehouseId,
          targetAreaId,
          productionDate: formatDateStr(item.productionDate, new Date()),
          expirationDate: formatDateStr(item.expirationDate, new Date(Date.now() + 365 * 24 * 60 * 60 * 1000)),
          inventoryDetailId: item.inventoryDetailId
        }
      })
      
      // 批量保存移库单详情
      await Promise.all(detailPayloads.map(detail => addMovementOrderDetailApi(detail)))
      
      ElMessage.success('移库单创建成功')
    } else if (currentEditingOrder.value) {
      // 编辑模式：正确处理新增、删除、更新的详情
      const editingOrder = currentEditingOrder.value
      const movementOrderId = editingOrder.id
      const originalDetails = editingOrder.detailList || []
      const originalDetailIds = new Set(originalDetails.map(d => d.id))
      const currentDetailIds = new Set(transferForm.items.filter(item => item.detailId).map(item => item.detailId!))
      
      // 格式化日期：统一转换为 YYYY-MM-DDTHH:mm:ss 格式（LocalDateTime）
      const formatDateStr = (dateStr?: string, defaultDate?: Date): string => {
        if (dateStr) {
          try {
            const date = new Date(dateStr)
            if (!isNaN(date.getTime())) {
              return date.toISOString().slice(0, 19)
            }
          } catch (e) {
            console.warn('日期格式解析失败:', dateStr, e)
          }
          if (dateStr.includes('T')) {
            if (dateStr.length >= 19) {
              return dateStr.slice(0, 19)
            }
            return `${dateStr.split('T')[0]}T00:00:00`
          }
          if (/^\d{4}-\d{2}-\d{2}$/.test(dateStr)) {
            return `${dateStr}T00:00:00`
          }
        }
        const date = defaultDate || new Date()
        return date.toISOString().slice(0, 19)
      }
      
      // 1. 删除已移除的详情
      const deletedIds: number[] = []
      originalDetails.forEach(original => {
        if (!currentDetailIds.has(original.id)) {
          deletedIds.push(original.id)
        }
      })
      if (deletedIds.length > 0) {
        await deleteMovementOrderDetailApi(deletedIds.join(','))
      }
      
      // 2. 新增和更新的详情
      const addPromises: Promise<void>[] = []
      const updatePromises: Promise<void>[] = []
      
      transferForm.items.forEach(item => {
        const detailPayload = {
          movementOrderId,
          skuId: item.skuId,
          quantity: item.transferQuantity,
          sourceWarehouseId: editingOrder.sourceWarehouseId,
          sourceAreaId: editingOrder.sourceAreaId,
          targetWarehouseId: editingOrder.targetWarehouseId,
          targetAreaId: editingOrder.targetAreaId,
          productionDate: formatDateStr(item.productionDate, new Date()),
          expirationDate: formatDateStr(item.expirationDate, new Date(Date.now() + 365 * 24 * 60 * 60 * 1000)),
          inventoryDetailId: item.inventoryDetailId
        }
        
        if (!item.detailId) {
          // 新增详情
          addPromises.push(addMovementOrderDetailApi(detailPayload))
        } else {
          // 更新详情
          updatePromises.push(updateMovementOrderDetailApi({
            id: item.detailId,
            ...detailPayload
          }))
        }
      })
      
      // 执行新增和更新操作
      await Promise.all([...addPromises, ...updatePromises])
      
      // 3. 更新移库单基本信息（备注和总数量，仓库和库区保持不变）
      await updateMovementOrderApi({
        id: movementOrderId,
        movementOrderNo: editingOrder.movementOrderNo,
        movementOrderStatus: editingOrder.movementOrderStatus,
        sourceWarehouseId: editingOrder.sourceWarehouseId,
        sourceAreaId: editingOrder.sourceAreaId,
        targetWarehouseId: editingOrder.targetWarehouseId,
        targetAreaId: editingOrder.targetAreaId,
        remark: transferForm.remark,
        totalQuantity: transferForm.items.reduce((sum, item) => sum + (item.transferQuantity || 0), 0)
      } as any)
      
      ElMessage.success('移库单更新成功')
    }
    
    transferDialogVisible.value = false
    transferDialogMode.value = 'create'
    loadTransferList()
  } catch (error: any) {
    console.error('保存移库单失败:', error)
    const errorMessage = error?.message || error?.response?.data?.msg || error?.response?.data?.message
    ElMessage.error(
      transferDialogMode.value === 'create' 
        ? `创建移库单失败: ${errorMessage || '未知错误'}` 
        : `更新移库单失败: ${errorMessage || '未知错误'}`
    )
  } finally {
    transferDialogLoading.value = false
  }
}

// 对话框关闭
const handleDialogClose = () => {
  resetTransferForm()
  transferDialogMode.value = 'create'
  currentEditingOrder.value = null
  transferDialogLoading.value = false
  selectItemDialogVisible.value = false
}

onMounted(async () => {
  // 先加载仓库和库区数据，再加载移库单列表
  await loadWarehouseOptions()
  await loadAllAreas()
  
  // 最后加载移库单列表，确保仓库和库区数据已加载
  loadTransferList()
})
</script>

<style scoped>
.inventory-transfer-container {
  @apply min-h-full;
  width: 100%;
  max-width: 100%;
  padding: 0;
  margin: 0;
  box-sizing: border-box;
}

.inventory-transfer-container :deep(.el-card) {
  width: 100%;
  max-width: 100%;
}

.inventory-transfer-container :deep(.el-card__body) {
  width: 100%;
  max-width: 100%;
}

/* 页面头部 */
.page-header {
  @apply flex items-center justify-between mb-6 p-4 bg-white rounded-lg shadow-sm border border-gray-200;
}

.header-info h2 {
  @apply text-xl font-semibold text-gray-800 mb-1;
}

.header-info p {
  @apply text-sm text-gray-500;
}

.header-actions .create-btn {
  @apply px-4 py-2;
}

/* 搜索容器 */
.search-container {
  @apply space-y-4;
}

/* 搜索表单 */
.search-form {
  @apply space-y-4;
}

.search-row {
  @apply flex flex-wrap items-center gap-4;
}

.search-row .el-form-item {
  @apply mb-0;
}

/* 操作按钮区域 */
.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-buttons {
  @apply flex items-center gap-3;
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

.table-header {
  @apply border-b border-gray-200 pb-4;
}

.pagination-container {
  @apply flex justify-center;
}

/* 确保表格占满宽度 */
.inventory-transfer-container :deep(.el-table) {
  width: 100%;
  max-width: 100%;
}

.inventory-transfer-container :deep(.el-table__inner-wrapper) {
  width: 100%;
  max-width: 100%;
}

.inventory-transfer-container :deep(.el-scrollbar__view) {
  width: 100%;
  max-width: 100%;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .search-row {
    @apply flex-col items-stretch gap-3;
  }
  
  .search-row .el-form-item {
    @apply w-full;
  }
  
  .search-row .el-form-item .el-select,
  .search-row .el-form-item .el-date-editor {
    @apply w-full;
  }
  
  .page-header {
    @apply flex-col items-stretch gap-4;
  }
  
  .header-info {
    @apply text-center;
  }
  
  .action-buttons {
    @apply justify-center;
  }
}

@media (max-width: 768px) {
  .search-container {
    @apply space-y-3;
  }
  
  .action-buttons {
    @apply pt-3 border-t-0;
  }
  
  .search-buttons .el-button,
  .create-btn {
    @apply w-full;
  }
}
</style>

