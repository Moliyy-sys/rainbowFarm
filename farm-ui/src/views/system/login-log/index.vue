<template>
  <div class="login-log-container">
    
    <div class="content-area">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>登录日志列表</span>
            <el-button type="danger" @click="handleClearLogs">
              <el-icon><Delete /></el-icon>
              清空日志
            </el-button>
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
            <el-form-item label="登录IP">
              <el-input 
                v-model="searchForm.loginIp" 
                placeholder="请输入登录IP"
                clearable
              />
            </el-form-item>
            <el-form-item label="登录状态">
              <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
                <el-option label="成功" value="0" />
                <el-option label="失败" value="1" />
              </el-select>
            </el-form-item>
            <el-form-item label="登录时间">
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
              <el-button type="success" @click="handleExport">导出</el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="loginIp" label="登录IP" width="150" />
          <el-table-column prop="browser" label="浏览器" width="120" show-overflow-tooltip />
          <el-table-column prop="os" label="操作系统" width="120" show-overflow-tooltip />
          <el-table-column prop="status" label="登录状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === '0' ? 'success' : 'danger'" size="small">
                {{ row.status === '0' ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="message" label="提示信息" show-overflow-tooltip />
          <el-table-column prop="loginTime" label="登录时间" width="180" />
          <el-table-column label="操作" width="100" fixed="right">
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
      title="登录详情"
      width="600px"
    >
      <div class="detail-content" v-if="currentLog">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{ currentLog.username }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ currentLog.realName }}</el-descriptions-item>
          <el-descriptions-item label="登录IP">{{ currentLog.loginIp }}</el-descriptions-item>
          <el-descriptions-item label="登录地点">{{ currentLog.loginLocation }}</el-descriptions-item>
          <el-descriptions-item label="浏览器">{{ currentLog.browser }}</el-descriptions-item>
          <el-descriptions-item label="操作系统">{{ currentLog.os }}</el-descriptions-item>
          <el-descriptions-item label="登录状态">
            <el-tag :type="currentLog.status === '0' ? 'success' : 'danger'">
              {{ currentLog.status === '0' ? '成功' : '失败' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="登录时间">{{ currentLog.loginTime }}</el-descriptions-item>
          <el-descriptions-item label="提示信息" :span="2">{{ currentLog.message }}</el-descriptions-item>
          <el-descriptions-item label="User-Agent" :span="2">{{ currentLog.userAgent }}</el-descriptions-item>
        </el-descriptions>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { getLoginInfoListApi, getLoginInfoByIdApi } from '@/api/system'

// 搜索表单
const searchForm = reactive({
  username: '',
  loginIp: '',
  status: '',
  dateRange: null as any
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 详情弹窗
const detailDialogVisible = ref(false)
const currentLog = ref<any>(null)

// 数据字段转换函数
const transformLoginData = (data: any): any => {
  if (!data) return data
  
  // 如果是数组，递归处理每个元素
  if (Array.isArray(data)) {
    return data.map((item: any) => transformLoginData(item))
  }
  
  // 字段映射
  const transformed = {
    id: data.id || data.infoId || data.dictId,
    username: data.username || data.userName || data.dictName,
    realName: data.realName || data.nickName || '',
    loginIp: data.loginIp || data.ipaddr || '',
    loginLocation: data.loginLocation || '',
    browser: data.browser || '',
    os: data.os || '',
    status: data.status ? String(data.status) : '', // 保持状态值为字符串 '0' 或 '1'
    message: data.message || data.msg || data.remark || '',
    loginTime: data.loginTime || data.createTime || '',
    userAgent: data.userAgent || '',
    createBy: data.createBy || '',
    createTime: data.createTime || '',
    updateBy: data.updateBy || '',
    updateTime: data.updateTime || '',
    remark: data.remark || ''
  }
  
  return transformed
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.size,
      userName: searchForm.username || undefined,
      ipaddr: searchForm.loginIp || undefined,
      status: searchForm.status || undefined,
      beginTime: searchForm.dateRange?.[0] || undefined,
      endTime: searchForm.dateRange?.[1] || undefined
    }
    
    const result = await getLoginInfoListApi(params)
    // 转换数据字段
    tableData.value = transformLoginData(result.records || [])
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
    loginIp: '',
    status: '',
    dateRange: null
  })
  handleSearch()
}

// 导出
const handleExport = () => {
  ElMessage.success('导出功能开发中...')
}

// 清空日志
const handleClearLogs = async () => {
  try {
    await ElMessageBox.confirm(
      '确认清空所有登录日志吗？此操作不可恢复！',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    // 注意：需要在后端API中实现清空登录日志功能
    ElMessage.info('清空日志功能需要后端API支持')
    
  } catch (error: any) {
    if (error !== 'cancel' && error?.message) {
      ElMessage.error(error.message)
    }
  }
}

// 查看详情
const handleDetail = async (row: any) => {
  try {
    const detail = await getLoginInfoByIdApi(row.id)
    // 转换详情数据字段
    currentLog.value = transformLoginData(detail)
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
.login-log-container {
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

:deep(.el-descriptions__label) {
  font-weight: bold;
}
</style>
