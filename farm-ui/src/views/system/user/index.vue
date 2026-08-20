<template>
  <div class="user-container">
    
    <div class="content-area">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>用户列表</span>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增用户
            </el-button>
          </div>
        </template>
        
        <div class="search-area">
          <el-form :model="searchForm" inline>
            <el-form-item label="用户名">
              <el-input 
                v-model="searchForm.userName" 
                placeholder="请输入用户名"
                clearable
              />
            </el-form-item>
             <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="请选择状态" clearable @change="handleStatusChange" style="width: 120px">
                  <el-option label="正常" value="0"></el-option>
                  <el-option label="停用" value="1"></el-option>
              </el-select>
             </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="userId" label="ID" width="80" />
          <el-table-column label="头像" width="100">
            <template #default="{ row }">
              <el-avatar
                :src="row.avatar"
                :size="50"
                :alt="row.nickName"
              >
                {{ row.nickName?.charAt(0) }}
              </el-avatar>
            </template>
          </el-table-column>
          <el-table-column prop="userName" label="用户名" />
          <el-table-column prop="nickName" label="姓名" />
          <el-table-column prop="sex" label="性别" width="80">
            <template #default="{ row }">
              <el-tag :type="row.sex === '0' ? 'primary' : 'success'" size="small">
                {{ row.sex === '0' ? '男' : row.sex === '1' ? '女' : '未知' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="phonenumber" label="手机号" />
          <el-table-column prop="email" label="邮箱" />
          
                     <el-table-column prop="status" label="状态" width="100">
             <template #default="{ row }">
               <el-tag :type="row.status === '0' ? 'success' : 'danger'">
                 {{ row.status === '0' ? '正常' : '停用' }}
               </el-tag>
             </template>
           </el-table-column>
          <el-table-column prop="loginDate" label="最后登录" width="180">
            <template #default="{ row }">
              {{ row.loginDate ? formatDate(row.loginDate) : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="{ row }">
              {{ row.createTime ? formatDate(row.createTime) : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="400" fixed="right">
            <template #default="{ row }">
              <el-button type="success" size="small" @click="handleViewDetail(row)">
                详情
              </el-button>
              <el-button type="primary" size="small" @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button type="info" size="small" @click="handleResetPassword(row)">
                重置密码
              </el-button>
              <el-button type="warning" size="small" @click="handleAssignRoles(row)">
                分配角色
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

    <!-- 用户表单弹窗 -->
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
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="form.sex">
                <el-radio :label="0">男</el-radio>
                <el-radio :label="1">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 预留位置，保持布局平衡 -->
          </el-col>
        </el-row>
        
        <el-row :gutter="20" v-if="!form.id">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码"
                show-password
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input 
                v-model="form.confirmPassword" 
                type="password" 
                placeholder="请确认密码"
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :label="0">正常</el-radio>
                <el-radio :label="1">停用</el-radio>
              </el-radio-group>
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

    <!-- 分配角色弹窗 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="分配角色"
      width="500px"
      @close="handleRoleDialogClose"
    >
      <div class="role-assign-content">
        <div class="user-info">
          <el-avatar :src="currentUser.avatar" :size="50">
            {{ currentUser.nickName?.charAt(0) }}
          </el-avatar>
          <div class="user-details">
            <h4>{{ currentUser.nickName }}</h4>
            <p>{{ currentUser.userName }}</p>
          </div>
        </div>
        
        <el-divider>角色选择</el-divider>
        
        <el-checkbox-group v-model="selectedRoleIds">
          <div class="role-list">
            <el-checkbox
              v-for="role in roleOptions"
              :key="role.roleId"
              :label="role.roleId"
              class="role-item"
            >
              {{ role.roleName }}
            </el-checkbox>
          </div>
        </el-checkbox-group>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRoleSubmit" :loading="roleSubmitLoading">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 用户详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="用户详情"
      width="800px"
      @close="handleDetailDialogClose"
    >
      <div class="detail-content" v-loading="detailLoading">
        <div class="detail-section">
          <div class="user-info-header">
            <el-avatar :src="userDetail.avatar" :size="80">
              {{ userDetail.nickName?.charAt(0) }}
            </el-avatar>
            <div class="user-basic-info">
              <h2>{{ userDetail.nickName }}</h2>
              <p>@{{ userDetail.userName }}</p>
              <el-tag :type="userDetail.status === '0' ? 'success' : 'danger'">
                {{ userDetail.status === '0' ? '正常' : '停用' }}
              </el-tag>
            </div>
          </div>

          <el-divider>基本信息</el-divider>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户ID">{{ userDetail.userId }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ userDetail.userName }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ userDetail.nickName }}</el-descriptions-item>
            <el-descriptions-item label="性别">
              <el-tag :type="userDetail.sex === '0' ? 'primary' : 'success'" size="small">
                {{ userDetail.sex === '0' ? '男' : userDetail.sex === '1' ? '女' : '未知' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="手机号">{{ userDetail.phonenumber }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userDetail.email }}</el-descriptions-item>
            <el-descriptions-item label="最后登录IP">{{ userDetail.loginIp || '-' }}</el-descriptions-item>
            <el-descriptions-item label="最后登录时间">{{ userDetail.loginDate ? formatDate(userDetail.loginDate) : '-' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间" :span="2">{{ userDetail.createTime ? formatDate(userDetail.createTime) : '-' }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ userDetail.remark || '-' }}</el-descriptions-item>
          </el-descriptions>

          <el-divider>角色信息</el-divider>
          <div class="roles-section">
            <el-empty v-if="!userDetail.roles || userDetail.roles.length === 0" description="暂无角色" />
            <div v-else class="roles-grid">
              <el-card 
                v-for="role in userDetail.roles" 
                :key="role.roleId"
                class="role-card"
                shadow="hover"
              >
                <div class="role-card-header">
                  <h4>{{ role.roleName }}</h4>
                  <el-tag :type="role.status === '0' ? 'success' : 'danger'" size="small">
                    {{ role.status === '0' ? '启用' : '禁用' }}
                  </el-tag>
                </div>
                <p class="role-remark">{{ role.remark || '暂无描述' }}</p>
                
                <el-divider style="margin: 10px 0;">权限列表</el-divider>
                <div class="permissions-list">
                  <el-empty v-if="!role.permissions || role.permissions.length === 0" description="暂无权限" />
                  <el-tag
                    v-else
                    v-for="permission in role.permissions"
                    :key="permission.permissionId"
                    type="info"
                    size="small"
                    class="permission-tag"
                  >
                    {{ permission.permissionName }}
                  </el-tag>
                </div>
              </el-card>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { User, Role } from '@/types/system'
import {
  getUserListApi,
  getUserByIdApi,
  addUserApi,
  updateUserApi,
  deleteUserApi,
  changeUserStatusApi,
  resetPasswordApi,
  getRoleSelectListApi,
  assignRolesToUserApi
} from '@/api/system'
import { formatDate } from '@/utils/common'

// 搜索表单
const searchForm = reactive({
  userName: '',
  realName: '',
  phone: '',
  status: ''
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref<User[]>([])
const loading = ref(false)

// 角色选项
const roleOptions = ref<Role[]>([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 分配角色相关
const roleDialogVisible = ref(false)
const roleSubmitLoading = ref(false)
const currentUser = ref<User>({} as User)
const selectedRoleIds = ref<number[]>([])

// 用户详情相关
const detailDialogVisible = ref(false)
const detailLoading = ref(false)
const userDetail = ref<User>({} as User)

// 表单数据
const form = reactive({
  id: null,
  username: '',
  realName: '',
  sex: 0,
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
  status: 0,
  remark: ''
})

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email' as const, message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== form.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取角色选项
const fetchRoleOptions = async () => {
  try {
    const roles = await getRoleSelectListApi()
    roleOptions.value = roles
  } catch (error: any) {
    console.error('获取角色列表失败:', error)
  }
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.size,
      userName: searchForm.userName || undefined,
      status: searchForm.status !== '' ? searchForm.status : undefined
    }
    
    const result = await getUserListApi(params)
    tableData.value = result.records || []
    pagination.total = result.total || 0
    
  } catch (error: any) {
    ElMessage.error('获取数据失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 状态改变处理
const handleStatusChange = (value: string) => {
  // 状态改变时的处理逻辑
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    userName: '',
    phone: '',
    status: ''
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增用户'
  resetForm()
  dialogVisible.value = true
}

// 查看详情
const handleViewDetail = async (row: User) => {
  try {
    detailLoading.value = true
    detailDialogVisible.value = true
    // 通过API获取用户完整信息（包含角色和权限）
    const detail = await getUserByIdApi(row.userId)
    userDetail.value = detail
  } catch (error: any) {
    console.error('获取用户详情失败:', error)
    ElMessage.error('获取用户详情失败')
  } finally {
    detailLoading.value = false
  }
}

// 详情弹窗关闭事件
const handleDetailDialogClose = () => {
  userDetail.value = {} as User
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑用户'
  Object.assign(form, {
    id: row.userId,
    username: row.userName,
    realName: row.nickName,
    sex: row.sex || 0,
    phone: row.phonenumber,
    email: row.email,
    status: row.status,
    remark: row.remark,
    password: '',
    confirmPassword: ''
  })
  dialogVisible.value = true
}

// 切换状态
const handleToggleStatus = async (row: User) => {
  try {
    await ElMessageBox.confirm(
      `确认${row.status === '0' ? '停用' : '启用'}用户"${row.nickName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const newStatus = row.status === '0' ? '1' : '0'
    await changeUserStatusApi({
      userId: row.userId,
      status: newStatus
    })
    
    row.status = newStatus
    ElMessage.success(`${row.status === '0' ? '正常' : '停用'}成功`)

  } catch (error: any) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  }
}

// 重置密码
const handleResetPassword = async (row: User) => {
  try {
    const { value: newPassword } = await ElMessageBox.prompt(
      `请输入用户"${row.nickName}"的新密码`,
      '重置密码',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'password',
        inputValidator: (value: string) => {
          if (!value || value.length < 6) {
            return '密码长度不能少于6位'
          }
          return true
        }
      }
    )
    
    await resetPasswordApi({
      userId: row.userId,
      newPassword
    })
    
    ElMessage.success('密码重置成功')
    
  } catch (error: any) {
    if (error !== 'cancel' && error.message) {
      ElMessage.error(error.message)
    }
  }
}

// 删除
const handleDelete = async (row: User) => {
  try {
    await ElMessageBox.confirm(
      `确认删除用户"${row.nickName}"吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deleteUserApi(row.userId.toString())
    ElMessage.success('删除成功')
    fetchData()
    
  } catch (error: any) {
    if (error !== 'cancel' && error.message) {
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
    
    const userData = {
      userName: form.username,
      nickName: form.realName,
      sex: form.sex.toString(),
      phonenumber: form.phone,
      email: form.email,
      status: form.status.toString(),
      remark: form.remark
    }
    
    if (form.id !== undefined && form.id !== null) {
      // 编辑逻辑
      await updateUserApi({ ...userData, userId: form.id })
      ElMessage.success('编辑成功')
    } else {
      // 新增逻辑 - 新增时需要密码
      await addUserApi({ 
        ...userData, 
        password: form.password 
      })
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    fetchData()
    
  } catch (error: any) {
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      console.error('表单验证失败:', error)
    }
  } finally {
    submitLoading.value = false
  }
}

// 分配角色
const handleAssignRoles = async (row: User) => {
  try {
    // 通过API获取用户完整信息（包含角色）
    const userDetail = await getUserByIdApi(row.userId)
    currentUser.value = userDetail
  // 设置当前用户已有的角色
    selectedRoleIds.value = userDetail.roles ? userDetail.roles.map((role: Role) => role.roleId) : []
  roleDialogVisible.value = true
  } catch (error: any) {
    console.error('获取用户详情失败:', error)
    ElMessage.error('获取用户详情失败')
  }
}

// 提交角色分配
const handleRoleSubmit = async () => {
  try {
    roleSubmitLoading.value = true
    
    await assignRolesToUserApi({
      userId: currentUser.value.userId,
      roleIds: selectedRoleIds.value.join(',')
    })
    
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
    fetchData()
    
  } catch (error: any) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    roleSubmitLoading.value = false
  }
}

// 分配角色弹窗关闭事件
const handleRoleDialogClose = () => {
  currentUser.value = {} as User
  selectedRoleIds.value = []
}



// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
    username: '',
    realName: '',
    sex: 0,
    phone: '',
    email: '',
    password: '',
    confirmPassword: '',
    status: 0,
    remark: ''
  })
  formRef.value?.clearValidate()
}

// 弹窗关闭事件
const handleDialogClose = () => {
  resetForm()
}

onMounted(() => {
  fetchRoleOptions()
  fetchData()
})
</script>

<style scoped>
.user-container {
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

:deep(.el-avatar) {
  border: 1px solid #dcdfe6;
}

.role-assign-content {
  padding: 10px 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.user-details h4 {
  margin: 0 0 5px 0;
  color: #303133;
  font-size: 16px;
}

.user-details p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.role-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 10px;
}

.role-item {
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #f9f9f9;
  transition: all 0.3s;
  width: 100%;
}

.role-item:hover {
  background-color: #f0f9ff;
  border-color: #409eff;
}

.role-item.is-checked {
  background-color: #ecf5ff;
  border-color: #409eff;
  color: #409eff;
}

/* 用户详情样式 */
.detail-content {
  min-height: 300px;
}

.detail-section {
  padding: 10px 0;
}

.user-info-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
}

.user-basic-info h2 {
  margin: 0 0 5px 0;
  font-size: 24px;
  color: white;
}

.user-basic-info p {
  margin: 0 0 10px 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.roles-section {
  margin-top: 20px;
}

.roles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 15px;
}

.role-card {
  transition: all 0.3s;
}

.role-card:hover {
  transform: translateY(-2px);
}

.role-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.role-card-header h4 {
  margin: 0;
  color: #303133;
  font-size: 16px;
}

.role-remark {
  margin: 0 0 10px 0;
  color: #909399;
  font-size: 13px;
  line-height: 1.6;
}

.permissions-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  max-height: 150px;
  overflow-y: auto;
}

.permission-tag {
  margin: 0;
}
</style>
