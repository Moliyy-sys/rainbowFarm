<template>
  <div class="permission-container">
    
    <div class="content-area">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>权限列表</span>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增权限
            </el-button>
          </div>
        </template>
        
        <div class="search-area">
          <el-form :model="searchForm" inline>
            <el-form-item label="权限名称">
              <el-input 
                v-model="searchForm.name" 
                placeholder="请输入权限名称"
                clearable
              />
            </el-form-item>


            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <el-table 
          :data="tableData" 
          v-loading="loading" 
          stripe
        >
          <el-table-column prop="name" label="权限名称" width="200" />

          <el-table-column prop="component" label="组件路径" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handleEdit(row)">
                编辑
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

    <!-- 权限表单弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
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
            <el-form-item label="权限名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入权限名称" />
            </el-form-item>
          </el-col>

        </el-row>
        

        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="组件路径">
              <el-input v-model="form.component" placeholder="请输入组件路径" />
            </el-form-item>
          </el-col>
        </el-row>
        

        

        

        
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
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
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
  getPermissionListApi,
  addPermissionApi,
  updatePermissionApi,
  deletePermissionApi
} from '@/api/system'

// 搜索表单
const searchForm = reactive({
  name: ''
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref<any[]>([])
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增权限')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单数据
const form = reactive({
  id: null,
  name: '',
  component: '',
  remark: ''
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入权限名称', trigger: 'blur' }
  ]
}







// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.size,
      permissionName: searchForm.name || undefined
    }
    
    const result = await getPermissionListApi(params)
    // 处理后台返回的数据结构
    const permissions = result.records || []
    
    // 数据映射转换
    tableData.value = permissions.map((item: any) => ({
      id: item.permissionId,
      permissionId: item.permissionId,
      name: item.permissionName,
      component: item.component || '',
      remark: item.remark || '',
      createTime: item.createTime,
      updateTime: item.updateTime,
      createBy: item.createBy,
      updateBy: item.updateBy
    }))
    
    pagination.total = result.total || 0
    
  } catch (error) {
    ElMessage.error('获取数据失败')
    console.error(error)
  } finally {
    loading.value = false
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
    name: ''
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增权限'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑权限'
  Object.assign(form, {
    id: row.id,
    name: row.name,
    component: row.component,
    remark: row.remark
  })
  dialogVisible.value = true
}



// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确认删除权限"${row.name}"吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deletePermissionApi((row.id || row.permissionId).toString())
    ElMessage.success('删除成功')
    fetchData()
    
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
    
    const permissionData = {
      permissionName: form.name,
      component: form.component,
      remark: form.remark
    }
    
    if (form.id) {
      await updatePermissionApi({ ...permissionData, permissionId: form.id })
      ElMessage.success('编辑成功')
    } else {
      await addPermissionApi(permissionData)
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

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
    name: '',
    component: '',
    remark: ''
  })
  formRef.value?.clearValidate()
}

// 弹窗关闭事件
const handleDialogClose = () => {
  resetForm()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.permission-container {
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
</style>
