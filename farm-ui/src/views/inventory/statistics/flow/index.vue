<template>
  <div class="inventory-statistics-container">
    <div class="content-card w-full">
      <div class="p-4 border-b border-gray-200">
        <h3 class="text-lg font-semibold text-gray-800">库存流水</h3>
      </div>

      <div class="px-4 pb-2">
        <el-form
          :model="historySearchForm"
          label-width="80px"
          class="search-form"
        >
          <div class="filter-row">
            <el-form-item label="操作类型" class="filter-item">
              <el-select
                v-model="historySearchForm.orderType"
                placeholder="请选择操作类型"
                clearable
              >
                <el-option label="入库" :value="1" />
                <el-option label="出库" :value="2" />
                <el-option label="移库" :value="3" />
                <el-option label="盘库" :value="4" />
              </el-select>
            </el-form-item>
            <el-form-item label="仓库" class="filter-item">
              <el-select
                v-model="historySearchForm.warehouseId"
                placeholder="请选择仓库"
                clearable
              >
                <el-option
                  v-for="item in warehouseOptions"
                  :key="item.id"
                  :label="item.warehouseName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="库区" class="filter-item">
              <el-select
                v-model="historySearchForm.areaId"
                placeholder="请选择库区"
                clearable
              >
                <el-option
                  v-for="area in filteredAreaOptions"
                  :key="area.id"
                  :label="area.areaName"
                  :value="area.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="商品名称" class="filter-item">
              <el-input
                v-model="historySearchForm.itemName"
                placeholder="请输入商品名称"
                clearable
              />
            </el-form-item>
            <el-form-item label="规格名称" class="filter-item">
              <el-input
                v-model="historySearchForm.skuName"
                placeholder="请输入规格名称"
                clearable
              />
            </el-form-item>
            <el-form-item label="操作单号" class="filter-item">
              <el-input
                v-model="historySearchForm.orderNo"
                placeholder="请输入操作单号"
                clearable
              />
            </el-form-item>
          </div>

          <div class="w-full flex justify-center gap-3">
            <el-button type="primary" @click="handleHistorySearch">
              <el-icon class="mr-1"><Search /></el-icon>
              查询
            </el-button>
            <el-button @click="handleHistoryReset">
              <el-icon class="mr-1"><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </el-form>
      </div>

      <div class="px-4 pb-4">
        <el-table
          v-loading="historyLoading"
          :data="historyList"
          stripe
          class="w-full"
        >
          <el-table-column prop="itemName" label="商品信息" min-width="180" show-overflow-tooltip>
            <template #default="{ row }">
              <div>
                <div class="font-medium text-gray-800">{{ row.itemName }}</div>
                <div class="text-xs text-gray-500">{{ row.skuName }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="warehouseName" label="仓库" width="120" show-overflow-tooltip />
          <el-table-column prop="areaName" label="库区" width="120" show-overflow-tooltip />
          <el-table-column label="操作类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getOrderTypeTagType(row.orderType)" size="small">
                {{ getOrderTypeText(row.orderType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="100" align="center" />
          <el-table-column prop="orderNo" label="单号" width="180" show-overflow-tooltip />
          <el-table-column prop="createTime" label="操作时间" width="180" align="center">
            <template #default="{ row }">
              {{ formatDate(row.createTime, 'YYYY-MM-DD HH:mm') }}
            </template>
          </el-table-column>
          <el-table-column prop="createBy" label="操作人" width="120" show-overflow-tooltip />
          <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
        </el-table>
      </div>

      <div class="pagination-container px-4 pb-4">
        <el-pagination
          v-model:current-page="historyPagination.current"
          v-model:page-size="historyPagination.size"
          :total="historyPagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          small
          @size-change="handleHistorySizeChange"
          @current-change="handleHistoryCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { formatDate } from '@/utils/common'
import type { InventoryHistory, Item, ItemSku } from '@/types/inventory'
import {
  getInventoryHistoryListApi,
  getItemByIdApi,
  getItemSkuByIdApi,
  getWarehouseAreaOptionsApi,
  getWarehouseOptionsApi
} from '@/api/inventory'

const historyLoading = ref(false)
const historyList = ref<InventoryHistory[]>([])
const warehouseOptions = ref<Array<{ id: number; warehouseName: string }>>([])
const areaOptions = ref<Array<{ id: number; areaName: string; warehouseId: number }>>([])
const warehouseNameMap = reactive<Record<number, string>>({})
const areaNameMap = reactive<Record<number, string>>({})
const skuInfoMap = reactive<Record<number, ItemSku>>({})
const itemInfoMap = reactive<Record<number, Item>>({})

const historySearchForm = reactive({
  orderType: undefined as number | undefined,
  warehouseId: undefined as number | undefined,
  areaId: undefined as number | undefined,
  itemName: '',
  skuName: '',
  orderNo: ''
})
const filteredAreaOptions = computed(() => {
  if (!historySearchForm.warehouseId) {
    return areaOptions.value
  }
  return areaOptions.value.filter((area) => area.warehouseId === historySearchForm.warehouseId)
})

watch(
  () => historySearchForm.warehouseId,
  () => {
    historySearchForm.areaId = undefined
  }
)


const historyPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const getOrderTypeTagType = (orderType: number) => {
  switch (orderType) {
    case 1: return 'success'
    case 2: return 'danger'
    case 3: return 'warning'
    case 4: return 'info'
    default: return 'primary'
  }
}

const getOrderTypeText = (orderType: number) => {
  switch (orderType) {
    case 1: return '入库'
    case 2: return '出库'
    case 3: return '移库'
    case 4: return '盘库'
    default: return '未知'
  }
}

const handleHistorySearch = () => {
  historyPagination.current = 1
  loadInventoryHistory()
}

const handleHistoryReset = () => {
  historySearchForm.orderType = undefined
  historySearchForm.warehouseId = undefined
  historySearchForm.areaId = undefined
  historySearchForm.itemName = ''
  historySearchForm.skuName = ''
  historySearchForm.orderNo = ''
  historyPagination.current = 1
  loadInventoryHistory()
}

const handleHistorySizeChange = (size: number) => {
  historyPagination.size = size
  historyPagination.current = 1
  loadInventoryHistory()
}

const handleHistoryCurrentChange = (current: number) => {
  historyPagination.current = current
  loadInventoryHistory()
}

const loadInventoryHistory = async () => {
  historyLoading.value = true
  try {
    const params: Parameters<typeof getInventoryHistoryListApi>[0] = {
      pageNum: historyPagination.current,
      pageSize: historyPagination.size
    }
    if (historySearchForm.orderType !== undefined) {
      params.orderType = historySearchForm.orderType
    }
    if (historySearchForm.warehouseId !== undefined) {
      params.warehouseId = historySearchForm.warehouseId
    }
    if (historySearchForm.areaId !== undefined) {
      params.areaId = historySearchForm.areaId
    }
    if (historySearchForm.itemName) {
      params.itemName = historySearchForm.itemName
    }
    if (historySearchForm.skuName) {
      params.skuName = historySearchForm.skuName
    }
    if (historySearchForm.orderNo) {
      params.orderNo = historySearchForm.orderNo
    }

    const response = await getInventoryHistoryListApi(params)
    const records = response.records || []
    historyList.value = await enrichHistoryRecords(records)
    historyPagination.total = response.total || 0
  } catch (error) {
    console.error('加载库存流水失败:', error)
    ElMessage.error('加载库存流水失败')
  } finally {
    historyLoading.value = false
  }
}

const loadWarehouseOptions = async () => {
  try {
    warehouseOptions.value = await getWarehouseOptionsApi()
    warehouseOptions.value.forEach((warehouse) => {
      if (warehouse.id !== undefined) {
        warehouseNameMap[warehouse.id] = warehouse.warehouseName
      }
    })
  } catch (error) {
    console.error('加载仓库选项失败:', error)
    ElMessage.error('加载仓库选项失败')
  }
}

const loadAreaOptions = async () => {
  try {
    areaOptions.value = await getWarehouseAreaOptionsApi()
    areaOptions.value.forEach((area) => {
      if (area.id !== undefined) {
        areaNameMap[area.id] = area.areaName
      }
    })
  } catch (error) {
    console.error('加载库区选项失败:', error)
    ElMessage.error('加载库区选项失败')
  }
}

const getWarehouseNameById = (warehouseId?: number) => {
  if (!warehouseId) return '-'
  const option = warehouseOptions.value.find((item) => item.id === warehouseId)
  return option?.warehouseName || warehouseNameMap[warehouseId] || '-'
}

const getAreaNameById = (areaId?: number) => {
  if (!areaId) return '-'
  const option = areaOptions.value.find((item) => item.id === areaId)
  return option?.areaName || areaNameMap[areaId] || '-'
}

const ensureSkuInfo = async (skuIds: number[]) => {
  const missingSkuIds = skuIds.filter((id) => !skuInfoMap[id])
  if (!missingSkuIds.length) {
    return
  }
  await Promise.all(
    missingSkuIds.map(async (id) => {
      try {
        const sku = await getItemSkuByIdApi(id)
        if (sku?.id !== undefined) {
          skuInfoMap[sku.id] = sku
        }
      } catch (error) {
        console.error(`获取SKU信息失败: ${id}`, error)
      }
    })
  )
}

const ensureItemInfo = async (itemIds: number[]) => {
  const missingItemIds = itemIds.filter((id) => !itemInfoMap[id])
  if (!missingItemIds.length) {
    return
  }
  await Promise.all(
    missingItemIds.map(async (id) => {
      try {
        const item = await getItemByIdApi(id)
        if (item?.id !== undefined) {
          itemInfoMap[item.id] = item
        }
      } catch (error) {
        console.error(`获取物料信息失败: ${id}`, error)
      }
    })
  )
}

const enrichHistoryRecords = async (records: InventoryHistory[]) => {
  if (!records.length) {
    return []
  }

  const skuIds = Array.from(
    new Set(
      records
        .map((item) => item.skuId)
        .filter((id): id is number => typeof id === 'number')
    )
  )
  await ensureSkuInfo(skuIds)

  const itemIds = Array.from(
    new Set(
      skuIds
        .map((id) => skuInfoMap[id]?.itemId)
        .filter((id): id is number => typeof id === 'number')
    )
  )
  await ensureItemInfo(itemIds)

  return records.map((record) => {
    const skuInfo = typeof record.skuId === 'number' ? skuInfoMap[record.skuId] : undefined
    const itemId = skuInfo?.itemId
    const itemInfo = typeof itemId === 'number' ? itemInfoMap[itemId] : undefined
    return {
      ...record,
      warehouseName: record.warehouseName || getWarehouseNameById(record.warehouseId),
      areaName: record.areaName || getAreaNameById(record.areaId),
      itemName: itemInfo?.itemName || skuInfo?.itemName || record.itemName || '-',
      skuName: record.skuName || skuInfo?.skuName || '-'
    }
  })
}

onMounted(async () => {
  await Promise.all([loadWarehouseOptions(), loadAreaOptions()])
  await loadInventoryHistory()
})
</script>

<style scoped>
.inventory-statistics-container {
  @apply min-h-full bg-gray-50 p-6;
}

.content-card {
  @apply bg-white rounded-lg shadow-sm border border-gray-200;
}

.search-form {
  @apply flex flex-col gap-4 p-6;
}

.filter-row {
  display: flex;
  gap: 16px;
  width: 100%;
  flex-wrap: nowrap;
}

.filter-item {
  flex: 1;
  min-width: 0;
}

.pagination-container {
  @apply flex justify-center;
}

@media (max-width: 1280px) {
  .inventory-statistics-container {
    @apply p-4;
  }
}

@media (max-width: 768px) {
  .inventory-statistics-container {
    @apply p-3;
  }

  .search-form {
    @apply flex-col items-stretch p-4;
  }

  .filter-row {
    flex-wrap: wrap;
  }
}
</style>

