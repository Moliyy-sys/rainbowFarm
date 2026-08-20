<template>
  <div class="dict-container">
    
    <div class="content-area">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>字典列表</span>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增字典
            </el-button>
          </div>
        </template>
        
        <div class="search-area">
          <el-form :model="searchForm" inline>
            <el-form-item label="字典名称">
              <el-input 
                v-model="searchForm.name" 
                placeholder="请输入字典名称"
                clearable
              />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
                <el-option label="启用" value="1" />
                <el-option label="禁用" value="0" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="dictId" label="ID" width="80" />
          <el-table-column prop="dictName" label="字典名称" />

          <el-table-column prop="remark" label="描述" show-overflow-tooltip />
          <el-table-column prop="itemCount" label="字典项数" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === '1' ? 'success' : 'danger'">
                {{ row.status === '1' ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="{ row }">
              {{ row.createTime ? formatDate(row.createTime) : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button type="info" size="small" @click="handleItems(row)">
                字典项
              </el-button> 
              <el-button type="danger" size="small" @click="handleDelete(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </el-card>
    </div>

    <!-- 字典表单弹窗 -->
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
        label-width="80px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="字典名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入字典名称" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="描述">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入字典描述"
          />
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input 
            v-model="form.remark" 
            type="textarea" 
            :rows="2" 
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 字典项管理弹窗 -->
    <el-dialog
      v-model="itemsDialogVisible"
      title="字典项管理"
      width="900px"
      @close="handleItemsDialogClose"
    >
      <div class="items-header">
        <span>字典"{{ currentDict?.dictName }}"的字典项</span>
        <el-button type="primary" size="small" @click="handleAddItem">
          <el-icon><Plus /></el-icon>
          新增字典项
        </el-button>
      </div>
      
      <el-table :data="itemsData" v-loading="itemsLoading" stripe>
        <el-table-column prop="dictLabel" label="标签" />
        <el-table-column prop="dictValue" label="值" />
        <el-table-column prop="dictSort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'" size="small">
              {{ row.status === '0' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEditItem(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteItem(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 字典项表单弹窗 -->
    <el-dialog
      v-model="itemFormDialogVisible"
      :title="itemDialogTitle"
      width="500px"
      @close="handleItemFormDialogClose"
    >
      <el-form
        ref="itemFormRef"
        :model="itemForm"
        :rules="itemFormRules"
        label-width="80px"
      >
        <el-form-item label="标签" prop="label">
          <el-input v-model="itemForm.label" placeholder="请输入标签" />
        </el-form-item>
        
        <el-form-item label="值" prop="value">
          <el-input v-model="itemForm.value" placeholder="请输入值" />
        </el-form-item>
        
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="itemForm.sort" :min="0" :max="9999" />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="itemForm.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input 
            v-model="itemForm.remark" 
            type="textarea" 
            :rows="2" 
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="itemFormDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleItemSubmit" :loading="itemSubmitLoading">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getDictTypeListApi,
  getDictTypeByIdApi,
  addDictTypeApi,
  updateDictTypeApi,
  deleteDictTypeApi,
  getDictDataListApi,
  getDictDataByIdApi,
  addDictDataApi,
  updateDictDataApi,
  deleteDictDataApi,
  getDictDataByTypeApi
} from '@/api/system'
import type { DictType, DictData } from '@/types/system'
import { formatDate } from '@/utils/common'

// 搜索表单
const searchForm = reactive({
  name: '',
  code: '',
  status: ''
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref<DictType[]>([])
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增字典')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 字典项相关
const itemsDialogVisible = ref(false)
const itemsData = ref<DictData[]>([])
const itemsLoading = ref(false)
const currentDict = ref<DictType | null>(null)

// 字典项表单弹窗
const itemFormDialogVisible = ref(false)
const itemDialogTitle = ref('新增字典项')
const itemFormRef = ref<FormInstance>()
const itemSubmitLoading = ref(false)

// 表单数据
const form = reactive({
  id: null as number | null,
  name: '',
  code: '',
  description: '',
  status: '1',
  remark: ''
})

// 字典项表单数据
const itemForm = reactive({
  id: null as number | null,
  dictId: null as number | null,
  label: '',
  value: '',
  sort: 0,
  status: '0',
  remark: ''
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入字典名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入字典编码', trigger: 'blur' },
    { pattern: /^[a-z_]+$/, message: '字典编码只能包含小写字母和下划线', trigger: 'blur' }
  ]
}

// 字典项表单验证规则
const itemFormRules = {
  label: [
    { required: true, message: '请输入标签', trigger: 'blur' }
  ],
  value: [
    { required: true, message: '请输入值', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.size,
      dictName: searchForm.name || undefined,
      dictType: searchForm.code || undefined,
      status: searchForm.status !== '' ? searchForm.status : undefined
    }
    
    const result = await getDictTypeListApi(params)
    tableData.value = result.records || []
    pagination.total = result.total || 0
    
  } catch (error) {
    ElMessage.error('获取数据失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 获取字典项数据
const fetchItemsData = async (dictId: number) => {
  itemsLoading.value = true
  try {
    const result = await getDictDataByTypeApi(dictId)
    itemsData.value = result || []
  } catch (error) {
    ElMessage.error('获取字典项失败')
    console.error(error)
  } finally {
    itemsLoading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    code: '',
    status: ''
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增字典'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑字典'
  Object.assign(form, {
    id: row.dictId,
    name: row.dictName,
    code: row.dictType,
    description: row.remark,
    status: row.status,
    remark: row.remark
  })
  dialogVisible.value = true
}

// 字典项管理
const handleItems = (row: any) => {
  currentDict.value = row
  fetchItemsData(row.dictId)
  itemsDialogVisible.value = true
}

// 切换状态
const handleToggleStatus = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确认${row.status === '1' ? '禁用' : '启用'}字典"${row.dictName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 注意：需要在后端API中实现字典类型状态切换功能
    row.status = row.status === '1' ? '0' : '1'
    ElMessage.success(`${row.status === '1' ? '启用' : '禁用'}成功`)
    fetchData()
    
  } catch (error: any) {
    if (error !== 'cancel' && error?.message) {
      ElMessage.error(error.message)
    }
  }
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确认删除字典"${row.dictName}"吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deleteDictTypeApi(row.dictId.toString())
    ElMessage.success('删除成功')
    fetchData()
    
  } catch (error: any) {
    if (error !== 'cancel' && error?.message) {
      ElMessage.error(error.message)
    }
  }
}

// 新增字典项
const handleAddItem = () => {
  itemDialogTitle.value = '新增字典项'
  resetItemForm()
  itemForm.dictId = currentDict.value?.dictId || null
  itemFormDialogVisible.value = true
}

// 编辑字典项
const handleEditItem = (row: any) => {
  itemDialogTitle.value = '编辑字典项'
  Object.assign(itemForm, {
    id: row.dictCode,
    dictId: row.dictType,
    label: row.dictLabel,
    value: row.dictValue,
    sort: row.dictSort,
    status: row.status,
    remark: row.remark
  })
  itemFormDialogVisible.value = true
}

// 删除字典项
const handleDeleteItem = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确认删除字典项"${row.label}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deleteDictDataApi(row.dictCode.toString())
    ElMessage.success('删除成功')
    if (currentDict.value?.dictId) {
      fetchItemsData(currentDict.value.dictId)
    }
    
  } catch (error: any) {
    if (error !== 'cancel' && error?.message) {
      ElMessage.error(error.message)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    submitLoading.value = true
    
    const dictTypeData = {
      dictName: form.name,
      dictType: form.code,
      status: form.status,
      remark: form.remark
    }
    
    if (form.id) {
      await updateDictTypeApi({ ...dictTypeData, dictId: form.id })
      ElMessage.success('编辑成功')
    } else {
      await addDictTypeApi(dictTypeData)
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    fetchData()
    
  } catch (error: any) {
    if (error?.message) {
      ElMessage.error(error.message)
    } else {
      console.error('表单验证失败:', error)
    }
  } finally {
    submitLoading.value = false
  }
}

// 提交字典项表单
const handleItemSubmit = async () => {
  if (!itemFormRef.value) return
  
  try {
    await itemFormRef.value.validate()
    
    itemSubmitLoading.value = true
    
    const dictDataItem = {
      dictLabel: itemForm.label,
      dictValue: itemForm.value,
      dictSort: itemForm.sort,
      status: itemForm.status,
      remark: itemForm.remark
    }
    
    if (itemForm.id) {
      await updateDictDataApi({ ...dictDataItem, dictCode: itemForm.id })
      ElMessage.success('编辑成功')
    } else {
      await addDictDataApi({ ...dictDataItem, dictType: currentDict.value?.dictId as unknown as string })
      ElMessage.success('新增成功')
    }
    
    itemFormDialogVisible.value = false
    if (currentDict.value?.dictId) {
      fetchItemsData(currentDict.value.dictId)
    }
    
  } catch (error: any) {
    if (error?.message) {
      ElMessage.error(error.message)
    } else {
      console.error('表单验证失败:', error)
    }
  } finally {
    itemSubmitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
    name: '',
    code: '',
    description: '',
    status: '1',
    remark: ''
  })
  formRef.value?.clearValidate()
}

// 重置字典项表单
const resetItemForm = () => {
  Object.assign(itemForm, {
    id: null,
    dictId: null,
    label: '',
    value: '',
    sort: 0,
    status: '0',
    remark: ''
  })
  itemFormRef.value?.clearValidate()
}

// 弹窗关闭事件
const handleDialogClose = () => {
  resetForm()
}

const handleItemsDialogClose = () => {
  currentDict.value = null
  itemsData.value = []
}

const handleItemFormDialogClose = () => {
  resetItemForm()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.dict-container {
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

.el-pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}

.items-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-weight: bold;
  color: #303133;
}
</style>
