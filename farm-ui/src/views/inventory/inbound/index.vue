<template>
  <div class="inbound-container">
    
    <div class="content-area">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>入库记录列表</span>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增入库单
            </el-button>
          </div>
        </template>
        
        <div class="search-area">
          <el-form :model="searchForm" inline>
            <el-form-item label="入库单号">
              <el-input 
                v-model="searchForm.receiptOrderNo" 
                placeholder="请输入入库单号"
                clearable
              />
            </el-form-item>
            <el-form-item label="供应商">
              <el-select 
                v-model="searchForm.merchantId" 
                placeholder="请选择供应商" 
                clearable
                filterable
                style="min-width: 220px"
              >
                <el-option 
                  v-for="merchant in merchantOptions" 
                  :key="merchant.value"
                  :label="merchant.label" 
                  :value="merchant.value" 
                />
              </el-select>
            </el-form-item>
            <el-form-item label="入库状态">
              <el-select 
                v-model="searchForm.receiptOrderStatus" 
                placeholder="请选择状态" 
                clearable
                style="min-width: 160px"
              >
                <el-option label="待入库" :value="0" />
                <el-option label="已入库" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="receiptOrderNo" label="入库单号" width="150" />
          <el-table-column label="供应商" width="150">
            <template #default="{ row }">
              {{ row.merchantName || (row.merchantId != null ? merchantMap[row.merchantId] : '') || row.merchantId }}
            </template>
          </el-table-column>
          <el-table-column prop="totalQuantity" label="总数量" width="120">
            <template #default="{ row }">
              <span class="quantity">{{ row.totalQuantity || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="payableAmount" label="总金额(元)" width="120">
            <template #default="{ row }">
              <span class="price total">¥{{ row.payableAmount || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="仓库" width="120">
            <template #default="{ row }">
              {{ row.warehouseName || (row.warehouseId != null ? warehouseMap[row.warehouseId] : '') || row.warehouseId }}
            </template>
          </el-table-column>
          <el-table-column label="库区" width="120">
            <template #default="{ row }">
              {{ row.areaName || (row.areaId != null ? areaMap[row.areaId] : '') || row.areaId }}
            </template>
          </el-table-column>
          <el-table-column prop="receiptOrderStatus" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.receiptOrderStatus)">
                {{ getStatusText(row.receiptOrderStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.createTime, 'YYYY-MM-DD HH:mm:ss') }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="320" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
                <el-button 
                  v-if="row.receiptOrderStatus === 0"
                  type="warning" 
                  size="small" 
                  @click="handleEdit(row)"
                >编辑</el-button>
                <el-button 
                  v-if="row.receiptOrderStatus === 0"
                  type="success" 
                  size="small" 
                  @click="handleComplete(row)"
                >完成入库</el-button>
                <el-button 
                  v-if="row.receiptOrderStatus === 0"
                  type="danger" 
                  size="small" 
                  @click="handleDelete(row)"
                >删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        
        <el-pagination
          v-model:current-page="pagination.pageNo"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </el-card>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogMode === 'add' ? '新增入库单' : '编辑入库单'"
      width="1200px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="供应商" prop="merchantId">
              <el-select v-model="form.merchantId" placeholder="请选择供应商" style="width: 100%">
                <el-option 
                  v-for="merchant in merchantOptions" 
                  :key="merchant.value"
                  :label="merchant.label" 
                  :value="merchant.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="仓库" prop="warehouseId">
              <el-select 
                v-model="form.warehouseId" 
                placeholder="请选择仓库" 
                style="width: 100%"
                @change="handleWarehouseChange"
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
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="库区" prop="areaId">
              <el-select v-model="form.areaId" placeholder="请选择库区" style="width: 100%">
                <el-option 
                  v-for="area in areaOptions" 
                  :key="area.id"
                  :label="area.areaName" 
                  :value="area.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="盘库单">
              <el-select 
                v-model="form.checkOrderId" 
                placeholder="请选择盘库单（可选）" 
                style="width: 100%"
                clearable
                filterable
                @change="handleCheckOrderChange"
              >
                <el-option 
                  v-for="order in checkOrderOptions" 
                  :key="order.id"
                  :label="`${order.checkOrderNo} (${order.checkOrderStatus === 1 ? '已盘库' : '未盘库'})`" 
                  :value="order.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="备注" prop="remark">
              <el-input 
                v-model="form.remark" 
                placeholder="请输入备注信息"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.checkOrderId && form.checkOrderTotal !== undefined">
          <el-col :span="8">
            <el-form-item label="盈亏数">
              <el-input 
                :value="form.checkOrderTotal.toFixed(2)" 
                disabled
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <!-- 入库单详情 -->
      <div class="detail-section">
        <div class="detail-header">
          <h4>入库详情</h4>
          <el-button type="primary" size="small" @click="openSkuDialog">
            <el-icon><Plus /></el-icon>
            添加物料
          </el-button>
        </div>
        
        <el-table :data="form.detailList" border>
          <el-table-column label="序号" width="60" type="index" />
          <el-table-column label="物料规格">
            <template #default="{ row }">
              <span>{{ row.itemName && row.skuName ? (row.itemName + ' ' + row.skuName) : (row.itemName || row.skuName || '-') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="160">
            <template #default="{ row, $index }">
              <el-input-number 
                v-model="row.quantity" 
                :min="0" 
                :precision="2"
                size="small"
                style="width: 100%"
              />
            </template>
          </el-table-column>
          <el-table-column label="单价" width="160">
            <template #default="{ row, $index }">
              <el-input-number 
                v-model="row.amount" 
                :min="0" 
                :precision="2"
                size="small"
                placeholder="单价"
                style="width: 100%"
              />
            </template>
          </el-table-column>
          <el-table-column label="生产日期" width="150">
            <template #default="{ row, $index }">
              <el-date-picker
                v-model="row.productionDate"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DDTHH:mm:ss"
                size="small"
                style="width: 100%"
              />
            </template>
          </el-table-column>
          <el-table-column label="过期日期" width="150">
            <template #default="{ row, $index }">
              <el-date-picker
                v-model="row.expirationDate"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DDTHH:mm:ss"
                size="small"
                style="width: 100%"
              />
            </template>
          </el-table-column>
          <el-table-column label="备注" width="120">
            <template #default="{ row, $index }">
              <el-input v-model="row.remark" placeholder="备注" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row, $index }">
              <el-button 
                type="primary" 
                plain
                size="small" 
                @click="openSkuDialogForRow($index)"
                style="margin-right: 8px;"
              >
                更改规格
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="handleRemoveDetail($index)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="summary-info">
          <span>合计数量：<strong class="quantity">{{ totalQuantity }}</strong></span>
          <span style="margin-left: 20px;">合计金额：<strong class="price total">¥{{ totalAmount.toFixed(2) }}</strong></span>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog 
      v-model="viewDialogVisible" 
      title="入库单详情"
      width="1000px"
    >
      <div v-if="currentOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="入库单号">{{ currentOrder.receiptOrderNo }}</el-descriptions-item>
          <el-descriptions-item label="供应商">
            {{ currentOrder.merchantName || (currentOrder.merchantId != null ? merchantMap[currentOrder.merchantId] : '') || currentOrder.merchantId }}
          </el-descriptions-item>
          <el-descriptions-item label="仓库">
            {{ currentOrder.warehouseName || (currentOrder.warehouseId != null ? warehouseMap[currentOrder.warehouseId] : '') || currentOrder.warehouseId }}
          </el-descriptions-item>
          <el-descriptions-item label="库区">
            {{ currentOrder.areaName || (currentOrder.areaId != null ? areaMap[currentOrder.areaId] : '') || currentOrder.areaId }}
          </el-descriptions-item>
          <el-descriptions-item label="总数量">
            {{ (currentOrder.totalQuantity != null ? currentOrder.totalQuantity : (currentOrder.detailList || []).reduce((s, i) => s + (i.quantity || 0), 0)) }}
          </el-descriptions-item>
          <!-- 不显示总金额 -->
          <el-descriptions-item label="入库状态">
            <el-tag :type="getStatusTagType(currentOrder.receiptOrderStatus)">
              {{ getStatusText(currentOrder.receiptOrderStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentOrder?.createTime ? formatDate(currentOrder.createTime, 'YYYY-MM-DD HH:mm:ss') : '-' }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ currentOrder?.updateTime ? formatDate(currentOrder.updateTime, 'YYYY-MM-DD HH:mm:ss') : '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '无' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 入库明细 -->
        <div style="margin-top: 20px;">
          <h4>入库明细</h4>
          <el-table :data="currentOrder.detailList || []" border>
            <el-table-column label="物料名称">
              <template #default="{ row }">
                {{ row.itemName || '-' }}
              </template>
            </el-table-column>
            <el-table-column label="物料规格">
              <template #default="{ row }">
                {{ row.itemName && row.skuName ? (row.itemName + ' ' + row.skuName) : (row.itemName || row.skuName || row.skuId || '-') }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" />
          <el-table-column prop="amount" label="单价" />
            <el-table-column label="仓库">
              <template #default="{ row }">
                {{ row.warehouseName || (row.warehouseId != null ? warehouseMap[row.warehouseId] : '') || row.warehouseId }}
              </template>
            </el-table-column>
            <el-table-column label="库区">
              <template #default="{ row }">
                {{ row.areaName || (row.areaId != null ? areaMap[row.areaId] : '') || row.areaId }}
              </template>
            </el-table-column>
            <el-table-column prop="productionDate" label="生产日期">
              <template #default="{ row }">
                {{ row.productionDate ? formatDate(row.productionDate, 'YYYY-MM-DD') : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="expirationDate" label="过期日期">
              <template #default="{ row }">
                {{ row.expirationDate ? formatDate(row.expirationDate, 'YYYY-MM-DD') : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" />
          </el-table>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 选择物料对话框 -->
    <el-dialog v-model="skuDialogVisible" title="选择物料规格" width="1000px" @close="editingDetailIndex = null">
      <!-- 筛选区域 -->
      <div class="sku-search-area">
        <el-form :model="skuSearchForm" inline>
          <el-form-item label="物料分类">
            <el-tree-select
              v-model="skuSearchForm.categoryId"
              :data="categoryTreeData"
              :props="{ children: 'children', label: 'categoryName', value: 'id' }"
              placeholder="请选择物料分类"
              clearable
              check-strictly
              filterable
              style="width: 180px"
            />
          </el-form-item>
          <el-form-item label="物料名称">
            <el-input
              v-model="skuSearchForm.itemName"
              placeholder="输入物料名称"
              clearable
              style="width: 200px"
              @keyup.enter="handleSkuSearch"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSkuSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleSkuReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-row :gutter="16">
        <el-col :span="12">
          <div style="margin-bottom: 8px; font-weight: 600;">物料</div>
          <el-table :data="itemList" @row-click="handleSelectItem" style="cursor: pointer;"
                    :row-class-name="({ row }) => (row.id === selectedItemId ? 'is-selected' : '')">
            <el-table-column prop="itemName" label="物料名称" />
            <el-table-column prop="itemCode" label="物料编码" />
            <el-table-column label="分类">
              <template #default="{ row }">
                {{ getCategoryNameById(row.itemCategory) }}
              </template>
            </el-table-column>
            <el-table-column prop="unit" label="单位" width="80" />
          </el-table>
          <el-pagination
            v-model:current-page="itemPagination.pageNo"
            v-model:page-size="itemPagination.pageSize"
            :total="itemPagination.total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="fetchItemList"
            @current-change="fetchItemList"
          />
        </el-col>
        <el-col :span="12">
          <div style="margin-bottom: 8px; font-weight: 600;">物料规格</div>
          <el-table :data="skuList" @row-click="handleSelectSku" style="cursor: pointer;">
            <el-table-column prop="skuName" label="规格名称" />
            <el-table-column prop="skuCode" label="规格编码" />
            <el-table-column prop="unit" label="单位" width="80" />
          </el-table>
          <el-pagination
            v-model:current-page="skuPagination.pageNo"
            v-model:page-size="skuPagination.pageSize"
            :total="skuPagination.total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="fetchSkuList"
            @current-change="fetchSkuList"
          />
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/common'
import { 
  getReceiptOrderListApi, 
  addReceiptOrderApi, 
  updateReceiptOrderApi,
  deleteReceiptOrderApi,
  getMerchantListApi,
  getReceiptOrderByIdApi,
  completeReceiptApi,
  getWarehouseOptionsApi,
  getWarehouseAreaOptionsApi,
  getItemSkuListApi,
  getItemListApi,
  getItemByIdApi,
  getItemSkuByIdApi,
  getItemCategoryTreeApi,
  addReceiptOrderDetailApi,
  updateReceiptOrderDetailApi,
  deleteReceiptOrderDetailApi,
  getCheckOrderListApi,
  getCheckOrderDetailListApi
} from '@/api/inventory'
import type { ReceiptOrder, ReceiptOrderDetail, Merchant, Item, ItemSku, ItemCategory, CheckOrder, CheckOrderDetail } from '@/types/inventory'

// 搜索表单
const searchForm = reactive({
  receiptOrderNo: '',
  merchantId: '',
  receiptOrderStatus: ''
})

// 分页
const pagination = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref<ReceiptOrder[]>([])
const loading = ref(false)

// 供应商列表
const merchantList = ref<Merchant[]>([])
const warehouseOptions = ref<Array<{ id: number; warehouseName: string }>>([])
const areaOptions = ref<Array<{ id: number; areaName: string; warehouseId: number }>>([])
const allAreas = ref<Array<{ id: number; areaName: string; warehouseId: number }>>([])

// 物料/物料规格选择相关
const itemList = ref<Item[]>([])
const itemPagination = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0
})
const selectedItemId = ref<number | null>(null)
const skuList = ref<ItemSku[]>([])
const skuDialogVisible = ref(false)
const skuPagination = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0
})
// 若为修改明细规格，记录正在编辑的行索引；新增时为 null
const editingDetailIndex = ref<number | null>(null)

// 物料筛选表单（先筛物料，再选规格）
const skuSearchForm = reactive({
  categoryId: undefined as number | undefined,
  itemName: ''
})

// 分类树数据
const categoryTreeData = ref<ItemCategory[]>([])

// 根据分类ID获取分类名称
const getCategoryNameById = (categoryId: number | string): string => {
  if (!categoryId) return '-'
  
  const findCategoryName = (categories: ItemCategory[], id: number | string): string => {
    for (const category of categories) {
      if (category.id === Number(id)) {
        return category.categoryName
      }
      if (category.children && category.children.length > 0) {
        const childResult = findCategoryName(category.children, id)
        if (childResult !== '-') {
          return childResult
        }
      }
    }
    return '-'
  }
  
  return findCategoryName(categoryTreeData.value, categoryId)
}

// 物料名称缓存，避免重复请求
const itemNameCache = ref<Record<number, string>>({})
// 规格缓存，避免重复请求
const skuCache = ref<Record<number, ItemSku>>({})

// 计算供应商选项（从往来单位数据中筛选供应商类型）
const merchantOptions = computed(() => {
  return merchantList.value
    .map(merchant => ({
      label: merchant.merchantName,
      value: merchant.id!
    }))
})

const merchantMap = computed<Record<number, string>>(() => {
  const map: Record<number, string> = {}
  merchantList.value.forEach(m => {
    if (m.id != null) map[m.id] = m.merchantName
  })
  return map
})

const warehouseMap = computed<Record<number, string>>(() => {
  const map: Record<number, string> = {}
  warehouseOptions.value.forEach(w => {
    if (w.id != null) map[w.id] = w.warehouseName
  })
  return map
})

const areaMap = computed<Record<number, string>>(() => {
  const map: Record<number, string> = {}
  allAreas.value.forEach(a => {
    if (a.id != null) map[a.id] = a.areaName
  })
  return map
})

// 对话框相关
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit'>('add')
const formRef = ref()

// 查看详情对话框
const viewDialogVisible = ref(false)
const currentOrder = ref<ReceiptOrder | null>(null)

// 表单数据
const form = reactive<Partial<ReceiptOrder> & { detailList: ReceiptOrderDetail[] }>({
  receiptOrderNo: '',
  merchantId: undefined,
  warehouseId: undefined,
  areaId: undefined,
  checkOrderId: undefined,
  checkOrderTotal: undefined,
  remark: '',
  detailList: []
})

// 盘库单选项列表
const checkOrderOptions = ref<CheckOrder[]>([])

// 编辑模式下，缓存原始明细用于对比增改删
const originalDetailList = ref<ReceiptOrderDetail[]>([])

// 表单验证规则
const rules = {
  receiptOrderNo: [
    { required: true, message: '请输入入库单号', trigger: 'blur' }
  ],
  merchantId: [
    { required: true, message: '请选择供应商', trigger: 'change' }
  ],
  warehouseId: [
    { required: true, message: '请选择仓库', trigger: 'change' }
  ],
  areaId: [
    { required: true, message: '请选择库区', trigger: 'change' }
  ]
}

// 计算合计信息
const totalQuantity = computed(() => {
  return form.detailList.reduce((sum, item) => sum + (item.quantity || 0), 0)
})

// 计算总金额（所有明细的单价 * 数量的总和）
const totalAmount = computed(() => {
  return form.detailList.reduce((sum, item) => {
    const quantity = item.quantity || 0
    const amount = item.amount || 0
    return sum + (quantity * amount)
  }, 0)
})

// 获取供应商列表（往来单位中类型为供应商的数据）
const fetchMerchants = async () => {
  try {
    const response = await getMerchantListApi({ 
      pageNum: 1, 
      pageSize: 1000
    })
    if (response?.records) {
      merchantList.value = response.records
    }
  } catch (error) {
    console.error('获取供应商列表失败:', error)
  }
}

// 获取仓库列表
const fetchWarehouses = async () => {
  try {
    warehouseOptions.value = await getWarehouseOptionsApi()
  } catch (error) {
    console.error('获取仓库列表失败:', error)
  }
}

// 获取库区列表
const fetchAreas = async (warehouseId?: number) => {
  try {
    if (warehouseId) {
      areaOptions.value = await getWarehouseAreaOptionsApi(warehouseId)
    } else {
      areaOptions.value = []
    }
  } catch (error) {
    console.error('获取库区列表失败:', error)
    ElMessage.error('获取库区列表失败')
  }
}

// 获取全量库区用于名称映射
const fetchAllAreas = async () => {
  try {
    allAreas.value = await getWarehouseAreaOptionsApi()
  } catch (error) {
    console.error('获取全量库区失败:', error)
  }
}

// 加载盘库单列表
const loadCheckOrderOptions = async () => {
  try {
    const response = await getCheckOrderListApi({
      pageNo: 1,
      pageSize: 1000
    })
    if (response?.records) {
      checkOrderOptions.value = response.records
    }
  } catch (error) {
    console.error('获取盘库单列表失败:', error)
  }
}

// 计算盘库单盈亏数（所有物料盈亏数之和）
// 每个盘库单详情的盈亏数 = checkQuantity - quantity
const calculateDifferenceQuantity = async (checkOrderId: number): Promise<number> => {
  try {
    const detailResponse = await getCheckOrderDetailListApi({
      pageNum: 1,
      pageSize: 1000, // 获取所有详情
      checkOrderId: checkOrderId
    })
    const details = detailResponse.records || []
    // 计算所有物料的盈亏数之和
    // 优先使用 checkQuantity - quantity，如果没有则使用 actualQuantity - systemQuantity
    const totalDifference = details.reduce((sum, detail) => {
      const detailAny = detail as any
      let diff = 0
      // 优先使用 checkQuantity 和 quantity 字段
      if (detailAny.checkQuantity !== undefined && detailAny.quantity !== undefined) {
        diff = Number(detailAny.checkQuantity) - Number(detailAny.quantity)
      } 
      // 如果没有，使用 actualQuantity 和 systemQuantity 字段
      else if (detail.actualQuantity !== undefined && detail.systemQuantity !== undefined) {
        diff = Number(detail.actualQuantity) - Number(detail.systemQuantity)
      }
      // 最后使用 differenceQuantity 字段（作为后备）
      else {
        diff = Number(detail.differenceQuantity ?? 0)
      }
      return sum + (isNaN(diff) ? 0 : diff)
    }, 0)
    return Number(totalDifference.toFixed(2))
  } catch (error) {
    console.error('计算盘库单盈亏数失败:', error)
    return 0
  }
}

// 处理盘库单变更
const handleCheckOrderChange = async (checkOrderId: number | undefined) => {
  if (checkOrderId) {
    const differenceQuantity = await calculateDifferenceQuantity(checkOrderId)
    form.checkOrderTotal = differenceQuantity
  } else {
    form.checkOrderTotal = undefined
  }
}

// 获取物料列表（按分类、名称）
const fetchItemList = async () => {
  try {
    const params = {
      pageNum: itemPagination.pageNo,
      pageSize: itemPagination.pageSize,
      itemCategory: skuSearchForm.categoryId ? String(skuSearchForm.categoryId) : undefined,
      itemName: skuSearchForm.itemName || undefined
    }
    const response = await getItemListApi(params)
    if (response?.records) {
      itemList.value = response.records
      itemPagination.total = response.total || 0
    }
    // 清空已选物料对应的规格列表
    selectedItemId.value = null
    skuList.value = []
    skuPagination.pageNo = 1
    skuPagination.total = 0
  } catch (error) {
    console.error('获取物料列表失败:', error)
  }
}

// 获取某物料下的规格列表
const fetchSkuList = async () => {
  try {
    if (!selectedItemId.value) {
      skuList.value = []
      skuPagination.total = 0
      return
    }
    const params = {
      pageNum: skuPagination.pageNo,
      pageSize: skuPagination.pageSize,
      itemId: selectedItemId.value
    }
    const response = await getItemSkuListApi(params)
    if (response?.records) {
      skuList.value = response.records
      skuPagination.total = response.total || 0
    } else {
      skuList.value = []
      skuPagination.total = 0
    }
  } catch (error) {
    console.error('获取物料规格列表失败:', error)
  }
}

// 根据 itemId 关联 wh_item 表，补全 itemName（对于Item类型，一般已经包含itemName，此函数可简化）
const enrichSkuItemNames = async (list: Item[]) => {
  // Item类型通常已包含itemName，此函数可能不需要额外处理
  // 保留函数结构以兼容现有调用
  return Promise.resolve()
}

// 通过 ItemSku 补全物料名称：先读缓存，再必要时请求接口
const resolveItemNameForSku = async (sku: ItemSku): Promise<string> => {
  // 1) 直接从参数拿
  if (sku?.itemName) return sku.itemName

  const skuId = sku?.id
  if (!skuId) return ''

  // 2) 尝试从 sku 缓存拿 itemId / itemName
  const cachedSku = skuCache.value[skuId]
  if (cachedSku?.itemName) return cachedSku.itemName

  // 3) 若无，尝试拉取 sku 详情放入缓存
  let ensuredSku = cachedSku
  if (!ensuredSku) {
    try {
      const fetched = await getItemSkuByIdApi(skuId)
      if (fetched) {
        skuCache.value[skuId] = fetched
        ensuredSku = fetched
      }
    } catch (e) {
      // ignore
    }
  }

  // 4) 如果 sku 详情里仍无 itemName，则根据 itemId 去查物料名称
  const itemId = ensuredSku?.itemId
  if (itemId != null) {
    // 看看本地缓存
    const cachedName = itemNameCache.value[itemId]
    if (cachedName) return cachedName
    try {
      const item = await getItemByIdApi(itemId)
      if (item?.itemName) {
        itemNameCache.value[itemId] = item.itemName
        return item.itemName
      }
    } catch (e) {
      // ignore
    }
  }

  return ''
}

// 根据 skuId 补全入库明细中的 skuName 和 itemName
const enrichDetailSkuInfo = async (details: ReceiptOrderDetail[]) => {
  if (!Array.isArray(details) || details.length === 0) return

  try {
    // 需要补全的 skuId
    const needSkuIds = Array.from(new Set(
      details
        .filter(d => (!!d.skuId && (!d.skuName || !d.itemName)))
        .map(d => d.skuId)
    ))

    // 拉取缺失的 sku
    const toFetchSkuIds = needSkuIds.filter(id => !skuCache.value[id])
    if (toFetchSkuIds.length > 0) {
      await Promise.all(
        toFetchSkuIds.map(async id => {
          const sku = await getItemSkuByIdApi(id)
          if (sku) {
            skuCache.value[id] = sku
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
          const item = await getItemByIdApi(id)
          if (item?.itemName) {
            itemNameCache.value[id] = item.itemName
          }
        })
      )
    }

    // 应用到明细
    details.forEach(d => {
      const sku = skuCache.value[d.skuId]
      if (sku) {
        if (!d.skuName && sku.skuName) d.skuName = sku.skuName
        const itemId = sku.itemId
        if (!d.itemName && itemId != null) {
          d.itemName = sku.itemName || itemNameCache.value[itemId] || d.itemName
        }
      }
    })
  } catch (e) {
    console.error('补全入库明细规格信息失败:', e)
  }
}

// 仓库变化处理
const handleWarehouseChange = (warehouseId: number) => {
  // 清空库区选择
  form.areaId = undefined
  // 清空库区选项
  areaOptions.value = []
  
  if (warehouseId) {
    // 获取该仓库下的库区列表
    fetchAreas(warehouseId)
  }
}

// 获取状态标签类型
const getStatusTagType = (status: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
  const statusMap: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    0: 'warning',  // 待入库
    1: 'success'   // 已入库
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '待入库',
    1: '已入库'
  }
  return statusMap[status] || status.toString()
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNo,
      pageSize: pagination.pageSize,
      receiptOrderNo: searchForm.receiptOrderNo || undefined,
      receiptOrderStatus: searchForm.receiptOrderStatus !== '' ? Number(searchForm.receiptOrderStatus) : undefined
    }

    const response = await getReceiptOrderListApi(params)
    
    if (response?.records) {
      tableData.value = response.records
      pagination.total = response.total || 0
    } else {
      tableData.value = []
      pagination.total = 0
    }
    
  } catch (error) {
    console.error('获取入库单列表失败:', error)
    ElMessage.error('获取数据失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNo = 1
  fetchData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    receiptOrderNo: '',
    merchantId: '',
    receiptOrderStatus: ''
  })
  handleSearch()
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    receiptOrderNo: '',
    merchantId: undefined,
    warehouseId: undefined,
    areaId: undefined,
    checkOrderId: undefined,
    checkOrderTotal: undefined,
    remark: '',
    detailList: []
  })
  // 清空库区选项
  areaOptions.value = []
}

// 新增
const handleAdd = () => {
  dialogMode.value = 'add'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: ReceiptOrder) => {
  try {
    const orderDetail = await getReceiptOrderByIdApi(row.id!)
    dialogMode.value = 'edit'
    Object.assign(form, {
      ...orderDetail,
      detailList: orderDetail.detailList || []
    })
    // 记录编辑前的原始明细（深拷贝）
    originalDetailList.value = JSON.parse(JSON.stringify(orderDetail.detailList || []))
    // 补全明细中的物料名与规格名，确保“物料名+规格名”能正确显示
    await enrichDetailSkuInfo(form.detailList)
    
    // 加载对应的库区
    if (form.warehouseId) {
      await fetchAreas(form.warehouseId)
    } else {
      areaOptions.value = []
    }
    
    // 如果有关联的盘库单，重新计算盈亏数（确保数据准确性）
    if (form.checkOrderId) {
      await handleCheckOrderChange(form.checkOrderId)
    }
    
    dialogVisible.value = true
  } catch (error) {
    console.error('获取入库单详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}


// 点击物料行，加载该物料的规格
const handleSelectItem = (item: Item) => {
  selectedItemId.value = item.id || null
  skuPagination.pageNo = 1
  fetchSkuList()
}

// 选择物料规格（基于 ItemSku）
const handleSelectSku = async (item: ItemSku) => {
  // 如果是行内修改规格
  if (editingDetailIndex.value != null) {
    const idx = editingDetailIndex.value
    const target = form.detailList[idx]
    if (!target) {
      skuDialogVisible.value = false
      editingDetailIndex.value = null
      return
    }
    // 若选择的规格与其它行重复，提示
    const duplicate = form.detailList.some((d, i) => i !== idx && d.skuId === item.id)
    if (duplicate) {
      ElMessage.warning('该物料已存在于其他明细中')
      return
    }
    // 更新目标行的规格与物料名称；保留数量、单价等其他字段
    target.skuId = item.id!
    target.skuName = item.skuName
    target.itemName = item.itemName || await resolveItemNameForSku(item) || ''
    // 关闭并清理编辑状态
    skuDialogVisible.value = false
    editingDetailIndex.value = null
    return
  }

  // 否则为新增一条明细（不判断是否已存在，允许重复添加）
  const ensuredItemName = item.itemName || await resolveItemNameForSku(item) || ''

  // 新增明细时，明确不设置ID字段，确保是真正的新增
  form.detailList.push({
    skuId: item.id!,
    skuName: item.skuName,
    itemName: ensuredItemName,
    quantity: 1,
    amount: 0,
    warehouseId: form.warehouseId!,
    areaId: form.areaId!,
    productionDate: '',
    expirationDate: '',
    remark: ''
    // 注意：不设置id字段，确保新增时不会有ID冲突
  })

  skuDialogVisible.value = false
}

// 删除物料详情
const handleRemoveDetail = (index: number) => {
  form.detailList.splice(index, 1)
}

// 不再计算总金额，金额即为单价

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (form.detailList.length === 0) {
      ElMessage.error('请添加入库物料')
      return
    }
    
    // 准备详情列表数据（用于后续保存）
    const detailListData = form.detailList.map(item => ({
      ...item,
      warehouseId: form.warehouseId!,
      areaId: form.areaId!
    }))
    
    // 构建主表提交数据（不包含详情列表，详情需要单独保存）
    // receiptOrderNo 由后端自动生成，不需要前端传递
    const { detailList, receiptOrderNo, ...mainFormData } = form
    const submitData = {
      ...mainFormData,
      totalQuantity: totalQuantity.value,
      payableAmount: totalAmount.value, // 计算总金额（所有明细的单价 * 数量的总和）
      receiptOrderStatus: 0, // 待入库状态
      // 如果选择了盘库单，传递盘库单ID和盈亏数
      checkOrderId: form.checkOrderId,
      checkOrderTotal: form.checkOrderTotal
    }
    
    if (dialogMode.value === 'add') {
      // 新增模式：先保存主表，获取返回的入库单号，然后查询ID，最后保存详情
      // 后端返回 BaseResult<String>，响应拦截器会提取 data 字段，所以返回的是入库单号（字符串）
      const receiptOrderNo = await addReceiptOrderApi(submitData)
      
      if (!receiptOrderNo) {
        ElMessage.error('新增入库单成功，但无法获取入库单号，请刷新页面后手动添加详情')
        return
      }
      
      // 通过入库单号查询获取新创建的入库单ID
      const searchResult = await getReceiptOrderListApi({
        pageNum: 1,
        pageSize: 1,
        receiptOrderNo: receiptOrderNo
      })
      
      if (!searchResult.records || searchResult.records.length === 0) {
        ElMessage.error('新增入库单成功，但无法获取入库单ID，请刷新页面后手动添加详情')
        return
      }
      
      const newReceiptOrderId = searchResult.records[0].id!
      
      if (!newReceiptOrderId) {
        ElMessage.error('新增入库单成功，但无法获取入库单ID，请刷新页面后手动添加详情')
        return
      }
      
      // 保存入库单详情
      if (detailListData.length > 0) {
        await Promise.all(
          detailListData.map(d => {
            // 构建新增payload时，明确删除ID字段，避免主键冲突
            const { id, ...rest } = d
            const payload = {
              ...rest,
              receiptOrderId: newReceiptOrderId, // 使用新增后获取的主表ID
              warehouseId: form.warehouseId!,
              areaId: form.areaId!
            }
            return addReceiptOrderDetailApi(payload)
          })
        )
      }
      
      ElMessage.success('新增成功')
    } else {
      await updateReceiptOrderApi(submitData)

      // 同步入库单详情（仅在编辑模式下，后端不做同步时在前端处理）
      const incoming: ReceiptOrderDetail[] = detailListData as ReceiptOrderDetail[]
      const existing: ReceiptOrderDetail[] = originalDetailList.value || []

      // 建立现存明细映射，便于对比
      const existingMap = new Map<number, ReceiptOrderDetail>(
        existing.filter(d => d.id != null).map(d => [d.id as number, d])
      )

      // 判断是否有变更的字段
      const isChanged = (a: ReceiptOrderDetail, b: ReceiptOrderDetail) => {
        const fields: (keyof ReceiptOrderDetail)[] = [
          'skuId', 'quantity', 'amount',
          'warehouseId', 'areaId', 'productionDate', 'expirationDate', 'remark'
        ]
        return fields.some(k => (a as any)[k] !== (b as any)[k])
      }

      // 需要新增的（无 id 或 id 为 undefined/null/0）
      const toAdd = incoming.filter(d => !d.id && d.id !== 0)
      // 需要更新的（有 id 且和原来不同）
      const toUpdate = incoming.filter(d => !!d.id && existingMap.has(d.id as number) && isChanged(d, existingMap.get(d.id as number)!))
      // 需要删除的（原来有，现在没有）
      const toDeleteIds = existing
        .filter(d => d.id != null && !incoming.some(x => x.id === d.id))
        .map(d => d.id!) as number[]

      // 执行新增
      if (toAdd.length > 0) {
        await Promise.all(
          toAdd.map(d => {
            // 构建新增payload时，明确删除ID字段，避免主键冲突
            const { id, ...rest } = d
            const payload = {
              ...rest,
              receiptOrderId: form.id!, // 绑定主表ID
              warehouseId: form.warehouseId!,
              areaId: form.areaId!
            }
            return addReceiptOrderDetailApi(payload)
          })
        )
      }

      // 执行更新
      if (toUpdate.length > 0) {
        await Promise.all(
          toUpdate.map(d => {
            const payload = {
              ...d,
              receiptOrderId: form.id!,
              warehouseId: form.warehouseId!,
              areaId: form.areaId!
            }
            return updateReceiptOrderDetailApi(payload)
          })
        )
      }

      // 执行删除
      if (toDeleteIds.length > 0) {
        await deleteReceiptOrderDetailApi(toDeleteIds.join(','))
      }

      ElMessage.success('更新成功')
    }
    
    dialogVisible.value = false
    fetchData()
    
  } catch (error) {
    if (error !== false) {
      console.error('提交失败:', error)
      ElMessage.error('提交失败')
    }
  }
}

// 查看
const handleView = async (row: ReceiptOrder) => {
  try {
    const response = await getReceiptOrderByIdApi(row.id!)
    // 补充名称字段，避免后端只返回 ID 时显示为空
    const enriched = { ...response }
    if (enriched) {
      if (!enriched.merchantName && enriched.merchantId != null) {
        enriched.merchantName = merchantMap.value[enriched.merchantId] || ''
      }
      if (!enriched.warehouseName && enriched.warehouseId != null) {
        enriched.warehouseName = warehouseMap.value[enriched.warehouseId] || ''
      }
      if (!enriched.areaName && enriched.areaId != null) {
        enriched.areaName = areaMap.value[enriched.areaId] || ''
      }
      if (Array.isArray(enriched.detailList)) {
        // 先映射仓库库区名称
        enriched.detailList = enriched.detailList.map(d => ({
          ...d,
          warehouseName: d.warehouseName || (d.warehouseId != null ? warehouseMap.value[d.warehouseId] : ''),
          areaName: d.areaName || (d.areaId != null ? areaMap.value[d.areaId] : ''),
        }))
        // 再通过 skuId 补全物料名称与规格名称
        await enrichDetailSkuInfo(enriched.detailList)
      }
    }
    currentOrder.value = enriched
    viewDialogVisible.value = true
  } catch (error) {
    console.error('获取入库单详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

// 完成入库
const handleComplete = async (row: ReceiptOrder) => {
  try {
    await ElMessageBox.confirm(
      `确认完成入库单"${row.receiptOrderNo}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await completeReceiptApi(row.id!)
    
    ElMessage.success('完成入库成功')
    fetchData()
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('完成入库失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 删除
const handleDelete = async (row: ReceiptOrder) => {
  try {
    await ElMessageBox.confirm(
      `确认删除入库单"${row.receiptOrderNo}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deleteReceiptOrderApi(row.id!)
    
    ElMessage.success('删除成功')
    fetchData()
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除入库单失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 物料筛选搜索（搜索物料列表）
const handleSkuSearch = () => {
  itemPagination.pageNo = 1
  fetchItemList()
}

// 重置物料筛选条件
const handleSkuReset = () => {
  Object.assign(skuSearchForm, {
    categoryId: undefined,
    itemName: ''
  })
  itemPagination.pageNo = 1
  fetchItemList()
}

// 获取分类树数据
const fetchCategoryTree = async () => {
  try {
    const result = await getItemCategoryTreeApi()
    categoryTreeData.value = result
  } catch (error) {
    console.error('获取分类数据失败:', error)
  }
}

// 打开物料选择对话框
const openSkuDialog = () => {
  skuDialogVisible.value = true
  // 每次打开对话框时重置筛选条件
  Object.assign(skuSearchForm, {
    categoryId: undefined,
    itemName: ''
  })
  itemPagination.pageNo = 1
  fetchItemList()
  // 如果分类数据为空，则获取分类数据
  if (categoryTreeData.value.length === 0) {
    fetchCategoryTree()
  }
}

// 行内“更改规格”打开同一对话框
const openSkuDialogForRow = (index: number) => {
  editingDetailIndex.value = index
  skuDialogVisible.value = true
  Object.assign(skuSearchForm, {
    categoryId: undefined,
    itemName: ''
  })
  itemPagination.pageNo = 1
  fetchItemList()
  if (categoryTreeData.value.length === 0) {
    fetchCategoryTree()
  }
}

onMounted(() => {
  fetchMerchants()
  fetchWarehouses()
  fetchAllAreas()
  fetchData()
  fetchCategoryTree()
  loadCheckOrderOptions()
})
</script>

<style scoped>
.inbound-container {
  padding: 0px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
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

.detail-section {
  margin-top: 20px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.detail-header h4 {
  margin: 0;
  color: #303133;
}

.summary-info {
  margin-top: 10px;
  text-align: right;
  font-size: 14px;
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

.el-pagination {
  margin-top: 20px;
  text-align: right;
}

.sku-search-area {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.action-buttons {
  display: inline-flex;
  gap: 8px;
  flex-wrap: nowrap;
  align-items: center;
  white-space: nowrap;
}
</style>