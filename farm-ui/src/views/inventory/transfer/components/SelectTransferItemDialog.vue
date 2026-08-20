<template>
  <el-dialog
    v-model="visible"
    title="选择移库商品"
    width="80%"
    :before-close="handleClose"
    destroy-on-close
  >
    <div class="select-transfer-item-container">
      <div class="search-area">
        <el-form :model="searchForm" inline>
          <el-form-item label="商品名称">
            <el-input
              v-model="searchForm.itemName"
              placeholder="请输入商品名称"
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
          <el-form-item label="入库单号">
            <el-input
              v-model="searchForm.orderNo"
              placeholder="请输入入库单号"
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
        v-if="!props.sourceWarehouseId || !props.sourceAreaId"
        type="warning"
        show-icon
        title="请先在表单中选择源仓库和源库区后再选择移库商品"
        class="mb-3"
      />

      <el-table
        ref="tableRef"
        :data="inventoryList"
        border
        v-loading="loading"
        @selection-change="handleSelectionChange"
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="itemName" label="商品名称" min-width="160">
          <template #default="{ row }">
            <div class="item-cell">
              <div class="name">{{ row.itemName || '未知商品' }}</div>
              <div class="code">{{ row.itemCode || '-' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="skuName" label="规格名称" min-width="140" />
        <el-table-column prop="orderNo" label="入库单号" min-width="160">
          <template #default="{ row }">
            <span>{{ row.orderNo || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="80" align="center" />
        <el-table-column prop="availableQuantity" label="可用库存" width="120" align="center">
          <template #default="{ row }">
            <span class="quantity">{{ row.availableQuantity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="productionDate" label="生产日期" width="140">
          <template #default="{ row }">
            <span>{{ formatDate(row.productionDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="expirationDate" label="过期日期" width="140">
          <template #default="{ row }">
            <span>{{ formatDate(row.expirationDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="移库数量" width="140" align="center">
          <template #default="{ row }">
            <el-input-number
              v-model="row.transferQuantity"
              :min="0"
              :max="row.availableQuantity"
              :precision="2"
              size="small"
              style="width: 120px"
              @change="value => handleQuantityChange(value, row)"
            />
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

    <template #footer>
      <div class="dialog-footer">
        <span class="selected-info">已选择 {{ selectedItems.length }} 个商品</span>
        <div>
          <el-button @click="handleClose">取消</el-button>
          <el-button type="primary" @click="handleConfirm" :disabled="selectedItems.length === 0">
            确定
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getInventoryDetailListApi, getItemSkuByIdApi, getItemByIdApi } from '@/api/inventory'
import type { InventoryDetail, Item, ItemSku } from '@/types/inventory'

interface Props {
  /**
   * 是否显示弹窗
   */
  modelValue: boolean
  /**
   * 源仓库编号
   */
  sourceWarehouseId?: number
  /**
   * 源库区编号
   */
  sourceAreaId?: number
}

interface Emits {
  /**
   * 更新弹窗显示状态
   */
  (e: 'update:modelValue', value: boolean): void
  /**
   * 确认选择移库商品
   */
  (e: 'confirm', items: SelectedTransferItem[]): void
}

interface SelectedTransferItem {
  /**
   * 库存明细主键
   */
  inventoryDetailId: number
  /**
   * SKU 主键
   */
  skuId: number
  /**
   * 移库数量
   */
  quantity: number
  /**
   * 可用库存
   */
  availableQuantity: number
  /**
   * 商品名称
   */
  itemName?: string
  /**
   * 规格名称
   */
  skuName?: string
  /**
   * 商品单位
   */
  unit?: string
  /**
   * 入库单号
   */
  orderNo?: string
  /**
   * 生产日期
   */
  productionDate?: string
  /**
   * 过期日期
   */
  expirationDate?: string
}

type InventoryRow = InventoryDetail & {
  /**
   * 可用库存数量
   */
  availableQuantity: number
  /**
   * 移库数量
   */
  transferQuantity: number
  /**
   * 商品名称
   */
  itemName?: string
  /**
   * 商品编码
   */
  itemCode?: string
  /**
   * 规格名称
   */
  skuName?: string
  /**
   * 商品单位
   */
  unit?: string
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const visible = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
})

const searchForm = reactive({
  itemName: '',
  skuName: '',
  orderNo: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const loading = ref(false)
const inventoryList = ref<InventoryRow[]>([])
const selectedItems = ref<InventoryRow[]>([])
const tableRef = ref()

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
 * 获取库存明细列表
 */
const fetchInventoryList = async () => {
  if (!props.sourceWarehouseId || !props.sourceAreaId) {
    inventoryList.value = []
    pagination.total = 0
    return
  }

  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      warehouseId: props.sourceWarehouseId,
      areaId: props.sourceAreaId,
      itemName: searchForm.itemName || undefined,
      skuName: searchForm.skuName || undefined,
      orderNo: searchForm.orderNo || undefined
    }
    const res = await getInventoryDetailListApi(params)
    const records = res.records || []

    const skuIds = Array.from(
      new Set(
        records
          .map(item => item.skuId)
          .filter((id): id is number => typeof id === 'number')
      )
    )

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
        itemCode: itemInfo?.itemCode || skuInfo?.itemCode,
        skuName: skuInfo?.skuName || item.skuName,
        unit: itemInfo?.unit || skuInfo?.unit || item.unit,
        availableQuantity: item.remainQuantity ?? item.availableQuantity ?? 0,
        transferQuantity: 0
      }
    })

    pagination.total = res.total || 0
    pagination.current = res.current || params.pageNum
    pagination.size = res.size || params.pageSize
  } catch (error) {
    console.error('获取库存明细失败:', error)
    ElMessage.error('获取库存明细失败')
  } finally {
    loading.value = false
  }
}

/**
 * 处理数量变化同步勾选状态
 */
const handleQuantityChange = (value: number | null | undefined, row: InventoryRow) => {
  const isSelected = selectedItems.value.some(item => item.id === row.id)
  if (value && value > 0) {
    if (!isSelected) {
      tableRef.value?.toggleRowSelection?.(row, true)
    }
  } else if (isSelected) {
    tableRef.value?.toggleRowSelection?.(row, false)
    row.transferQuantity = 0
  }
}

/**
 * 搜索库存
 */
const handleSearch = () => {
  pagination.current = 1
  fetchInventoryList()
}

/**
 * 重置搜索条件
 */
const handleReset = () => {
  searchForm.itemName = ''
  searchForm.skuName = ''
  searchForm.orderNo = ''
  handleSearch()
}

/**
 * 处理选中数据变化
 */
const handleSelectionChange = (selection: InventoryRow[]) => {
  selectedItems.value = selection
  inventoryList.value.forEach(item => {
    if (!selection.some(selected => selected.id === item.id)) {
      item.transferQuantity = 0
    }
  })
}

/**
 * 确认选择商品
 */
const handleConfirm = () => {
  const validItems = selectedItems.value.filter(item => {
    if (!item.transferQuantity || item.transferQuantity <= 0) {
      ElMessage.warning(`商品"${item.itemName}"的移库数量必须大于0`)
      return false
    }
    if (item.transferQuantity > item.availableQuantity) {
      ElMessage.warning(`商品"${item.itemName}"的移库数量不能大于可用库存`)
      return false
    }
    if (typeof item.id !== 'number' || typeof item.skuId !== 'number') {
      ElMessage.warning('缺少必要的库存明细或SKU信息，无法移库')
      return false
    }
    return true
  })

  if (validItems.length !== selectedItems.value.length) {
    return
  }

  const result = validItems.map<SelectedTransferItem>(item => ({
    inventoryDetailId: item.id as number,
    skuId: item.skuId as number,
    quantity: item.transferQuantity,
    availableQuantity: item.availableQuantity,
    itemName: item.itemName,
    skuName: item.skuName,
    unit: item.unit,
    orderNo: item.orderNo,
    productionDate: item.productionDate,
    expirationDate: item.expirationDate
  }))

  emit('confirm', result)
  handleClose()
}

/**
 * 关闭弹窗并重置状态
 */
const handleClose = () => {
  visible.value = false
  selectedItems.value = []
  tableRef.value?.clearSelection?.()
}

watch(visible, newVal => {
  if (newVal) {
    handleReset()
    fetchInventoryList()
  }
})

watch([() => props.sourceWarehouseId, () => props.sourceAreaId], () => {
  if (visible.value) {
    fetchInventoryList()
  }
})
</script>

<style scoped>
.select-transfer-item-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-area {
  background-color: #f5f7fa;
  padding: 12px 16px;
  border-radius: 8px;
}

.item-cell {
  display: flex;
  flex-direction: column;
}

.item-cell .name {
  font-weight: 500;
  color: #303133;
}

.item-cell .code {
  font-size: 12px;
  color: #909399;
}

.quantity {
  color: #16a34a;
  font-weight: 600;
}

.pagination {
  margin-top: 16px;
  align-self: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.selected-info {
  color: #909399;
}
</style>
