<template>
  <div class="plant-batch-container">
    <div class="content-area">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>批次列表</span>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新建批次
            </el-button>
          </div>
        </template>
        
        <div class="search-area">
          <el-form :model="searchForm" inline>
            <el-form-item label="批次名称">
              <el-input 
                v-model="searchForm.batchName" 
                placeholder="请输入批次名称"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="种质">
              <el-select
                v-model="searchForm.germplasmId"
                placeholder="请选择种质"
                clearable
                filterable
                style="width: 200px"
              >
                <el-option
                  v-for="item in germplasmOptions"
                  :key="item.germplasmId"
                  :label="item.germplasmName"
                  :value="item.germplasmId!"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="地块">
              <el-select
                v-model="searchForm.landId"
                placeholder="请选择地块"
                clearable
                filterable
                style="width: 200px"
              >
                <el-option
                  v-for="item in landOptions"
                  :key="item.landId"
                  :label="item.landName"
                  :value="item.landId!"
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
          <el-table-column prop="batchName" label="批次名称" width="150" />
          <el-table-column prop="germplasmName" label="种质名称" width="120" />
          <el-table-column prop="landName" label="地块名称" width="120" />
          <el-table-column prop="cropArea" label="种植面积(亩)" width="120">
            <template #default="{ row }">
              <span class="area">{{ row.cropArea }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="160">
            <template #default="{ row }">
              {{ row.startTime ? new Date(row.startTime).toLocaleString() : '' }}
            </template>
          </el-table-column>
          <el-table-column label="负责人" width="120">
            <template #default="{ row }">
              {{ getBatchHeadName(row.batchHead) }}
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" width="150" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" width="160">
            <template #default="{ row }">
              {{ row.createTime ? new Date(row.createTime).toLocaleString() : '' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="260" fixed="right">
            <template #default="{ row }">
              <el-button type="warning" size="small" @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button type="success" size="small" @click="handleBatchTask(row)">
                批次任务
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
    
    <!-- 新建批次对话框 -->
    <el-dialog
      v-model="addBatchDialogVisible"
      title="新建种植批次"
      width="600px"
      :close-on-click-modal="false"
      :append-to-body="true"
    >
      <el-form
        ref="addBatchFormRef"
        :model="addBatchForm"
        :rules="addBatchRules"
        label-width="100px"
      >
        <el-form-item label="批次名称" prop="batchName">
          <el-input
            v-model="addBatchForm.batchName"
            placeholder="请输入批次名称"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="种质" prop="germplasmId">
          <el-select
            v-model="addBatchForm.germplasmId"
            placeholder="请选择种质"
            filterable
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="item in germplasmOptions"
              :key="item.germplasmId"
              :label="item.germplasmName"
              :value="item.germplasmId"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="地块" prop="landId">
          <el-select
            v-model="addBatchForm.landId"
            placeholder="请选择地块"
            filterable
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="item in landOptions"
              :key="item.landId"
              :label="`${item.landName} (${item.landArea}亩)`"
              :value="item.landId!"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="种植面积" prop="cropArea">
          <el-input-number
            v-model="addBatchForm.cropArea"
            :min="0.1"
            :max="safeMaxArea"
            :precision="2"
            placeholder="请输入种植面积"
            style="width: 100%"
          />
          <div class="form-tip">单位：亩，最大可种植面积：{{ selectedLandArea || 0 }} 亩</div>
        </el-form-item>
        
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="addBatchForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="负责人" prop="batchHead">
          <el-select
            v-model="addBatchForm.batchHead"
            placeholder="请选择负责人"
            filterable
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="item in userOptions"
              :key="item.userId"
              :label="`${item.nickName} (${item.userName})`"
              :value="item.userId"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input
            v-model="addBatchForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addBatchDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveAddBatch">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 批次任务对话框 -->
    <el-dialog
      v-model="batchTaskDialogVisible"
      title="批次任务管理"
      width="1200px"
      :close-on-click-modal="false"
    >
      <div class="batch-task-dialog">
        <div class="batch-info">
          <h3>批次信息</h3>
          <el-descriptions :column="4" border>
            <el-descriptions-item label="批次名称">{{ currentBatch?.batchName }}</el-descriptions-item>
            <el-descriptions-item label="种质名称">{{ currentBatch?.germplasmName }}</el-descriptions-item>
            <el-descriptions-item label="地块名称">{{ currentBatch?.landName }}</el-descriptions-item>
            <el-descriptions-item label="种植面积">{{ currentBatch?.cropArea }} 亩</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="task-management">
          <div class="task-header">
            <h3>任务管理</h3>
            <el-button type="primary" @click="handleAddTask">
              <el-icon><Plus /></el-icon>
              新增任务
            </el-button>
          </div>
          
          <!-- 任务视图切换 -->
          <el-tabs v-model="activeTaskView" class="task-tabs">
            <el-tab-pane label="列表视图" name="list">
              <!-- 任务列表表格 -->
              <el-table 
                :data="batchTaskList" 
                v-loading="taskListLoading" 
                stripe
                :style="{ marginBottom: '20px' }"
              >
                <el-table-column prop="taskName" label="任务名称" width="150" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag 
                      :type="getStatusTagType(row.status)"
                      size="small"
                    >
                      {{ getStatusText(row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="taskHeadName" label="负责人" width="140" />
                <el-table-column prop="planStart" label="计划开始时间" width="160">
                  <template #default="{ row }">
                    {{ row.planStart ? new Date(row.planStart).toLocaleString() : '' }}
                  </template>
                </el-table-column>
                <el-table-column prop="planFinish" label="计划结束时间" width="160">
                  <template #default="{ row }">
                    {{ row.planFinish ? new Date(row.planFinish).toLocaleString() : '' }}
                  </template>
                </el-table-column>
                <el-table-column prop="taskDetail" label="任务描述" show-overflow-tooltip />
                <el-table-column prop="remark" label="备注" width="150" show-overflow-tooltip />
                <el-table-column label="操作" width="220" fixed="right">
                  <template #default="{ row }">
                    <el-button type="primary" size="small" @click="handleEditTask(row)">
                      编辑
                    </el-button>
                    <el-button type="danger" size="small" @click="handleDeleteTask(row)">
                      删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            
            <el-tab-pane label="甘特图" name="gantt">
              <div class="gantt-container">
                <GanttChart 
                  :tasks="batchTaskList" 
                  v-loading="taskListLoading"
                />
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="batchTaskDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 任务新增/编辑对话框 -->
    <el-dialog
      v-model="taskFormDialogVisible"
      :title="isEditTask ? '编辑任务' : '新增任务'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="taskFormRef"
        :model="taskForm"
        :rules="taskFormRules"
        label-width="100px"
      >
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="taskForm.taskName" placeholder="请输入任务名称" />
        </el-form-item>

        <el-form-item label="任务负责人">
          <span>
            {{ currentBatch ? getBatchHeadName(currentBatch.batchHead as number | null | undefined) : '未设置' }}
          </span>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划开始" prop="planStart">
              <el-date-picker
                v-model="taskForm.planStart"
                type="datetime"
                placeholder="选择开始时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划结束" prop="planFinish">
              <el-date-picker
                v-model="taskForm.planFinish"
                type="datetime"
                placeholder="选择结束时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="任务描述">
          <el-input
            v-model="taskForm.taskDetail"
            type="textarea"
            :rows="3"
            placeholder="请输入任务描述"
          />
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input
            v-model="taskForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="taskFormDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveTask">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getTaskBatchListApi, deleteTaskBatchApi, addTaskApi, getGermplasmByIdApi, getLandByIdApi, addTaskBatchApi, getGermplasmListApi, getLandListApi, getTaskListApi, updateTaskApi, deleteTaskApi, getTaskBatchByIdApi, updateTaskBatchApi } from '@/api/plant'
import { getUserListApi, getUserByIdApi } from '@/api/system'
import type { TaskBatch, PlantTask, Germplasm, Land } from '@/types/plant'
import type { User } from '@/types/system'
import GanttChart from '@/components/GanttChart.vue'

// 扩展的批次类型，包含关联数据
interface TaskBatchWithDetails extends TaskBatch {
  germplasmName?: string
  landName?: string
}

// 搜索表单
const searchForm = reactive({
  batchName: '',
  germplasmId: null as number | null,
  landId: null as number | null
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

// 格式化日期时间为ISO 8601格式（用于后端LocalDateTime解析）
const formatDateTime = (dateStr: string) => {
  if (!dateStr) return dateStr
  const date = new Date(dateStr)
  return date.toISOString().slice(0, 19) // 去掉末尾的Z，保留YYYY-MM-DDTHH:mm:ss格式
}

// 批次任务对话框
const batchTaskDialogVisible = ref(false)
const currentBatch = ref<TaskBatchWithDetails | null>(null)
const activeTaskView = ref('list') // 任务视图：list 或 gantt

// 新建/编辑批次对话框
const addBatchDialogVisible = ref(false)
const addBatchFormRef = ref()
const isEditBatch = ref(false)
const currentEditBatchId = ref<number | null>(null)

// 新建/编辑批次表单
const addBatchForm = reactive({
  batchName: '',
  germplasmId: null as number | null,
  landId: null as number | null,
  cropArea: null as number | null,
  startTime: '',
  batchHead: null as number | null,
  remark: ''
})

// 新建批次表单验证规则
const addBatchRules = {
  batchName: [
    { required: true, message: '请输入批次名称', trigger: 'blur' },
    { min: 2, max: 50, message: '批次名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  germplasmId: [
    { required: true, message: '请选择种质', trigger: 'change' }
  ],
  landId: [
    { required: true, message: '请选择地块', trigger: 'change' }
  ],
  cropArea: [
    { required: true, message: '请输入种植面积', trigger: 'blur' },
    { type: 'number' as const, min: 0.1, message: '种植面积必须大于 0.1 亩', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  batchHead: [
    { required: true, message: '请选择负责人', trigger: 'change' }
  ]
}

// 下拉选项数据
const germplasmOptions = ref<Germplasm[]>([])
const landOptions = ref<Land[]>([])
const userOptions = ref<User[]>([])
const batchHeadNameCache = ref<Record<number, string>>({})

// 异步根据负责人ID获取负责人名称并缓存
const fetchBatchHeadNameById = async (batchHeadId: number) => {
  try {
    const user = await getUserByIdApi(batchHeadId)
    batchHeadNameCache.value[batchHeadId] = user.nickName || user.userName || '未知用户'
  } catch (error) {
    console.error('根据负责人ID获取用户失败:', error)
  }
}

// 根据负责人ID获取负责人名称（优先使用缓存和已加载列表）
const getBatchHeadName = (batchHeadId: number | null | undefined) => {
  if (!batchHeadId) {
    return '未设置'
  }

  // 先从已加载的用户列表中查找
  const userFromList = userOptions.value.find(u => u.userId === batchHeadId)
  if (userFromList) {
    const name = userFromList.nickName || userFromList.userName || '未知用户'
    batchHeadNameCache.value[batchHeadId] = name
    return name
  }

  // 再从缓存中取
  const cached = batchHeadNameCache.value[batchHeadId]
  if (cached) {
    return cached
  }

  // 启动异步加载
  fetchBatchHeadNameById(batchHeadId)
  return '加载中'
}

// 选中的地块面积
const selectedLandArea = computed(() => {
  const selectedLand = landOptions.value.find(land => land.landId === addBatchForm.landId)
  return selectedLand?.landArea || 0
})

// 确保最大值始终大于最小值的安全最大值
const safeMaxArea = computed(() => {
  return Math.max(0.1, selectedLandArea.value)
})

// 任务管理相关状态
const batchTaskList = ref<PlantTask[]>([])
const taskListLoading = ref(false)
const taskFormDialogVisible = ref(false)
const taskFormRef = ref()
const isEditTask = ref(false)
const currentEditTask = ref<PlantTask | null>(null)

// 任务表单
const taskForm = reactive({
  taskName: '',
  taskHead: null as number | null,
  taskHeadName: '',
  planStart: '',
  planFinish: '',
  taskDetail: '',
  remark: ''
})

// 任务表单验证规则
const taskFormRules = {
  taskName: [
    { required: true, message: '请输入任务名称', trigger: 'blur' },
    { min: 2, max: 50, message: '任务名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  planStart: [
    { required: true, message: '请选择计划开始时间', trigger: 'change' }
  ],
  planFinish: [
    { required: true, message: '请选择计划结束时间', trigger: 'change' }
  ]
}

// 状态相关工具函数
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    '0': '未分配',
    '1': '已分配', 
    '2': '进行中',
    '3': '已完成'
  }
  return statusMap[status] || '未知状态'
}

const getStatusTagType = (status: string) => {
  const typeMap: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger' | ''> = {
    '0': 'info',
    '1': 'warning',
    '2': 'primary', 
    '3': 'success'
  }
  return typeMap[status] || 'info'
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.size,
      batchName: searchForm.batchName || undefined,
      germplasmId: searchForm.germplasmId ?? undefined,
      landId: searchForm.landId ?? undefined
    }
    
    const result = await getTaskBatchListApi(params)
    
    // 为每个批次获取种质和地块名称
    const enrichedData = await Promise.all(
      result.records.map(async (batch: any) => {
        try {
          // 获取种质名称
          let germplasmName = '未知种质'
          if (batch.germplasmId) {
            try {
              const germplasm = await getGermplasmByIdApi(batch.germplasmId)
              germplasmName = germplasm.germplasmName || '未知种质'
            } catch {
              // 如果获取失败，保持默认值
            }
          }
          
          // 获取地块名称
          let landName = '未知地块'
          if (batch.landId) {
            try {
              const land = await getLandByIdApi(batch.landId)
              landName = land.landName || '未知地块'
            } catch {
              // 如果获取失败，保持默认值
            }
          }
          
          return {
            ...batch,
            germplasmName,
            landName
          }
        } catch {
          return {
            ...batch,
            germplasmName: '未知种质',
            landName: '未知地块'
          }
        }
      })
    )
    
    tableData.value = enrichedData
    pagination.total = result.total
    
  } catch (error) {
    ElMessage.error('获取数据失败')
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
    batchName: '',
    germplasmId: null,
    landId: null
  })
  handleSearch()
}

// 获取下拉选项数据
const fetchDropdownData = async () => {
  try {
    // 获取种质列表
    const germplasmResult = await getGermplasmListApi({ pageNum: 1, pageSize: 1000 })
    germplasmOptions.value = germplasmResult.records
    
    // 获取地块列表
    const landResult = await getLandListApi({ pageNum: 1, pageSize: 1000 })
    landOptions.value = landResult.records
    
    // 获取系统用户列表（作为负责人选择）
    const userResult = await getUserListApi({ pageNum: 1, pageSize: 1000 })
    userOptions.value = userResult.records || []
    
  } catch (error) {
    console.error('获取下拉选项数据失败:', error)
  }
}

// 新增
const handleAdd = async () => {
  isEditBatch.value = false
  currentEditBatchId.value = null
  
  // 重置表单
  Object.assign(addBatchForm, {
    batchName: '',
    germplasmId: null,
    landId: null,
    cropArea: null,
    startTime: '',
    batchHead: null,
    remark: ''
  })
  
  // 清除表单验证状态
  if (addBatchFormRef.value) {
    addBatchFormRef.value.clearValidate()
  }
  
  // 先获取下拉选项数据，然后显示对话框
  try {
    await fetchDropdownData()
  } catch (error) {
    console.error('Failed to fetch dropdown data:', error)
    ElMessage.warning('获取选项数据失败，请重试')
    return
  }
  
  // 显示对话框
  addBatchDialogVisible.value = true
}

// 查看（预留，当前仅弹提示）
const handleView = (row: TaskBatch) => {
  ElMessage.info(`查看批次: ${row.batchName}`)
}

// 编辑批次
const handleEdit = async (row: TaskBatch) => {
  try {
    isEditBatch.value = true
    currentEditBatchId.value = row.batchId
    
    // 获取下拉选项数据
    await fetchDropdownData()
    
    // 根据ID获取批次详情
    const detail = await getTaskBatchByIdApi(row.batchId)
    
    // 填充表单
    Object.assign(addBatchForm, {
      batchName: detail.batchName,
      germplasmId: detail.germplasmId ?? null,
      landId: detail.landId ?? null,
      cropArea: detail.cropArea ?? null,
      startTime: detail.startTime || '',
      batchHead: detail.batchHead ?? null,
      remark: detail.remark || ''
    })
    
    // 清除验证状态
    if (addBatchFormRef.value) {
      addBatchFormRef.value.clearValidate()
    }
    
    addBatchDialogVisible.value = true
  } catch (error) {
    console.error('获取批次详情失败:', error)
    ElMessage.error('获取批次详情失败')
  }
}

// 批次任务
const handleBatchTask = async (row: TaskBatch) => {
  currentBatch.value = row
  batchTaskDialogVisible.value = true
  
  // 加载该批次的任务列表
  await fetchBatchTaskList()
}

// 获取批次任务列表
const fetchBatchTaskList = async () => {
  if (!currentBatch.value) return
  
  taskListLoading.value = true
  try {
    const result = await getTaskListApi({
      pageNum: 1,
      pageSize: 100,
      batchId: currentBatch.value.batchId
    })
    batchTaskList.value = result.records
  } catch (error) {
    ElMessage.error('获取任务列表失败')
  } finally {
    taskListLoading.value = false
  }
}

// 新增任务
const handleAddTask = () => {
  isEditTask.value = false
  currentEditTask.value = null
  
  // 重置表单
  Object.assign(taskForm, {
    taskName: '',
    taskHead: null,
    taskHeadName: '',
    planStart: '',
    planFinish: '',
    taskDetail: '',
    remark: ''
  })
  
  taskFormDialogVisible.value = true
}

// 编辑任务
const handleEditTask = (task: PlantTask) => {
  isEditTask.value = true
  currentEditTask.value = task
  
  // 填充表单数据
  Object.assign(taskForm, {
    taskName: task.taskName,
    taskHead: task.taskHead ?? null,
    taskHeadName: task.taskHeadName || '',
    planStart: task.planStart,
    planFinish: task.planFinish,
    taskDetail: task.taskDetail,
    remark: task.remark
  })
  
  taskFormDialogVisible.value = true
}

// 删除任务
const handleDeleteTask = async (task: PlantTask) => {
  try {
    await ElMessageBox.confirm(
      `确认删除任务"${task.taskName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteTaskApi(task.taskId.toString())
    ElMessage.success('删除成功')
    
    // 刷新任务列表
    await fetchBatchTaskList()
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 保存任务
const handleSaveTask = async () => {
  try {
    // 表单验证
    await taskFormRef.value?.validate()
    
    // 验证时间逻辑
    if (new Date(taskForm.planStart) >= new Date(taskForm.planFinish)) {
      ElMessage.warning('计划结束时间必须晚于开始时间')
      return
    }
    
    // 使用批次负责人作为任务负责人
    const taskHead = currentBatch.value?.batchHead ?? null
    if (!taskHead) {
      ElMessage.warning('当前批次未设置负责人，无法保存任务')
      return
    }

    const taskHeadName = getBatchHeadName(taskHead)

    const taskData = {
      taskName: taskForm.taskName,
      taskHead,
      taskHeadName,
      planStart: formatDateTime(taskForm.planStart),
      planFinish: formatDateTime(taskForm.planFinish),
      taskDetail: taskForm.taskDetail,
      remark: taskForm.remark,
      batchId: currentBatch.value?.batchId,
      status: '0' // 未分配状态
    }
    
    if (isEditTask.value && currentEditTask.value) {
      // 编辑模式
      await updateTaskApi({
        ...taskData,
        taskId: currentEditTask.value.taskId
      })
      ElMessage.success('任务更新成功')
    } else {
      // 新增模式
      await addTaskApi(taskData)
      ElMessage.success('任务创建成功')
    }
    
    taskFormDialogVisible.value = false
    
    // 刷新任务列表
    await fetchBatchTaskList()
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(isEditTask.value ? '任务更新失败' : '任务创建失败')
    }
  }
}

// 删除
const handleDelete = async (row: TaskBatch) => {
  try {
    await ElMessageBox.confirm(
      `确认删除批次"${row.batchName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteTaskBatchApi(row.batchId.toString())
    ElMessage.success('删除成功')
    fetchData()
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 保存新建/编辑批次
const handleSaveAddBatch = async () => {
  try {
    // 表单验证
    await addBatchFormRef.value?.validate()
    
    // 验证种植面积不能超过地块面积
    if (addBatchForm.cropArea && addBatchForm.cropArea > selectedLandArea.value) {
      ElMessage.warning(`种植面积不能超过地块面积 ${selectedLandArea.value} 亩`)
      return
    }
    
    // 验证开始时间不能在当前时间之前
    if (new Date(addBatchForm.startTime) < new Date()) {
      ElMessage.warning('开始时间不能在当前时间之前')
      return
    }

    const baseData = {
      batchName: addBatchForm.batchName,
      germplasmId: addBatchForm.germplasmId!,
      landId: addBatchForm.landId!,
      cropArea: addBatchForm.cropArea!,
      startTime: formatDateTime(addBatchForm.startTime),
      batchHead: addBatchForm.batchHead!,
      remark: addBatchForm.remark
    }

    if (isEditBatch.value && currentEditBatchId.value) {
      const updateData = {
        ...baseData,
        batchId: currentEditBatchId.value
      }
      await updateTaskBatchApi(updateData)
      ElMessage.success('批次更新成功')
    } else {
      await addTaskBatchApi(baseData)
      ElMessage.success('新建批次成功')
    }

    addBatchDialogVisible.value = false
    
    // 刷新列表
    fetchData()
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('新建批次失败')
    }
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.plant-batch-container {
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

.quantity {
  color: #67c23a;
  font-weight: bold;
}

.growth-days {
  color: #e6a23c;
  font-weight: bold;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}

/* 批次任务对话框样式 */
.batch-task-dialog {
  padding: 0;
}

.batch-info {
  margin-bottom: 30px;
}

.batch-info h3 {
  margin: 0 0 16px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.task-management {
  margin-top: 20px;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.task-header h3 {
  margin: 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.dialog-footer {
  text-align: right;
}

/* 新建批次对话框样式 */
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}

.el-input-number {
  width: 100%;
}

/* 任务标签页样式 */
.task-tabs {
  margin-top: 20px;
}

.gantt-container {
  margin-top: 20px;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.task-tabs .el-tabs__header {
  margin-bottom: 0;
}

.task-tabs .el-tabs__content {
  padding-top: 20px;
}
</style>
