<template>
  <div class="operation-log-container">
    
    <div class="content-area">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>操作日志列表</span>
          </div>
        </template>
        
        <div class="search-area">
          <el-form :model="searchForm" inline>
            <el-form-item label="用户名">
              <el-input 
                v-model="searchForm.username" 
                placeholder="请输入用户名"
                clearable
              />
            </el-form-item>
            <el-form-item label="操作模块">
              <el-select v-model="searchForm.module" placeholder="请选择模块" clearable style="width: 150px">
                <el-option label="用户管理" value="用户管理" />
                <el-option label="角色管理" value="角色管理" />
                <el-option label="权限管理" value="权限管理" />
                <el-option label="字典管理" value="字典管理" />
                <el-option label="库存管理" value="库存管理" />
                <el-option label="种植管理" value="种植管理" />
              </el-select>
            </el-form-item>
            <el-form-item label="操作类型">
              <el-select v-model="searchForm.action" placeholder="请选择操作" clearable style="width: 150px">
                <el-option label="新增" value="INSERT" />
                <el-option label="修改" value="UPDATE" />
                <el-option label="删除" value="DELETE" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
            <el-form-item label="操作状态">
              <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
                <el-option label="正常" value="0" />
                <el-option label="异常" value="1" />
              </el-select>
            </el-form-item>
            <el-form-item label="请求方式">
              <el-select v-model="searchForm.requestMethod" placeholder="请选择请求方式" clearable style="width: 150px">
                <el-option label="GET" value="GET" />
                <el-option label="POST" value="POST" />
                <el-option label="PUT" value="PUT" />
                <el-option label="DELETE" value="DELETE" />
              </el-select>
            </el-form-item>
            <el-form-item label="请求IP">
              <el-input 
                v-model="searchForm.requestIp" 
                placeholder="请输入请求IP"
                clearable
                style="width: 150px"
              />
            </el-form-item>
            <el-form-item label="操作时间">
              <el-date-picker
                v-model="searchForm.dateRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="username" label="操作用户" width="110" />
          <el-table-column prop="realName" label="姓名" width="100" />
          <el-table-column prop="module" label="操作模块" width="110">
            <template #default="{ row }">
              <el-tag size="small" :type="getModuleTagType(row.module)">
                {{ getModuleText(row.module) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="action" label="操作类型" width="90">
            <template #default="{ row }">
              <el-tag size="small" :type="getActionTagType(row.action)">
                {{ getActionText(row.action) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="requestMethod" label="请求方式" width="100" />
          <el-table-column prop="description" label="操作描述" show-overflow-tooltip />
          <el-table-column prop="requestIp" label="请求IP" width="120" />
          <el-table-column prop="status" label="操作状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === '0' || row.status === 0 ? 'success' : 'danger'" size="small">
                {{ row.status === '0' || row.status === 0 ? '正常' : '异常' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="executionTime" label="执行时长" width="90">
            <template #default="{ row }">
              <span :class="{ 'slow-execution': row.executionTime > 1000 }">
                {{ row.executionTime }}ms
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="operationTime" label="操作时间" width="160">
            <template #default="{ row }">
              {{ row.operationTime ? formatDate(row.operationTime) : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ row }">
              <el-button type="info" size="small" @click="handleDetail(row)">
                详情
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
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </el-card>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="操作详情"
      width="800px"
    >
      <div class="detail-content" v-if="currentLog">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="操作用户">{{ currentLog.username }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ currentLog.realName }}</el-descriptions-item>
          <el-descriptions-item label="操作模块">
            <el-tag size="small" :type="getModuleTagType(currentLog.module)">
              {{ getModuleText(currentLog.module) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="操作类型">
            <el-tag size="small" :type="getActionTagType(currentLog.action)">
              {{ getActionText(currentLog.action) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="请求IP">{{ currentLog.requestIp }}</el-descriptions-item>
          <el-descriptions-item label="请求地址">{{ currentLog.requestUrl }}</el-descriptions-item>
          <el-descriptions-item label="请求方法">{{ currentLog.requestMethod }}</el-descriptions-item>
          <el-descriptions-item label="操作状态">
            <el-tag :type="currentLog.status === '0' || currentLog.status === 0 ? 'success' : 'danger'">
              {{ currentLog.status === '0' || currentLog.status === 0 ? '正常' : '异常' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="执行时长">
            <span :class="{ 'slow-execution': currentLog.executionTime > 1000 }">
              {{ currentLog.executionTime }}ms
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="操作时间">{{ currentLog.operationTime ? formatDate(currentLog.operationTime) : '-' }}</el-descriptions-item>
          <el-descriptions-item label="操作描述" :span="2">{{ currentLog.description }}</el-descriptions-item>
          <el-descriptions-item label="错误信息" :span="2" v-if="currentLog.errorMessage">
            <span class="error-message">{{ currentLog.errorMessage }}</span>
          </el-descriptions-item>
        </el-descriptions>
        
        <!-- 请求参数 -->
        <div class="detail-section" v-if="currentLog.requestParams">
          <h4>请求参数</h4>
          <el-input
            type="textarea"
            :value="formatJson(currentLog.requestParams)"
            :rows="6"
            readonly
          />
        </div>
        
        <!-- 响应数据 -->
        <div class="detail-section" v-if="currentLog.responseData">
          <h4>响应数据</h4>
          <el-input
            type="textarea"
            :value="formatJson(currentLog.responseData)"
            :rows="6"
            readonly
          />
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
import { ElMessage } from 'element-plus'
import { getOperLogListApi, getOperLogByIdApi } from '@/api/system'
import { formatDate } from '@/utils/common'

// 搜索表单
const searchForm = reactive({
  username: '',
  module: '',
  action: '',
  status: '',
  requestMethod: '',
  requestIp: '',
  dateRange: null as any
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

// 详情弹窗
const detailDialogVisible = ref(false)
const currentLog = ref<any>(null)



// 获取模块标签类型
const getModuleTagType = (module: string) => {
  const typeMap: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    user: 'primary',
    role: 'success',
    permission: 'warning',
    dict: 'info',
    inventory: 'danger',
    plant: 'primary'
  }
  return typeMap[module] || 'primary'
}

// 获取模块文本
const getModuleText = (module: string) => {
  const textMap: Record<string, string> = {
    user: '用户管理',
    role: '角色管理',
    permission: '权限管理',
    dict: '字典管理',
    inventory: '库存管理',
    plant: '种植管理'
  }
  return textMap[module] || module
}

// 获取操作标签类型
const getActionTagType = (action: string) => {
  const typeMap: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    INSERT: 'success',
    UPDATE: 'primary',
    DELETE: 'danger',
    OTHER: 'info'
  }
  return typeMap[action] || 'info'
}

// 获取操作文本
const getActionText = (action: string) => {
  const textMap: Record<string, string> = {
    INSERT: '新增',
    UPDATE: '修改',
    DELETE: '删除',
    OTHER: '其他'
  }
  return textMap[action] || action
}

// 格式化JSON
const formatJson = (jsonString: string) => {
  try {
    const obj = JSON.parse(jsonString)
    return JSON.stringify(obj, null, 2)
  } catch (error) {
    return jsonString
  }
}

// 业务类型映射（已废弃，直接使用INSERT/UPDATE/DELETE/OTHER）

// 模块映射 - 根据title判断模块
const getModuleByTitle = (title: string) => {
  if (title.includes('用户')) return 'user'
  if (title.includes('角色')) return 'role'
  if (title.includes('权限')) return 'permission'
  if (title.includes('字典')) return 'dict'
  if (title.includes('仓库') || title.includes('库存')) return 'inventory'
  if (title.includes('种植') || title.includes('农作物')) return 'plant'
  return 'other'
}

// 转换后端数据格式
const transformData = (record: any) => {
  return {
    id: record.operId,
    username: record.operName || 'unknown',
    realName: record.operName || 'unknown',
    module: getModuleByTitle(record.title || ''),
    action: record.businessType || 'OTHER', // 直接使用后端的businessType
    description: record.title || '',
    requestUrl: record.operUrl || '',
    requestMethod: record.requestMethod || '',
    requestIp: record.operIp || '',
    requestParams: record.operParam || '',
    responseData: record.jsonResult || '',
    status: record.status, // 保持原始状态值 0或1
    executionTime: record.costTime || 0,
    operationTime: record.operTime || '',
    errorMessage: record.errorMsg || ''
  }
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.size,
      operName: searchForm.username || undefined,
      title: searchForm.module || undefined,
      businessType: (searchForm.action as 'INSERT' | 'UPDATE' | 'DELETE' | 'OTHER' | undefined) || undefined,
      status: searchForm.status || undefined,
      requestMethod: searchForm.requestMethod || undefined,
      operIp: searchForm.requestIp || undefined,
      startTime: searchForm.dateRange?.[0] || undefined,
      endTime: searchForm.dateRange?.[1] || undefined
    }
    
    const result = await getOperLogListApi(params)
    const records = result.records || []
    tableData.value = records.map(transformData)
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

// 页码改变
const handlePageChange = () => {
  fetchData()
}

// 每页显示数量改变
const handleSizeChange = () => {
  pagination.page = 1
  fetchData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    username: '',
    module: '',
    action: '',
    status: '',
    requestMethod: '',
    requestIp: '',
    dateRange: null
  })
  handleSearch()
}


// 查看详情
const handleDetail = async (row: any) => {
  try {
    const detail = await getOperLogByIdApi(row.id)
    // 转换详情数据格式
    currentLog.value = transformData(detail)
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
    console.error(error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.operation-log-container {
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

.detail-content {
  padding: 10px 0;
}

.detail-section {
  margin-top: 20px;
}

.detail-section h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-weight: bold;
}

.slow-execution {
  color: #e6a23c;
  font-weight: bold;
}

.error-message {
  color: #f56c6c;
  font-weight: bold;
}

:deep(.el-descriptions__label) {
  font-weight: bold;
}

:deep(.el-textarea__inner) {
  font-family: 'Courier New', monospace;
  font-size: 12px;
}
</style>
