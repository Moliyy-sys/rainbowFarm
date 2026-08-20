<template>
  <div class="inventory-statistics-container">
    <div class="content-card w-full">
      <div class="flex items-center justify-between mb-4 p-4 border-b border-gray-200">
        <h3 class="text-lg font-semibold text-gray-800">库存数量</h3>
      </div>

      <div class="px-4 pb-2">
        <el-form
          :model="inventorySearchForm"
          label-width="80px"
          class="search-form"
        >
          <div class="filter-row">
            <el-form-item label="仓库" class="filter-item">
              <el-select
                v-model="inventorySearchForm.warehouseId"
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
                v-model="inventorySearchForm.areaId"
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
                v-model="inventorySearchForm.itemName"
                placeholder="请输入商品名称"
                clearable
              />
            </el-form-item>

            <el-form-item label="规格名称" class="filter-item">
              <el-input
                v-model="inventorySearchForm.skuName"
                placeholder="请输入规格名称"
                clearable
              />
            </el-form-item>
          </div>

          <div class="w-full flex justify-center gap-3">
            <el-button type="primary" @click="handleInventorySearch">
              <el-icon class="mr-1"><Search /></el-icon>
              查询
            </el-button>
            <el-button @click="handleInventoryReset">
              <el-icon class="mr-1"><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </el-form>
      </div>

      <div class="px-4 pb-4">
        <el-table
          v-loading="loading"
          :data="inventoryList"
          stripe
          class="w-full"
        >
          <el-table-column prop="warehouseName" label="仓库" width="100" show-overflow-tooltip />
          <el-table-column prop="areaName" label="库区" width="100" show-overflow-tooltip />
          <el-table-column prop="itemName" label="商品信息" min-width="150" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="flex items-center">
                <el-avatar :size="32" :src="row.itemImage" class="mr-3">
                  <el-icon><Goods /></el-icon>
                </el-avatar>
                <div>
                  <div class="font-medium text-gray-800">{{ row.itemName }}</div>
                  <div class="text-xs text-gray-500">{{ row.skuName }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="库存" width="100" sortable align="center">
            <template #default="{ row }">
              <span class="font-medium">{{ row.quantity }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="100" show-overflow-tooltip />
        </el-table>
      </div>

      <div class="pagination-container px-4 pb-4">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          small
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { InventoryItem, Item, ItemSku } from '@/types/inventory'
import {
  getInventoryListApi,
  getItemByIdApi,
  getItemSkuByIdApi,
  getWarehouseAreaOptionsApi,
  getWarehouseOptionsApi
} from '@/api/inventory'

const loading = ref(false)
const inventoryList = ref<InventoryItem[]>([])

const warehouseOptions = ref<Array<{ id: number; warehouseName: string }>>([])
const areaOptions = ref<Array<{ id: number; areaName: string; warehouseId: number }>>([])
const warehouseNameMap = reactive<Record<number, string>>({})
const areaNameMap = reactive<Record<number, string>>({})
const skuInfoMap = reactive<Record<number, ItemSku>>({})
const itemInfoMap = reactive<Record<number, Item>>({})

const inventorySearchForm = reactive({
  warehouseId: undefined as number | undefined,
  areaId: undefined as number | undefined,
  itemName: '',
  skuName: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const filteredAreaOptions = computed(() => {
  if (!inventorySearchForm.warehouseId) {
    return areaOptions.value
  }
  return areaOptions.value.filter((area) => area.warehouseId === inventorySearchForm.warehouseId)
})

watch(
  () => inventorySearchForm.warehouseId,
  () => {
    inventorySearchForm.areaId = undefined
  }
)

const handleInventorySearch = () => {
  pagination.current = 1
  loadInventoryList()
}

const handleInventoryReset = () => {
  inventorySearchForm.warehouseId = undefined
  inventorySearchForm.areaId = undefined
  inventorySearchForm.itemName = ''
  inventorySearchForm.skuName = ''
  pagination.current = 1
  loadInventoryList()
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  loadInventoryList()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadInventoryList()
}

const loadInventoryList = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      warehouseId: inventorySearchForm.warehouseId || undefined,
      areaId: inventorySearchForm.areaId || undefined,
      itemName: inventorySearchForm.itemName || undefined,
      skuName: inventorySearchForm.skuName || undefined
    }
    Object.keys(params).forEach((key) => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })

    const response = await getInventoryListApi(params)
    const records = response.records || []
    inventoryList.value = await enrichInventoryRecords(records)
    pagination.total = response.total || 0
  } catch (error) {
    console.error('加载库存列表失败:', error)
    ElMessage.error('加载库存列表失败')
  } finally {
    loading.value = false
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

const enrichInventoryRecords = async (records: InventoryItem[]) => {
  if (!records.length) {
    return []
  }

  const skuIds = Array.from(
    new Set(
      records
        .map((item) => (item as any).skuId)
        .filter((id): id is number => typeof id === 'number')
    )
  )
  await ensureSkuInfo(skuIds)

  const itemIds = Array.from(
    new Set(
      records
        .map((item) => {
          const skuId = (item as any).skuId
          if (typeof (item as any).itemId === 'number') {
            return (item as any).itemId
          }
          if (typeof skuId === 'number' && skuInfoMap[skuId]?.itemId !== undefined) {
            return skuInfoMap[skuId].itemId
          }
          return undefined
        })
        .filter((id): id is number => typeof id === 'number')
    )
  )
  await ensureItemInfo(itemIds)

  return records.map((record) => {
    const skuId = (record as any).skuId
    const itemId = (record as any).itemId ?? (typeof skuId === 'number' ? skuInfoMap[skuId]?.itemId : undefined)
    const skuInfo = typeof skuId === 'number' ? skuInfoMap[skuId] : undefined
    const itemInfo = typeof itemId === 'number' ? itemInfoMap[itemId] : undefined
    return {
      ...record,
      warehouseName: record.warehouseName || getWarehouseNameById(record.warehouseId),
      areaName: record.areaName || getAreaNameById(record.areaId),
      itemName: record.itemName || itemInfo?.itemName || skuInfo?.itemName || '-',
      skuName: record.skuName || skuInfo?.skuName || '-'
    }
  })
}

onMounted(async () => {
  await Promise.all([loadWarehouseOptions(), loadAreaOptions()])
  await loadInventoryList()
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

  .filter-item {
    flex: 1 1 calc(50% - 16px);
  }

  .search-form .el-form-item {
    @apply w-full;
  }
}
</style>

