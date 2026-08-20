<template>
  <div class="machine-container">
    
    <div class="content-area">
      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="农机信息" name="machine">
          <el-card>
        <template #header>
          <div class="card-header">
            <span>农机列表</span>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加农机
            </el-button>
          </div>
        </template>
        
        <div class="search-area">
          <el-form :model="searchForm" inline>
            <el-form-item label="农机名称">
              <el-input 
                v-model="searchForm.machineName" 
                placeholder="请输入农机名称"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="农机编码">
              <el-input 
                v-model="searchForm.machineCode" 
                placeholder="请输入农机编码"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="农机类型">
              <el-select v-model="searchForm.machineTypeId" placeholder="请选择类型" clearable style="width: 200px">
                <el-option 
                  v-for="type in machineTypes" 
                  :key="type.machineTypeId" 
                  :label="type.machineTypeName" 
                  :value="type.machineTypeId" 
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="machineCode" label="编号" width="120" />
          <el-table-column prop="machineName" label="农机名称" min-width="150" />
          <el-table-column prop="machineTypeId" label="类型" width="120">
            <template #default="{ row }">
              <el-tag size="small">
                {{ getMachineTypeName(row.machineTypeId) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="measureUnit" label="计量单位" width="100" />
          <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" width="160">
            <template #default="{ row }">
              {{ formatDateTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handleView(row)">
                查看
              </el-button>
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
        </el-tab-pane>
        
        <el-tab-pane label="农机类型" name="machineType">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>农机类型列表</span>
                <el-button type="primary" @click="handleAddType">
                  <el-icon><Plus /></el-icon>
                  添加类型
                </el-button>
              </div>
            </template>
            
            <div class="search-area">
              <el-form :model="typeSearchForm" inline>
                <el-form-item label="类型名称">
                  <el-input 
                    v-model="typeSearchForm.machineTypeName" 
                    placeholder="请输入类型名称"
                    clearable
                    style="width: 200px"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleTypeSearch">查询</el-button>
                  <el-button @click="handleTypeReset">重置</el-button>
                </el-form-item>
              </el-form>
            </div>
            
            <el-table :data="typeTableData" v-loading="typeLoading" stripe>
              <el-table-column prop="machineTypeName" label="类型名称" min-width="150" />
              <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
              <el-table-column prop="createTime" label="创建时间" width="160">
                <template #default="{ row }">
                  {{ formatDateTime(row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="240" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="handleViewType(row)">
                    查看
                  </el-button>
                  <el-button type="info" size="small" @click="handleEditType(row)">
                    编辑
                  </el-button>
                  <el-button type="danger" size="small" @click="handleDeleteType(row)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <el-pagination
              v-model:current-page="typePagination.pageNum"
              v-model:page-size="typePagination.pageSize"
              :total="typePagination.total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleTypeSearch"
              @current-change="handleTypeSearch"
            />
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 查看对话框 -->
    <el-dialog v-model="viewDialog.visible" title="查看农机信息" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="农机编码">{{ viewDialog.data.machineCode }}</el-descriptions-item>
        <el-descriptions-item label="农机名称">{{ viewDialog.data.machineName }}</el-descriptions-item>
        <el-descriptions-item label="农机类型">{{ getMachineTypeName(viewDialog.data.machineTypeId) }}</el-descriptions-item>
        <el-descriptions-item label="计量单位">{{ viewDialog.data.measureUnit }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(viewDialog.data.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewDialog.data.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 添加/编辑对话框 -->
    <el-dialog 
      v-model="formDialog.visible" 
      :title="formDialog.isEdit ? '编辑农机信息' : '添加农机信息'" 
      width="600px"
    >
      <el-form 
        ref="formRef" 
        :model="formDialog.data" 
        :rules="formRules" 
        label-width="100px"
      >
        <el-form-item label="农机编码" prop="machineCode">
          <el-input v-model="formDialog.data.machineCode" placeholder="请输入农机编码" />
        </el-form-item>
        <el-form-item label="农机名称" prop="machineName">
          <el-input v-model="formDialog.data.machineName" placeholder="请输入农机名称" />
        </el-form-item>
        <el-form-item label="农机类型" prop="machineTypeId">
          <el-select v-model="formDialog.data.machineTypeId" placeholder="请选择农机类型" style="width: 100%">
            <el-option 
              v-for="type in machineTypes" 
              :key="type.machineTypeId" 
              :label="type.machineTypeName" 
              :value="type.machineTypeId" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="计量单位" prop="measureUnit">
          <el-input v-model="formDialog.data.measureUnit" placeholder="请输入计量单位" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input 
            v-model="formDialog.data.remark" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ formDialog.isEdit ? '更新' : '添加' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 农机类型查看对话框 -->
    <el-dialog v-model="typeViewDialog.visible" title="查看农机类型信息" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="类型名称">{{ typeViewDialog.data.machineTypeName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(typeViewDialog.data.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ typeViewDialog.data.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="typeViewDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 农机类型添加/编辑对话框 -->
    <el-dialog 
      v-model="typeFormDialog.visible" 
      :title="typeFormDialog.isEdit ? '编辑农机类型' : '添加农机类型'" 
      width="600px"
    >
      <el-form 
        ref="typeFormRef" 
        :model="typeFormDialog.data" 
        :rules="typeFormRules" 
        label-width="100px"
      >
        <el-form-item label="类型名称" prop="machineTypeName">
          <el-input v-model="typeFormDialog.data.machineTypeName" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input 
            v-model="typeFormDialog.data.remark" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeFormDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleTypeSubmit" :loading="typeSubmitLoading">
          {{ typeFormDialog.isEdit ? '更新' : '添加' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { 
  getMachineListApi, 
  getMachineByIdApi, 
  addMachineApi, 
  updateMachineApi, 
  deleteMachineApi,
  getMachineTypeListApi,
  getMachineTypeByIdApi,
  addMachineTypeApi,
  updateMachineTypeApi,
  deleteMachineTypeApi
} from '@/api/plant'
import type { Machine, MachineType } from '@/types/plant'

// 当前激活的标签页
const activeTab = ref('machine')

// 搜索表单
const searchForm = reactive({
  machineName: '',
  machineCode: '',
  machineTypeId: undefined as number | undefined
})

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref<Machine[]>([])
const loading = ref(false)

// 农机类型列表
const machineTypes = ref<MachineType[]>([])

// ========== 农机类型管理 ==========

// 农机类型搜索表单
const typeSearchForm = reactive({
  machineTypeName: ''
})

// 农机类型分页参数
const typePagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 农机类型表格数据
const typeTableData = ref<MachineType[]>([])
const typeLoading = ref(false)

// 农机类型查看对话框
const typeViewDialog = reactive({
  visible: false,
  data: {} as MachineType
})

// 农机类型表单对话框
const typeFormDialog = reactive({
  visible: false,
  isEdit: false,
  data: {} as Partial<MachineType>
})

// 农机类型表单引用和提交状态
const typeFormRef = ref<FormInstance>()
const typeSubmitLoading = ref(false)

// 农机类型表单验证规则
const typeFormRules = {
  machineTypeName: [
    { required: true, message: '请输入类型名称', trigger: 'blur' }
  ]
}

// 查看对话框
const viewDialog = reactive({
  visible: false,
  data: {} as Machine
})

// 表单对话框
const formDialog = reactive({
  visible: false,
  isEdit: false,
  data: {} as Partial<Machine>
})

// 表单引用和提交状态
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单验证规则
const formRules = {
  machineCode: [
    { required: true, message: '请输入农机编码', trigger: 'blur' }
  ],
  machineName: [
    { required: true, message: '请输入农机名称', trigger: 'blur' }
  ],
  machineTypeId: [
    { required: true, message: '请选择农机类型', trigger: 'change' }
  ],
  measureUnit: [
    { required: true, message: '请输入计量单位', trigger: 'blur' }
  ]
}

// 格式化日期时间
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

// 获取农机类型名称
const getMachineTypeName = (typeId: number) => {
  const type = machineTypes.value.find(t => t.machineTypeId === typeId)
  return type?.machineTypeName || '未知类型'
}

// 获取农机列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      machineName: searchForm.machineName || undefined,
      machineCode: searchForm.machineCode || undefined,
      machineTypeId: searchForm.machineTypeId
    }
    
    const response = await getMachineListApi(params)
    tableData.value = response.records
    pagination.total = response.total
    
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 获取农机类型列表
const fetchMachineTypes = async () => {
  try {
    const response = await getMachineTypeListApi({ pageNum: 1, pageSize: 100 })
    machineTypes.value = response.records
  } catch (error) {
    ElMessage.error('获取农机类型失败')
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    machineName: '',
    machineCode: '',
    machineTypeId: undefined
  })
  handleSearch()
}

// 查看详情
const handleView = async (row: Machine) => {
  try {
    const data = await getMachineByIdApi(row.machineId)
    viewDialog.data = data
    viewDialog.visible = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 添加
const handleAdd = () => {
  formDialog.isEdit = false
  formDialog.data = {
    machineCode: '',
    machineName: '',
    machineTypeId: undefined,
    measureUnit: '',
    remark: ''
  }
  formDialog.visible = true
}

// 编辑
const handleEdit = async (row: Machine) => {
  try {
    const data = await getMachineByIdApi(row.machineId)
    formDialog.isEdit = true
    formDialog.data = { ...data }
    formDialog.visible = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 删除
const handleDelete = (row: Machine) => {
  ElMessageBox.confirm(
    `确定要删除农机"${row.machineName}"吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteMachineApi(row.machineId.toString())
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  // 防止重复提交
  if (submitLoading.value) return
  
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    if (formDialog.isEdit) {
      await updateMachineApi(formDialog.data)
      ElMessage.success('更新成功')
    } else {
      await addMachineApi(formDialog.data)
      ElMessage.success('添加成功')
    }
    
    formDialog.visible = false
    fetchData()
    
  } catch (error) {
    if (error !== false) {
      ElMessage.error(formDialog.isEdit ? '更新失败' : '添加失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// ========== 农机类型管理方法 ==========

// 获取农机类型列表
const fetchTypeData = async () => {
  typeLoading.value = true
  try {
    const params = {
      pageNum: typePagination.pageNum,
      pageSize: typePagination.pageSize,
      machineTypeName: typeSearchForm.machineTypeName || undefined
    }
    
    const response = await getMachineTypeListApi(params)
    typeTableData.value = response.records
    typePagination.total = response.total
    
  } catch (error) {
    ElMessage.error('获取农机类型数据失败')
  } finally {
    typeLoading.value = false
  }
}

// 农机类型搜索
const handleTypeSearch = () => {
  typePagination.pageNum = 1
  fetchTypeData()
}

// 重置农机类型搜索
const handleTypeReset = () => {
  Object.assign(typeSearchForm, {
    machineTypeName: ''
  })
  handleTypeSearch()
}

// 查看农机类型详情
const handleViewType = async (row: MachineType) => {
  try {
    const data = await getMachineTypeByIdApi(row.machineTypeId)
    typeViewDialog.data = data
    typeViewDialog.visible = true
  } catch (error) {
    ElMessage.error('获取农机类型详情失败')
  }
}

// 添加农机类型
const handleAddType = () => {
  typeFormDialog.isEdit = false
  typeFormDialog.data = {
    machineTypeName: '',
    remark: ''
  }
  typeFormDialog.visible = true
}

// 编辑农机类型
const handleEditType = async (row: MachineType) => {
  try {
    const data = await getMachineTypeByIdApi(row.machineTypeId)
    typeFormDialog.isEdit = true
    typeFormDialog.data = { ...data }
    typeFormDialog.visible = true
  } catch (error) {
    ElMessage.error('获取农机类型详情失败')
  }
}

// 删除农机类型
const handleDeleteType = (row: MachineType) => {
  ElMessageBox.confirm(
    `确定要删除农机类型"${row.machineTypeName}"吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteMachineTypeApi(row.machineTypeId.toString())
      ElMessage.success('删除成功')
      fetchTypeData()
      // 删除后刷新农机类型下拉列表
      fetchMachineTypes()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交农机类型表单
const handleTypeSubmit = async () => {
  if (!typeFormRef.value) return
  
  try {
    await typeFormRef.value.validate()
    typeSubmitLoading.value = true
    
    if (typeFormDialog.isEdit) {
      await updateMachineTypeApi(typeFormDialog.data)
      ElMessage.success('更新成功')
    } else {
      await addMachineTypeApi(typeFormDialog.data)
      ElMessage.success('添加成功')
    }
    
    typeFormDialog.visible = false
    fetchTypeData()
    // 添加或更新后刷新农机类型下拉列表
    fetchMachineTypes()
    
  } catch (error) {
    if (error !== false) {
      ElMessage.error(typeFormDialog.isEdit ? '更新失败' : '添加失败')
    }
  } finally {
    typeSubmitLoading.value = false
  }
}

// 初始化
onMounted(() => {
  fetchMachineTypes()
  fetchData()
  fetchTypeData()
})
</script>

<style scoped>
.machine-container {
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
</style>
