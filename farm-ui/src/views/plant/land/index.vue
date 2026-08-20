<template>
  <div class="land-container">
    
    <div class="content-area">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>地块列表</span>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加地块
            </el-button>
          </div>
        </template>
        
        <div class="search-area">
          <el-form :model="searchForm" inline>
            <el-form-item label="地块名称">
              <el-input 
                v-model="searchForm.landName" 
                placeholder="请输入地块名称"
                clearable
              />
            </el-form-item>
            <el-form-item label="地块类型">
              <el-select 
                v-model="searchForm.landType" 
                placeholder="请选择地块类型"
                clearable
                style="width: 200px"
              >
                <el-option label="耕地" value="0" />
                <el-option label="大棚" value="1" />
                <el-option label="果园" value="2" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="landId" label="ID" width="80" />
          <el-table-column prop="landName" label="地块名称" min-width="150" />
          <el-table-column prop="landType" label="地块类型" width="120">
            <template #default="{ row }">
              {{ getLandTypeLabel(row.landType) }}
            </template>
          </el-table-column>
          <el-table-column prop="landArea" label="面积(亩)" width="100">
            <template #default="{ row }">
              <span class="area">{{ row.landArea }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" width="160">
            <template #default="{ row }">
              {{ formatDateTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button type="info" size="small" @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">
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
      </el-card>
    </div>
    
    <!-- 添加/编辑对话框 -->
    <LandDialog
      v-model:visible="dialogVisible"
      :land="currentLand"
      :mode="dialogMode"
      @confirm="handleDialogConfirm"
    />
    
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getLandListApi, addLandApi, updateLandApi, deleteLandApi, getLandByIdApi } from '@/api/plant'
import type { Land } from '@/types/plant'
import LandDialog from './components/LandDialog.vue'

const searchForm = reactive({
  landName: '',
  landType: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<Land[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit'>('add')
const currentLand = ref<Partial<Land>>({})

const formatDateTime = (dateTime: string | undefined) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

/**
 * 获取地块类型标签
 */
const getLandTypeLabel = (landType: string | number | undefined) => {
  if (landType === undefined || landType === null || landType === '') return '-'
  const typeMap: Record<string, string> = {
    '0': '耕地',
    '1': '大棚',
    '2': '果园'
  }
  return typeMap[String(landType)] || '-'
}


/**
 * 查询地块列表
 */
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      landName: searchForm.landName || undefined,
      landType: searchForm.landType || undefined
    }
    
    const response = await getLandListApi(params)
    tableData.value = response.records || []
    pagination.total = response.total || 0
    
  } catch (error) {
    ElMessage.error('获取数据失败')
    console.error('获取地块列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(searchForm, {
    landName: '',
    landType: ''
  })
  handleSearch()
}

const handleAdd = () => {
  dialogMode.value = 'add'
  currentLand.value = {}
  dialogVisible.value = true
}


const handleEdit = async (row: Land) => {
  try {
    const land = await getLandByIdApi(row.landId!)
    dialogMode.value = 'edit'
    currentLand.value = land
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取地块详情失败')
    console.error('获取地块详情失败:', error)
  }
}

const handleDelete = async (row: Land) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除地块"${row.landName}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteLandApi(row.landId!.toString())
    ElMessage.success('删除成功')
    await fetchData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('删除地块失败:', error)
    }
  }
}

const handleDialogConfirm = async (land: Land) => {
  try {
    if (dialogMode.value === 'add') {
      await addLandApi(land)
      ElMessage.success('添加成功')
    } else {
      await updateLandApi(land)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    await fetchData()
  } catch (error) {
    ElMessage.error(dialogMode.value === 'add' ? '添加失败' : '更新失败')
    console.error('操作失败:', error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.land-container {
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

.area {
  color: #409eff;
  font-weight: bold;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
