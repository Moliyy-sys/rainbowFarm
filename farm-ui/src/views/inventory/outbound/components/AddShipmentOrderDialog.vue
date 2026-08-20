<template>
  <el-dialog
    v-model="visible"
    :title="dialogTitle"
    width="80%"
    :before-close="handleClose"
    destroy-on-close
  >
    <div class="add-shipment-form">
      <!-- 基本信息 -->
      <el-card class="form-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
          </div>
        </template>
        
        <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="120px"
          class="shipment-form"
        >
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="客户" prop="merchantId">
                <el-select
                  v-model="formData.merchantId"
                  placeholder="请选择客户"
                  filterable
                  clearable
                  style="width: 100%"
                  :disabled="isEditMode"
                >
                  <el-option
                    v-for="item in merchantOptions"
                    :key="item.id"
                    :label="item.merchantName"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="仓库" prop="warehouseId">
                <el-select
                  v-model="formData.warehouseId"
                  placeholder="请选择仓库"
                  filterable
                  clearable
                  style="width: 100%"
                  @change="handleWarehouseChange"
                  :disabled="isEditMode"
                >
                  <el-option
                    v-for="item in warehouseOptions"
                    :key="item.id"
                    :label="item.warehouseName"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="库区" prop="areaId">
                <el-select
                  v-model="formData.areaId"
                  placeholder="请选择库区"
                  filterable
                  clearable
                  style="width: 100%"
                  :disabled="isEditMode || !formData.warehouseId"
                >
                  <el-option
                    v-for="item in filteredAreaOptions"
                    :key="item.id"
                    :label="item.areaName"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="16">
              <el-form-item label="备注">
                <el-input
                  v-model="formData.remark"
                  placeholder="请输入备注信息"
                  type="textarea"
                  :rows="2"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>

      <!-- 出库物料详情 -->
      <el-card class="form-card" shadow="never" style="margin-top: 20px;">
        <template #header>
          <div class="card-header">
            <span>出库物料详情</span>
            <el-button type="primary" size="small" @click="handleAddItem">
              <el-icon><Plus /></el-icon>
              添加物料
            </el-button>
          </div>
        </template>

        <el-table :data="formData.detailList" border style="width: 100%">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="itemName" label="物料名称" width="150" />
          <el-table-column prop="skuName" label="规格" width="120" />
        <el-table-column prop="inboundOrderNo" label="入库单号" width="180">
            <template #default="{ row }">
              <span>{{ row.inboundOrderNo || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="出库数量" width="120">
            <template #default="{ row, $index }">
              <el-input-number
                v-model="row.quantity"
                :min="0"
                :precision="2"
                size="small"
                style="width: 100%"
                @change="handleQuantityChange($index)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="unitPrice" label="单价(元)" width="120">
            <template #default="{ row }">
              <span class="amount">¥{{ Number(row.unitPrice || 0).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额(元)" width="120">
            <template #default="{ row }">
              <span class="amount">¥{{ row.amount.toFixed(2) }}</span>
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
          <el-table-column prop="remark" label="备注" width="120">
            <template #default="{ row }">
              <el-input
                v-model="row.remark"
                placeholder="备注"
                size="small"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ $index }">
              <el-button
                type="danger"
                size="small"
                @click="handleRemoveItem($index)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 汇总信息 -->
        <div class="summary-info">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="summary-item">
                <span class="label">总数量：</span>
                <span class="value">{{ totalQuantity }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="summary-item">
                <span class="label">总金额：</span>
                <span class="value amount">¥{{ totalAmount.toFixed(2) }}</span>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-card>
    </div>

    <!-- 选择物料弹窗 -->
    <SelectItemDialog
      v-model="selectItemDialogVisible"
      :warehouse-id="formData.warehouseId"
      :area-id="formData.areaId"
      @confirm="handleSelectItems"
    />

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import {
  addShipmentOrderApi,
  addShipmentOrderDetailApi,
  updateShipmentOrderDetailApi,
  deleteShipmentOrderDetailApi,
  getMerchantOptionsApi,
  getWarehouseOptionsApi,
  getWarehouseAreaOptionsApi,
  getShipmentOrderByIdApi,
  getShipmentOrderListApi,
  updateShipmentOrderApi,
  getItemSkuByIdApi,
  getItemByIdApi,
  getInventoryDetailByIdApi
} from '@/api/inventory'
import type { ShipmentOrder, ShipmentOrderDetail, ItemSku, Item } from '@/types/inventory'
import SelectItemDialog from './SelectItemDialog.vue'

interface Props {
  modelValue: boolean
  mode?: 'create' | 'edit'
  orderId?: number
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}

const props = withDefaults(defineProps<Props>(), {
  mode: 'create',
  orderId: undefined
})
const emit = defineEmits<Emits>()

// 弹窗显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const isEditMode = computed(() => props.mode === 'edit')

const dialogTitle = computed(() => (isEditMode.value ? '编辑出库单' : '新增出库单'))

// 表单引用
const formRef = ref<FormInstance>()

// 表单数据
const formData = reactive<Partial<ShipmentOrder> & { detailList: ShipmentOrderDetail[] }>({
  id: undefined,
  shipmentOrderNo: '系统自动生成',
  merchantId: undefined,
  warehouseId: undefined,
  areaId: undefined,
  remark: '',
  shipmentOrderStatus: 0,
  detailList: []
})

// 表单验证规则
const formRules: FormRules = {
  merchantId: [
    { required: true, message: '请选择客户', trigger: 'change' }
  ],
  warehouseId: [
    { required: true, message: '请选择仓库', trigger: 'change' }
  ],
  areaId: [
    { required: true, message: '请选择库区', trigger: 'change' }
  ]
}

// 字典数据
const merchantOptions = ref<Array<{ id: number; merchantName: string }>>([])
const warehouseOptions = ref<Array<{ id: number; warehouseName: string }>>([])
const areaOptions = ref<Array<{ id: number; areaName: string; warehouseId: number }>>([])

// 过滤后的库区选项
const filteredAreaOptions = computed(() => {
  if (!formData.warehouseId) return []
  return areaOptions.value.filter(item => item.warehouseId === formData.warehouseId)
})

// 提交状态
const submitting = ref(false)

// 选择物料弹窗
const selectItemDialogVisible = ref(false)

// 原始明细列表（用于编辑时比较变更）
const originalDetailList = ref<ShipmentOrderDetail[]>([])

// 计算总数量和总金额
const totalQuantity = computed(() => {
  return formData.detailList.reduce((sum, item) => sum + (item.quantity || 0), 0)
})

const totalAmount = computed(() => {
  return formData.detailList.reduce((sum, item) => sum + (item.amount || 0), 0)
})

/**
 * 格式化日期字符串
 * @param date 日期字符串
 * @returns 格式化后的日期文本
 */
const formatDate = (date?: string) => {
  if (!date) {
    return '-'
  }
  const parsed = dayjs(date)
  if (!parsed.isValid()) {
    return '-'
  }
  return parsed.format('YYYY-MM-DD')
}

// SKU信息缓存
const skuInfoMap = reactive<Record<number, ItemSku>>({})
// 物料信息缓存
const itemInfoMap = reactive<Record<number, Item>>({})
// 库存详情入库单号缓存
const inventoryDetailOrderNoMap = reactive<Record<number, string>>({})

/**
 * 补全出库明细对应的入库单号
 * @param details 出库明细集合
 */
const enrichDetailOrderInfo = async (details: ShipmentOrderDetail[]) => {
  if (!details.length) {
    return
  }

  const inventoryDetailIds = Array.from(
    new Set(
      details
        .map(detail => detail.inventoryDetailId)
        .filter((id): id is number => typeof id === 'number')
    )
  )

  const pendingIds = inventoryDetailIds.filter(id => typeof inventoryDetailOrderNoMap[id] === 'undefined')
  if (pendingIds.length > 0) {
    await Promise.all(
      pendingIds.map(async id => {
        try {
          const detail = await getInventoryDetailByIdApi(id)
          if (detail?.id !== undefined) {
            inventoryDetailOrderNoMap[id] = detail.orderNo || ''
          }
        } catch (error) {
          console.error(`获取库存详情失败，库存详情ID: ${id}`, error)
        }
      })
    )
  }

  details.forEach(detail => {
    const orderNo = inventoryDetailOrderNoMap[detail.inventoryDetailId]
    if (orderNo) {
      detail.inboundOrderNo = orderNo
    }
  })
}

/**
 * 根据出库明细补全展示所需的物料信息
 * @param details 出库明细集合
 */
const enrichDetailDisplayInfo = async (details: ShipmentOrderDetail[]) => {
  if (!details.length) {
    return
  }

  const skuIds = Array.from(
    new Set(
      details
        .filter(item => item.skuId && (!item.itemName || !item.skuName || !item.unit))
        .map(item => item.skuId)
    )
  )

  if (skuIds.length > 0) {
    const pendingSkuIds = skuIds.filter(id => !skuInfoMap[id])
    if (pendingSkuIds.length > 0) {
      await Promise.all(
        pendingSkuIds.map(async id => {
          try {
            const sku = await getItemSkuByIdApi(id)
            if (sku?.id !== undefined) {
              skuInfoMap[sku.id] = sku
            }
          } catch (error) {
            console.error(`获取SKU信息失败，SKU ID: ${id}`, error)
          }
        })
      )
    }

    const itemIds = Array.from(
      new Set(
        skuIds
          .map(id => skuInfoMap[id]?.itemId)
          .filter((id): id is number => typeof id === 'number')
      )
    )

    const pendingItemIds = itemIds.filter(id => !itemInfoMap[id])
    if (pendingItemIds.length > 0) {
      await Promise.all(
        pendingItemIds.map(async id => {
          try {
            const item = await getItemByIdApi(id)
            if (item?.id !== undefined) {
              itemInfoMap[item.id] = item
            }
          } catch (error) {
            console.error(`获取物料信息失败，物料ID: ${id}`, error)
          }
        })
      )
    }
  }

  details.forEach(detail => {
    if (!detail.skuId) {
      return
    }
    const sku = skuInfoMap[detail.skuId]
    if (sku) {
      if (!detail.skuName && sku.skuName) {
        detail.skuName = sku.skuName
      }
      if (!detail.unit && sku.unit) {
        detail.unit = sku.unit
      }
      const itemId = sku.itemId
      if (!detail.itemName && itemId !== undefined) {
        const cacheItem = itemInfoMap[itemId]
        if (sku.itemName) {
          detail.itemName = sku.itemName
        } else if (cacheItem?.itemName) {
          detail.itemName = cacheItem.itemName
        }
      }
    }

  })
}

/**
 * 根据出库单号获取出库单ID
 * @param shipmentOrderNo 出库单号
 * @returns 出库单ID
 */
const fetchShipmentOrderIdByNo = async (shipmentOrderNo: string): Promise<number> => {
  const result = await getShipmentOrderListApi({
    pageNum: 1,
    pageSize: 1,
    shipmentOrderNo
  })
  const targetOrder = result?.records?.[0]
  if (!targetOrder?.id) {
    throw new Error('未获取到出库单ID')
  }
  return targetOrder.id
}

/**
 * 保存出库单详情
 * @param shipmentOrderId 出库单ID
 */
const saveShipmentOrderDetails = async (shipmentOrderId: number) => {
  const detailPayloads = formData.detailList.map(detail => {
    const {
      itemName,
      skuName,
      unit,
      warehouseName,
      areaName,
      unitPrice,
      inboundOrderNo,
      ...payload
    } = detail
    return {
      ...payload,
      shipmentOrderId,
      amount: Number((detail.amount || 0).toFixed(2))
    }
  })
  await Promise.all(detailPayloads.map(detail => addShipmentOrderDetailApi(detail)))
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

// 加载出库单详情
const loadOrderDetail = async (orderId: number) => {
  try {
    const order = await getShipmentOrderByIdApi(orderId)
    if (!order) {
      ElMessage.error('未获取到出库单信息')
      return
    }

    const detailList = (order.detailList || []).map((detail) => {
      const unitPrice = detail.unitPrice !== undefined
        ? detail.unitPrice
        : (detail.quantity ? Number(((detail.amount || 0) / detail.quantity).toFixed(2)) : 0)
      const amount = detail.amount !== undefined
        ? detail.amount
        : Number((unitPrice * (detail.quantity || 0)).toFixed(2))

      return {
        id: detail.id,
        shipmentOrderId: detail.shipmentOrderId,
        skuId: detail.skuId,
        quantity: detail.quantity,
        amount,
        warehouseId: detail.warehouseId,
        areaId: detail.areaId,
        productionDate: detail.productionDate,
        expirationDate: detail.expirationDate,
        inventoryDetailId: detail.inventoryDetailId,
        remark: detail.remark || '',
        itemName: detail.itemName,
        skuName: detail.skuName,
        unit: detail.unit,
        unitPrice
      } as ShipmentOrderDetail
    })

    await enrichDetailDisplayInfo(detailList)
    await enrichDetailOrderInfo(detailList)

    // 保存原始明细列表（深拷贝）
    originalDetailList.value = detailList.map(item => ({ ...item }))

    Object.assign(formData, {
      id: order.id,
      shipmentOrderNo: order.shipmentOrderNo,
      merchantId: order.merchantId,
      warehouseId: order.warehouseId,
      areaId: order.areaId,
      remark: order.remark || '',
      shipmentOrderStatus: order.shipmentOrderStatus,
      detailList
    })
  } catch (error) {
    console.error('加载出库单详情失败:', error)
    ElMessage.error('加载出库单失败')
  }
}

// 仓库变更处理
const handleWarehouseChange = () => {
  formData.areaId = undefined
}

// 添加物料
const handleAddItem = () => {
  if (!formData.warehouseId) {
    ElMessage.warning('请先选择仓库')
    return
  }
  if (!formData.areaId) {
    ElMessage.warning('请先选择库区')
    return
  }
  selectItemDialogVisible.value = true
}

// 选择物料确认
const handleSelectItems = async (items: any[]) => {
  items.forEach(item => {
    if (typeof item.inventoryDetailId === 'number' && item.orderNo) {
      inventoryDetailOrderNoMap[item.inventoryDetailId] = item.orderNo
    }
    // 无论是否重复，均新增一条明细记录
    formData.detailList.push({
      skuId: item.skuId,
      quantity: item.quantity,
      amount: 0,
      unitPrice: item.unitPrice || 0,
      warehouseId: formData.warehouseId!,
      areaId: formData.areaId!,
      inventoryDetailId: item.inventoryDetailId,
      productionDate: item.productionDate,
      expirationDate: item.expirationDate,
      remark: '',
      // 显示信息
      itemName: item.itemName,
      skuName: item.skuName,
      unit: item.unit,
      inboundOrderNo: item.orderNo
    })

    // 计算金额
    const newIndex = formData.detailList.length - 1
    handleQuantityChange(newIndex)
  })

  await enrichDetailOrderInfo(formData.detailList)
}

// 移除物料
const handleRemoveItem = (index: number) => {
  formData.detailList.splice(index, 1)
}

// 数量变更处理
const handleQuantityChange = (index: number) => {
  const item = formData.detailList[index]
  item.amount = (item.quantity || 0) * (item.unitPrice || 0)
}

// 关闭弹窗
const handleClose = () => {
  visible.value = false
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (formData.detailList.length === 0) {
      ElMessage.warning('请至少添加一个物料')
      return
    }
    
    // 验证物料详情
    for (let i = 0; i < formData.detailList.length; i++) {
      const item = formData.detailList[i]
      if (!item.quantity || item.quantity <= 0) {
        ElMessage.warning(`第${i + 1}行物料数量必须大于0`)
        return
      }
      if (!item.unitPrice || item.unitPrice <= 0) {
        ElMessage.warning(`第${i + 1}行物料单价必须大于0`)
        return
      }
    }
    
    submitting.value = true
    
    formData.detailList.forEach(item => {
      const quantity = item.quantity || 0
      const unitPrice = item.unitPrice || 0
      item.amount = Number((quantity * unitPrice).toFixed(2))
    })

    const submitData = {
      ...formData,
      totalQuantity: totalQuantity.value,
      receivableAmount: totalAmount.value,
      detailList: formData.detailList.map(item => ({ ...item }))
    } as Partial<ShipmentOrder>

    if (isEditMode.value) {
      // 编辑模式：先更新出库单主表
      await updateShipmentOrderApi(submitData)
      
      // 处理明细变更
      const shipmentOrderId = formData.id!
      const originalDetails = originalDetailList.value
      const currentDetails = formData.detailList
      
      // 找出需要删除的明细（原始有但当前没有的）
      const originalDetailIds = new Set(originalDetails.map(d => d.id).filter((id): id is number => typeof id === 'number'))
      const currentDetailIds = new Set(currentDetails.map(d => d.id).filter((id): id is number => typeof id === 'number'))
      const toDeleteIds = Array.from(originalDetailIds).filter(id => !currentDetailIds.has(id))
      
      // 找出需要新增的明细（当前有但没有 id 的）
      const toAddDetails = currentDetails.filter(d => !d.id)
      
      // 找出需要更新的明细（有 id 且内容有变化的）
      const toUpdateDetails: ShipmentOrderDetail[] = []
      currentDetails.forEach(current => {
        if (current.id) {
          const original = originalDetails.find(o => o.id === current.id)
          if (original) {
            // 比较关键字段是否有变化
            if (
              original.skuId !== current.skuId ||
              original.quantity !== current.quantity ||
              original.unitPrice !== current.unitPrice ||
              original.amount !== current.amount ||
              original.warehouseId !== current.warehouseId ||
              original.areaId !== current.areaId ||
              original.productionDate !== current.productionDate ||
              original.expirationDate !== current.expirationDate ||
              original.inventoryDetailId !== current.inventoryDetailId ||
              original.remark !== current.remark
            ) {
              toUpdateDetails.push(current)
            }
          }
        }
      })
      
      // 执行删除操作
      if (toDeleteIds.length > 0) {
        await deleteShipmentOrderDetailApi(toDeleteIds.join(','))
      }
      
      // 执行新增操作
      if (toAddDetails.length > 0) {
        const detailPayloads = toAddDetails.map(detail => {
          const {
            itemName,
            skuName,
            unit,
            warehouseName,
            areaName,
            unitPrice,
            inboundOrderNo,
            ...payload
          } = detail
          return {
            ...payload,
            shipmentOrderId,
            amount: Number((detail.amount || 0).toFixed(2))
          }
        })
        await Promise.all(detailPayloads.map(detail => addShipmentOrderDetailApi(detail)))
      }
      
      // 执行更新操作
      if (toUpdateDetails.length > 0) {
        const updatePayloads = toUpdateDetails.map(detail => {
          const {
            itemName,
            skuName,
            unit,
            warehouseName,
            areaName,
            unitPrice,
            inboundOrderNo,
            ...payload
          } = detail
          return {
            ...payload,
            shipmentOrderId,
            amount: Number((detail.amount || 0).toFixed(2))
          }
        })
        await Promise.all(updatePayloads.map(detail => updateShipmentOrderDetailApi(detail)))
      }
      
      ElMessage.success('出库单更新成功')
    } else {
      submitData.shipmentOrderStatus = 0
      const shipmentOrderNo = await addShipmentOrderApi(submitData)
      const shipmentOrderId = await fetchShipmentOrderIdByNo(shipmentOrderNo)
      await saveShipmentOrderDetails(shipmentOrderId)
      ElMessage.success('出库单创建成功')
    }

    emit('success')
    
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(formData, {
    id: undefined,
    shipmentOrderNo: '系统自动生成',
    merchantId: undefined,
    warehouseId: undefined,
    areaId: undefined,
    remark: '',
    shipmentOrderStatus: 0,
    detailList: []
  })
  originalDetailList.value = []
  selectItemDialogVisible.value = false
}

// 监听弹窗显示状态
watch(visible, async (newVal) => {
  if (newVal) {
    resetForm()
    await loadDictData()
    if (isEditMode.value && props.orderId) {
      await loadOrderDetail(props.orderId)
    }
  } else {
    resetForm()
  }
})
</script>

<style scoped>
.add-shipment-form {
  max-height: 70vh;
  overflow-y: auto;
}

.form-card {
  border: 1px solid #e4e7ed;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.shipment-form {
  padding: 0 20px;
}

.summary-info {
  margin-top: 20px;
  padding: 15px;
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
  font-weight: bold;
  color: #606266;
  margin-right: 8px;
}

.summary-item .value {
  color: #303133;
  font-weight: bold;
}

.summary-item .value.amount {
  color: #f56c6c;
  font-size: 16px;
}

.amount {
  color: #e6a23c;
  font-weight: bold;
}

.dialog-footer {
  text-align: right;
}
</style>
