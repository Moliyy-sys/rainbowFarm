<template>
  <div class="my-batch-container">
    
    <div class="content-layout">
      <!-- 左侧：种植批次列表 -->
      <div class="left-panel">
        <el-card class="batch-card">
        <template #header>
          <div class="card-header">
              <span>种植批次</span>
          </div>
        </template>
        
          <div class="batch-search">
              <el-input 
              v-model="batchSearchText" 
              placeholder="搜索批次名称"
              prefix-icon="Search"
                clearable
              @input="handleBatchSearch"
            />
          </div>
          
          <div class="batch-list">
            <div 
              v-for="batch in filteredBatches" 
              :key="batch.batchId"
              :class="['batch-item', { 'active': selectedBatch?.batchId === batch.batchId }]"
              @click="handleSelectBatch(batch)"
            >
              <div class="batch-info">
                <div class="batch-name">{{ batch.batchName }}</div>
                <div class="batch-details">
                  <div class="detail-item">
                    <span class="label">种质：</span>
                    <span class="value">{{ batch.germplasmName || '未知' }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="label">地块：</span>
                    <span class="value">{{ batch.landName || '未知' }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="label">面积：</span>
                    <span class="value area">{{ batch.cropArea }} 亩</span>
                  </div>
                  <div class="detail-item">
                    <span class="label">开始时间：</span>
                    <span class="value">{{ formatDate(batch.startTime) }}</span>
                  </div>
                </div>
              </div>
              <div class="batch-status">
                <el-tag size="small" type="success">进行中</el-tag>
              </div>
            </div>
            
            <div v-if="filteredBatches.length === 0" class="empty-state">
              <el-empty description="暂无批次数据" :image-size="80" />
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 右侧：批次任务列表 -->
      <div class="right-panel">
        <el-card class="task-card">
          <template #header>
            <div class="card-header">
              <span>
                批次任务
                <span v-if="selectedBatch" class="batch-name-tag">
                  ({{ selectedBatch.batchName }})
                </span>
              </span>
            </div>
          </template>
          
          <div v-if="!selectedBatch" class="no-selection">
            <el-empty description="请先选择左侧的批次" :image-size="120" />
          </div>
          
          <div v-else class="task-content">
            <!-- 批次基本信息 -->
            <div class="batch-summary">
              <el-descriptions :column="2" border size="small">
                <el-descriptions-item label="批次名称">{{ selectedBatch.batchName }}</el-descriptions-item>
                <el-descriptions-item label="种质名称">{{ selectedBatch.germplasmName }}</el-descriptions-item>
                <el-descriptions-item label="地块名称">{{ selectedBatch.landName }}</el-descriptions-item>
                <el-descriptions-item label="种植面积">{{ selectedBatch.cropArea }} 亩</el-descriptions-item>
              </el-descriptions>
            </div>
            
            <!-- 任务列表 -->
            <div class="task-list">
              <div class="task-header">
                <span>任务列表 ({{ batchTasks.length }})</span>
              </div>
              
              <el-table 
                :data="batchTasks" 
                v-loading="taskLoading" 
                stripe
                size="small"
                :height="400"
              >
                <el-table-column prop="taskName" label="任务名称" min-width="150" />
                <el-table-column prop="status" label="状态" width="100" align="center">
                  <template #default="{ row }">
                    <el-tag 
                      :type="getStatusTagType(row.status)"
                      size="small"
                    >
                      {{ getStatusText(row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="planStart" label="计划开始" width="120">
                  <template #default="{ row }">
                    {{ formatDate(row.planStart) }}
                  </template>
                </el-table-column>
                <el-table-column prop="planFinish" label="计划结束" width="120">
                  <template #default="{ row }">
                    {{ formatDate(row.planFinish) }}
                  </template>
                </el-table-column>
                <el-table-column prop="taskDetail" label="任务描述" show-overflow-tooltip />
                <el-table-column label="操作" width="120" align="center" fixed="right">
                  <template #default="{ row }">
                    <el-button 
                      type="primary" 
                      size="small" 
                      @click="handleTaskProcess(row)"
                    >
                      任务处理
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              
              <div v-if="batchTasks.length === 0 && !taskLoading" class="empty-tasks">
                <el-empty description="该批次暂无任务" :image-size="60" />
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
    
    <!-- 任务编辑对话框 -->
    <el-dialog
      v-model="taskDialogVisible"
      title="任务编辑"
      width="1200px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div v-if="currentTask" class="task-edit-content">
        <el-tabs v-model="activeTab" type="card">
          <!-- 基本信息 -->
          <el-tab-pane label="基本信息" name="basic">
            <el-form :model="taskForm" label-width="120px" size="small">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="任务名称">
                    <el-input v-model="taskForm.taskName" placeholder="请输入任务名称" />
            </el-form-item>
                </el-col>
                <el-col :span="12">
            <el-form-item label="任务状态">
                    <el-select v-model="taskForm.status" placeholder="请选择状态">
                <el-option label="未分配" value="0" />
                <el-option label="已分配" value="1" />
                <el-option label="进行中" value="2" />
                <el-option label="已完成" value="3" />
              </el-select>
            </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="责任人">
                    <el-select 
                      v-model="taskForm.taskHead" 
                      placeholder="请选择责任人"
                      filterable
                      @change="handleResponsibleChange"
                    >
                      <el-option 
                        v-for="user in users" 
                        :key="user.userId"
                        :label="user.nickName || user.userName"
                        :value="user.userId"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="参与雇员">
                    <el-select 
                      v-model="selectedEmployees" 
                      placeholder="请选择参与雇员"
                      multiple
                      filterable
                    >
                      <el-option 
                        v-for="employee in employees" 
                        :key="employee.employeeId"
                        :label="employee.employeeName"
                        :value="employee.employeeId"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="计划开始时间">
                    <el-date-picker
                      v-model="taskForm.planStart"
                      type="datetime"
                      placeholder="选择日期时间"
                      format="YYYY-MM-DD HH:mm:ss"
                      value-format="YYYY-MM-DD HH:mm:ss"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="计划结束时间">
                    <el-date-picker
                      v-model="taskForm.planFinish"
                      type="datetime"
                      placeholder="选择日期时间"
                      format="YYYY-MM-DD HH:mm:ss"
                      value-format="YYYY-MM-DD HH:mm:ss"
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
          </el-tab-pane>
          
          <!-- 人工工时 -->
          <el-tab-pane label="人工工时" name="employee-time">
            <div class="time-record-section">
              <div class="section-header">
                <span>人工工时记录</span>
                <el-button type="primary" size="small" @click="handleAddEmployeeTime">
                  <el-icon><Plus /></el-icon>
                  添加工时
                </el-button>
        </div>
        
              <el-table :data="employeeTimes" size="small" border>
                <el-table-column prop="employeeName" label="雇员姓名" width="120" />
                <el-table-column prop="workingHours" label="工时(小时)" width="100" />
                <el-table-column prop="workingStart" label="开始时间" width="150">
            <template #default="{ row }">
                    {{ formatDateTime(row.workingStart) }}
            </template>
          </el-table-column>
                <el-table-column prop="workingFinish" label="结束时间" width="150">
            <template #default="{ row }">
                    {{ formatDateTime(row.workingFinish) }}
            </template>
          </el-table-column>
                <el-table-column prop="remark" label="备注" show-overflow-tooltip />
                <el-table-column label="操作" width="80" align="center">
                  <template #default="{ row, $index }">
                    <el-button 
                      type="danger" 
                      size="small" 
                      @click="handleDeleteEmployeeTime($index)"
                    >
                      删除
                    </el-button>
            </template>
          </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>
          
          <!-- 机械工时 -->
          <el-tab-pane label="机械工时" name="machine-time">
            <div class="time-record-section">
              <div class="section-header">
                <span>机械工时记录</span>
                <el-button type="primary" size="small" @click="handleAddMachineTime">
                  <el-icon><Plus /></el-icon>
                  添加工时
                </el-button>
              </div>
              
              <el-table :data="machineTimes" size="small" border>
                <el-table-column prop="machineName" label="机械名称" width="120" />
                <el-table-column prop="machineCount" label="数量" width="80" />
                <el-table-column prop="workingHours" label="工时(小时)" width="100" />
                <el-table-column prop="workingStart" label="开始时间" width="150">
            <template #default="{ row }">
                    {{ formatDateTime(row.workingStart) }}
            </template>
          </el-table-column>
                <el-table-column prop="workingFinish" label="结束时间" width="150">
            <template #default="{ row }">
                    {{ formatDateTime(row.workingFinish) }}
            </template>
          </el-table-column>
                <el-table-column prop="remark" label="备注" show-overflow-tooltip />
                <el-table-column label="操作" width="80" align="center">
                  <template #default="{ row, $index }">
              <el-button 
                      type="danger" 
                size="small" 
                      @click="handleDeleteMachineTime($index)"
              >
                      删除
              </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>
          
          <!-- 农资用量 -->
          <el-tab-pane label="农资用量" name="material-usage">
            <div class="time-record-section">
              <div class="section-header">
                <span>农资用量记录</span>
                <el-button type="primary" size="small" @click="handleAddMaterialUsage">
                  <el-icon><Plus /></el-icon>
                  添加用量
              </el-button>
              </div>
              
              <el-table :data="materialUsages" size="small" border>
                <el-table-column prop="materialName" label="农资名称" width="150" />
                <el-table-column prop="materialCount" label="用量" width="100" />
                <el-table-column prop="measureUnit" label="单位" width="80" />
                <el-table-column prop="workingStart" label="使用开始时间" width="150">
            <template #default="{ row }">
                    {{ formatDateTime(row.workingStart) }}
            </template>
          </el-table-column>
                <el-table-column prop="workingFinish" label="使用结束时间" width="150">
            <template #default="{ row }">
                    {{ formatDateTime(row.workingFinish) }}
                  </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" show-overflow-tooltip />
                <el-table-column label="操作" width="80" align="center">
                  <template #default="{ row, $index }">
              <el-button 
                      type="danger" 
                size="small" 
                      @click="handleDeleteMaterialUsage($index)"
              >
                      删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
            </div>
          </el-tab-pane>
          
        </el-tabs>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="taskDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveTask">保存</el-button>
              <el-button 
            v-if="currentTask && currentTask.status === '1'"
            type="success" 
            @click="handleStartTask"
          >
            开始任务
              </el-button>
              <el-button 
            v-if="currentTask && currentTask.status === '2'"
            type="warning" 
            @click="handleCompleteTask"
          >
            完成任务
              </el-button>
        </span>
            </template>
    </el-dialog>
    
    <!-- 添加人工工时对话框 -->
    <el-dialog
      v-model="employeeTimeDialogVisible"
      title="添加人工工时"
      width="500px"
    >
      <el-form :model="employeeTimeForm" label-width="100px" size="small">
        <el-form-item label="雇员" required>
          <el-select v-model="employeeTimeForm.employeeId" placeholder="请选择雇员" filterable>
            <el-option 
              v-for="employee in employees" 
              :key="employee.employeeId"
              :label="employee.employeeName"
              :value="employee.employeeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="工时(小时)" required>
          <el-input-number 
            v-model="employeeTimeForm.workingHours" 
            :min="0" 
            :precision="2"
            placeholder="请输入工时"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="开始时间" required>
          <el-date-picker
            v-model="employeeTimeForm.workingStart"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" required>
          <el-date-picker
            v-model="employeeTimeForm.workingFinish"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input 
            v-model="employeeTimeForm.remark" 
            type="textarea" 
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="employeeTimeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleConfirmEmployeeTime">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 添加机械工时对话框 -->
    <el-dialog
      v-model="machineTimeDialogVisible"
      title="添加机械工时"
      width="500px"
    >
      <el-form :model="machineTimeForm" label-width="100px" size="small">
        <el-form-item label="机械" required>
          <el-select v-model="machineTimeForm.machineId" placeholder="请选择机械" filterable>
            <el-option 
              v-for="machine in machines" 
              :key="machine.machineId"
              :label="machine.machineName"
              :value="machine.machineId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" required>
          <el-input-number 
            v-model="machineTimeForm.machineCount" 
            :min="1" 
            placeholder="请输入数量"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="工时(小时)" required>
          <el-input-number 
            v-model="machineTimeForm.workingHours" 
            :min="0" 
            :precision="2"
            placeholder="请输入工时"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="开始时间" required>
          <el-date-picker
            v-model="machineTimeForm.workingStart"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" required>
          <el-date-picker
            v-model="machineTimeForm.workingFinish"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input 
            v-model="machineTimeForm.remark" 
            type="textarea" 
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="machineTimeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleConfirmMachineTime">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 添加农资用量对话框 -->
    <el-dialog
      v-model="materialUsageDialogVisible"
      title="添加农资用量"
      width="500px"
    >
      <el-form :model="materialUsageForm" label-width="100px" size="small">
        <el-form-item label="农资" required>
          <el-select v-model="materialUsageForm.materialId" placeholder="请选择农资" filterable>
            <el-option 
              v-for="material in materials" 
              :key="material.materialId"
              :label="material.materialName"
              :value="material.materialId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="用量" required>
          <el-input-number 
            v-model="materialUsageForm.materialCount" 
            :min="0" 
            :precision="2"
            placeholder="请输入用量"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="计量单位">
          <el-input
            :model-value="currentMaterialUnit"
            disabled
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input 
            v-model="materialUsageForm.remark" 
            type="textarea" 
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="materialUsageDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleConfirmMaterialUsage">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { 
  getUserTaskBatchListApi,
  getTaskListApi, 
  updateTaskApi, 
  getGermplasmByIdApi, 
  getLandByIdApi,
  getEmployeeListApi,
  getMaterialListApi,
  getMachineListApi,
  addTaskEmployeeTimeApi,
  addTaskMachineTimeApi,
  addTaskMaterialTimeApi,
  getTaskEmployeeTimeListApi,
  getTaskMachineTimeListApi,
  getTaskMaterialTimeListApi,
  getTaskEmployeesApi,
  addTaskEmployeesApi,
  removeAllTaskEmployeesApi,
  deleteTaskEmployeeTimeApi,
  deleteTaskMachineTimeApi,
  deleteTaskMaterialTimeApi
} from '@/api/plant'
import { getUserListApi } from '@/api/system'
import { useUserStore } from '@/store/modules/user'
import type { 
  TaskBatch, 
  PlantTask, 
  Employee, 
  Material, 
  Machine, 
  TaskEmployeeTime, 
  TaskMachineTime, 
  TaskMaterialTime 
} from '@/types/plant'
import type { User } from '@/types/system'

// 扩展的批次类型，包含关联数据
interface TaskBatchWithDetails extends TaskBatch {
  germplasmName?: string
  landName?: string
}

// 用户信息
const userStore = useUserStore()

// 批次相关状态
const batchList = ref<TaskBatchWithDetails[]>([])
const batchSearchText = ref('')
const selectedBatch = ref<TaskBatchWithDetails | null>(null)
const batchLoading = ref(false)

// 任务相关状态
const batchTasks = ref<PlantTask[]>([])
const taskLoading = ref(false)
const taskDialogVisible = ref(false)
const currentTask = ref<PlantTask | null>(null)
const activeTab = ref('basic')

// 任务表单
const taskForm = reactive({
  taskId: 0,
  batchId: 0,
  taskHead: undefined as number | undefined,
  taskHeadName: '',
  taskName: '',
  planStart: '',
  planFinish: '',
  actualStart: '',
  actualFinish: '',
  taskDetail: '',
  taskImages: '',
  taskVideos: '',
  remark: '',
  status: '0',
  createBy: 0,
  createTime: '',
  updateBy: 0,
  updateTime: ''
})

// 基础数据
const employees = ref<Employee[]>([])
const users = ref<User[]>([]) // 系统用户列表
const materials = ref<Material[]>([])
const machines = ref<Machine[]>([])
const selectedEmployees = ref<number[]>([])

// 工时和用量数据
const employeeTimes = ref<any[]>([])
const machineTimes = ref<any[]>([])
const materialUsages = ref<any[]>([])

// 当前选择农资的计量单位
const currentMaterialUnit = computed(() => {
  if (!materialUsageForm.materialId) {
    return ''
  }
  const material = materials.value.find(m => m.materialId === materialUsageForm.materialId)
  return material?.measureUnit || ''
})

// 对话框状态
const employeeTimeDialogVisible = ref(false)
const machineTimeDialogVisible = ref(false)
const materialUsageDialogVisible = ref(false)

// 表单数据
const employeeTimeForm = reactive({
  taskId: 0,
  employeeId: null as number | null,
  workingHours: 0,
  workingStart: '',
  workingFinish: '',
  remark: ''
})

const machineTimeForm = reactive({
  taskId: 0,
  machineId: null as number | null,
  machineCount: 1,
  workingHours: 0,
  workingStart: '',
  workingFinish: '',
  remark: ''
})

const materialUsageForm = reactive({
  taskId: 0,
  materialId: null as number | null,
  materialCount: 0,
  remark: ''
})

// 过滤后的批次列表
const filteredBatches = computed(() => {
  if (!batchSearchText.value) {
    return batchList.value
  }
  return batchList.value.filter(batch => 
    batch.batchName.toLowerCase().includes(batchSearchText.value.toLowerCase())
  )
})

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

// 格式化日期时间（用于展示）
const formatDateTime = (dateStr: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

// 格式化日期时间为ISO 8601格式（用于后端LocalDateTime解析）
const formatToLocalDateTime = (dateStr: string) => {
  if (!dateStr) return dateStr
  const date = new Date(dateStr)
  return date.toISOString().slice(0, 19)
}

// 获取状态标签类型
const getStatusTagType = (status: string) => {
  const statusMap: Record<string, 'info' | 'warning' | 'primary' | 'success'> = {
    '0': 'info',     // 未分配
    '1': 'warning',  // 已分配
    '2': 'primary',  // 进行中
    '3': 'success'   // 已完成
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    '0': '未分配',
    '1': '已分配',
    '2': '进行中',
    '3': '已完成'
  }
  return statusMap[status] || '未知'
}

// 获取我的批次列表（当前登录用户负责的批次）
const fetchMyBatches = async () => {
  batchLoading.value = true
  try {
    const currentUserId = userStore.userInfo?.userId
    if (!currentUserId) {
      ElMessage.error('未获取到当前用户信息，无法加载我的批次')
      return
    }

    const result = await getUserTaskBatchListApi({
      pageNum: 1,
      pageSize: 100,
      batchHead: currentUserId
    })
    
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
    
    batchList.value = enrichedData
    
  } catch (error) {
    ElMessage.error('获取批次数据失败')
    console.error('获取批次列表失败:', error)
  } finally {
    batchLoading.value = false
  }
}

// 获取批次任务列表
const fetchBatchTasks = async (batchId: number) => {
  taskLoading.value = true
  try {
    const result = await getTaskListApi({
      pageNum: 1,
      pageSize: 100,
      batchId: batchId
    })
    batchTasks.value = result.records || []
  } catch (error) {
    ElMessage.error('获取任务列表失败')
    console.error('获取任务列表失败:', error)
  } finally {
    taskLoading.value = false
  }
}

// 批次搜索
const handleBatchSearch = () => {
  // 搜索逻辑已通过computed实现
}

// 选择批次
const handleSelectBatch = async (batch: TaskBatchWithDetails) => {
  selectedBatch.value = batch
  await fetchBatchTasks(batch.batchId)
}

// 获取基础数据
const fetchBasicData = async () => {
  try {
    // 获取雇员列表
    const employeeResult = await getEmployeeListApi({
      pageNum: 1,
      pageSize: 100
    })
    employees.value = employeeResult.records || []

    // 获取系统用户列表（用于责任人选择）
    const userResult = await getUserListApi({
      pageNum: 1,
      pageSize: 100,
      status: '0' // 只获取正常状态的用户
    })
    users.value = userResult.records || []

    // 获取农资列表
    const materialResult = await getMaterialListApi({
      pageNum: 1,
      pageSize: 100
    })
    materials.value = materialResult.records || []

    // 获取机械列表
    const machineResult = await getMachineListApi({
      pageNum: 1,
      pageSize: 100
    })
    machines.value = machineResult.records || []

  } catch (error) {
    console.error('获取基础数据失败:', error)
  }
}

// 任务处理
const handleTaskProcess = async (task: PlantTask) => {
  currentTask.value = task
  
  // 初始化表单数据
  Object.assign(taskForm, {
    taskId: task.taskId,
    batchId: task.batchId,
    taskHead: task.taskHead,
    taskHeadName: task.taskHeadName,
    taskName: task.taskName,
    planStart: task.planStart,
    planFinish: task.planFinish,
    actualStart: task.actualStart,
    actualFinish: task.actualFinish,
    taskDetail: task.taskDetail,
    taskImages: task.taskImages,
    taskVideos: task.taskVideos,
    remark: task.remark,
    status: task.status,
    createBy: task.createBy,
    createTime: task.createTime,
    updateBy: task.updateBy,
    updateTime: task.updateTime
  })
  
  // 初始化工时和用量数据
  await loadTaskDetails(task.taskId)
  
  // 获取基础数据
  await fetchBasicData()
  
  // 加载参与雇员
  try {
    const taskEmployees = await getTaskEmployeesApi(task.taskId)
    selectedEmployees.value = taskEmployees.map(emp => emp.employeeId)
  } catch (error) {
    console.error('获取任务参与雇员失败:', error)
  }
  
  taskDialogVisible.value = true
  activeTab.value = 'basic'
}

// 加载任务详细信息
const loadTaskDetails = async (taskId: number) => {
  try {
    // 加载人工工时
    const employeeTimeResult = await getTaskEmployeeTimeListApi({
      pageNum: 1,
      pageSize: 100,
      taskId: taskId
    })
    employeeTimes.value = (employeeTimeResult.records || []).map(item => ({
      ...item,
      employeeName: employees.value.find(emp => emp.employeeId === item.employeeId)?.employeeName || '未知'
    }))

    // 加载机械工时
    const machineTimeResult = await getTaskMachineTimeListApi({
      pageNum: 1,
      pageSize: 100,
      taskId: taskId
    })
    machineTimes.value = (machineTimeResult.records || []).map(item => ({
      ...item,
      machineName: machines.value.find(machine => machine.machineId === item.machineId)?.machineName || '未知'
    }))

    // 加载农资用量
    const materialTimeResult = await getTaskMaterialTimeListApi({
      pageNum: 1,
      pageSize: 100,
      taskId: taskId
    })
    materialUsages.value = (materialTimeResult.records || []).map(item => ({
      ...item,
      materialName: materials.value.find(material => material.materialId === item.materialId)?.materialName || '未知',
      measureUnit: materials.value.find(material => material.materialId === item.materialId)?.measureUnit || ''
    }))

  } catch (error) {
    console.error('加载任务详情失败:', error)
  }
}

// 责任人变更处理
const handleResponsibleChange = (userId: number) => {
  const user = users.value.find(u => u.userId === userId)
  if (user) {
    taskForm.taskHeadName = user.nickName || user.userName
  }
}

// 保存任务
const handleSaveTask = async () => {
  try {
    if (!currentTask.value) {
      ElMessage.error('当前任务信息不存在')
      return
    }

    // 构建更新的任务数据
    const updateData = {
      ...taskForm,
      taskHead: taskForm.taskHead || 0, // 确保taskHead不为undefined
    }
    
    await updateTaskApi(updateData)

    // 先清空原有参与雇员，再按当前选择重新保存
    const taskId = currentTask.value.taskId
    await removeAllTaskEmployeesApi(taskId)
    if (selectedEmployees.value.length > 0) {
      await addTaskEmployeesApi({
        taskId,
        employeeIds: selectedEmployees.value.join(',')
      })
    }

    ElMessage.success('任务保存成功')
    
    // 刷新任务列表
    if (selectedBatch.value) {
      await fetchBatchTasks(selectedBatch.value.batchId)
    }
    
  } catch (error) {
    ElMessage.error('任务保存失败')
    console.error('保存任务失败:', error)
  }
}

// 添加人工工时
const handleAddEmployeeTime = () => {
  // 重置表单
  Object.assign(employeeTimeForm, {
    taskId: currentTask.value?.taskId || 0,
    employeeId: null,
    workingHours: 0,
    workingStart: '',
    workingFinish: '',
    remark: ''
  })
  employeeTimeDialogVisible.value = true
}

// 确认添加人工工时
const handleConfirmEmployeeTime = async () => {
  if (!employeeTimeForm.employeeId) {
    ElMessage.warning('请选择雇员')
    return
  }
  
  if (!employeeTimeForm.workingHours || employeeTimeForm.workingHours <= 0) {
    ElMessage.warning('请输入有效的工时')
    return
  }
  
  if (!employeeTimeForm.workingStart || !employeeTimeForm.workingFinish) {
    ElMessage.warning('请选择开始和结束时间')
    return
  }

  if (!currentTask.value) {
    ElMessage.error('当前任务信息不存在')
    return
  }

  try {
    await addTaskEmployeeTimeApi({
      taskId: currentTask.value.taskId,
      employeeId: employeeTimeForm.employeeId!,
      workingHours: employeeTimeForm.workingHours,
      workingStart: formatToLocalDateTime(employeeTimeForm.workingStart),
      workingFinish: formatToLocalDateTime(employeeTimeForm.workingFinish),
      remark: employeeTimeForm.remark
    })

    employeeTimeDialogVisible.value = false
    ElMessage.success('工时记录添加成功')

    // 重新加载工时列表
    await loadTaskDetails(currentTask.value.taskId)
  } catch (error) {
    ElMessage.error('工时记录添加失败')
    console.error('添加人工工时失败:', error)
  }
}

// 删除人工工时
const handleDeleteEmployeeTime = async (index: number) => {
  const record = employeeTimes.value[index]
  if (!record || !record.costId) {
    employeeTimes.value.splice(index, 1)
    return
  }

  try {
    await deleteTaskEmployeeTimeApi(String(record.costId))
    ElMessage.success('工时记录删除成功')

    if (currentTask.value) {
      await loadTaskDetails(currentTask.value.taskId)
    }
  } catch (error) {
    ElMessage.error('工时记录删除失败')
    console.error('删除人工工时失败:', error)
  }
}

// 添加机械工时
const handleAddMachineTime = () => {
  // 重置表单
  Object.assign(machineTimeForm, {
    taskId: currentTask.value?.taskId || 0,
    machineId: null,
    machineCount: 1,
    workingHours: 0,
    workingStart: '',
    workingFinish: '',
    remark: ''
  })
  machineTimeDialogVisible.value = true
}

// 确认添加机械工时
const handleConfirmMachineTime = async () => {
  if (!machineTimeForm.machineId) {
    ElMessage.warning('请选择机械')
    return
  }
  
  if (!machineTimeForm.workingHours || machineTimeForm.workingHours <= 0) {
    ElMessage.warning('请输入有效的工时')
    return
  }
  
  if (!machineTimeForm.workingStart || !machineTimeForm.workingFinish) {
    ElMessage.warning('请选择开始和结束时间')
    return
  }

  if (!currentTask.value) {
    ElMessage.error('当前任务信息不存在')
    return
  }

  try {
    await addTaskMachineTimeApi({
      taskId: currentTask.value.taskId,
      machineId: machineTimeForm.machineId!,
      machineCount: machineTimeForm.machineCount,
      workingHours: machineTimeForm.workingHours,
      workingStart: formatToLocalDateTime(machineTimeForm.workingStart),
      workingFinish: formatToLocalDateTime(machineTimeForm.workingFinish),
      remark: machineTimeForm.remark
    })

    machineTimeDialogVisible.value = false
    ElMessage.success('机械工时记录添加成功')

    if (currentTask.value) {
      await loadTaskDetails(currentTask.value.taskId)
    }
  } catch (error) {
    ElMessage.error('机械工时记录添加失败')
    console.error('添加机械工时失败:', error)
  }
}

// 删除机械工时
const handleDeleteMachineTime = async (index: number) => {
  const record = machineTimes.value[index]
  if (!record || !record.costId) {
    machineTimes.value.splice(index, 1)
    return
  }

  try {
    await deleteTaskMachineTimeApi(String(record.costId))
    ElMessage.success('机械工时记录删除成功')

    if (currentTask.value) {
      await loadTaskDetails(currentTask.value.taskId)
    }
  } catch (error) {
    ElMessage.error('机械工时记录删除失败')
    console.error('删除机械工时失败:', error)
  }
}

// 添加农资用量
const handleAddMaterialUsage = () => {
  // 重置表单
  Object.assign(materialUsageForm, {
    taskId: currentTask.value?.taskId || 0,
    materialId: null,
    materialCount: 0,
    remark: ''
  })
  materialUsageDialogVisible.value = true
}

// 确认添加农资用量
const handleConfirmMaterialUsage = async () => {
  if (!materialUsageForm.materialId) {
    ElMessage.warning('请选择农资')
    return
  }
  
  if (!materialUsageForm.materialCount || materialUsageForm.materialCount <= 0) {
    ElMessage.warning('请输入有效的用量')
    return
  }
  if (!currentTask.value) {
    ElMessage.error('当前任务信息不存在')
    return
  }

  try {
    await addTaskMaterialTimeApi({
      taskId: currentTask.value.taskId,
      materialId: materialUsageForm.materialId!,
      materialCount: materialUsageForm.materialCount,
      measureUnit: currentMaterialUnit.value,
      remark: materialUsageForm.remark
    })

    materialUsageDialogVisible.value = false
    ElMessage.success('农资用量记录添加成功')

    if (currentTask.value) {
      await loadTaskDetails(currentTask.value.taskId)
    }
  } catch (error) {
    ElMessage.error('农资用量记录添加失败')
    console.error('添加农资用量失败:', error)
  }
}

// 删除农资用量
const handleDeleteMaterialUsage = async (index: number) => {
  const record = materialUsages.value[index]
  if (!record || !record.costId) {
    materialUsages.value.splice(index, 1)
    return
  }

  try {
    await deleteTaskMaterialTimeApi(String(record.costId))
    ElMessage.success('农资用量记录删除成功')

    if (currentTask.value) {
      await loadTaskDetails(currentTask.value.taskId)
    }
  } catch (error) {
    ElMessage.error('农资用量记录删除失败')
    console.error('删除农资用量失败:', error)
  }
}

// 开始任务
const handleStartTask = async () => {
  if (!currentTask.value) return
  
  try {
    await ElMessageBox.confirm(
      `确认开始任务"${currentTask.value.taskName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const updatedTask = {
      ...currentTask.value,
      status: '2', // 进行中
      actualStart: new Date().toISOString()
    }
    
    await updateTaskApi(updatedTask)
    ElMessage.success('任务已开始')
    
    // 刷新任务列表
    if (selectedBatch.value) {
      await fetchBatchTasks(selectedBatch.value.batchId)
    }
    
    taskDialogVisible.value = false
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('开始任务失败')
    }
  }
}

// 完成任务
const handleCompleteTask = async () => {
  if (!currentTask.value) return
  
  try {
    await ElMessageBox.confirm(
      `确认完成任务"${currentTask.value.taskName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )
    
    const updatedTask = {
      ...currentTask.value,
      status: '3', // 已完成
      actualFinish: new Date().toISOString()
    }
    
    await updateTaskApi(updatedTask)
    ElMessage.success('任务已完成')
    
    // 刷新任务列表
    if (selectedBatch.value) {
      await fetchBatchTasks(selectedBatch.value.batchId)
    }
    
    taskDialogVisible.value = false
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('完成任务失败')
    }
  }
}


onMounted(() => {
  fetchMyBatches()
})
</script>

<style scoped>
.my-batch-container {
  padding: 0px;
  height: calc(100vh - 160px);
  display: flex;
  flex-direction: column;
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

.content-layout {
  display: flex;
  gap: 20px;
  flex: 1;
  overflow: hidden;
}

/* 左侧面板 */
.left-panel {
  width: 400px;
  flex-shrink: 0;
}

.batch-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.batch-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.batch-search {
  margin-bottom: 16px;
}

.batch-list {
  flex: 1;
  overflow-y: auto;
}

.batch-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.batch-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.batch-item.active {
  border-color: #409eff;
  background-color: #f0f8ff;
}

.batch-info {
  flex: 1;
}

.batch-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.batch-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item {
  font-size: 13px;
  color: #606266;
  display: flex;
  align-items: center;
}

.detail-item .label {
  min-width: 50px;
  color: #909399;
}

.detail-item .value {
  color: #303133;
}

.detail-item .value.area {
  color: #409eff;
  font-weight: 600;
}

.batch-status {
  margin-left: 12px;
}

/* 右侧面板 */
.right-panel {
  flex: 1;
  min-width: 0;
}

.task-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.task-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.batch-name-tag {
  color: #409eff;
  font-weight: normal;
  font-size: 14px;
}

.no-selection {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.task-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.batch-summary {
  margin-bottom: 20px;
}

.task-list {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.task-header {
  margin-bottom: 16px;
  font-weight: 600;
  color: #303133;
}

.empty-state,
.empty-tasks {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}

/* 任务处理对话框 */
.task-process-content {
  padding: 0;
}

.task-actions {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.task-actions h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 14px;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.dialog-footer {
  text-align: right;
}

/* 任务编辑样式 */
.task-edit-content {
  min-height: 500px;
}

.time-record-section {
  padding: 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-weight: 600;
  color: #303133;
}

.attachment-section {
  padding: 0;
}

.attachment-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 14px;
  font-weight: 600;
}

:deep(.el-upload-list--picture-card) {
  --el-upload-list-picture-card-size: 100px;
}

:deep(.el-upload--picture-card) {
  --el-upload-picture-card-size: 100px;
}
</style>