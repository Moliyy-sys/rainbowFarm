<template>
  <div class="inventory-statistics-container">
    <div class="content-card w-full">
      <div class="p-4 border-b border-gray-200">
        <h3 class="text-lg font-semibold text-gray-800">库存详情</h3>
      </div>

      <div class="px-4 pb-2">
        <el-form
          :model="inventoryDetailSearchForm"
          label-width="80px"
          class="detail-search-form"
        >
          <div class="w-full grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-3">
            <el-form-item label="仓库">
              <el-select
                v-model="inventoryDetailSearchForm.warehouseId"
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
            <el-form-item label="库区">
              <el-select
                v-model="inventoryDetailSearchForm.areaId"
                placeholder="请选择库区"
                clearable
              >
                <el-option
                  v-for="area in inventoryDetailFilteredAreaOptions"
                  :key="area.id"
                  :label="area.areaName"
                  :value="area.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="入库单号">
              <el-input
                v-model="inventoryDetailSearchForm.orderNo"
                placeholder="请输入入库单号"
                clearable
              />
            </el-form-item>
            <el-form-item label="商品名称">
              <el-input
                v-model="inventoryDetailSearchForm.itemName"
                placeholder="请输入商品名称"
                clearable
              />
            </el-form-item>
            <el-form-item label="规格名称">
              <el-input
                v-model="inventoryDetailSearchForm.skuName"
                placeholder="请输入规格名称"
                clearable
              />
            </el-form-item>
            <el-form-item label="入库日期">
              <el-date-picker
                v-model="inventoryDetailSearchForm.receiptDate"
                type="date"
                placeholder="请选择入库日期"
                value-format="YYYY-MM-DD"
                clearable
                style="width: 100%"
              />
            </el-form-item>
          </div>

          <div class="flex justify-center gap-4 mt-4">
            <el-button type="primary" @click="handleInventoryDetailSearch">
              <el-icon class="mr-1"><Search /></el-icon>
              查询
            </el-button>
            <el-button @click="handleInventoryDetailReset">
              <el-icon class="mr-1"><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </el-form>
      </div>

      <div class="px-4 pb-4">
        <el-table
          v-loading="inventoryDetailLoading"
          :data="inventoryDetailList"
          stripe
          class="w-full"
        >
          <el-table-column prop="warehouseName" label="仓库" width="110" show-overflow-tooltip />
          <el-table-column prop="areaName" label="库区" width="110" show-overflow-tooltip />
          <el-table-column label="商品信息" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="flex items-center">
                <el-avatar :size="32" :src="row.itemImage" class="mr-3">
                  <el-icon><Goods /></el-icon>
                </el-avatar>
                <div>
                  <div class="font-medium text-gray-800">{{ row.itemName || '-' }}</div>
                  <div class="text-xs text-gray-500">{{ row.skuName || '-' }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="orderNo" label="入库单号" min-width="140" show-overflow-tooltip />
          <el-table-column prop="quantity" label="入库数量" width="110" align="center" />
          <el-table-column prop="remainQuantity" label="剩余数量" width="110" align="center" />
          <el-table-column label="生产日期" width="140" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.productionDate ? formatDate(row.productionDate, 'YYYY-MM-DD') : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="过期时间" width="140" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.expirationDate ? formatDate(row.expirationDate, 'YYYY-MM-DD') : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
          <el-table-column label="入库日期" width="160" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.createTime ? formatDate(row.createTime, 'YYYY-MM-DD HH:mm') : '-' }}
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-container px-4 pb-4">
        <el-pagination
          v-model:current-page="inventoryDetailPagination.current"
          v-model:page-size="inventoryDetailPagination.size"
          :total="inventoryDetailPagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          small
          @size-change="handleInventoryDetailSizeChange"
          @current-change="handleInventoryDetailCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { formatDate } from '@/utils/common'
import type { InventoryDetail, Item, ItemSku } from '@/types/inventory'
import {
  getInventoryDetailListApi,
  getItemByIdApi,
  getItemSkuByIdApi,
  getWarehouseAreaOptionsApi,
  getWarehouseOptionsApi
} from '@/api/inventory'

const inventoryDetailList = ref<InventoryDetail[]>([])
const inventoryDetailLoading = ref(false)

const warehouseOptions = ref<Array<{ id: number; warehouseName: string }>>([])
const areaOptions = ref<Array<{ id: number; areaName: string; warehouseId: number }>>([])
const warehouseNameMap = reactive<Record<number, string>>({})
const areaNameMap = reactive<Record<number, string>>({})
const skuInfoMap = reactive<Record<number, ItemSku>>({})
const itemInfoMap = reactive<Record<number, Item>>({})

const inventoryDetailSearchForm = reactive({
  warehouseId: undefined as number | undefined,
  areaId: undefined as number | undefined,
  orderNo: '',
  itemName: '',
  skuName: '',
  receiptDate: ''
})

const inventoryDetailPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const inventoryDetailFilteredAreaOptions = computed(() => {
  if (!inventoryDetailSearchForm.warehouseId) {
    return areaOptions.value
  }
  return areaOptions.value.filter((area) => area.warehouseId === inventoryDetailSearchForm.warehouseId)
})

watch(
  () => inventoryDetailSearchForm.warehouseId,
  () => {
    inventoryDetailSearchForm.areaId = undefined
  }
)

const handleInventoryDetailSearch = () => {
  inventoryDetailPagination.current = 1
  loadInventoryDetailList()
}

const handleInventoryDetailReset = () => {
  inventoryDetailSearchForm.warehouseId = undefined
  inventoryDetailSearchForm.areaId = undefined
  inventoryDetailSearchForm.orderNo = ''
  inventoryDetailSearchForm.itemName = ''
  inventoryDetailSearchForm.skuName = ''
  inventoryDetailSearchForm.receiptDate = ''
  inventoryDetailPagination.current = 1
  loadInventoryDetailList()
}

const handleInventoryDetailSizeChange = (size: number) => {
  inventoryDetailPagination.size = size
  inventoryDetailPagination.current = 1
  loadInventoryDetailList()
}

const handleInventoryDetailCurrentChange = (current: number) => {
  inventoryDetailPagination.current = current
  loadInventoryDetailList()
}

const loadInventoryDetailList = async () => {
  inventoryDetailLoading.value = true
  try {
    const params: Record<string, any> = {
      pageNum: inventoryDetailPagination.current,
      pageSize: inventoryDetailPagination.size,
      warehouseId: inventoryDetailSearchForm.warehouseId || undefined,
      areaId: inventoryDetailSearchForm.areaId || undefined,
      orderNo: inventoryDetailSearchForm.orderNo || undefined,
      itemName: inventoryDetailSearchForm.itemName || undefined,
      skuName: inventoryDetailSearchForm.skuName || undefined,
      receiptDate: inventoryDetailSearchForm.receiptDate || undefined
    }

    Object.keys(params).forEach((key) => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })

    const response = await getInventoryDetailListApi(params)
    const records = response.records || []
    inventoryDetailList.value = await enrichInventoryDetailRecords(records)
    inventoryDetailPagination.total = response.total || 0
  } catch (error) {
    console.error('加载库存详情失败:', error)
    ElMessage.error('加载库存详情失败')
  } finally {
    inventoryDetailLoading.value = false
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
  if (!warehouseId) {
    return '-'
  }
  const option = warehouseOptions.value.find((item) => item.id === warehouseId)
  return option?.warehouseName || warehouseNameMap[warehouseId] || '-'
}

const getAreaNameById = (areaId?: number) => {
  if (!areaId) {
    return '-'
  }
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

const enrichInventoryDetailRecords = async (records: InventoryDetail[]) => {
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
      records
        .map((item) => {
          if (typeof item.itemId === 'number') {
            return item.itemId
          }
          const skuInfo = skuInfoMap[item.skuId]
          return skuInfo?.itemId
        })
        .filter((id): id is number => typeof id === 'number')
    )
  )
  await ensureItemInfo(itemIds)

  return records.map((record) => {
    const skuInfo = skuInfoMap[record.skuId]
    const itemInfo = typeof record.itemId === 'number'
      ? itemInfoMap[record.itemId]
      : (skuInfo?.itemId ? itemInfoMap[skuInfo.itemId] : undefined)

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
  await loadInventoryDetailList()
})
</script>

<style scoped>
.inventory-statistics-container {
  @apply min-h-full bg-gray-50 p-6;
}

.content-card {
  @apply bg-white rounded-lg shadow-sm border border-gray-200;
}

.detail-search-form {
  @apply flex flex-col gap-4 p-6;
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
}
</style>

