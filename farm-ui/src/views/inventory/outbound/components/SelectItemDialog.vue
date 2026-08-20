<template>
  <el-dialog
    v-model="visible"
    title="选择出库商品"
    width="80%"
    :before-close="handleClose"
    destroy-on-close
  >
    <div class="select-item-container">
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" inline>
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

      <!-- 库存列表 -->
      <el-table 
        ref="tableRef"
        :data="inventoryList" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
        border
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="itemName" label="商品名称" width="150" />
        <el-table-column prop="skuName" label="规格名称" width="120" />
        <el-table-column prop="orderNo" label="入库单号" width="180">
          <template #default="{ row }">
            <span>{{ row.orderNo || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="availableQuantity" label="可用库存" width="100">
          <template #default="{ row }">
            <span class="quantity available">{{ row.availableQuantity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unitPrice" label="单价(元)" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.unitPrice?.toFixed(2) || '0.00' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="productionDate" label="生产日期" width="120">
          <template #default="{ row }">
            <span>{{ formatDate(row.productionDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="expirationDate" label="过期日期" width="120">
          <template #default="{ row }">
            <span>{{ formatDate(row.expirationDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="出库数量" width="120">
          <template #default="{ row }">
            <el-input-number
              v-model="row.outQuantity"
              :min="0"
              :max="row.availableQuantity"
              :precision="2"
              size="small"
              style="width: 100%"
              @change="value => handleQuantityChange(value, row)"
            />
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSearch"
        @current-change="handleSearch"
        style="margin-top: 20px; text-align: right;"
      />
    </div>

    <template #footer>
      <div class="dialog-footer">
        <span class="selected-info">
          已选择 {{ selectedItems.length }} 个商品
        </span>
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
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getInventoryDetailListApi, getItemByIdApi, getItemSkuByIdApi } from '@/api/inventory'
import type { InventoryDetail, Item, ItemSku } from '@/types/inventory'

interface Props {
  modelValue: boolean
  warehouseId?: number
  areaId?: number
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'confirm', items: any[]): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 弹窗显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 搜索表单
const searchForm = reactive({
  orderNo: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 库存列表数据
type InventoryRow = InventoryDetail & { 
  itemName?: string
  skuName?: string
  unit?: string
  outQuantity?: number
  orderNo?: string
}

const inventoryList = ref<InventoryRow[]>([])
const loading = ref(false)

// 选中的商品
const selectedItems = ref<any[]>([])

// 表格引用
const tableRef = ref()

// sku信息缓存，避免重复请求
const skuInfoMap = reactive<Record<number, ItemSku>>({})
// 商品信息缓存
const itemInfoMap = reactive<Record<number, Item>>({})

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) {
    return '-'
  }
  const date = dayjs(dateStr)
  if (!date.isValid()) {
    return '-'
  }
  return date.format('YYYY-MM-DD')
}

/**
 * 确保表格行选中状态与数量输入保持一致
 */
const ensureRowSelection = (row: InventoryRow, selected: boolean) => {
  tableRef.value?.toggleRowSelection?.(row, selected)
}

/**
 * 处理出库数量变化，自动同步表格选中状态
 */
const handleQuantityChange = (value: number | null | undefined, row: InventoryRow) => {
  const isSelected = selectedItems.value.some(item => item.id === row.id)
  if (value !== null && value !== undefined && value > 0) {
    if (!isSelected) {
      ensureRowSelection(row, true)
    }
  } else if (isSelected) {
    ensureRowSelection(row, false)
    row.outQuantity = 0
  }
}

// 获取库存详情列表
const fetchInventoryList = async () => {
  if (!props.warehouseId || !props.areaId) {
    inventoryList.value = []
    return
  }
  
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      warehouseId: props.warehouseId,
      areaId: props.areaId,
      ...searchForm
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
      const skuPromises = missingSkuIds.map(id =>
        getItemSkuByIdApi(id)
          .then(sku => {
            if (sku?.id !== undefined) {
              skuInfoMap[sku.id] = sku
            }
          })
          .catch(() => undefined)
      )
      await Promise.all(skuPromises)
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
      const itemPromises = missingItemIds.map(id =>
        getItemByIdApi(id)
          .then(item => {
            if (item?.id !== undefined) {
              itemInfoMap[item.id] = item
            }
          })
          .catch(() => undefined)
      )
      await Promise.all(itemPromises)
    }

    // 为每个库存项补充展示字段
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
        skuName: skuInfo?.skuName || item.skuName,
        unit: itemInfo?.unit || skuInfo?.unit || item.unit,
        orderNo: item.orderNo,
        availableQuantity: item.remainQuantity ?? item.availableQuantity ?? 0,
        unitPrice: item.amount ?? item.unitPrice ?? 0,
        outQuantity: 0
      }
    })
    
    pagination.total = res.total || 0
    pagination.current = res.current || 1
    pagination.size = res.size || 10
    
  } catch (error) {
    console.error('获取库存列表失败:', error)
    ElMessage.error('获取库存数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchInventoryList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    orderNo: ''
  })
  handleSearch()
}

// 选择变更处理
const handleSelectionChange = (selection: any[]) => {
  selectedItems.value = selection
  
  // 清空未选中项的出库数量
  inventoryList.value.forEach(item => {
    if (!selection.some(selected => selected.id === item.id)) {
      item.outQuantity = 0
    }
  })
}

// 确认选择
const handleConfirm = () => {
  const validItems = selectedItems.value.filter(item => {
    if (!item.outQuantity || item.outQuantity <= 0) {
      ElMessage.warning(`商品"${item.itemName}"的出库数量必须大于0`)
      return false
    }
    if (item.outQuantity > item.availableQuantity) {
      ElMessage.warning(`商品"${item.itemName}"的出库数量不能大于可用库存`)
      return false
    }
    return true
  })
  
  if (validItems.length !== selectedItems.value.length) {
    return
  }
  
  // 构造返回数据
  const resultItems = validItems.map(item => ({
    skuId: item.skuId,
    quantity: item.outQuantity,
    unitPrice: item.unitPrice || 0,
    inventoryDetailId: item.id,
    orderNo: item.orderNo,
    productionDate: item.productionDate,
    expirationDate: item.expirationDate,
    itemName: item.itemName,
    skuName: item.skuName,
    unit: item.unit
  }))
  
  emit('confirm', resultItems)
  handleClose()
}

// 关闭弹窗
const handleClose = () => {
  visible.value = false
  // 重置状态
  selectedItems.value = []
  if (tableRef.value) {
    tableRef.value.clearSelection()
  }
}

// 监听弹窗显示状态
watch(visible, (newVal) => {
  if (newVal) {
    handleReset()
    fetchInventoryList()
  }
})

// 监听仓库和库区变化
watch([() => props.warehouseId, () => props.areaId], () => {
  if (visible.value) {
    fetchInventoryList()
  }
})
</script>

<style scoped>
.select-item-container {
  max-height: 60vh;
  overflow-y: auto;
}

.search-area {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.quantity {
  font-weight: bold;
}

.quantity.available {
  color: #67c23a;
}

.price {
  color: #e6a23c;
  font-weight: bold;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selected-info {
  color: #909399;
  font-size: 14px;
}
</style>
