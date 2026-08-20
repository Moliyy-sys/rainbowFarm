<template>
  <div class="material-container">
    
    <div class="content-area">
      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="农资信息" name="material">
          <el-card>
        <template #header>
          <div class="card-header">
            <span>农资列表</span>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加农资
            </el-button>
          </div>
        </template>
        
        <div class="search-area">
          <el-form :model="searchForm" inline>
            <el-form-item label="农资名称">
              <el-input 
                v-model="searchForm.materialName" 
                placeholder="请输入农资名称"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="农资编码">
              <el-input 
                v-model="searchForm.materialCode" 
                placeholder="请输入农资编码"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="农资类型">
              <el-select v-model="searchForm.materialTypeId" placeholder="请选择类型" clearable style="width: 200px">
                <el-option 
                  v-for="type in materialTypes" 
                  :key="type.materialTypeId" 
                  :label="type.materialTypeName" 
                  :value="type.materialTypeId" 
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
          <el-table-column prop="materialCode" label="编号" width="120" />
          <el-table-column prop="materialName" label="农资名称" min-width="150" />
          <el-table-column prop="materialTypeId" label="类型" width="120">
            <template #default="{ row }">
              <el-tag size="small">
                {{ getMaterialTypeName(row.materialTypeId) }}
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
        
        <el-tab-pane label="农资类型" name="materialType">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>农资类型列表</span>
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
                    v-model="typeSearchForm.materialTypeName" 
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
              <el-table-column prop="materialTypeName" label="类型名称" min-width="150" />
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
    <el-dialog v-model="viewDialog.visible" title="查看农资信息" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="农资ID">{{ viewDialog.data.materialId }}</el-descriptions-item>
        <el-descriptions-item label="农资编码">{{ viewDialog.data.materialCode }}</el-descriptions-item>
        <el-descriptions-item label="农资名称">{{ viewDialog.data.materialName }}</el-descriptions-item>
        <el-descriptions-item label="农资类型">{{ getMaterialTypeName(viewDialog.data.materialTypeId) }}</el-descriptions-item>
        <el-descriptions-item label="计量单位">{{ viewDialog.data.measureUnit }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ viewDialog.data.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewDialog.data.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 添加/编辑对话框 -->
    <el-dialog 
      v-model="formDialog.visible" 
      :title="formDialog.isEdit ? '编辑农资信息' : '添加农资信息'" 
      width="600px"
    >
      <el-form 
        ref="formRef" 
        :model="formDialog.data" 
        :rules="formRules" 
        label-width="100px"
      >
        <el-form-item label="农资编码" prop="materialCode">
          <el-input v-model="formDialog.data.materialCode" placeholder="请输入农资编码" />
        </el-form-item>
        <el-form-item label="农资名称" prop="materialName">
          <el-input v-model="formDialog.data.materialName" placeholder="请输入农资名称" />
        </el-form-item>
        <el-form-item label="农资类型" prop="materialTypeId">
          <el-select v-model="formDialog.data.materialTypeId" placeholder="请选择农资类型" style="width: 100%">
            <el-option 
              v-for="type in materialTypes" 
              :key="type.materialTypeId" 
              :label="type.materialTypeName" 
              :value="type.materialTypeId" 
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

    <!-- 农资类型查看对话框 -->
    <el-dialog v-model="typeViewDialog.visible" title="查看农资类型信息" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="类型名称">{{ typeViewDialog.data.materialTypeName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(typeViewDialog.data.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ typeViewDialog.data.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="typeViewDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 农资类型添加/编辑对话框 -->
    <el-dialog 
      v-model="typeFormDialog.visible" 
      :title="typeFormDialog.isEdit ? '编辑农资类型' : '添加农资类型'" 
      width="600px"
    >
      <el-form 
        ref="typeFormRef" 
        :model="typeFormDialog.data" 
        :rules="typeFormRules" 
        label-width="100px"
      >
        <el-form-item label="类型名称" prop="materialTypeName">
          <el-input v-model="typeFormDialog.data.materialTypeName" placeholder="请输入类型名称" />
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
  getMaterialListApi, 
  getMaterialByIdApi, 
  addMaterialApi, 
  updateMaterialApi, 
  deleteMaterialApi,
  getMaterialTypeListApi,
  getMaterialTypeByIdApi,
  addMaterialTypeApi,
  updateMaterialTypeApi,
  deleteMaterialTypeApi
} from '@/api/plant'
import type { Material, MaterialType } from '@/types/plant'

// 当前激活的标签页
const activeTab = ref('material')

// 搜索表单
const searchForm = reactive({
  materialName: '',
  materialCode: '',
  materialTypeId: undefined as number | undefined
})

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref<Material[]>([])
const loading = ref(false)

// 农资类型列表
const materialTypes = ref<MaterialType[]>([])

// ========== 农资类型管理 ==========

// 农资类型搜索表单
const typeSearchForm = reactive({
  materialTypeName: ''
})

// 农资类型分页参数
const typePagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 农资类型表格数据
const typeTableData = ref<MaterialType[]>([])
const typeLoading = ref(false)

// 农资类型查看对话框
const typeViewDialog = reactive({
  visible: false,
  data: {} as MaterialType
})

// 农资类型表单对话框
const typeFormDialog = reactive({
  visible: false,
  isEdit: false,
  data: {} as Partial<MaterialType>
})

// 农资类型表单引用和提交状态
const typeFormRef = ref<FormInstance>()
const typeSubmitLoading = ref(false)

// 农资类型表单验证规则
const typeFormRules = {
  materialTypeName: [
    { required: true, message: '请输入类型名称', trigger: 'blur' }
  ]
}

// 查看对话框
const viewDialog = reactive({
  visible: false,
  data: {} as Material
})

// 表单对话框
const formDialog = reactive({
  visible: false,
  isEdit: false,
  data: {} as Partial<Material>
})

// 表单引用和提交状态
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单验证规则
const formRules = {
  materialCode: [
    { required: true, message: '请输入农资编码', trigger: 'blur' }
  ],
  materialName: [
    { required: true, message: '请输入农资名称', trigger: 'blur' }
  ],
  materialTypeId: [
    { required: true, message: '请选择农资类型', trigger: 'change' }
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

// 获取农资类型名称
const getMaterialTypeName = (typeId: number) => {
  const type = materialTypes.value.find(t => t.materialTypeId === typeId)
  return type?.materialTypeName || '未知类型'
}

// 获取农资列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      materialName: searchForm.materialName || undefined,
      materialCode: searchForm.materialCode || undefined,
      materialTypeId: searchForm.materialTypeId
    }
    
    const response = await getMaterialListApi(params)
    tableData.value = response.records
    pagination.total = response.total
    
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 获取农资类型列表
const fetchMaterialTypes = async () => {
  try {
    const response = await getMaterialTypeListApi({ pageNum: 1, pageSize: 100 })
    materialTypes.value = response.records
  } catch (error) {
    ElMessage.error('获取农资类型失败')
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
    materialName: '',
    materialCode: '',
    materialTypeId: undefined
  })
  handleSearch()
}

// 查看详情
const handleView = async (row: Material) => {
  try {
    const data = await getMaterialByIdApi(row.materialId)
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
    materialCode: '',
    materialName: '',
    materialTypeId: undefined,
    measureUnit: '',
    remark: ''
  }
  formDialog.visible = true
}

// 编辑
const handleEdit = async (row: Material) => {
  try {
    const data = await getMaterialByIdApi(row.materialId)
    formDialog.isEdit = true
    formDialog.data = { ...data }
    formDialog.visible = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 删除
const handleDelete = (row: Material) => {
  ElMessageBox.confirm(
    `确定要删除农资"${row.materialName}"吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteMaterialApi(row.materialId.toString())
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
      await updateMaterialApi(formDialog.data)
      ElMessage.success('更新成功')
    } else {
      await addMaterialApi(formDialog.data)
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

// ========== 农资类型管理方法 ==========

// 获取农资类型列表
const fetchTypeData = async () => {
  typeLoading.value = true
  try {
    const params = {
      pageNum: typePagination.pageNum,
      pageSize: typePagination.pageSize,
      materialTypeName: typeSearchForm.materialTypeName || undefined
    }
    
    const response = await getMaterialTypeListApi(params)
    typeTableData.value = response.records
    typePagination.total = response.total
    
  } catch (error) {
    ElMessage.error('获取农资类型数据失败')
  } finally {
    typeLoading.value = false
  }
}

// 农资类型搜索
const handleTypeSearch = () => {
  typePagination.pageNum = 1
  fetchTypeData()
}

// 重置农资类型搜索
const handleTypeReset = () => {
  Object.assign(typeSearchForm, {
    materialTypeName: ''
  })
  handleTypeSearch()
}

// 查看农资类型详情
const handleViewType = async (row: MaterialType) => {
  try {
    const data = await getMaterialTypeByIdApi(row.materialTypeId)
    typeViewDialog.data = data
    typeViewDialog.visible = true
  } catch (error) {
    ElMessage.error('获取农资类型详情失败')
  }
}

// 添加农资类型
const handleAddType = () => {
  typeFormDialog.isEdit = false
  typeFormDialog.data = {
    materialTypeName: '',
    remark: ''
  }
  typeFormDialog.visible = true
}

// 编辑农资类型
const handleEditType = async (row: MaterialType) => {
  try {
    const data = await getMaterialTypeByIdApi(row.materialTypeId)
    typeFormDialog.isEdit = true
    typeFormDialog.data = { ...data }
    typeFormDialog.visible = true
  } catch (error) {
    ElMessage.error('获取农资类型详情失败')
  }
}

// 删除农资类型
const handleDeleteType = (row: MaterialType) => {
  ElMessageBox.confirm(
    `确定要删除农资类型"${row.materialTypeName}"吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteMaterialTypeApi(row.materialTypeId.toString())
      ElMessage.success('删除成功')
      fetchTypeData()
      // 删除后刷新农资类型下拉列表
      fetchMaterialTypes()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交农资类型表单
const handleTypeSubmit = async () => {
  if (!typeFormRef.value) return
  
  try {
    await typeFormRef.value.validate()
    typeSubmitLoading.value = true
    
    if (typeFormDialog.isEdit) {
      await updateMaterialTypeApi(typeFormDialog.data)
      ElMessage.success('更新成功')
    } else {
      await addMaterialTypeApi(typeFormDialog.data)
      ElMessage.success('添加成功')
    }
    
    typeFormDialog.visible = false
    fetchTypeData()
    // 添加或更新后刷新农资类型下拉列表
    fetchMaterialTypes()
    
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
  fetchMaterialTypes()
  fetchData()
  fetchTypeData()
})
</script>

<style scoped>
.material-container {
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
