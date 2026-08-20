<template>
  <div class="brand-container">
    <!-- 搜索筛选区域 -->
    <div class="content-card mb-6">
      <el-form
        :model="searchForm"
        inline
        label-width="80px"
        class="search-form"
      >
        <el-form-item label="品牌名称">
          <el-input
            v-model="searchForm.brandName"
            placeholder="请输入品牌名称"
            clearable
            style="width: 200px"
          />
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
              新增品牌
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
        :data="brandList"
        @selection-change="handleSelectionChange"
        border
        class="w-full"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="brandName" label="品牌名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
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
    <BrandDialog
      v-model="dialogVisible"
      :brand-data="currentBrand"
      :is-edit="isEdit"
      @success="handleDialogSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getItemBrandListApi, deleteItemBrandApi } from '@/api/inventory'
import type { ItemBrand } from '@/types/inventory'
import type { PageResult } from '@/types/api'
import { formatDate } from '@/utils/common'
import BrandDialog from './components/BrandDialog.vue'

// 响应式数据
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const brandList = ref<ItemBrand[]>([])
const selectedIds = ref<number[]>([])
const currentBrand = ref<ItemBrand | null>(null)
const total = ref(0)

// 搜索表单
const searchForm = reactive({
  brandName: ''
})

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 20
})

// 获取品牌列表
const fetchBrandList = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const response: PageResult<ItemBrand> = await getItemBrandListApi(params)
    brandList.value = response.records || []
    total.value = response.total || 0
  } catch (error) {
    console.error('获取品牌列表失败:', error)
    ElMessage.error('获取品牌列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.pageNum = 1
  fetchBrandList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    brandName: ''
  })
  pagination.pageNum = 1
  fetchBrandList()
}

// 新增品牌
const handleAdd = () => {
  currentBrand.value = null
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑品牌
const handleEdit = (row: ItemBrand) => {
  currentBrand.value = { ...row }
  isEdit.value = true
  dialogVisible.value = true
}

// 删除品牌
const handleDelete = async (row: ItemBrand) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除品牌"${row.brandName}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteItemBrandApi(row.id!.toString())
    ElMessage.success('删除成功')
    fetchBrandList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除品牌失败:', error)
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
    
    await deleteItemBrandApi(selectedIds.value.join(','))
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    fetchBrandList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量删除品牌失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 表格选择变化
const handleSelectionChange = (selection: ItemBrand[]) => {
  selectedIds.value = selection.map(item => item.id!).filter(Boolean)
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  fetchBrandList()
}

// 当前页变化
const handleCurrentChange = (page: number) => {
  pagination.pageNum = page
  fetchBrandList()
}

// 弹窗成功回调
const handleDialogSuccess = () => {
  dialogVisible.value = false
  fetchBrandList()
}

// 组件挂载时获取数据
onMounted(() => {
  fetchBrandList()
})
</script>

<style scoped>
.brand-container {
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

