<template>
  <el-dialog
    v-model="visible"
    title="选择盘库物料"
    width="80%"
    :before-close="handleClose"
    destroy-on-close
  >
    <div class="select-check-item-container">
      <div class="search-area">
        <el-form :model="searchForm" inline>
          <el-form-item label="物料名称">
            <el-input
              v-model="searchForm.itemName"
              placeholder="请输入物料名称"
              clearable
            />
          </el-form-item>
          <el-form-item label="规格名称">
            <el-input
              v-model="searchForm.skuName"
              placeholder="请输入规格名称"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-alert
        v-if="!props.warehouseId || !props.areaId"
        type="warning"
        show-icon
        title="请选择仓库和库区后再进行物料选择"
        class="mb-3"
      />

      <div class="inventory-panel">
        <div class="panel-header">
          <span class="panel-title">可选物料</span>
          <span class="panel-tip">支持多选，可跨页选择</span>
        </div>
        <el-table
          ref="tableRef"
          :data="inventoryList"
          border
          v-loading="loading"
          @selection-change="handleSelectionChange"
          style="width: 100%"
          height="420px"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="itemName" label="物料名称" min-width="160">
            <template #default="{ row }">
              <div class="item-cell">
                <div class="name">{{ row.itemName || '未知物料' }}</div>
                <div class="code">{{ row.itemCode || '-' }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="skuName" label="规格名称" min-width="140" />
          <el-table-column prop="unit" label="单位" width="80" align="center" />
          <el-table-column prop="orderNo" label="来源单号" min-width="140">
            <template #default="{ row }">
              <span>{{ row.orderNo || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="availableQuantity" label="系统库存" width="120" align="center">
            <template #default="{ row }">
              <span class="quantity">{{ row.availableQuantity }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="productionDate" label="生产日期" width="140">
            <template #default="{ row }">
              <span>{{ formatDate(row.productionDate) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="expirationDate" label="失效日期" width="140">
            <template #default="{ row }">
              <span>{{ formatDate(row.expirationDate) }}</span>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
          class="pagination"
        />
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <span class="selected-info">已选择 {{ currentSelectedRows.length }} 个物料</span>
        <div>
          <el-button @click="handleClose">取消</el-button>
          <el-button type="primary" @click="handleConfirm" :disabled="currentSelectedRows.length === 0">
            确定
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import { computed, nextTick, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getInventoryDetailListApi, getItemSkuByIdApi, getItemByIdApi } from '@/api/inventory'
import type { InventoryDetail, Item, ItemSku } from '@/types/inventory'

interface Props {
  /**
   * 弹窗显隐
   */
  modelValue: boolean
  /**
   * 仓库编号
   */
  warehouseId?: number | null
  /**
   * 库区编号
   */
  areaId?: number | null
  /**
   * 已选择的盘库物料
   */
  selectedItems?: SelectedCheckItem[]
}

interface Emits {
  /**
   * 更新显隐状态
   */
  (e: 'update:modelValue', value: boolean): void
  /**
   * 选择完成
   */
  (e: 'confirm', items: SelectedCheckItem[]): void
}

export interface SelectedCheckItem {
  /**
   * 库存明细主键
   */
  inventoryDetailId: number
  /**
   * SKU 主键
   */
  skuId?: number
  /**
   * 物料主键
   */
  itemId?: number
  /**
   * 物料名称
   */
  itemName?: string
  /**
   * 规格名称
   */
  skuName?: string
  /**
   * 单位
   */
  unit?: string
  /**
   * 系统库存
   */
  systemQuantity: number
  /**
   * 可用库存
   */
  availableQuantity: number
  /**
   * 来源单号
   */
  orderNo?: string
  /**
   * 生产日期
   */
  productionDate?: string
  /**
   * 失效日期
   */
  expirationDate?: string
}

type InventoryRow = InventoryDetail & {
  itemName?: string
  itemCode?: string
  skuName?: string
  unit?: string
  availableQuantity: number
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const visible = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
})

const searchForm = reactive({
  itemName: '',
  skuName: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const loading = ref(false)
const inventoryList = ref<InventoryRow[]>([])
const currentSelectedRows = ref<SelectedCheckItem[]>([])
const tableRef = ref()
const selectedItemsMap = new Map<number, SelectedCheckItem>()
const syncingSelection = ref(false)

const skuInfoMap = reactive<Record<number, ItemSku>>({})
const itemInfoMap = reactive<Record<number, Item>>({})

/**
 * 格式化日期
 */
const formatDate = (dateStr?: string) => {
  if (!dateStr) {
    return '-'
  }
  const date = dayjs(dateStr)
  return date.isValid() ? date.format('YYYY-MM-DD') : '-'
}

/**
 * 拉取库存明细
 */
const fetchInventoryList = async () => {
  if (!props.warehouseId || !props.areaId) {
    inventoryList.value = []
    pagination.total = 0
    return
  }
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      warehouseId: Number(props.warehouseId),
      areaId: Number(props.areaId),
      itemName: searchForm.itemName || undefined,
      skuName: searchForm.skuName || undefined
    }
    const res = await getInventoryDetailListApi(params)
    const records = res.records || []

    const skuIds = Array.from(new Set(records.map(item => item.skuId).filter((id): id is number => typeof id === 'number')))
    const missingSkuIds = skuIds.filter(id => !skuInfoMap[id])
    if (missingSkuIds.length > 0) {
      await Promise.all(
        missingSkuIds.map(id =>
          getItemSkuByIdApi(id)
            .then(sku => {
              if (sku?.id !== undefined) {
                skuInfoMap[sku.id] = sku
              }
            })
            .catch(() => undefined)
        )
      )
    }

    const itemIds = Array.from(
      new Set(
        records
          .map(detail => {
            if (typeof detail.itemId === 'number') {
              return detail.itemId
            }
            const skuInfo = detail.skuId ? skuInfoMap[detail.skuId] : undefined
            return skuInfo?.itemId
          })
          .filter((id): id is number => typeof id === 'number')
      )
    )
    const missingItemIds = itemIds.filter(id => !itemInfoMap[id])
    if (missingItemIds.length > 0) {
      await Promise.all(
        missingItemIds.map(id =>
          getItemByIdApi(id)
            .then(item => {
              if (item?.id !== undefined) {
                itemInfoMap[item.id] = item
              }
            })
            .catch(() => undefined)
        )
      )
    }

    inventoryList.value = records.map(item => {
      const skuInfo = item.skuId ? skuInfoMap[item.skuId] : undefined
      const skuInfoWithExtra = skuInfo as (ItemSku & { itemCode?: string }) | undefined
      const itemInfo = (() => {
        if (typeof item.itemId === 'number' && itemInfoMap[item.itemId]) {
          return itemInfoMap[item.itemId]
        }
        if (skuInfo?.itemId !== undefined && itemInfoMap[skuInfo.itemId]) {
          return itemInfoMap[skuInfo.itemId]
        }
        return undefined
      })()
      return {
        ...item,
        itemName: itemInfo?.itemName || skuInfo?.itemName || item.itemName,
        itemCode: itemInfo?.itemCode || skuInfoWithExtra?.itemCode,
        skuName: skuInfo?.skuName || item.skuName,
        unit: itemInfo?.unit || skuInfo?.unit || item.unit,
        availableQuantity: item.remainQuantity ?? item.availableQuantity ?? 0
      }
    })

    pagination.total = res.total || 0
    pagination.current = res.current || params.pageNum
    pagination.size = res.size || params.pageSize
    await syncTableSelection()
  } catch (error) {
    console.error('获取库存明细失败:', error)
    ElMessage.error('获取库存明细失败')
  } finally {
    loading.value = false
  }
}

/**
 * 处理搜索
 */
const handleSearch = () => {
  pagination.current = 1
  fetchInventoryList()
}

/**
 * 重置搜索
 */
const handleReset = () => {
  searchForm.itemName = ''
  searchForm.skuName = ''
  handleSearch()
}

/**
 * 选择变化
 */
const transformRowToSelected = (row: InventoryRow): SelectedCheckItem => ({
  inventoryDetailId: row.id,
  skuId: row.skuId,
  itemId: row.itemId,
  itemName: row.itemName,
  skuName: row.skuName,
  unit: row.unit,
  systemQuantity: row.availableQuantity,
  availableQuantity: row.availableQuantity,
  orderNo: row.orderNo,
  productionDate: row.productionDate,
  expirationDate: row.expirationDate
})

const updateCurrentSelectedRows = () => {
  currentSelectedRows.value = Array.from(selectedItemsMap.values())
}

const syncTableSelection = async () => {
  if (!tableRef.value) {
    return
  }
  syncingSelection.value = true
  await nextTick()
  tableRef.value.clearSelection()
  inventoryList.value.forEach(row => {
    if (row?.id !== undefined && selectedItemsMap.has(row.id)) {
      tableRef.value.toggleRowSelection(row, true)
    }
  })
  await nextTick()
  syncingSelection.value = false
}

const handleSelectionChange = (rows: InventoryRow[]) => {
  if (syncingSelection.value) {
    return
  }
  const currentPageIds = new Set(inventoryList.value.map(item => item.id))
  currentPageIds.forEach(id => {
    if (id !== undefined && !rows.some(row => row.id === id)) {
      selectedItemsMap.delete(id)
    }
  })
  rows.forEach(row => {
    if (row?.id !== undefined) {
      selectedItemsMap.set(row.id, transformRowToSelected(row))
    }
  })
  updateCurrentSelectedRows()
}

/**
 * 关闭弹窗
 */
const handleClose = () => {
  emit('update:modelValue', false)
}

/**
 * 确认选择
 */
const handleConfirm = () => {
  emit('confirm', currentSelectedRows.value)
  handleClose()
}

const initializeSelectedItems = () => {
  selectedItemsMap.clear()
  ;(props.selectedItems || []).forEach(item => {
    if (item.inventoryDetailId !== undefined) {
      selectedItemsMap.set(item.inventoryDetailId, { ...item })
    }
  })
  updateCurrentSelectedRows()
}

watch(
  () => [props.warehouseId, props.areaId],
  ([warehouseId, areaId]) => {
    if (warehouseId && areaId && visible.value) {
      handleSearch()
    } else {
      inventoryList.value = []
      pagination.total = 0
    }
  }
)

watch(
  () => visible.value,
  value => {
    if (value) {
      initializeSelectedItems()
      handleSearch()
    } else {
      selectedItemsMap.clear()
      currentSelectedRows.value = []
    }
  }
)

watch(
  () => props.selectedItems,
  () => {
    if (!visible.value) {
      return
    }
    initializeSelectedItems()
    syncTableSelection()
  },
  { deep: true }
)
</script>

<style scoped>
.select-check-item-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.inventory-panel {
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.panel-tip {
  font-size: 12px;
  color: #909399;
}

.search-area {
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.item-cell .name {
  font-weight: 600;
}

.item-cell .code {
  font-size: 12px;
  color: #909399;
}

.quantity {
  font-weight: 600;
  color: #16a34a;
}

.pagination {
  margin-top: 12px;
  text-align: right;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.selected-info {
  font-size: 13px;
  color: #606266;
}

</style>

