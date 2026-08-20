<template>
  <div class="warehouse-container">
    <!-- 搜索和操作区域 -->
    <div class="content-card mb-6">
      <el-form
        :model="searchForm"
        :inline="true"
        class="search-form"
      >
        <el-form-item label="仓库名：">
          <el-input
            v-model="searchForm.warehouseName"
            placeholder="请输入仓库名称"
            style="width: 200px"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="仓库编号：">
          <el-input
            v-model="searchForm.warehouseCode"
            placeholder="请输入仓库编号"
            style="width: 200px"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            查询
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item class="ml-auto">
          <el-button type="primary" @click="handleAdd">
            <el-icon class="mr-1"><Plus /></el-icon>
            新增仓库
          </el-button>
          <el-button type="success" @click="handleBatchImport">
            <el-icon class="mr-1"><Upload /></el-icon>
            批量导入
          </el-button>
          <el-button type="info" @click="handleExport">
            <el-icon class="mr-1"><Download /></el-icon>
            导出
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 仓库列表 -->
    <div class="content-card table-container">
      <div class="table-wrapper">
        <el-table
          v-loading="loading"
          :data="warehouseList"
          stripe
          @selection-change="handleSelectionChange"
        >
        <el-table-column type="selection" width="50" />
        
        <el-table-column prop="warehouseCode" label="仓库编码" width="140" />
        
        <el-table-column prop="warehouseName" label="仓库名称" min-width="180">
          <template #default="{ row }">
            <div class="flex items-center">
              <el-icon class="text-blue-500 mr-2"><OfficeBuilding /></el-icon>
              <div>
                <div class="font-medium text-gray-800">{{ row.warehouseName }}</div>
                <div class="text-xs text-gray-500">{{ row.warehouseCode }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="orderNum" label="排序号" width="100" />

        <el-table-column prop="remark" label="备注" min-width="120">
          <template #default="{ row }">
            <span class="text-gray-600">{{ row.remark || '无' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="flex flex-wrap gap-1">
              <el-button type="text" size="small" @click="handleView(row)">
                查看
              </el-button>
              <el-button type="text" size="small" @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button
                type="text"
                size="small"
                class="text-red-500"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="仓库编码" prop="warehouseCode">
              <el-input
                v-model="form.warehouseCode"
                placeholder="请输入仓库编码"
                :disabled="isEdit"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库名称" prop="warehouseName">
              <el-input
                v-model="form.warehouseName"
                placeholder="请输入仓库名称"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="排序号">
          <el-input-number
            v-model="form.orderNum"
            :min="0"
            placeholder="请输入排序号"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 仓库详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="仓库详情"
      width="800px"
    >
      <div v-if="currentWarehouse" class="warehouse-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="仓库编码">
            {{ currentWarehouse.warehouseCode }}
          </el-descriptions-item>
          <el-descriptions-item label="仓库名称">
            {{ currentWarehouse.warehouseName }}
          </el-descriptions-item>
          <el-descriptions-item label="排序号">
            {{ currentWarehouse.orderNum }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDate(currentWarehouse.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            {{ currentWarehouse.remark || '无' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Upload, Download, OfficeBuilding } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/common'
import type { Warehouse } from '@/types/inventory'
import {
  getWarehouseListApi,
  getWarehouseByIdApi,
  addWarehouseApi,
  updateWarehouseApi,
  deleteWarehouseApi
} from '@/api/inventory'

// 响应式数据
const loading = ref(false)
const warehouseList = ref<Warehouse[]>([])
const selectedRows = ref<Warehouse[]>([])
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentWarehouse = ref<Warehouse | null>(null)
const isEdit = ref(false)
const formRef = ref()

// 搜索表单
const searchForm = reactive({
  warehouseName: '',
  warehouseCode: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 表单数据
const form = reactive({
  id: undefined as number | undefined,
  warehouseCode: '',
  warehouseName: '',
  remark: '',
  orderNum: 0
})

// 表单验证规则
const formRules = {
  warehouseCode: [
    { required: true, message: '请输入仓库编码', trigger: 'blur' }
  ],
  warehouseName: [
    { required: true, message: '请输入仓库名称', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑仓库' : '新增仓库')

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadWarehouseList()
}

// 重置
const handleReset = () => {
  searchForm.warehouseName = ''
  searchForm.warehouseCode = ''
  pagination.current = 1
  loadWarehouseList()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: Warehouse) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    warehouseCode: row.warehouseCode,
    warehouseName: row.warehouseName,
    remark: row.remark,
    orderNum: row.orderNum
  })
  dialogVisible.value = true
}

// 查看详情
const handleView = (row: Warehouse) => {
  currentWarehouse.value = row
  detailDialogVisible.value = true
}

// 删除
const handleDelete = (row: Warehouse) => {
  ElMessageBox.confirm(
    `确定要删除仓库"${row.warehouseName}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteWarehouseApi(String(row.id))
      ElMessage.success('删除成功')
      loadWarehouseList()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  })
}



// 批量导入
const handleBatchImport = () => {
  ElMessage.info('批量导入功能开发中...')
}

// 导出
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 选择变更
const handleSelectionChange = (selection: Warehouse[]) => {
  selectedRows.value = selection
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    const formData = { ...form }
    
    if (isEdit.value) {
      await updateWarehouseApi(formData)
      ElMessage.success('更新成功')
    } else {
      const { id, ...createData } = formData
      await addWarehouseApi(createData)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    loadWarehouseList()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  } finally {
    loading.value = false
  }
}

// 对话框关闭
const handleDialogClose = () => {
  resetForm()
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: undefined,
    warehouseCode: '',
    warehouseName: '',
    remark: '',
    orderNum: 0
  })
  
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  loadWarehouseList()
}

// 当前页变化
const handlePageChange = () => {
  loadWarehouseList()
}

// 加载仓库列表
const loadWarehouseList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      warehouseName: searchForm.warehouseName || undefined,
      warehouseCode: searchForm.warehouseCode || undefined
    }
    
    const response = await getWarehouseListApi(params)
    warehouseList.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('加载仓库列表失败:', error)
    ElMessage.error('加载仓库列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadWarehouseList()
})
</script>

<style scoped>
.warehouse-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.content-card {
  overflow: visible;
}

.content-card.table-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 600px;
  height: calc(100vh - 200px);
}

.table-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
  min-height: 0;
}

.table-wrapper {
  flex: 1;
  overflow: auto;
  min-height: 0;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 16px;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.search-form :deep(.el-form-item.ml-auto) {
  margin-left: auto;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 4px 0;
  border-top: 1px solid #e9ecef;
  flex-shrink: 0;
  margin-top: -20px;
}

.warehouse-detail {
  @apply p-4;
}

/* 表格样式 */
:deep(.el-table) {
  width: 100%;
  height: 100%;
}

:deep(.el-table__body-wrapper) {
  overflow-x: auto !important;
  overflow-y: auto !important;
}

:deep(.el-table__header-wrapper) {
  overflow-x: auto !important;
}

/* 表格容器允许横向滚动 */
:deep(.el-table__inner-wrapper) {
  overflow-x: auto !important;
}

/* 确保表格内容不换行 */
:deep(.el-table td),
:deep(.el-table th) {
  white-space: nowrap;
}

/* 确保固定列正确显示 */
:deep(.el-table__fixed),
:deep(.el-table__fixed-right) {
  height: auto !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .flex-between {
    @apply flex-col items-stretch space-y-4;
  }
  
  .search-section,
  .action-section {
    @apply w-full justify-start;
  }
  
  .action-section {
    @apply flex-wrap;
  }
}

/* 表格操作按钮样式优化 */
.el-table .el-button + .el-button {
  margin-left: 0;
}

/* 确保操作列按钮不换行 */
.el-table .flex.flex-wrap {
  flex-wrap: nowrap;
  white-space: nowrap;
}

@media (max-width: 1200px) {
  .el-table .flex.flex-wrap {
    flex-wrap: wrap;
  }
}
</style>
