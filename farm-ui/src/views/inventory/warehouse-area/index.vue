<template>
  <div class="warehouse-area-container">
    <el-row :gutter="20">
      <!-- 左侧仓库管理 -->
      <el-col :span="6">
        <div class="warehouse-panel">
          <div class="panel-header">
            <div class="header-title">
              <el-icon class="title-icon"><OfficeBuilding /></el-icon>
              <span>仓库管理</span>
            </div>
            <el-button 
              type="primary" 
              @click="handleAddWarehouse"
            >
              <el-icon><Plus /></el-icon>
              新增仓库
            </el-button>
          </div>
          
          <!-- 仓库查询表单 -->
          <div class="warehouse-search-form">
            <div class="search-form-container">
              <el-form :model="warehouseSearchForm" size="small" label-width="80px">
                <el-form-item label="仓库名称">
                  <el-input 
                    v-model="warehouseSearchForm.warehouseName" 
                    placeholder="请输入仓库名称"
                    clearable
                    @keyup.enter="handleWarehouseSearch"
                  />
                </el-form-item>
                <el-form-item label="仓库编号">
                  <el-input 
                    v-model="warehouseSearchForm.warehouseCode" 
                    placeholder="请输入仓库编号"
                    clearable
                    @keyup.enter="handleWarehouseSearch"
                  />
                </el-form-item>
              </el-form>
              <div class="button-group">
                <el-button type="primary" @click="handleWarehouseSearch" size="small">
                  <el-icon><Search /></el-icon>
                  查询
                </el-button>
                <el-button @click="handleWarehouseReset" size="small">重置</el-button>
              </div>
            </div>
          </div>
          
          <div class="warehouse-container" v-loading="warehouseLoading">
            <div class="warehouse-list">
              <div 
                v-for="warehouse in warehouseList" 
                :key="warehouse.id"
                :class="['warehouse-card', { active: selectedWarehouseId === warehouse.id }]"
                @click="selectWarehouse(warehouse.id)"
              >
                <div class="card-content">
                  <div class="warehouse-icon">
                    <el-icon><House /></el-icon>
                  </div>
                  <div class="warehouse-info">
                    <div class="warehouse-name">{{ warehouse.warehouseName }}</div>
                    <div class="warehouse-code">编码: {{ warehouse.warehouseCode }}</div>
                    <div class="warehouse-remark" v-if="warehouse.remark">{{ warehouse.remark }}</div>
                  </div>
                </div>
                <div class="card-actions">
                  <el-dropdown trigger="click" @click.stop>
                    <el-button type="text" class="action-btn">
                      <el-icon><MoreFilled /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item @click="handleEditWarehouse(warehouse)">
                          <el-icon><Edit /></el-icon>
                          编辑
                        </el-dropdown-item>
                        <el-dropdown-item 
                          @click="handleDeleteWarehouse(warehouse)"
                          class="danger-item"
                        >
                          <el-icon><Delete /></el-icon>
                          删除
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
                <div class="active-indicator" v-if="selectedWarehouseId === warehouse.id"></div>
              </div>
            </div>
            
            <div v-if="warehouseList.length === 0 && !warehouseLoading" class="empty-state">
              <div class="empty-icon">
                <el-icon><Box /></el-icon>
              </div>
              <div class="empty-text">暂无仓库数据</div>
              <el-button type="primary" @click="handleAddWarehouse">
                <el-icon><Plus /></el-icon>
                创建第一个仓库
              </el-button>
            </div>
            
            <el-pagination
              v-if="warehousePagination.total > 0"
              v-model:current-page="warehousePagination.pageNum"
              v-model:page-size="warehousePagination.pageSize"
              :total="warehousePagination.total"
              :page-sizes="[5, 10, 20, 50]"
              layout="prev, pager, next"
              small
              class="warehouse-pagination"
              @size-change="handleWarehouseSizeChange"
              @current-change="handleWarehousePageChange"
            />
          </div>
        </div>
      </el-col>
      
      <!-- 右侧库区管理 -->
      <el-col :span="18">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>库区列表</span>
              <el-button 
                type="primary" 
                @click="handleAddArea"
                :disabled="!selectedWarehouseId"
              >
                <el-icon><Plus /></el-icon>
                新增库区
              </el-button>
            </div>
          </template>
          
          <div v-if="!selectedWarehouseId" class="select-warehouse-tip">
            <el-empty description="请先选择左侧仓库查看库区" />
          </div>
          
          <div v-else>
            <div class="search-area">
              <el-form :model="searchForm" inline>
                <el-form-item label="库区名称">
                  <el-input 
                    v-model="searchForm.areaName" 
                    placeholder="请输入库区名称"
                    clearable
                  />
                </el-form-item>
                <el-form-item label="库区编号">
                  <el-input 
                    v-model="searchForm.areaCode" 
                    placeholder="请输入库区编号"
                    clearable
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleSearch">查询</el-button>
                  <el-button @click="handleReset">重置</el-button>
                </el-form-item>
              </el-form>
            </div>
            
            <el-table :data="areaTableData" v-loading="areaLoading" stripe>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="areaName" label="库区名称" />
              <el-table-column prop="areaCode" label="库区编码" />
              <el-table-column label="所属仓库">
                <template #default="{ row }">
                  {{ getWarehouseName(row.warehouseId) }}
                </template>
              </el-table-column>
              <el-table-column prop="remark" label="备注" />
              <el-table-column label="创建时间" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="160" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="handleEditArea(row)">
                    编辑
                  </el-button>
                  <el-button type="danger" size="small" @click="handleDeleteArea(row)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <el-pagination
              v-model:current-page="pagination.pageNum"
              v-model:page-size="pagination.pageSize"
              :total="pagination.total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSearch"
              @current-change="handleSearch"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 仓库编辑对话框 -->
    <WarehouseDialog
      v-model:visible="warehouseDialogVisible"
      :warehouse="currentWarehouse"
      @success="handleWarehouseSuccess"
    />
    
    <!-- 库区编辑对话框 -->
    <AreaDialog
      v-model:visible="areaDialogVisible"
      :area="currentArea"
      :warehouse-id="selectedWarehouseId"
      @success="handleAreaSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, 
  OfficeBuilding, 
  House, 
  MoreFilled, 
  Edit, 
  Delete, 
  Box,
  Grid,
  InfoFilled,
  Search
} from '@element-plus/icons-vue'
import { 
  getAreaListApi, 
  deleteAreaApi, 
  getWarehouseListApi,
  deleteWarehouseApi
} from '@/api/inventory'
import type { WarehouseArea, Warehouse } from '@/types/inventory'
import WarehouseDialog from './components/WarehouseDialog.vue'
import AreaDialog from './components/AreaDialog.vue'
import { formatDate } from '@/utils/common'

// 仓库相关
const warehouseList = ref<Warehouse[]>([])
const selectedWarehouseId = ref<number | null>(null)
const warehouseLoading = ref(false)
const warehouseDialogVisible = ref(false)
const currentWarehouse = ref<Partial<Warehouse>>({})

const warehouseSearchForm = reactive({
  warehouseName: '',
  warehouseCode: ''
})

const warehousePagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 库区相关
const searchForm = reactive({
  areaName: '',
  areaCode: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const areaTableData = ref<WarehouseArea[]>([])
const areaLoading = ref(false)
const areaDialogVisible = ref(false)
const currentArea = ref<Partial<WarehouseArea>>({})

// 获取仓库列表
const fetchWarehouseList = async () => {
  warehouseLoading.value = true
  try {
    const params = {
      pageNum: warehousePagination.pageNum, 
      pageSize: warehousePagination.pageSize,
      warehouseName: warehouseSearchForm.warehouseName || undefined,
      warehouseCode: warehouseSearchForm.warehouseCode || undefined
    }
    const result = await getWarehouseListApi(params)
    warehouseList.value = result.records || []
    warehousePagination.total = result.total || 0
    // 如果有仓库且没有选中任何仓库，默认选中第一个
    if (warehouseList.value.length > 0 && !selectedWarehouseId.value) {
      selectedWarehouseId.value = warehouseList.value[0].id
    }
  } catch (error) {
    console.error('获取仓库列表失败:', error)
    ElMessage.error('获取仓库数据失败')
  } finally {
    warehouseLoading.value = false
  }
}

// 仓库搜索
const handleWarehouseSearch = () => {
  warehousePagination.pageNum = 1
  fetchWarehouseList()
}

// 仓库重置
const handleWarehouseReset = () => {
  Object.assign(warehouseSearchForm, {
    warehouseName: '',
    warehouseCode: ''
  })
  handleWarehouseSearch()
}

// 仓库分页处理
const handleWarehouseSizeChange = (size: number) => {
  warehousePagination.pageSize = size
  warehousePagination.pageNum = 1
  fetchWarehouseList()
}

const handleWarehousePageChange = (page: number) => {
  warehousePagination.pageNum = page
  fetchWarehouseList()
}

// 选择仓库
const selectWarehouse = (warehouseId: number) => {
  selectedWarehouseId.value = warehouseId
  // 重置分页并获取库区数据
  pagination.pageNum = 1
  fetchAreaData()
}

// 根据仓库ID获取仓库名称
const getWarehouseName = (warehouseId: number): string => {
  const warehouse = warehouseList.value.find(w => w.id === warehouseId)
  return warehouse?.warehouseName || '未知仓库'
}

// 获取库区数据
const fetchAreaData = async () => {
  if (!selectedWarehouseId.value) {
    areaTableData.value = []
    pagination.total = 0
    return
  }
  
  areaLoading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      areaName: searchForm.areaName || undefined,
      areaCode: searchForm.areaCode || undefined,
      warehouseId: selectedWarehouseId.value
    }
    
    const result = await getAreaListApi(params)
    areaTableData.value = result.records || []
    pagination.total = result.total || 0
    
  } catch (error) {
    console.error('获取库区数据失败:', error)
    ElMessage.error('获取库区数据失败')
  } finally {
    areaLoading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchAreaData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    areaName: '',
    areaCode: ''
  })
  handleSearch()
}

// 仓库管理
const handleAddWarehouse = () => {
  currentWarehouse.value = {}
  warehouseDialogVisible.value = true
}

const handleEditWarehouse = (warehouse: Warehouse) => {
  currentWarehouse.value = { ...warehouse }
  warehouseDialogVisible.value = true
}

const handleDeleteWarehouse = async (warehouse: Warehouse) => {
  try {
    await ElMessageBox.confirm(
      `确认删除仓库"${warehouse.warehouseName}"吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deleteWarehouseApi(String(warehouse.id))
    ElMessage.success('删除成功')
    
    // 计算删除后应该显示的页码
    const totalBeforeDelete = warehousePagination.total
    const isLastPage = warehousePagination.pageNum === Math.ceil(totalBeforeDelete / warehousePagination.pageSize)
    const isOnlyOneItemOnPage = warehouseList.value.length === 1
    
    // 如果删除的是当前页最后一条数据且不是第一页，则跳转到前一页
    if (isLastPage && isOnlyOneItemOnPage && warehousePagination.pageNum > 1) {
      warehousePagination.pageNum--
    }
    
    await fetchWarehouseList()
    
    // 如果删除的是当前选中的仓库，重置选择
    if (selectedWarehouseId.value === warehouse.id) {
      selectedWarehouseId.value = warehouseList.value.length > 0 ? warehouseList.value[0].id : null
      if (selectedWarehouseId.value) {
        fetchAreaData()
      } else {
        areaTableData.value = []
        pagination.total = 0
      }
    }
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除仓库失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleWarehouseSuccess = () => {
  warehouseDialogVisible.value = false
  fetchWarehouseList()
}

// 库区管理
const handleAddArea = () => {
  currentArea.value = {}
  areaDialogVisible.value = true
}

const handleEditArea = (area: WarehouseArea) => {
  currentArea.value = { ...area }
  areaDialogVisible.value = true
}

const handleDeleteArea = async (area: WarehouseArea) => {
  try {
    await ElMessageBox.confirm(
      `确认删除库区"${area.areaName}"吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deleteAreaApi(String(area.id))
    ElMessage.success('删除成功')
    await fetchAreaData()
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除库区失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleAreaSuccess = () => {
  areaDialogVisible.value = false
  fetchAreaData()
}

// 监听选中仓库变化，获取库区数据
watch(selectedWarehouseId, (newId) => {
  if (newId) {
    fetchAreaData()
  } else {
    areaTableData.value = []
    pagination.total = 0
  }
})

onMounted(() => {
  fetchWarehouseList()
})
</script>

<style scoped>
.warehouse-area-container {
  padding: 0px;
  overflow: hidden;
  width: 100%;
  height: 100%;
  margin-top: -10px;
}

/* 仓库面板样式 */
.warehouse-panel {
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 140px);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.header-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #495057;
}

.title-icon {
  font-size: 18px;
  margin-right: 8px;
  color: #6c757d;
}

.warehouse-search-form {
  padding: 6px 12px;
  background: #ffffff;
  border-bottom: 1px solid #e9ecef;
}

.search-form-container {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.warehouse-search-form :deep(.el-form) {
  flex: 1;
}

.warehouse-search-form :deep(.el-form-item) {
  margin-bottom: 8px;
}

.warehouse-search-form :deep(.el-form-item:last-child) {
  margin-bottom: 0;
}

.warehouse-search-form :deep(.el-form-item__label) {
  font-size: 14px;
  padding-right: 4px;
}

.button-group {
  display: flex;
  flex-direction: row;
  gap: 8px;
  flex-shrink: 0;
  align-items: flex-start;
  padding-top: 0;
}

.warehouse-search-form :deep(.el-input) {
  width: 100%;
}

.warehouse-search-form :deep(.el-input__inner) {
  font-size: 14px;
}

.warehouse-search-form :deep(.el-button + .el-button) {
  margin-left: 0;
}

.warehouse-container {
  padding: 8px 12px;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.warehouse-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
  overflow-y: auto;
  margin-bottom: 0;
}

.warehouse-pagination {
  padding: 15px;
  border-top: 1px solid #e9ecef;
  display: flex;
  justify-content: center;
  flex-shrink: 0;
  margin-top: -5px;
}

.warehouse-card {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 9px 8px;
  background: #ffffff;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.warehouse-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 3px;
  height: 100%;
  background: transparent;
  transition: all 0.2s;
}

.warehouse-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.warehouse-card:hover::before {
  background: #409eff;
}

.warehouse-card.active {
  border-color: #409eff;
  background: #f0f9ff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.warehouse-card.active::before {
  background: #409eff;
}

.card-content {
  display: flex;
  align-items: center;
  flex: 1;
}

.warehouse-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: #409eff;
  border-radius: 3px;
  color: white;
  font-size: 14px;
  margin-right: 6px;
  transition: all 0.2s;
  flex-shrink: 0;
}

.warehouse-card.active .warehouse-icon {
  background: #337ecc;
}

.warehouse-info {
  flex: 1;
}

.warehouse-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 1px;
  line-height: 1.2;
}

.warehouse-code {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 0;
  line-height: 1.1;
}

.warehouse-remark {
  font-size: 13px;
  color: #9ca3af;
  margin-top: 1px;
  line-height: 1.1;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-actions {
  display: flex;
  align-items: center;
}

.action-btn {
  width: 22px;
  height: 22px;
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  transition: all 0.2s;
  font-size: 12px;
}

.action-btn:hover {
  background: #f3f4f6;
  color: #374151;
}

.active-indicator {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 4px;
  height: 4px;
  background: #409eff;
  border-radius: 50%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.danger-item {
  color: #ef4444;
}

.danger-item:hover {
  background: #fef2f2;
  color: #dc2626;
}

.select-warehouse-tip {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.search-area {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  color: #d1d5db;
  margin-bottom: 12px;
}

.empty-text {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 16px;
  font-weight: 500;
}

/* 去掉横向滚动条 */
:deep(.el-row) {
  margin: 0 !important;
  overflow: hidden;
}

:deep(.el-col) {
  overflow: hidden;
}

:deep(.el-card) {
  overflow: hidden;
}

:deep(.el-card__body) {
  overflow: hidden;
}

:deep(.el-table) {
  overflow: visible;
}

:deep(.el-table__body-wrapper) {
  overflow-x: hidden !important;
}

:deep(.el-table__header-wrapper) {
  overflow: hidden !important;
}

:deep(.el-table__inner-wrapper) {
  overflow-x: hidden !important;
}
</style>