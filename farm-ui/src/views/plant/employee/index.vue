<template>
  <div class="employee-container">
    <div class="content-area">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>雇员列表</span>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加雇员
            </el-button>
          </div>
        </template>
        
        <div class="search-area">
          <el-form :model="searchForm" inline>
            <el-form-item label="员工姓名">
              <el-input 
                v-model="searchForm.employeeName" 
                placeholder="请输入员工姓名"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="员工编码">
              <el-input 
                v-model="searchForm.employeeCode" 
                placeholder="请输入员工编码"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="员工类型">
              <el-select 
                v-model="searchForm.employeeType" 
                placeholder="请选择员工类型" 
                clearable 
                style="width: 200px"
              >
                <el-option label="普通员工" value="0" />
                <el-option label="管理人员" value="1" />
                <el-option label="技术人员" value="2" />
                <el-option label="临时工" value="3" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <el-table :data="tableData" v-loading="loading" stripe @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" />
          <el-table-column prop="employeeCode" label="员工编码" width="120" />
          <el-table-column prop="employeeName" label="员工姓名" width="120" />
          <el-table-column prop="employeeSex" label="性别" width="80">
            <template #default="{ row }">
              <el-tag :type="row.employeeSex === '0' ? 'primary' : 'success'" size="small">
                {{ getGenderText(row.employeeSex) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="employeeType" label="员工类型" width="120">
            <template #default="{ row }">
              {{ getEmployeeTypeText(row.employeeType) }}
            </template>
          </el-table-column>
          <el-table-column prop="employeeTel" label="联系电话" width="130" />
          <el-table-column prop="employeeAddress" label="地址" min-width="200" show-overflow-tooltip />
          <el-table-column prop="remark" label="备注" width="100" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" width="160">
            <template #default="{ row }">
              {{ formatDateTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
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
        
        <div class="table-footer">
          <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">
            批量删除
          </el-button>
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSearch"
            @current-change="handleSearch"
          />
        </div>
      </el-card>
    </div>

    <!-- 员工信息对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="employeeForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="员工编码" required>
              <el-input 
                v-model="employeeForm.employeeCode" 
                placeholder="请输入员工编码"
                :disabled="dialogTitle === '查看员工'"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="员工姓名" required>
              <el-input 
                v-model="employeeForm.employeeName" 
                placeholder="请输入员工姓名"
                :disabled="dialogTitle === '查看员工'"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="员工类型">
              <el-select 
                v-model="employeeForm.employeeType" 
                placeholder="请选择员工类型"
                :disabled="dialogTitle === '查看员工'"
                style="width: 100%"
              >
                <el-option label="普通员工" value="0" />
                <el-option label="管理人员" value="1" />
                <el-option label="技术人员" value="2" />
                <el-option label="临时工" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select 
                v-model="employeeForm.employeeSex" 
                placeholder="请选择性别"
                :disabled="dialogTitle === '查看员工'"
                style="width: 100%"
              >
                <el-option label="男" value="0" />
                <el-option label="女" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input 
                v-model="employeeForm.employeeTel" 
                placeholder="请输入联系电话"
                :disabled="dialogTitle === '查看员工'"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地址">
              <el-input 
                v-model="employeeForm.employeeAddress" 
                placeholder="请输入地址"
                :disabled="dialogTitle === '查看员工'"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input 
            v-model="employeeForm.remark" 
            type="textarea" 
            :rows="3"
            placeholder="请输入备注"
            :disabled="dialogTitle === '查看员工'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button 
            v-if="dialogTitle !== '查看员工'" 
            type="primary" 
            @click="handleSave"
          >
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getEmployeeListApi, addEmployeeApi, updateEmployeeApi, deleteEmployeeApi, getEmployeeByIdApi } from '@/api/plant'
import type { Employee } from '@/types/plant'

const searchForm = reactive({
  employeeName: '',
  employeeCode: '',
  employeeType: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const tableData = ref<Employee[]>([])
const loading = ref(false)
const selectedRows = ref<Employee[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)

const employeeForm = reactive<Partial<Employee>>({
  employeeCode: '',
  employeeName: '',
  employeeType: '0',
  employeeTel: '',
  employeeSex: '0',
  employeeAddress: '',
  remark: ''
})

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 获取员工类型文本
const getEmployeeTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    '0': '普通员工',
    '1': '管理人员', 
    '2': '技术人员',
    '3': '临时工'
  }
  return typeMap[type] || type
}

// 获取性别文本
const getGenderText = (sex: string) => {
  const sexMap: Record<string, string> = {
    '0': '男',
    '1': '女'
  }
  return sexMap[sex] || sex
}

// 表格选择变化
const handleSelectionChange = (selection: Employee[]) => {
  selectedRows.value = selection
}

// 获取员工列表数据
const fetchData = async () => {
  loading.value = true
  try {
    const params: any = {
      pageNum: pagination.page,
      pageSize: pagination.size
    }
    
    // 只传递有值的查询参数
    if (searchForm.employeeName) {
      params.employeeName = searchForm.employeeName
    }
    if (searchForm.employeeCode) {
      params.employeeCode = searchForm.employeeCode
    }
    if (searchForm.employeeType) {
      params.employeeType = searchForm.employeeType
    }
    
    const result = await getEmployeeListApi(params)
    tableData.value = result.records || []
    pagination.total = result.total || 0
  } catch (error) {
    console.error('获取员工数据失败:', error)
    ElMessage.error('获取员工数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(searchForm, {
    employeeName: '',
    employeeCode: '',
    employeeType: ''
  })
  handleSearch()
}

// 重置表单
const resetForm = () => {
  Object.assign(employeeForm, {
    employeeId: undefined,
    employeeCode: '',
    employeeName: '',
    employeeType: '0',
    employeeTel: '',
    employeeSex: '0',
    employeeAddress: '',
    remark: ''
  })
}

// 添加员工
const handleAdd = () => {
  resetForm()
  dialogTitle.value = '添加员工'
  isEdit.value = false
  dialogVisible.value = true
}

// 查看员工
const handleView = async (row: Employee) => {
  try {
    const employee = await getEmployeeByIdApi(row.employeeId)
    Object.assign(employeeForm, employee)
    dialogTitle.value = '查看员工'
    isEdit.value = false
    dialogVisible.value = true
  } catch (error) {
    console.error('获取员工详情失败:', error)
    ElMessage.error('获取员工详情失败')
  }
}

// 编辑员工
const handleEdit = async (row: Employee) => {
  try {
    const employee = await getEmployeeByIdApi(row.employeeId)
    Object.assign(employeeForm, employee)
    dialogTitle.value = '编辑员工'
    isEdit.value = true
    dialogVisible.value = true
  } catch (error) {
    console.error('获取员工详情失败:', error)
    ElMessage.error('获取员工详情失败')
  }
}

// 删除单个员工
const handleDelete = (row: Employee) => {
  ElMessageBox.confirm(
    `确认删除员工“${row.employeeName}”吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await deleteEmployeeApi(row.employeeId.toString())
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除员工失败:', error)
      ElMessage.error('删除员工失败')
    }
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的员工')
    return
  }
  
  ElMessageBox.confirm(
    `确认删除选中的 ${selectedRows.value.length} 个员工吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const ids = selectedRows.value.map(item => item.employeeId).join(',')
      await deleteEmployeeApi(ids)
      ElMessage.success('批量删除成功')
      selectedRows.value = []
      fetchData()
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  })
}

// 保存员工
const handleSave = async () => {
  try {
    if (isEdit.value) {
      await updateEmployeeApi(employeeForm)
      ElMessage.success('更新成功')
    } else {
      // 添加时确保employeeId为undefined
      const addData = { ...employeeForm }
      delete addData.employeeId
      await addEmployeeApi(addData)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    resetForm()
    fetchData()
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

// 取消对话框
const handleCancel = () => {
  dialogVisible.value = false
  resetForm()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.employee-container {
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

.salary {
  color: #e6a23c;
  font-weight: bold;
}

.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.el-pagination {
  margin-top: 0;
}

.dialog-footer {
  text-align: right;
}
</style>
