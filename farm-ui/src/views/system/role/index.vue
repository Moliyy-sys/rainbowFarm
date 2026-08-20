<template>
  <div class="role-container">
    
    <div class="content-area">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>角色列表</span>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增角色
            </el-button>
          </div>
        </template>
        
        <div class="search-area">
          <el-form :model="searchForm" inline>
            <el-form-item label="角色名称">
              <el-input 
                v-model="searchForm.roleName" 
                placeholder="请输入角色名称"
                clearable
              />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
                <el-option label="启用" value="0" />
                <el-option label="禁用" value="1" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="roleId" label="ID" width="80" />
          <el-table-column prop="roleName" label="角色名称" />
          <el-table-column prop="roleSort" label="排序" width="80" />
          <el-table-column prop="remark" label="角色描述" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === '0' ? 'success' : 'danger'">
                {{ row.status === '0' ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="{ row }">
              {{ row.createTime ? formatDate(row.createTime) : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="360" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button type="success" size="small" @click="handleManageUsers(row)">
                用户管理
              </el-button>
              <el-button type="info" size="small" @click="handlePermission(row)">
                权限配置
              </el-button>
              <el-button 
                :type="row.status === '0' ? 'warning' : 'success'" 
                size="small" 
                @click="handleToggleStatus(row)"
              >
                {{ row.status === '0' ? '禁用' : '启用' }}
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="handleDelete(row)"
                :disabled="row.roleId === 1"
              >
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

    <!-- 角色表单弹窗 -->
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
            <el-form-item label="角色名称" prop="roleName">
              <el-input v-model="form.roleName" placeholder="请输入角色名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色排序" prop="roleSort">
              <el-input-number 
                v-model="form.roleSort" 
                :min="0"
                placeholder="请输入排序"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">禁用</el-radio>
          </el-radio-group>
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

    <!-- 权限配置弹窗 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="权限配置"
      width="800px"
      @close="handlePermissionDialogClose"
    >
      <div class="permission-header">
        <span>为角色"{{ currentRole?.roleName }}"配置权限</span>
      </div>
      
      <el-tree
        ref="permissionTreeRef"
        :data="permissionTree"
        :props="treeProps"
        node-key="permissionId"
        show-checkbox
        :default-checked-keys="checkedPermissions"
        @check="handlePermissionCheck"
      />
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handlePermissionSubmit" :loading="permissionLoading">
            保存权限
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 用户管理弹窗 -->
    <el-dialog
      v-model="userManageDialogVisible"
      title="角色用户管理"
      width="1000px"
      @close="handleUserManageDialogClose"
    >
      <div class="user-manage-header">
        <span>角色：{{ currentRole?.roleName }}</span>
      </div>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 已分配用户 -->
        <el-tab-pane label="已分配用户" name="assigned">
          <div class="tab-content">
            <div class="search-bar">
              <el-input
                v-model="assignedUserSearch"
                placeholder="请输入用户名搜索"
                clearable
                style="width: 250px"
                @change="fetchAssignedUsers"
              />
              <el-button type="primary" @click="fetchAssignedUsers" style="margin-left: 10px">
                搜索
              </el-button>
              <el-button 
                type="danger" 
                @click="handleBatchCancelRole"
                :disabled="selectedAssignedUserIds.length === 0"
                style="margin-left: 10px"
              >
                批量取消角色
              </el-button>
            </div>

            <el-table
              :data="assignedUsers"
              v-loading="assignedUsersLoading"
              @selection-change="handleAssignedSelectionChange"
              style="margin-top: 15px"
            >
              <el-table-column type="selection" width="55" />
              <el-table-column prop="userId" label="用户ID" width="80" />
              <el-table-column prop="userName" label="用户名" />
              <el-table-column prop="nickName" label="姓名" />
              <el-table-column prop="phonenumber" label="手机号" />
              <el-table-column prop="email" label="邮箱" />
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button type="danger" size="small" @click="handleCancelSingleRole(row)">
                    取消角色
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-pagination
              v-model:current-page="assignedUsersPagination.page"
              v-model:page-size="assignedUsersPagination.size"
              :total="assignedUsersPagination.total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
              @size-change="fetchAssignedUsers"
              @current-change="fetchAssignedUsers"
              style="margin-top: 15px"
            />
          </div>
        </el-tab-pane>

        <!-- 未分配用户 -->
        <el-tab-pane label="未分配用户" name="unassigned">
          <div class="tab-content">
            <div class="search-bar">
              <el-input
                v-model="unassignedUserSearch"
                placeholder="请输入用户名搜索"
                clearable
                style="width: 250px"
                @change="fetchUnassignedUsers"
              />
              <el-button type="primary" @click="fetchUnassignedUsers" style="margin-left: 10px">
                搜索
              </el-button>
              <el-button 
                type="success" 
                @click="handleBatchAssignRole"
                :disabled="selectedUnassignedUserIds.length === 0"
                style="margin-left: 10px"
              >
                批量分配角色
              </el-button>
            </div>

            <el-table
              :data="unassignedUsers"
              v-loading="unassignedUsersLoading"
              @selection-change="handleUnassignedSelectionChange"
              style="margin-top: 15px"
            >
              <el-table-column type="selection" width="55" />
              <el-table-column prop="userId" label="用户ID" width="80" />
              <el-table-column prop="userName" label="用户名" />
              <el-table-column prop="nickName" label="姓名" />
              <el-table-column prop="phonenumber" label="手机号" />
              <el-table-column prop="email" label="邮箱" />
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button type="success" size="small" @click="handleAssignSingleRole(row)">
                    分配角色
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-pagination
              v-model:current-page="unassignedUsersPagination.page"
              v-model:page-size="unassignedUsersPagination.size"
              :total="unassignedUsersPagination.total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
              @size-change="fetchUnassignedUsers"
              @current-change="fetchUnassignedUsers"
              style="margin-top: 15px"
            />
          </div>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="userManageDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type TreeInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getRoleListApi,
  getRoleByIdApi,
  addRoleApi,
  updateRoleApi,
  deleteRoleApi,
  changeRoleStatusApi,
  assignPermissionsToRoleApi,
  getRolePermissionsApi,
  getAllPermissionsApi,
  getAssignedUsersApi,
  getUnassignedUsersApi,
  assignRoleToUsersApi,
  cancelRoleFromUsersApi
} from '@/api/system'
import type { Role, Permission, User } from '@/types/system'
import { formatDate } from '@/utils/common'

// 搜索表单
const searchForm = reactive({
  roleName: '',
  status: ''
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref<Role[]>([])
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 权限配置弹窗
const permissionDialogVisible = ref(false)
const permissionTreeRef = ref<TreeInstance>()
const currentRole = ref<any>(null)
const checkedPermissions = ref<string[]>([])
const permissionLoading = ref(false)

// 用户管理弹窗
const userManageDialogVisible = ref(false)
const activeTab = ref('assigned')
const assignedUserSearch = ref('')
const unassignedUserSearch = ref('')
const assignedUsers = ref<User[]>([])
const unassignedUsers = ref<User[]>([])
const assignedUsersLoading = ref(false)
const unassignedUsersLoading = ref(false)
const selectedAssignedUserIds = ref<number[]>([])
const selectedUnassignedUserIds = ref<number[]>([])

// 已分配用户分页
const assignedUsersPagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 未分配用户分页
const unassignedUsersPagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表单数据
const form = reactive<{
  roleId?: number | null
  roleName: string
  roleSort: number
  status: string
  remark: string
}>({
  roleId: undefined,
  roleName: '',
  roleSort: 1,
  status: '0',
  remark: ''
})

// 表单验证规则
const formRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '角色名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  roleSort: [
    { required: true, message: '请输入角色排序', trigger: 'blur' }
  ]
}

// 权限树配置
const treeProps = {
  children: 'children',
  label: 'permissionName'
}

// 权限数据
const permissionTree = ref<Permission[]>([])

// 获取权限树数据
const fetchPermissionTree = async () => {
  try {
    const permissions = await getAllPermissionsApi()
    // 由于权限数据是平铺的，直接使用，不需要构建树结构
    permissionTree.value = permissions.map(permission => ({
      ...permission,
      id: permission.permissionId, // 确保有id字段用于tree组件
      children: [] // 暂时设为空数组，后续可根据实际需求调整
    }))
  } catch (error) {
    console.error('获取权限树失败:', error)
  }
}

// 构建权限树（如果将来需要真正的树结构，可以使用这个函数）
const buildPermissionTree = (permissions: Permission[]) => {
  // 如果数据有parentId字段，可以使用这个函数构建树结构
  if (permissions.length === 0 || !permissions.some(p => 'parentId' in p)) {
    // 数据是平铺的，直接返回
    return permissions.map(permission => ({
      ...permission,
      id: permission.permissionId,
      children: []
    }))
  }
  
  const buildTree = (parentId: number | null = null): Permission[] => {
    return permissions
      .filter(item => (item as any).parentId === parentId)
      .map(item => ({
        ...item,
        id: (item as any).permissionId,
        children: buildTree((item as any).permissionId)
      } as Permission))
  }
  
  return buildTree()
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.size,
      roleName: searchForm.roleName || undefined,
      status: searchForm.status !== '' ? searchForm.status : undefined
    }
    
    const result = await getRoleListApi(params)
    tableData.value = result.records || []
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
    roleName: '',
    status: ''
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增角色'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑角色'
  Object.assign(form, {
    roleId: row.roleId,
    roleName: row.roleName,
    roleSort: row.roleSort,
    status: row.status,
    remark: row.remark
  })
  dialogVisible.value = true
}

// 权限配置
const handlePermission = async (row: any) => {
  // 先清空之前的数据，确保数据隔离
  currentRole.value = null
  checkedPermissions.value = []
  
  // 如果树组件已存在，先清空选中状态
  if (permissionTreeRef.value) {
    permissionTreeRef.value.setCheckedKeys([])
  }
  
  // 设置当前角色
  currentRole.value = row
  
  try {
    const permissions = await getRolePermissionsApi(row.roleId)
    checkedPermissions.value = permissions.map((p: any) => p.permissionId)
    
    // 打开弹窗
    permissionDialogVisible.value = true
    
    // 使用 nextTick 确保弹窗打开后再设置树的选中状态
    await nextTick()
    if (permissionTreeRef.value) {
      permissionTreeRef.value.setCheckedKeys(checkedPermissions.value)
    }
  } catch (error) {
    console.error('获取角色权限失败:', error)
    checkedPermissions.value = []
    permissionDialogVisible.value = true
  }
}

// 切换状态
const handleToggleStatus = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确认${row.status === '0' ? '禁用' : '启用'}角色"${row.roleName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const newStatus = row.status === '0' ? '1' : '0'
    await changeRoleStatusApi({
      roleId: row.roleId,
      status: newStatus
    })
    
    row.status = newStatus
    ElMessage.success(`${row.status === '0' ? '启用' : '禁用'}成功`)
    
  } catch (error: any) {
    if (error?.message) {
      ElMessage.error(error.message)
    }
  }
}

// 删除
const handleDelete = async (row: any) => {
  if (row.roleId === 1) {
    ElMessage.warning('超级管理员角色不能删除')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确认删除角色"${row.roleName}"吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deleteRoleApi(row.roleId.toString())
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
    
    const roleData = {
      roleName: form.roleName,
      roleSort: form.roleSort,
      status: form.status,
      remark: form.remark
    }
    
    if (form.roleId !== undefined && form.roleId !== null) {
      // 编辑逻辑
      await updateRoleApi({ ...roleData, roleId: form.roleId })
      ElMessage.success('编辑成功')
    } else {
      // 新增逻辑
      await addRoleApi(roleData)
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

// 权限选择
const handlePermissionCheck = (data: any, checked: any) => {
  // 处理权限选择逻辑
}

// 保存权限配置
const handlePermissionSubmit = async () => {
  if (!permissionTreeRef.value || !currentRole.value) return
  
  permissionLoading.value = true
  
  try {
    // 获取选中的权限
    const checkedKeys = permissionTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
    const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys]
    
    await assignPermissionsToRoleApi({
      roleId: currentRole.value.roleId,
      permissionIds: allCheckedKeys.join(',')
    })
    
    ElMessage.success('权限配置保存成功')
    permissionDialogVisible.value = false
    
  } catch (error: any) {
    if (error?.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('保存权限配置失败')
    }
  } finally {
    permissionLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    roleId: undefined,
    roleName: '',
    roleSort: 1,
    status: '0',
    remark: ''
  })
  formRef.value?.clearValidate()
}

// 弹窗关闭事件
const handleDialogClose = () => {
  resetForm()
}

// 权限弹窗关闭事件
const handlePermissionDialogClose = () => {
  currentRole.value = null
  checkedPermissions.value = []
  // 清空树组件的选中状态
  if (permissionTreeRef.value) {
    permissionTreeRef.value.setCheckedKeys([])
  }
}

// 用户管理
const handleManageUsers = (row: any) => {
  currentRole.value = row
  activeTab.value = 'assigned'
  userManageDialogVisible.value = true
  // 重置分页和搜索
  assignedUsersPagination.page = 1
  assignedUserSearch.value = ''
  unassignedUsersPagination.page = 1
  unassignedUserSearch.value = ''
  // 获取已分配用户
  fetchAssignedUsers()
}

// 获取已分配用户列表
const fetchAssignedUsers = async () => {
  if (!currentRole.value) return
  
  assignedUsersLoading.value = true
  try {
    const params = {
      roleId: currentRole.value.roleId,
      pageNum: assignedUsersPagination.page,
      pageSize: assignedUsersPagination.size,
      userName: assignedUserSearch.value || undefined
    }
    
    const result = await getAssignedUsersApi(params)
    assignedUsers.value = result.records || []
    assignedUsersPagination.total = result.total || 0
  } catch (error) {
    console.error('获取已分配用户失败:', error)
    ElMessage.error('获取已分配用户失败')
  } finally {
    assignedUsersLoading.value = false
  }
}

// 获取未分配用户列表
const fetchUnassignedUsers = async () => {
  if (!currentRole.value) return
  
  unassignedUsersLoading.value = true
  try {
    const params = {
      roleId: currentRole.value.roleId,
      pageNum: unassignedUsersPagination.page,
      pageSize: unassignedUsersPagination.size,
      userName: unassignedUserSearch.value || undefined
    }
    
    const result = await getUnassignedUsersApi(params)
    unassignedUsers.value = result.records || []
    unassignedUsersPagination.total = result.total || 0
  } catch (error) {
    console.error('获取未分配用户失败:', error)
    ElMessage.error('获取未分配用户失败')
  } finally {
    unassignedUsersLoading.value = false
  }
}

// 切换标签页
const handleTabChange = (tabName: string | number) => {
  if (tabName === 'assigned') {
    fetchAssignedUsers()
  } else if (tabName === 'unassigned') {
    fetchUnassignedUsers()
  }
}

// 处理已分配用户选择
const handleAssignedSelectionChange = (selection: User[]) => {
  selectedAssignedUserIds.value = selection.map(user => user.userId)
}

// 处理未分配用户选择
const handleUnassignedSelectionChange = (selection: User[]) => {
  selectedUnassignedUserIds.value = selection.map(user => user.userId)
}

// 批量分配角色
const handleBatchAssignRole = async () => {
  if (selectedUnassignedUserIds.value.length === 0) {
    ElMessage.warning('请选择要分配角色的用户')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认为选中的 ${selectedUnassignedUserIds.value.length} 个用户分配角色"${currentRole.value.roleName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await assignRoleToUsersApi({
      roleId: currentRole.value.roleId,
      userIds: selectedUnassignedUserIds.value.join(',')
    })

    ElMessage.success('批量分配角色成功')
    selectedUnassignedUserIds.value = []
    // 刷新两个列表
    fetchUnassignedUsers()
    fetchAssignedUsers()
  } catch (error: any) {
    if (error !== 'cancel' && error?.message) {
      ElMessage.error(error.message)
    }
  }
}

// 批量取消角色
const handleBatchCancelRole = async () => {
  if (selectedAssignedUserIds.value.length === 0) {
    ElMessage.warning('请选择要取消角色的用户')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认取消选中的 ${selectedAssignedUserIds.value.length} 个用户的角色"${currentRole.value.roleName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await cancelRoleFromUsersApi({
      roleId: currentRole.value.roleId,
      userIds: selectedAssignedUserIds.value.join(',')
    })

    ElMessage.success('批量取消角色成功')
    selectedAssignedUserIds.value = []
    // 刷新两个列表
    fetchAssignedUsers()
    fetchUnassignedUsers()
  } catch (error: any) {
    if (error !== 'cancel' && error?.message) {
      ElMessage.error(error.message)
    }
  }
}

// 单个分配角色
const handleAssignSingleRole = async (row: User) => {
  try {
    await ElMessageBox.confirm(
      `确认为用户"${row.nickName}"分配角色"${currentRole.value.roleName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await assignRoleToUsersApi({
      roleId: currentRole.value.roleId,
      userIds: row.userId.toString()
    })

    ElMessage.success('分配角色成功')
    // 刷新两个列表
    fetchUnassignedUsers()
    fetchAssignedUsers()
  } catch (error: any) {
    if (error !== 'cancel' && error?.message) {
      ElMessage.error(error.message)
    }
  }
}

// 单个取消角色
const handleCancelSingleRole = async (row: User) => {
  try {
    await ElMessageBox.confirm(
      `确认取消用户"${row.nickName}"的角色"${currentRole.value.roleName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await cancelRoleFromUsersApi({
      roleId: currentRole.value.roleId,
      userIds: row.userId.toString()
    })

    ElMessage.success('取消角色成功')
    // 刷新两个列表
    fetchAssignedUsers()
    fetchUnassignedUsers()
  } catch (error: any) {
    if (error !== 'cancel' && error?.message) {
      ElMessage.error(error.message)
    }
  }
}

// 用户管理弹窗关闭事件
const handleUserManageDialogClose = () => {
  currentRole.value = null
  activeTab.value = 'assigned'
  assignedUserSearch.value = ''
  unassignedUserSearch.value = ''
  assignedUsers.value = []
  unassignedUsers.value = []
  selectedAssignedUserIds.value = []
  selectedUnassignedUserIds.value = []
  assignedUsersPagination.page = 1
  assignedUsersPagination.total = 0
  unassignedUsersPagination.page = 1
  unassignedUsersPagination.total = 0
}

onMounted(() => {
  fetchPermissionTree()
  fetchData()
})
</script>

<style scoped>
.role-container {
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

.permission-header {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-weight: bold;
  color: #303133;
}

:deep(.el-tree) {
  max-height: 400px;
  overflow-y: auto;
}

/* 用户管理弹窗样式 */
.user-manage-header {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 20px;
  font-weight: bold;
  color: #303133;
}

.tab-content {
  padding: 10px 0;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}
</style>
