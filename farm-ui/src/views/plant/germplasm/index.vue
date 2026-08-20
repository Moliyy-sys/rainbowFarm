<template>
  <div class="germplasm-container">
    <div class="content-area">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>种质列表</span>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加种质
            </el-button>
          </div>
        </template>
        
        <div class="search-area">
          <el-form :model="searchForm" inline>
            <el-form-item label="种质名称">
              <el-input 
                v-model="searchForm.germplasmName" 
                placeholder="请输入种质名称"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="作物名称">
              <el-input 
                v-model="searchForm.cropName" 
                placeholder="请输入作物名称"
                clearable
                style="width: 200px"
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
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="cropName" label="作物名称" min-width="120" />
          <el-table-column prop="cropEnName" label="作物英文名称" min-width="150" show-overflow-tooltip />
          <el-table-column prop="germplasmName" label="种质名称" min-width="150" />
          <el-table-column prop="germplasmEnName" label="种质英文名称" min-width="150" show-overflow-tooltip />
          <el-table-column prop="germplasmDes" label="宣传语" min-width="200" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="340" fixed="right">
            <template #default="{ row }">
              <div class="operation-buttons">
                <el-button type="primary" size="small" @click="handleView(row)">
                  查看
                </el-button>
                <el-button type="info" size="small" @click="handleEdit(row)">
                  编辑
                </el-button>
                <el-button type="danger" size="small" @click="handleDelete(row)">
                  删除
                </el-button>
                <el-dropdown @command="(command) => handleDropdownCommand(command, row)">
                  <el-button type="success" size="small">
                    更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="method">种植方法</el-dropdown-item>
                      <el-dropdown-item command="intro">种质介绍</el-dropdown-item>
                      <el-dropdown-item command="job">作业流程</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="table-footer">
          <div class="batch-actions">
            <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">
              批量删除
            </el-button>
          </div>
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
    
    <!-- 种质详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="种质详情" 
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="currentGermplasm" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="作物名称">{{ currentGermplasm.cropName }}</el-descriptions-item>
          <el-descriptions-item label="作物英文名称">{{ currentGermplasm.cropEnName }}</el-descriptions-item>
          <el-descriptions-item label="种质名称">{{ currentGermplasm.germplasmName }}</el-descriptions-item>
          <el-descriptions-item label="种质英文名称">{{ currentGermplasm.germplasmEnName }}</el-descriptions-item>
          <el-descriptions-item label="宣传语" :span="2">{{ currentGermplasm.germplasmDes }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentGermplasm.remark || '无' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(currentGermplasm.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDateTime(currentGermplasm.updateTime) }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="currentGermplasm.germplasmImg" class="image-section">
          <h4>种质图片</h4>
          <el-image 
            :src="currentGermplasm.germplasmImg" 
            fit="cover" 
            style="width: 200px; height: 150px;"
            :preview-src-list="[currentGermplasm.germplasmImg]"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <!-- 添加/编辑种质对话框 -->
    <el-dialog 
      v-model="formDialogVisible" 
      :title="isEdit ? '编辑种质' : '添加种质'" 
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="120px">
        <el-form-item label="作物名称" prop="cropName">
          <el-input v-model="form.cropName" placeholder="请输入作物名称" />
        </el-form-item>
        <el-form-item label="作物英文名称" prop="cropEnName">
          <el-input v-model="form.cropEnName" placeholder="请输入作物英文名称" />
        </el-form-item>
        <el-form-item label="种质名称" prop="germplasmName">
          <el-input v-model="form.germplasmName" placeholder="请输入种质名称" />
        </el-form-item>
        <el-form-item label="种质英文名称" prop="germplasmEnName">
          <el-input v-model="form.germplasmEnName" placeholder="请输入种质英文名称" />
        </el-form-item>
        <el-form-item label="宣传语" prop="germplasmDes">
          <el-input 
            v-model="form.germplasmDes" 
            type="textarea" 
            :rows="3"
            placeholder="请输入宣传语"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="form.remark" 
            type="textarea" 
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 种植方法管理对话框 -->
    <el-dialog 
      v-model="methodDialogVisible" 
      title="种植方法管理" 
      width="900px"
      :close-on-click-modal="false"
    >
      <div class="method-header">
        <el-button type="primary" size="small" @click="handleAddMethod">
          <el-icon><Plus /></el-icon>
          添加种植方法
        </el-button>
      </div>
      
      <el-table :data="methodList" stripe :style="{ marginTop: '15px' }">
        <el-table-column prop="methodName" label="方法名称" min-width="150" />
        <el-table-column prop="methodDes" label="方法描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="info" size="small" @click="handleEditMethod(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeleteMethod(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <template #footer>
        <el-button @click="methodDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <!-- 种质介绍管理对话框 -->
    <el-dialog 
      v-model="introDialogVisible" 
      title="种质介绍管理" 
      width="900px"
      :close-on-click-modal="false"
    >
      <div class="intro-header">
        <el-button type="primary" size="small" @click="handleAddIntro">
          <el-icon><Plus /></el-icon>
          添加种质介绍
        </el-button>
      </div>
      
      <el-table :data="introList" stripe :style="{ marginTop: '15px' }">
        <el-table-column prop="introId" label="ID" width="80" />
        <el-table-column prop="introName" label="介绍标题" min-width="150" />
        <el-table-column prop="introDes" label="介绍内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="info" size="small" @click="handleEditIntro(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeleteIntro(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <template #footer>
        <el-button @click="introDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <!-- 作业流程管理对话框 -->
    <el-dialog 
      v-model="jobDialogVisible" 
      title="作业流程管理" 
      width="900px"
      :close-on-click-modal="false"
    >
      <div class="job-header">
        <el-button type="primary" size="small" @click="handleAddJob">
          <el-icon><Plus /></el-icon>
          添加作业流程
        </el-button>
      </div>
      
      <el-table :data="jobList" stripe :style="{ marginTop: '15px' }">
        <el-table-column prop="jobId" label="ID" width="80" />
        <el-table-column prop="jobName" label="流程名称" min-width="150" />
        <el-table-column prop="cycleUnit" label="周期单位" width="100">
          <template #default="{ row }">
            {{ row.cycleUnit === '0' ? '周' : '天' }}
          </template>
        </el-table-column>
        <el-table-column prop="jobStart" label="开始时间" width="100" />
        <el-table-column prop="jobFinish" label="结束时间" width="100" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="info" size="small" @click="handleEditJob(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeleteJob(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <template #footer>
        <el-button @click="jobDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <!-- 种植方法表单对话框 -->
    <el-dialog 
      v-model="methodFormDialogVisible" 
      :title="isEditMethod ? '编辑种植方法' : '添加种植方法'" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="methodFormRef" :model="methodForm" :rules="methodFormRules" label-width="100px">
        <el-form-item label="方法名称" prop="methodName">
          <el-input v-model="methodForm.methodName" placeholder="请输入方法名称" />
        </el-form-item>
        <el-form-item label="方法描述" prop="methodDes">
          <el-input 
            v-model="methodForm.methodDes" 
            type="textarea" 
            :rows="4"
            placeholder="请输入方法描述"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="methodForm.remark" 
            type="textarea" 
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="methodFormDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitMethod" :loading="submittingMethod">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 种质介绍表单对话框 -->
    <el-dialog 
      v-model="introFormDialogVisible" 
      :title="isEditIntro ? '编辑种质介绍' : '添加种质介绍'" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="introFormRef" :model="introForm" :rules="introFormRules" label-width="100px">
        <el-form-item label="介绍标题" prop="introName">
          <el-input v-model="introForm.introName" placeholder="请输入介绍标题" />
        </el-form-item>
        <el-form-item label="介绍内容" prop="introDes">
          <el-input 
            v-model="introForm.introDes" 
            type="textarea" 
            :rows="4"
            placeholder="请输入介绍内容"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="introForm.remark" 
            type="textarea" 
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="introFormDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitIntro" :loading="submittingIntro">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 作业流程表单对话框 -->
    <el-dialog 
      v-model="jobFormDialogVisible" 
      :title="isEditJob ? '编辑作业流程' : '添加作业流程'" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="jobFormRef" :model="jobForm" :rules="jobFormRules" label-width="100px">
        <el-form-item label="流程名称" prop="jobName">
          <el-input v-model="jobForm.jobName" placeholder="请输入流程名称" />
        </el-form-item>
        <el-form-item label="周期单位" prop="cycleUnit">
          <el-select v-model="jobForm.cycleUnit" placeholder="请选择周期单位" style="width: 100%">
            <el-option label="周" value="0" />
            <el-option label="天" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="jobStart">
          <el-input-number 
            v-model="jobForm.jobStart" 
            :min="0" 
            placeholder="请输入开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="jobFinish">
          <el-input-number 
            v-model="jobForm.jobFinish" 
            :min="0" 
            placeholder="请输入结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="jobForm.remark" 
            type="textarea" 
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="jobFormDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitJob" :loading="submittingJob">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { Plus, ArrowDown } from '@element-plus/icons-vue'
import { 
  getGermplasmListApi, 
  getGermplasmByIdApi, 
  addGermplasmApi, 
  updateGermplasmApi, 
  deleteGermplasmApi,
  getGermplasmMethodListApi,
  addGermplasmMethodApi,
  updateGermplasmMethodApi,
  deleteGermplasmMethodApi,
  getGermplasmIntroListApi,
  addGermplasmIntroApi,
  updateGermplasmIntroApi,
  deleteGermplasmIntroApi,
  getGermplasmJobListApi,
  addGermplasmJobApi,
  updateGermplasmJobApi,
  deleteGermplasmJobApi
} from '@/api/plant'
import type { Germplasm, GermplasmMethod, GermplasmIntro, GermplasmJob } from '@/types/plant'

// 搜索表单
const searchForm = reactive({
  germplasmName: '',
  cropName: ''
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref<Germplasm[]>([])
const loading = ref(false)
const selectedRows = ref<Germplasm[]>([])

// 对话框状态
const detailDialogVisible = ref(false)
const formDialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)

// 新增的对话框状态
const methodDialogVisible = ref(false)
const introDialogVisible = ref(false)
const jobDialogVisible = ref(false)
const methodFormDialogVisible = ref(false)
const introFormDialogVisible = ref(false)
const jobFormDialogVisible = ref(false)

// 新增的编辑状态
const isEditMethod = ref(false)
const isEditIntro = ref(false)
const isEditJob = ref(false)
const submittingMethod = ref(false)
const submittingIntro = ref(false)
const submittingJob = ref(false)

// 当前种质信息
const currentGermplasm = ref<Germplasm | null>(null)
const currentGermplasmId = ref<number | null>(null)

// 新增的数据列表
const methodList = ref<GermplasmMethod[]>([])
const introList = ref<GermplasmIntro[]>([])
const jobList = ref<GermplasmJob[]>([])

// 表单数据
const form = reactive({
  germplasmId: undefined as number | undefined,
  cropName: '',
  cropEnName: '',
  germplasmName: '',
  germplasmEnName: '',
  germplasmDes: '',
  remark: ''
})

// 表单引用
const formRef = ref<FormInstance>()
const methodFormRef = ref<FormInstance>()
const introFormRef = ref<FormInstance>()
const jobFormRef = ref<FormInstance>()

// 新增的表单数据
const methodForm = reactive({
  methodId: undefined as number | undefined,
  germplasmId: undefined as number | undefined,
  methodName: '',
  methodDes: '',
  remark: ''
})

const introForm = reactive({
  introId: undefined as number | undefined,
  germplasmId: undefined as number | undefined,
  introName: '',
  introDes: '',
  remark: ''
})

const jobForm = reactive({
  jobId: undefined as number | undefined,
  germplasmId: undefined as number | undefined,
  jobName: '',
  cycleUnit: '1', // 默认天
  jobStart: 0,
  jobFinish: 0,
  remark: ''
})

// 表单验证规则
const formRules = {
  cropName: [{ required: true, message: '请输入作物名称', trigger: 'blur' }],
  germplasmName: [{ required: true, message: '请输入种质名称', trigger: 'blur' }]
}

const methodFormRules = {
  methodName: [{ required: true, message: '请输入方法名称', trigger: 'blur' }],
  methodDes: [{ required: true, message: '请输入方法描述', trigger: 'blur' }]
}

const introFormRules = {
  introName: [{ required: true, message: '请输入介绍标题', trigger: 'blur' }],
  introDes: [{ required: true, message: '请输入介绍内容', trigger: 'blur' }]
}

const jobFormRules = {
  jobName: [{ required: true, message: '请输入流程名称', trigger: 'blur' }],
  cycleUnit: [{ required: true, message: '请选择周期单位', trigger: 'change' }],
  jobStart: [{ required: true, message: '请输入开始时间', trigger: 'blur' }],
  jobFinish: [{ required: true, message: '请输入结束时间', trigger: 'blur' }]
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

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.size,
      germplasmName: searchForm.germplasmName || undefined,
      cropName: searchForm.cropName || undefined
    }
    
    const result = await getGermplasmListApi(params)
    tableData.value = result.records || []
    pagination.total = result.total || 0
    
  } catch (error) {
    console.error('获取种质列表失败:', error)
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
    germplasmName: '',
    cropName: ''
  })
  handleSearch()
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    germplasmId: undefined,
    cropName: '',
    cropEnName: '',
    germplasmName: '',
    germplasmEnName: '',
    germplasmDes: '',
    remark: ''
  })
  formRef.value?.clearValidate()
}

// 查看
const handleView = async (row: Germplasm) => {
  try {
    const result = await getGermplasmByIdApi(row.germplasmId)
    currentGermplasm.value = result
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取种质详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

// 新增
const handleAdd = () => {
  resetForm()
  isEdit.value = false
  formDialogVisible.value = true
}

// 下拉菜单处理
const handleDropdownCommand = async (command: string, row: Germplasm) => {
  currentGermplasmId.value = row.germplasmId
  
  try {
    switch (command) {
      case 'method':
        await fetchMethodList(row.germplasmId)
        methodDialogVisible.value = true
        break
      case 'intro':
        await fetchIntroList(row.germplasmId)
        introDialogVisible.value = true
        break
      case 'job':
        await fetchJobList(row.germplasmId)
        jobDialogVisible.value = true
        break
    }
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('获取数据失败')
  }
}

// 编辑
const handleEdit = async (row: Germplasm) => {
  try {
    const result = await getGermplasmByIdApi(row.germplasmId)
    Object.assign(form, result)
    isEdit.value = true
    formDialogVisible.value = true
  } catch (error) {
    console.error('获取种质详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

// 删除单个
const handleDelete = (row: Germplasm) => {
  ElMessageBox.confirm(
    `确定要删除种质"${row.germplasmName}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteGermplasmApi(row.germplasmId.toString())
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除种质失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的数据')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedRows.value.length} 条数据吗？`,
    '批量删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const ids = selectedRows.value.map(item => item.germplasmId).join(',')
      await deleteGermplasmApi(ids)
      ElMessage.success('删除成功')
      selectedRows.value = []
      fetchData()
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    if (isEdit.value) {
      await updateGermplasmApi(form as Partial<Germplasm>)
      ElMessage.success('更新成功')
    } else {
      await addGermplasmApi(form as Partial<Germplasm>)
      ElMessage.success('添加成功')
    }
    
    formDialogVisible.value = false
    fetchData()
  } catch (error) {
    if (error !== false) { // 表单验证失败时会返回false
      console.error('提交失败:', error)
      ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
    }
  } finally {
    submitting.value = false
  }
}

// 表格选择变化
const handleSelectionChange = (selection: Germplasm[]) => {
  selectedRows.value = selection
}

// 获取种植方法列表
const fetchMethodList = async (germplasmId: number) => {
  try {
    const params = {
      pageNum: 1,
      pageSize: 100, // 获取所有数据
      germplasmId
    }
    const result = await getGermplasmMethodListApi(params)
    methodList.value = result.records || []
  } catch (error) {
    console.error('获取种植方法列表失败:', error)
    ElMessage.error('获取种植方法列表失败')
  }
}

// 获取种质介绍列表
const fetchIntroList = async (germplasmId: number) => {
  try {
    const params = {
      pageNum: 1,
      pageSize: 100, // 获取所有数据
      germplasmId
    }
    const result = await getGermplasmIntroListApi(params)
    introList.value = result.records || []
  } catch (error) {
    console.error('获取种质介绍列表失败:', error)
    ElMessage.error('获取种质介绍列表失败')
  }
}

// 获取作业流程列表
const fetchJobList = async (germplasmId: number) => {
  try {
    const params = {
      pageNum: 1,
      pageSize: 100, // 获取所有数据
      germplasmId
    }
    const result = await getGermplasmJobListApi(params)
    jobList.value = result.records || []
  } catch (error) {
    console.error('获取作业流程列表失败:', error)
    ElMessage.error('获取作业流程列表失败')
  }
}

// 重置种植方法表单
const resetMethodForm = () => {
  Object.assign(methodForm, {
    methodId: undefined,
    germplasmId: currentGermplasmId.value,
    methodName: '',
    methodImg: '',
    methodDes: '',
    remark: ''
  })
  methodFormRef.value?.clearValidate()
}

// 重置种质介绍表单
const resetIntroForm = () => {
  Object.assign(introForm, {
    introId: undefined,
    germplasmId: currentGermplasmId.value,
    introName: '',
    introDes: '',
    remark: ''
  })
  introFormRef.value?.clearValidate()
}

// 重置作业流程表单
const resetJobForm = () => {
  Object.assign(jobForm, {
    jobId: undefined,
    germplasmId: currentGermplasmId.value,
    jobName: '',
    cycleUnit: '1',
    jobStart: 0,
    jobFinish: 0,
    remark: ''
  })
  jobFormRef.value?.clearValidate()
}

// 添加种植方法
const handleAddMethod = () => {
  resetMethodForm()
  isEditMethod.value = false
  methodFormDialogVisible.value = true
}

// 编辑种植方法
const handleEditMethod = (row: any) => {
  Object.assign(methodForm, row)
  isEditMethod.value = true
  methodFormDialogVisible.value = true
}

// 删除种植方法
const handleDeleteMethod = (row: GermplasmMethod) => {
  ElMessageBox.confirm(
    `确定要删除种植方法"${row.methodName}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteGermplasmMethodApi(row.methodId.toString())
      ElMessage.success('删除成功')
      // 重新获取列表
      if (currentGermplasmId.value) {
        fetchMethodList(currentGermplasmId.value)
      }
    } catch (error) {
      console.error('删除种植方法失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 提交种植方法
const handleSubmitMethod = async () => {
  if (!methodFormRef.value) return
  
  try {
    await methodFormRef.value.validate()
    submittingMethod.value = true
    
    if (isEditMethod.value) {
      // 更新操作
      await updateGermplasmMethodApi(methodForm as Partial<GermplasmMethod>)
      ElMessage.success('更新成功')
    } else {
      // 添加操作
      await addGermplasmMethodApi(methodForm as Partial<GermplasmMethod>)
      ElMessage.success('添加成功')
    }
    
    methodFormDialogVisible.value = false
    // 重新获取列表
    if (currentGermplasmId.value) {
      fetchMethodList(currentGermplasmId.value)
    }
  } catch (error) {
    if (error !== false) {
      console.error('提交失败:', error)
      ElMessage.error(isEditMethod.value ? '更新失败' : '添加失败')
    }
  } finally {
    submittingMethod.value = false
  }
}

// 添加种质介绍
const handleAddIntro = () => {
  resetIntroForm()
  isEditIntro.value = false
  introFormDialogVisible.value = true
}

// 编辑种质介绍
const handleEditIntro = (row: GermplasmIntro) => {
  Object.assign(introForm, row)
  isEditIntro.value = true
  introFormDialogVisible.value = true
}

// 删除种质介绍
const handleDeleteIntro = (row: GermplasmIntro) => {
  ElMessageBox.confirm(
    `确定要删除种质介绍"${row.introName}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteGermplasmIntroApi(row.introId.toString())
      ElMessage.success('删除成功')
      // 重新获取列表
      if (currentGermplasmId.value) {
        fetchIntroList(currentGermplasmId.value)
      }
    } catch (error) {
      console.error('删除种质介绍失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 提交种质介绍
const handleSubmitIntro = async () => {
  if (!introFormRef.value) return
  
  try {
    await introFormRef.value.validate()
    submittingIntro.value = true
    
    if (isEditIntro.value) {
      // 更新操作
      await updateGermplasmIntroApi(introForm as Partial<GermplasmIntro>)
      ElMessage.success('更新成功')
    } else {
      // 添加操作
      await addGermplasmIntroApi(introForm as Partial<GermplasmIntro>)
      ElMessage.success('添加成功')
    }
    
    introFormDialogVisible.value = false
    // 重新获取列表
    if (currentGermplasmId.value) {
      fetchIntroList(currentGermplasmId.value)
    }
  } catch (error) {
    if (error !== false) {
      console.error('提交失败:', error)
      ElMessage.error(isEditIntro.value ? '更新失败' : '添加失败')
    }
  } finally {
    submittingIntro.value = false
  }
}

// 添加作业流程
const handleAddJob = () => {
  resetJobForm()
  isEditJob.value = false
  jobFormDialogVisible.value = true
}

// 编辑作业流程
const handleEditJob = (row: GermplasmJob) => {
  Object.assign(jobForm, row)
  isEditJob.value = true
  jobFormDialogVisible.value = true
}

// 删除作业流程
const handleDeleteJob = (row: GermplasmJob) => {
  ElMessageBox.confirm(
    `确定要删除作业流程"${row.jobName}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteGermplasmJobApi(row.jobId.toString())
      ElMessage.success('删除成功')
      // 重新获取列表
      if (currentGermplasmId.value) {
        fetchJobList(currentGermplasmId.value)
      }
    } catch (error) {
      console.error('删除作业流程失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 提交作业流程
const handleSubmitJob = async () => {
  if (!jobFormRef.value) return
  
  try {
    await jobFormRef.value.validate()
    submittingJob.value = true
    
    if (isEditJob.value) {
      // 更新操作
      await updateGermplasmJobApi(jobForm as Partial<GermplasmJob>)
      ElMessage.success('更新成功')
    } else {
      // 添加操作
      await addGermplasmJobApi(jobForm as Partial<GermplasmJob>)
      ElMessage.success('添加成功')
    }
    
    jobFormDialogVisible.value = false
    // 重新获取列表
    if (currentGermplasmId.value) {
      fetchJobList(currentGermplasmId.value)
    }
  } catch (error) {
    if (error !== false) {
      console.error('提交失败:', error)
      ElMessage.error(isEditJob.value ? '更新失败' : '添加失败')
    }
  } finally {
    submittingJob.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.germplasm-container {
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

.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.batch-actions {
  display: flex;
  gap: 10px;
}

.detail-content .image-section {
  margin-top: 20px;
}

.detail-content .image-section h4 {
  margin-bottom: 10px;
  color: #303133;
}

.method-header,
.intro-header,
.job-header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.operation-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: nowrap;
}

.operation-buttons .el-button {
  margin-left: 0 !important;
  margin-right: 0 !important;
}

</style>
