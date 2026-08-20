<template>
  <div class="merchant-container">
    <!-- 搜索筛选区域 -->
    <div class="content-card mb-6">
      <el-form
        :model="searchForm"
        inline
        label-width="80px"
        class="search-form"
      >
        <el-form-item label="单位名称">
          <el-input
            v-model="searchForm.merchantName"
            placeholder="请输入往来单位名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="单位编码">
          <el-input
            v-model="searchForm.merchantCode"
            placeholder="请输入往来单位编码"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="单位类型">
          <el-select
            v-model="searchForm.merchantType"
            placeholder="请选择单位类型"
            clearable
            style="width: 150px"
          >
            <el-option label="供应商" :value="1" />
            <el-option label="客户" :value="2" />
            <el-option label="供应商客户" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <i class="i-ep-search mr-1"></i>
            查询
          </el-button>
          <el-button @click="handleReset">
            <i class="i-ep-refresh mr-1"></i>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格操作栏 -->
    <div class="content-card">
      <div class="table-toolbar mb-4">
        <div class="flex justify-between items-center">
          <div class="flex items-center gap-3">
            <el-button type="primary" @click="handleAdd">
              <i class="i-ep-plus mr-1"></i>
              新增往来单位
            </el-button>
            <el-button
              type="danger"
              :disabled="selectedIds.length === 0"
              @click="handleBatchDelete"
            >
              <i class="i-ep-delete mr-1"></i>
              批量删除
            </el-button>
          </div>
          <div class="text-sm text-gray-500">
            共 {{ total }} 条记录
          </div>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="merchantList"
        @selection-change="handleSelectionChange"
        border
        class="w-full"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="merchantCode" label="单位编码" width="120" show-overflow-tooltip />
        <el-table-column prop="merchantName" label="单位名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="merchantType" label="单位类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getMerchantTypeTagType(row.merchantType) as 'success' | 'warning' | 'info'">
              {{ getMerchantTypeText(row.merchantType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contactPerson" label="联系人" width="100" show-overflow-tooltip />
        <el-table-column prop="mobile" label="手机号" width="120" show-overflow-tooltip />
        <el-table-column prop="tel" label="座机号" width="120" show-overflow-tooltip />
        <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
        <el-table-column prop="email" label="邮箱" width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <i class="i-ep-edit"></i>
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <i class="i-ep-delete"></i>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <MerchantDialog
      v-model="dialogVisible"
      :merchant-data="currentMerchant"
      :is-edit="isEdit"
      @success="handleDialogSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantListApi, deleteMerchantApi } from '@/api/inventory'
import type { Merchant } from '@/types/inventory'
import type { PageResult } from '@/types/api'
import MerchantDialog from './components/MerchantDialog.vue'

// 响应式数据
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const merchantList = ref<Merchant[]>([])
const selectedIds = ref<number[]>([])
const currentMerchant = ref<Merchant | null>(null)
const total = ref(0)

// 搜索表单
const searchForm = reactive({
  merchantName: '',
  merchantCode: '',
  merchantType: ''
})

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 20
})

// 获取往来单位列表
const fetchMerchantList = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const response: PageResult<Merchant> = await getMerchantListApi(params)
    merchantList.value = response.records || []
    total.value = response.total || 0
  } catch (error) {
    console.error('获取往来单位列表失败:', error)
    ElMessage.error('获取往来单位列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.pageNum = 1
  fetchMerchantList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    merchantName: '',
    merchantCode: '',
    merchantType: ''
  })
  pagination.pageNum = 1
  fetchMerchantList()
}

// 新增往来单位
const handleAdd = () => {
  currentMerchant.value = null
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑往来单位
const handleEdit = (row: Merchant) => {
  currentMerchant.value = { ...row }
  isEdit.value = true
  dialogVisible.value = true
}

// 删除往来单位
const handleDelete = async (row: Merchant) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除往来单位"${row.merchantName}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteMerchantApi(row.id!.toString())
    ElMessage.success('删除成功')
    fetchMerchantList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除往来单位失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedIds.value.length} 条记录吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteMerchantApi(selectedIds.value.join(','))
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    fetchMerchantList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量删除往来单位失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 表格选择变化
const handleSelectionChange = (selection: Merchant[]) => {
  selectedIds.value = selection.map(item => item.id!).filter(Boolean)
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  fetchMerchantList()
}

// 当前页变化
const handleCurrentChange = (page: number) => {
  pagination.pageNum = page
  fetchMerchantList()
}

// 弹窗成功回调
const handleDialogSuccess = () => {
  dialogVisible.value = false
  fetchMerchantList()
}

// 获取往来单位类型文本
const getMerchantTypeText = (type: number) => {
  const typeMap: Record<number, string> = {
    1: '供应商',
    2: '客户',
    3: '供应商客户'
  }
  return typeMap[type] || '未知'
}

// 获取往来单位类型标签类型
const getMerchantTypeTagType = (type: number) => {
  const typeMap: Record<number, string> = {
    1: 'success',
    2: 'warning',
    3: 'info'
  }
  return typeMap[type] || ''
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 组件挂载时获取数据
onMounted(() => {
  fetchMerchantList()
})
</script>

<style scoped>
.merchant-container {
  @apply p-6;
}

.content-card {
  @apply bg-white rounded-lg shadow-sm p-6;
}

.search-form {
  @apply flex flex-wrap gap-4;
}

.table-toolbar {
  @apply border-b border-gray-200 pb-4;
}

.pagination-container {
  @apply flex justify-end mt-6;
}
</style>
