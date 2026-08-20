<template>
  <el-dialog
    v-model="visible"
    title="出库单详情"
    width="90%"
    :before-close="handleClose"
    destroy-on-close
  >
    <div class="view-container" v-loading="loading">
      <!-- 基本信息 -->
      <el-card class="info-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="title">基本信息</span>
            <el-tag :type="getStatusTagType(orderDetail.shipmentOrderStatus)">
              {{ getStatusText(orderDetail.shipmentOrderStatus) }}
            </el-tag>
          </div>
        </template>
        
        <div class="info-content">
          <el-row :gutter="24">
            <el-col :span="8">
              <div class="info-item">
                <span class="label">出库单号：</span>
                <span class="value">{{ orderDetail.shipmentOrderNo || '-' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">客户：</span>
                <span class="value">{{ getMerchantName(orderDetail.merchantId) }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">仓库：</span>
                <span class="value">{{ getWarehouseName(orderDetail.warehouseId) }}</span>
              </div>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="8">
              <div class="info-item">
                <span class="label">库区：</span>
                <span class="value">{{ getAreaName(orderDetail.areaId) }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">总数量：</span>
                <span class="value quantity">{{ orderDetail.totalQuantity || 0 }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">应收金额：</span>
                <span class="value price">¥{{ (orderDetail.receivableAmount || 0).toFixed(2) }}</span>
              </div>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="8">
              <div class="info-item">
                <span class="label">创建人：</span>
                <span class="value">{{ orderDetail.createBy || '-' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">创建时间：</span>
                <span class="value">{{ formatDateTime(orderDetail.createTime) }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">更新时间：</span>
                <span class="value">{{ formatDateTime(orderDetail.updateTime) }}</span>
              </div>
            </el-col>
          </el-row>
          
          <el-row :gutter="24" v-if="orderDetail.remark">
            <el-col :span="24">
              <div class="info-item">
                <span class="label">备注：</span>
                <span class="value">{{ orderDetail.remark }}</span>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-card>

      <!-- 物料详情 -->
      <el-card class="detail-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="title">物料详情</span>
            <span class="item-count">共 {{ orderDetail.detailList?.length || 0 }} 个物料</span>
          </div>
        </template>
        
        <el-table 
          :data="orderDetail.detailList || []" 
          border
          style="width: 100%"
        >
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="itemName" label="物料名称" width="150">
            <template #default="{ row }">
              <span>{{ row.itemName || getItemName(row.skuId) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="skuName" label="规格名称" width="120">
            <template #default="{ row }">
              <span>{{ row.skuName || getSkuName(row.skuId) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="inboundOrderNo" label="入库单号" width="180">
            <template #default="{ row }">
              <span>{{ row.inboundOrderNo || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="出库数量" width="100">
            <template #default="{ row }">
              <span class="quantity">{{ row.quantity }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="unitPrice" label="单价(元)" width="100">
            <template #default="{ row }">
              <span class="price">¥{{ (row.unitPrice || 0).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额(元)" width="100">
            <template #default="{ row }">
              <span class="price total">¥{{ ((row.quantity || 0) * (row.unitPrice || 0)).toFixed(2) }}</span>
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
          <el-table-column prop="createTime" label="创建时间" width="160">
            <template #default="{ row }">
              <span>{{ formatDateTime(row.createTime) }}</span>
            </template>
          </el-table-column>
        </el-table>

        <!-- 汇总信息 -->
        <div class="summary-info">
          <el-row :gutter="24">
            <el-col :span="8">
              <div class="summary-item">
              <span class="label">物料种类：</span>
              <span class="value">{{ orderDetail.detailList?.length || 0 }} 种</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="summary-item">
                <span class="label">总数量：</span>
                <span class="value quantity">{{ orderDetail.totalQuantity || 0 }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="summary-item">
                <span class="label">总金额：</span>
                <span class="value price total">¥{{ (orderDetail.receivableAmount || 0).toFixed(2) }}</span>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-card>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  getShipmentOrderByIdApi,
  getShipmentOrderDetailListApi,
  getMerchantOptionsApi,
  getWarehouseOptionsApi,
  getWarehouseAreaOptionsApi,
  getItemSkuByIdApi,
  getItemByIdApi,
  getInventoryDetailByIdApi
} from '@/api/inventory'
import type { ShipmentOrder, ShipmentOrderDetail } from '@/types/inventory'

interface Props {
  modelValue: boolean
  orderId?: number
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 弹窗显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 出库单详情数据
const orderDetail = ref<ShipmentOrder>({} as ShipmentOrder)
const loading = ref(false)

// 字典数据
const merchantOptions = ref<Array<{ id: number; merchantName: string }>>([])
const warehouseOptions = ref<Array<{ id: number; warehouseName: string }>>([])
const areaOptions = ref<Array<{ id: number; areaName: string; warehouseId: number }>>([])

// 缓存数据
const skuCache = ref<Record<number, any>>({})
const itemNameCache = ref<Record<number, string>>({})
const inventoryDetailOrderNoMap = ref<Record<number, string>>({})

// 根据 skuId 补全出库明细中的 skuName 和 itemName
const enrichDetailSkuInfo = async (details: ShipmentOrderDetail[]) => {
  if (!Array.isArray(details) || details.length === 0) return

  try {
    // 需要补全的 skuId
    const needSkuIds = Array.from(new Set(
      details
        .filter(d => (!!d.skuId && (!d.skuName || !d.itemName || !d.unit)))
        .map(d => d.skuId)
    ))

    // 拉取缺失的 sku
    const toFetchSkuIds = needSkuIds.filter(id => !skuCache.value[id])
    if (toFetchSkuIds.length > 0) {
      await Promise.all(
        toFetchSkuIds.map(async id => {
          try {
            const sku = await getItemSkuByIdApi(id)
            if (sku) {
              skuCache.value[id] = sku
            }
          } catch (error) {
            console.error(`获取SKU ${id} 信息失败:`, error)
          }
        })
      )
    }

    // 准备需要补全的 itemId，用于查 itemName
    const missingItemIds = Array.from(new Set(
      details
        .map(d => skuCache.value[d.skuId]?.itemId)
        .filter((id): id is number => typeof id === 'number')
        .filter(id => !itemNameCache.value[id])
    ))

    if (missingItemIds.length > 0) {
      await Promise.all(
        missingItemIds.map(async id => {
          try {
            const item = await getItemByIdApi(id)
            if (item?.itemName) {
              itemNameCache.value[id] = item.itemName
            }
          } catch (error) {
            console.error(`获取物料 ${id} 信息失败:`, error)
          }
        })
      )
    }

    // 应用到明细
    details.forEach(d => {
      const sku = skuCache.value[d.skuId]
      if (sku) {
        if (!d.skuName && sku.skuName) d.skuName = sku.skuName
        if (!d.unit && sku.unit) d.unit = sku.unit
        const itemId = sku.itemId
        if (!d.itemName && itemId != null) {
          d.itemName = sku.itemName || itemNameCache.value[itemId] || d.itemName
        }
        // 计算单价
        if (!d.unitPrice && d.amount && d.quantity) {
          d.unitPrice = d.amount / d.quantity
        }
      }
    })
  } catch (e) {
    console.error('补全出库明细规格信息失败:', e)
  }
}

// 补全出库明细的入库单号
const enrichDetailOrderNo = async (details: ShipmentOrderDetail[]) => {
  if (!Array.isArray(details) || details.length === 0) {
    return
  }

  details.forEach(detail => {
    if (typeof detail.inventoryDetailId === 'number' && detail.inboundOrderNo) {
      inventoryDetailOrderNoMap.value[detail.inventoryDetailId] = detail.inboundOrderNo
    }
  })

  const inventoryDetailIds = Array.from(new Set(
    details
      .map(detail => detail.inventoryDetailId)
      .filter((id): id is number => typeof id === 'number')
  ))

  if (inventoryDetailIds.length === 0) {
    return
  }

  const pendingIds = inventoryDetailIds.filter(id => typeof inventoryDetailOrderNoMap.value[id] === 'undefined')
  if (pendingIds.length > 0) {
    await Promise.all(
      pendingIds.map(async id => {
        try {
          const inventoryDetail = await getInventoryDetailByIdApi(id)
          if (inventoryDetail?.id !== undefined) {
            inventoryDetailOrderNoMap.value[id] = inventoryDetail.orderNo || ''
          }
        } catch (error) {
          console.error(`获取库存详情失败，库存详情ID: ${id}`, error)
        }
      })
    )
  }

  details.forEach(detail => {
    const orderNo = inventoryDetailOrderNoMap.value[detail.inventoryDetailId]
    if (orderNo) {
      detail.inboundOrderNo = orderNo
    }
  })
}

// 加载字典数据
const loadDictData = async () => {
  try {
    const [merchants, warehouses, areas] = await Promise.all([
      getMerchantOptionsApi(),
      getWarehouseOptionsApi(),
      getWarehouseAreaOptionsApi()
    ])
    
    merchantOptions.value = merchants
    warehouseOptions.value = warehouses
    areaOptions.value = areas
  } catch (error) {
    console.error('加载字典数据失败:', error)
  }
}

// 获取出库单详情
const fetchOrderDetail = async () => {
  if (!props.orderId) {
    orderDetail.value = {} as ShipmentOrder
    return
  }
  
  loading.value = true
  try {
    const res = await getShipmentOrderByIdApi(props.orderId)
    
    // 如果没有detailList，尝试单独获取
    if (res && !res.detailList) {
      try {
        const detailRes = await getShipmentOrderDetailListApi({ 
          shipmentOrderId: props.orderId,
          pageNum: 1,
          pageSize: 1000
        })
        res.detailList = detailRes.records || []
      } catch (detailError) {
        console.error('获取物料详情失败:', detailError)
      }
    }
    
    // 补全物料信息
    if (res && res.detailList && res.detailList.length > 0) {
      await enrichDetailSkuInfo(res.detailList)
      await enrichDetailOrderNo(res.detailList)
    }
    
    orderDetail.value = res || ({} as ShipmentOrder)
  } catch (error) {
    console.error('获取出库单详情失败:', error)
    ElMessage.error('获取出库单详情失败')
  } finally {
    loading.value = false
  }
}

// 格式化日期时间
const formatDateTime = (dateTime?: string | null) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const parsed = dayjs(dateStr)
  if (!parsed.isValid()) {
    return '-'
  }
  return parsed.format('YYYY-MM-DD')
}

// 获取状态标签类型
const getStatusTagType = (status: number) => {
  const statusMap: Record<number, 'warning' | 'success' | 'info' | 'danger' | 'primary'> = {
    0: 'warning',  // 未出库
    1: 'success'   // 已出库
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '未出库',
    1: '已出库'
  }
  return statusMap[status] || status?.toString() || '-'
}

// 根据ID获取名称
const getMerchantName = (merchantId?: number) => {
  if (!merchantId) return '-'
  const merchant = merchantOptions.value.find(item => item.id === merchantId)
  return merchant?.merchantName || `ID:${merchantId}`
}

const getWarehouseName = (warehouseId?: number) => {
  if (!warehouseId) return '-'
  const warehouse = warehouseOptions.value.find(item => item.id === warehouseId)
  return warehouse?.warehouseName || `ID:${warehouseId}`
}

const getAreaName = (areaId?: number) => {
  if (!areaId) return '-'
  const area = areaOptions.value.find(item => item.id === areaId)
  return area?.areaName || `ID:${areaId}`
}

// 获取物料信息（如果详情中没有的话）
const getItemName = (skuId?: number) => {
  if (!skuId) return '-'
  const sku = skuCache.value[skuId]
  if (!sku) {
    return '-'
  }
  if (sku.itemName) {
    return sku.itemName
  }
  const itemId = sku.itemId
  if (itemId != null) {
    return itemNameCache.value[itemId] || '-'
  }
  return '-'
}

const getSkuName = (skuId?: number) => {
  if (!skuId) return '-'
  const sku = skuCache.value[skuId]
  return sku?.skuName || '-'
}

// 关闭弹窗
const handleClose = () => {
  visible.value = false
}

// 监听弹窗显示状态
watch(visible, (newVal) => {
  if (newVal) {
    loadDictData()
    fetchOrderDetail()
  }
})

// 监听orderId变化
watch(() => props.orderId, () => {
  if (visible.value && props.orderId) {
    fetchOrderDetail()
  }
})
</script>

<style scoped>
.view-container {
  max-height: 70vh;
  overflow-y: auto;
}

.info-card {
  margin-bottom: 20px;
}

.detail-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-weight: bold;
  font-size: 16px;
}

.item-count {
  color: #909399;
  font-size: 14px;
}

.info-content {
  padding: 10px 0;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.info-item .label {
  color: #606266;
  font-weight: 500;
  min-width: 80px;
  margin-right: 10px;
}

.info-item .value {
  color: #303133;
}

.quantity {
  color: #409eff;
  font-weight: bold;
}

.price {
  color: #e6a23c;
  font-weight: bold;
}

.price.total {
  color: #f56c6c;
  font-size: 14px;
}

.summary-info {
  margin-top: 20px;
  padding: 15px 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.summary-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.summary-item:last-child {
  margin-bottom: 0;
}

.summary-item .label {
  color: #606266;
  font-weight: 500;
  margin-right: 10px;
}

.summary-item .value {
  color: #303133;
  font-weight: bold;
}

.dialog-footer {
  text-align: center;
}
</style>
