<template>
  <div class="item-container">
 
    
    <!-- 左右布局容器 -->
    <div class="layout-container">
      <!-- 左侧分类树 -->
      <div class="left-panel">
        <el-card shadow="never" class="category-card">
          <template #header>
            <div class="category-header">
              <span>物料分类</span>
              <el-button
                type="primary"
                size="small"
                icon="Plus"
                @click="handleAddCategory"
              >新增</el-button>
            </div>
          </template>
          
          <el-tree
            ref="categoryTreeRef"
            :data="categoryTreeData"
            :props="treeProps"
            node-key="id"
            :default-expand-all="true"
            :highlight-current="true"
            @node-click="handleCategoryClick"
            class="category-tree"
          >
            <template #default="{ node, data }">
              <span class="custom-tree-node">
                <span class="tree-node-label">
                  <el-icon class="node-icon"><Folder /></el-icon>
                  {{ data.categoryName }}
                </span>
                <span class="tree-node-operations">
                  <el-button
                    link
                    type="primary"
                    size="small"
                    @click.stop="handleEditCategory(data)"
                  >编辑</el-button>
                  <el-button
                    link
                    type="danger"
                    size="small"
                    @click.stop="handleDeleteCategory(data)"
                  >删除</el-button>
                </span>
              </span>
            </template>
          </el-tree>
        </el-card>
      </div>

      <!-- 右侧物料列表 -->
      <div class="right-panel">
        <el-card shadow="never" class="item-card">
          <template #header>
            <div class="item-header">
              <span>{{ currentCategoryName ? `${currentCategoryName} - 物料列表` : '全部物料' }}</span>
              <el-button type="primary" @click="handleAdd">
                <el-icon><Plus /></el-icon>
                新增物料
              </el-button>
            </div>
          </template>

          <!-- 搜索区域 -->
          <div class="search-area">
            <el-form :model="searchForm" inline size="small">
              <el-form-item label="物料名称">
                <el-input 
                  v-model="searchForm.itemName" 
                  placeholder="请输入物料名称"
                  clearable
                  style="width: 160px"
                />
              </el-form-item>
              <el-form-item label="物料编码">
                <el-input 
                  v-model="searchForm.itemCode" 
                  placeholder="请输入物料编码"
                  clearable
                  style="width: 160px"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button @click="handleReset">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 表格数据 -->
          <el-table :data="tableData" v-loading="loading" stripe>
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column prop="itemCode" label="物料编码" width="120" />
            <el-table-column prop="itemName" label="物料名称" />
            <el-table-column label="品牌" width="120">
              <template #default="{ row }">
                {{ getBrandName(row.itemBrand) }}
              </template>
            </el-table-column>
            <el-table-column prop="unit" label="单位" width="80" />
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
            <el-table-column prop="createTime" label="创建时间" width="180" show-overflow-tooltip>
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="info" size="small" @click="handleView(row)" plain>
                  查看
                </el-button>
                <el-button type="primary" size="small" @click="handleEdit(row)">
                  编辑
                </el-button>
                <el-button type="danger" size="small" @click="handleDelete(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 分页 -->
          <el-pagination
            v-model:current-page="pagination.pageNum"
            v-model:page-size="pagination.pageSize"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSearch"
            @current-change="handleSearch"
            class="pagination"
          />
        </el-card>
      </div>
    </div>

    <!-- 新增/编辑物料对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="1000px"
      :before-close="handleCloseDialog"
      class="item-dialog"
      destroy-on-close
    >
      <template #header="{ titleId, titleClass }">
        <div class="dialog-header">
          <div class="header-content">
            <el-icon class="header-icon" :size="24">
              <Box />
            </el-icon>
            <span :id="titleId" :class="titleClass" class="dialog-title">{{ dialogTitle }}</span>
          </div>
        </div>
      </template>
      
      <div class="dialog-content">
        <!-- 基本信息区域 -->
        <div class="info-section">
                      <el-form
              ref="formRef"
              :model="formData"
              :rules="formRules"
              label-width="120px"
              class="item-form"
              :disabled="isView"
            >
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="物料编码" prop="itemCode">
                  <el-input 
                    v-model="formData.itemCode" 
                    placeholder="请输入物料编码"
                    prefix-icon="DocumentCopy"
                    size="large"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="物料名称" prop="itemName">
                  <el-input 
                    v-model="formData.itemName" 
                    placeholder="请输入物料名称"
                    prefix-icon="Goods"
                    size="large"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="物料分类" prop="itemCategory">
                  <el-tree-select
                    v-model="formData.itemCategory"
                    :data="categoryTreeData"
                    :props="treeSelectProps"
                    placeholder="请选择物料分类"
                    clearable
                    check-strictly
                    style="width: 100%"
                    size="large"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="商品品牌" prop="itemBrand">
                  <el-select
                    v-model="formData.itemBrand"
                    placeholder="请选择商品品牌"
                    clearable
                    filterable
                    style="width: 100%"
                    size="large"
                  >
                    <el-option
                      v-for="brand in brandList"
                      :key="brand.id"
                      :label="brand.brandName"
                      :value="brand.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="计量单位" prop="unit">
                  <el-input 
                    v-model="formData.unit" 
                    placeholder="请输入计量单位（如：个、kg、米等）"
                    prefix-icon="Odometer"
                    size="large"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="备注说明" prop="remark">
              <el-input 
                v-model="formData.remark" 
                type="textarea" 
                placeholder="请输入物料备注说明，如物料特性、存储要求等"
                :rows="3"
                resize="none"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </el-form>
        </div>

        <!-- 物料规格管理区域 -->
        <div class="sku-section">
          <div class="section-header">
            <el-icon class="section-icon"><Setting /></el-icon>
            <span class="section-title">物料规格配置</span>
            <el-button 
              v-if="!isView"
              type="primary" 
              @click="handleAddSku" 
              class="add-sku-btn"
            >
              <el-icon><Plus /></el-icon>
              添加规格
            </el-button>
          </div>
          
          <div class="sku-content">
            <el-table :data="skuList" border class="sku-table" max-height="350">
              <el-table-column prop="skuCode" label="规格编码" width="110" />
              <el-table-column prop="skuName" label="规格名称" width="110" />
              <el-table-column prop="barcode" label="条码" width="110" />
              <el-table-column prop="sellingPrice" label="售价" width="90" align="right">
                <template #default="{ row }">
                  <span class="price-text">{{ row.sellingPrice ? `¥${row.sellingPrice}` : '-' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="costPrice" label="成本价" width="90" align="right">
                <template #default="{ row }">
                  <span class="price-text">{{ row.costPrice ? `¥${row.costPrice}` : '-' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="grossWeight" label="毛重(kg)" width="90" align="right">
                <template #default="{ row }">
                  {{ row.grossWeight || '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="remark" label="备注" show-overflow-tooltip min-width="100" />
              <el-table-column v-if="!isView" label="操作" width="120" fixed="right" align="center">
                <template #default="{ row, $index }">
                  <el-button type="primary" size="small" @click="handleEditSku($index)" plain>
                    <el-icon><Edit /></el-icon>
                    编辑
                  </el-button>
                  <el-button type="danger" size="small" @click="handleDeleteSku($index)" plain>
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <div v-if="skuList.length === 0" class="empty-sku">
              <el-empty :description="isView ? '暂无物料规格' : '暂无物料规格，点击上方按钮添加'" :image-size="80">
                <template #image>
                  <el-icon :size="80" class="empty-icon"><Box /></el-icon>
                </template>
              </el-empty>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <div class="footer-info" v-if="!isView">
            <el-icon><InfoFilled /></el-icon>
            <span>保存后物料信息和规格配置将生效</span>
          </div>
          <div class="footer-actions">
            <el-button size="large" @click="handleCloseDialog">
              <el-icon><Close /></el-icon>
              {{ isView ? '关闭' : '取消' }}
            </el-button>
            <el-button 
              v-if="!isView"
              type="primary" 
              size="large" 
              @click="handleSubmit" 
              :loading="submitLoading"
            >
              <el-icon><Check /></el-icon>
              {{ isEdit ? '更新' : '保存' }}
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>

    <!-- 分类新增/编辑对话框 -->
    <el-dialog
      :title="categoryDialogTitle"
      v-model="categoryDialogVisible"
      width="500px"
      :before-close="handleCloseCategoryDialog"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryFormData"
        :rules="categoryFormRules"
        label-width="100px"
      >

        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="categoryFormData.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父级分类" prop="parentId">
          <el-tree-select
            v-model="categoryFormData.parentId"
            :data="categoryTreeData"
            :props="treeSelectProps"
            placeholder="请选择父级分类（可选）"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input-number 
            v-model="categoryFormData.orderNum" 
            :min="0" 
            :max="999" 
            placeholder="请输入排序值"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="categoryFormData.remark" 
            type="textarea" 
            placeholder="请输入备注"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseCategoryDialog">取消</el-button>
          <el-button type="primary" @click="handleSubmitCategory" :loading="categorySubmitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 物料规格新增/编辑对话框 -->
    <el-dialog
      :title="skuDialogTitle"
      v-model="skuDialogVisible"
      width="800px"
      :before-close="handleCloseSkuDialog"
    >
      <el-form
        ref="skuFormRef"
        :model="skuFormData"
        :rules="skuFormRules"
        label-width="100px"
        :inline="false"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="规格编码" prop="skuCode">
              <el-input v-model="skuFormData.skuCode" placeholder="请输入规格编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格名称" prop="skuName">
              <el-input v-model="skuFormData.skuName" placeholder="请输入规格名称" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="条码" prop="barcode">
              <el-input v-model="skuFormData.barcode" placeholder="请输入条码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="售价" prop="sellingPrice">
              <el-input-number 
                v-model="skuFormData.sellingPrice" 
                :min="0" 
                :precision="2"
                placeholder="请输入售价"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="成本价" prop="costPrice">
              <el-input-number 
                v-model="skuFormData.costPrice" 
                :min="0" 
                :precision="2"
                placeholder="请输入成本价"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="毛重(kg)" prop="grossWeight">
              <el-input-number 
                v-model="skuFormData.grossWeight" 
                :min="0" 
                :precision="3"
                placeholder="请输入毛重"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="净重(kg)" prop="netWeight">
              <el-input-number 
                v-model="skuFormData.netWeight" 
                :min="0" 
                :precision="3"
                placeholder="请输入净重"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="长度(cm)" prop="length">
              <el-input-number 
                v-model="skuFormData.length" 
                :min="0" 
                :precision="2"
                placeholder="请输入长度"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="宽度(cm)" prop="width">
              <el-input-number 
                v-model="skuFormData.width" 
                :min="0" 
                :precision="2"
                placeholder="请输入宽度"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="高度(cm)" prop="height">
              <el-input-number 
                v-model="skuFormData.height" 
                :min="0" 
                :precision="2"
                placeholder="请输入高度"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="skuFormData.remark" 
            type="textarea" 
            placeholder="请输入备注"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseSkuDialog">取消</el-button>
          <el-button type="primary" @click="handleSubmitSku">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Folder, Delete, Box, InfoFilled, Setting, Edit, Close, Check, Goods, DocumentCopy, Odometer } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { 
  getItemListApi, 
  getItemByIdApi, 
  addItemApi, 
  updateItemApi, 
  deleteItemApi,
  getItemCategoryTreeApi,
  getItemCategoryByIdApi,
  addItemCategoryApi,
  updateItemCategoryApi,
  deleteItemCategoryApi,
  getItemSkuListApi,
  addItemSkuApi,
  updateItemSkuApi,
  deleteItemSkuApi,
  getItemBrandListApi
} from '@/api/inventory'
import type { Item, ItemCategory, ItemSku, ItemBrand } from '@/types/inventory'
import type { FormInstance, FormRules } from 'element-plus'

// 搜索表单
const searchForm = reactive({
  itemName: '',
  itemCode: '',
  itemCategory: undefined as string | undefined
})

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref<Item[]>([])
const loading = ref(false)

// 当前选中的分类
const currentCategoryId = ref<number | undefined>()
const currentCategoryName = ref<string>('')

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const isView = ref(false)
const currentId = ref<number>()
const submitLoading = ref(false)

// 品牌列表
const brandList = ref<ItemBrand[]>([])

// 表单相关
const formRef = ref<FormInstance>()
const formData = reactive<Partial<Item> & { itemCategory?: number }>({
  itemCode: '',
  itemName: '',
  itemCategory: undefined,
  itemBrand: undefined,
  unit: '',
  remark: ''
})

// 物料规格相关
const skuList = ref<ItemSku[]>([])
const skuDialogVisible = ref(false)
const skuDialogTitle = ref('')
const isSkuEdit = ref(false)
const currentSkuEditIndex = ref<number>(-1)
const skuFormRef = ref<FormInstance>()
const skuFormData = reactive<Partial<ItemSku>>({
  skuCode: '',
  skuName: '',
  barcode: '',
  sellingPrice: undefined,
  costPrice: undefined,
  grossWeight: undefined,
  netWeight: undefined,
  length: undefined,
  width: undefined,
  height: undefined,
  remark: ''
})

// 表单验证规则
const formRules: FormRules = {
  itemCode: [
    { required: true, message: '请输入物料编码', trigger: 'blur' }
  ],
  itemName: [
    { required: true, message: '请输入物料名称', trigger: 'blur' }
  ],
  unit: [
    { required: true, message: '请输入计量单位', trigger: 'blur' }
  ]
}

// 物料规格表单验证规则
const skuFormRules: FormRules = {
  skuCode: [
    { required: true, message: '请输入规格编码', trigger: 'blur' }
  ],
  skuName: [
    { required: true, message: '请输入规格名称', trigger: 'blur' }
  ]
}

// 格式化日期时间
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

// 获取物料数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm,
      itemCategory: currentCategoryId.value ? String(currentCategoryId.value) : undefined // 添加分类筛选
    }
    
    const result = await getItemListApi(params)
    
    tableData.value = result.records || []
    pagination.total = result.total || 0
    
  } catch (error) {
    ElMessage.error('获取数据失败')
    console.error('获取物料列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    itemName: '',
    itemCode: ''
  })
  handleSearch()
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    id: undefined, // 清除ID，避免新增时携带
    itemCode: '',
    itemName: '',
    itemCategory: currentCategoryId.value, // 默认选择当前分类
    itemBrand: undefined,
    unit: '',
    remark: ''
  })
  skuList.value = []
  formRef.value?.clearValidate()
}

// 重置规格表单
const resetSkuForm = () => {
  Object.assign(skuFormData, {
    skuCode: '',
    skuName: '',
    barcode: '',
    sellingPrice: undefined,
    costPrice: undefined,
    grossWeight: undefined,
    netWeight: undefined,
    length: undefined,
    width: undefined,
    height: undefined,
    remark: ''
  })
  skuFormRef.value?.clearValidate()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增物料'
  isEdit.value = false
  isView.value = false
  currentId.value = undefined
  resetForm()
  dialogVisible.value = true
}

// 查看
const handleView = async (row: Item) => {
  dialogTitle.value = '查看物料详情'
  isEdit.value = false
  isView.value = true
  currentId.value = row.id
  
  try {
    const item = await getItemByIdApi(row.id!)
    Object.assign(formData, item)
    
    // 将 categoryId 映射到 itemCategory（用于表单显示）
    if (item.categoryId !== undefined && item.categoryId !== null) {
      formData.itemCategory = item.categoryId
    } else {
      formData.itemCategory = undefined
    }
    
    // 加载物料规格数据
    await loadItemSkuList(row.id!)
    
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取物料详情失败')
  }
}

// 编辑
const handleEdit = async (row: Item) => {
  dialogTitle.value = '编辑物料'
  isEdit.value = true
  isView.value = false
  currentId.value = row.id
  
  try {
    const item = await getItemByIdApi(row.id!)
    Object.assign(formData, item)
    
    // 将 categoryId 映射到 itemCategory（用于表单显示）
    if (item.categoryId !== undefined && item.categoryId !== null) {
      formData.itemCategory = item.categoryId
    } else {
      formData.itemCategory = undefined
    }
    
    // 加载物料规格数据
    await loadItemSkuList(row.id!)
    
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取物料详情失败')
  }
}

// 加载物料规格列表
const loadItemSkuList = async (itemId: number) => {
  try {
    const result = await getItemSkuListApi({
      pageNum: 1,
      pageSize: 1000,
      itemId
    })
    skuList.value = result.records || []
  } catch (error) {
    console.error('获取物料规格失败:', error)
  }
}

// 关闭对话框
const handleCloseDialog = () => {
  dialogVisible.value = false
  isView.value = false
  resetForm()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  // 防止重复提交
  if (submitLoading.value) return
  
  try {
    await formRef.value.validate()
    
    submitLoading.value = true
    
    let itemId: number
    
    // 准备提交数据，将 itemCategory 转换为 categoryId
    const submitData = {
      ...formData,
      categoryId: formData.itemCategory ?? undefined
    }
    
    if (isEdit.value) {
      await updateItemApi({ ...submitData, id: currentId.value })
      itemId = currentId.value!
      ElMessage.success('更新成功')
    } else {
      // 新增物料，排除 id 字段
      const { id, ...addData } = submitData
      await addItemApi(addData)
      ElMessage.success('新增成功')
      
      // 后端不返回ID，通过物料编码查询获取新创建的物料ID
      if (!submitData.itemCode) {
        throw new Error('物料编码不能为空')
      }
      
      const searchResult = await getItemListApi({
        pageNum: 1,
        pageSize: 1,
        itemCode: submitData.itemCode
      })
      
      if (!searchResult.records || searchResult.records.length === 0) {
        throw new Error('新增物料成功，但无法获取物料ID，请刷新页面后手动添加物料规格')
      }
      
      itemId = searchResult.records[0].id!
      
      if (!itemId) {
        throw new Error('新增物料成功，但无法获取物料ID，请刷新页面后手动添加物料规格')
      }
    }
    
    // 保存物料规格
    await saveItemSkuList(itemId)
    
    handleCloseDialog()
    fetchData()
    
  } catch (error) {
    if (error !== false) { // 排除表单验证失败的情况
      console.error(isEdit.value ? '更新物料失败' : '新增物料失败', error)
      const errorMessage = error instanceof Error ? error.message : (isEdit.value ? '更新失败' : '新增失败')
      ElMessage.error(errorMessage)
    }
  } finally {
    submitLoading.value = false
  }
}

// 保存物料规格列表
const saveItemSkuList = async (itemId: number) => {
  try {
    // 获取原有的规格列表
    const originalSkus = await getItemSkuListApi({
      pageNum: 1,
      pageSize: 1000,
      itemId
    })
    
    // 处理需要删除的规格（原有的但当前列表中没有的）
    const currentSkuIds = skuList.value.filter(sku => sku.id).map(sku => sku.id)
    const originalSkuIds = originalSkus.records?.map(sku => sku.id) || []
    const skusToDelete = originalSkuIds.filter(id => !currentSkuIds.includes(id))
    
    if (skusToDelete.length > 0) {
      await deleteItemSkuApi(skusToDelete.join(','))
    }
    
    // 处理新增和更新的规格
    for (const sku of skuList.value) {
      const skuData = { ...sku, itemId }
      if (sku.id) {
        await updateItemSkuApi(skuData)
      } else {
        await addItemSkuApi(skuData)
      }
    }
  } catch (error) {
    console.error('保存物料规格失败:', error)
    ElMessage.error('保存物料规格失败')
  }
}

// 删除
const handleDelete = async (row: Item) => {
  try {
    await ElMessageBox.confirm(
      `确认删除物料"${row.itemName}"吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deleteItemApi(String(row.id))
    ElMessage.success('删除成功')
    fetchData()
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// ========== 分类管理相关 ==========

// 分类树形数据
const categoryTreeData = ref<ItemCategory[]>([])
const categoryTreeRef = ref()

// 树形组件配置
const treeProps = {
  children: 'children',
  label: 'categoryName'
}

const treeSelectProps = {
  children: 'children',
  label: 'categoryName',
  value: 'id'
}

// 分类对话框相关
const categoryDialogVisible = ref(false)
const categoryDialogTitle = ref('')
const isCategoryEdit = ref(false)
const currentCategoryEditId = ref<number>()
const categorySubmitLoading = ref(false)

// 分类表单相关
const categoryFormRef = ref<FormInstance>()
const categoryFormData = reactive<Partial<ItemCategory>>({
  categoryName: '',
  parentId: undefined,
  orderNum: 0,
  remark: ''
})

// 分类表单验证规则
const categoryFormRules: FormRules = {
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ]
}

// 获取分类树形数据
const fetchCategoryTree = async () => {
  try {
    const result = await getItemCategoryTreeApi()
    categoryTreeData.value = result
  } catch (error) {
    ElMessage.error('获取分类数据失败')
    console.error('获取分类树失败:', error)
  }
}

// 分类树节点点击事件
const handleCategoryClick = (data: ItemCategory) => {
  currentCategoryId.value = data.id
  currentCategoryName.value = data.categoryName
  searchForm.itemCategory = data.id ? String(data.id) : undefined
  handleSearch()
}



// 重置分类表单
const resetCategoryForm = () => {
  Object.assign(categoryFormData, {
    categoryName: '',
    parentId: undefined,
    orderNum: 0,
    remark: ''
  })
  categoryFormRef.value?.clearValidate()
}

// 新增分类
const handleAddCategory = () => {
  categoryDialogTitle.value = '新增分类'
  isCategoryEdit.value = false
  currentCategoryEditId.value = undefined
  resetCategoryForm()
  categoryDialogVisible.value = true
}

// 编辑分类
const handleEditCategory = async (category: ItemCategory) => {
  categoryDialogTitle.value = '编辑分类'
  isCategoryEdit.value = true
  currentCategoryEditId.value = category.id
  
  try {
    const categoryDetail = await getItemCategoryByIdApi(category.id!)
    Object.assign(categoryFormData, categoryDetail)
    categoryDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取分类详情失败')
  }
}

// 关闭分类对话框
const handleCloseCategoryDialog = () => {
  categoryDialogVisible.value = false
  resetCategoryForm()
}

// 提交分类表单
const handleSubmitCategory = async () => {
  if (!categoryFormRef.value) return
  
  try {
    await categoryFormRef.value.validate()
    
    categorySubmitLoading.value = true
    
    if (isCategoryEdit.value) {
      await updateItemCategoryApi({ ...categoryFormData, id: currentCategoryEditId.value })
      ElMessage.success('更新成功')
    } else {
      await addItemCategoryApi(categoryFormData)
      ElMessage.success('新增成功')
    }
    
    handleCloseCategoryDialog()
    fetchCategoryTree()
    
  } catch (error) {
    if (error !== false) { // 排除表单验证失败的情况
      ElMessage.error(isCategoryEdit.value ? '更新失败' : '新增失败')
    }
  } finally {
    categorySubmitLoading.value = false
  }
}

// 删除单个分类
const handleDeleteCategory = async (category: ItemCategory) => {
  try {
    await ElMessageBox.confirm(
      `确认删除分类"${category.categoryName}"吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deleteItemCategoryApi(String(category.id))
    ElMessage.success('删除成功')
    fetchCategoryTree()
    
    // 如果删除的是当前选中的分类，重置选择
    if (currentCategoryId.value === category.id) {
      currentCategoryId.value = undefined
      currentCategoryName.value = ''
      searchForm.itemCategory = undefined
      handleSearch()
    }
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}



// ========== 物料规格管理相关 ==========

// 新增物料规格
const handleAddSku = () => {
  skuDialogTitle.value = '新增物料规格'
  isSkuEdit.value = false
  currentSkuEditIndex.value = -1
  resetSkuForm()
  skuDialogVisible.value = true
}

// 编辑物料规格
const handleEditSku = (index: number) => {
  skuDialogTitle.value = '编辑物料规格'
  isSkuEdit.value = true
  currentSkuEditIndex.value = index
  Object.assign(skuFormData, skuList.value[index])
  skuDialogVisible.value = true
}

// 删除物料规格
const handleDeleteSku = async (index: number) => {
  try {
    await ElMessageBox.confirm(
      '确认删除该物料规格吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const sku = skuList.value[index]
    if (sku.id) {
      // 如果有ID，说明是已保存的规格，需要调用删除接口
      await deleteItemSkuApi(String(sku.id))
    }
    
    skuList.value.splice(index, 1)
    ElMessage.success('删除成功')
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 关闭规格对话框
const handleCloseSkuDialog = () => {
  skuDialogVisible.value = false
  resetSkuForm()
}

// 提交规格表单
const handleSubmitSku = async () => {
  if (!skuFormRef.value) return
  
  try {
    await skuFormRef.value.validate()
    
    if (isSkuEdit.value) {
      // 编辑模式：更新列表中的数据
      Object.assign(skuList.value[currentSkuEditIndex.value], skuFormData)
    } else {
      // 新增模式：添加到列表
      skuList.value.push({ ...skuFormData } as ItemSku)
    }
    
    handleCloseSkuDialog()
    ElMessage.success(isSkuEdit.value ? '更新成功' : '新增成功')
    
  } catch (error) {
    if (error !== false) {
      ElMessage.error(isSkuEdit.value ? '更新失败' : '新增失败')
    }
  }
}

// 获取品牌列表
const fetchBrandList = async () => {
  try {
    const result = await getItemBrandListApi({
      pageNum: 1,
      pageSize: 1000 // 获取所有品牌
    })
    brandList.value = result.records || []
  } catch (error) {
    console.error('获取品牌列表失败:', error)
  }
}

// 根据品牌ID获取品牌名称
const getBrandName = (brandId?: number) => {
  if (!brandId) return '-'
  const brand = brandList.value.find(b => b.id === brandId)
  return brand ? brand.brandName : '-'
}

onMounted(() => {
  fetchCategoryTree()
  fetchBrandList()
  fetchData()
})
</script>

<style scoped>
.item-container {
  padding: 0px;
  height: 100%;
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

/* 左右布局 */
.layout-container {
  display: flex;
  gap: 20px;
  height: calc(100vh - 160px);
}

.left-panel {
  width: 320px;
  flex-shrink: 0;
}

.right-panel {
  flex: 1;
  min-width: 0;
}

.category-card,
.item-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.category-card .el-card__body),
:deep(.item-card .el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 分类管理样式 */
.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}



.category-tree {
  flex: 1;
  overflow-y: auto;
}

.custom-tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding-right: 8px;
}

.tree-node-label {
  display: flex;
  align-items: center;
  flex: 1;
}

.node-icon {
  margin-right: 8px;
  color: #409eff;
}

.tree-node-operations {
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.2s;
}

.custom-tree-node:hover .tree-node-operations {
  opacity: 1;
}

/* 物料列表样式 */
.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-area {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

/* 树形节点样式优化 */
:deep(.el-tree-node__content) {
  height: 36px;
  line-height: 36px;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: #e6f7ff;
  color: #1890ff;
}

/* 物料规格管理样式 */
.sku-section {
  margin-top: 20px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
}

.section-title {
  font-weight: 600;
  color: #303133;
}

.empty-sku {
  padding: 20px;
  text-align: center;
}

/* 新增物料弹窗样式 */
.item-dialog {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.item-dialog .el-dialog__header) {
  padding: 0;
  border-bottom: none;
}

:deep(.item-dialog .el-dialog__body) {
  padding: 0;
  max-height: 80vh;
  overflow-y: auto;
}

:deep(.item-dialog .el-dialog__footer) {
  padding: 0;
  border-top: 1px solid #e4e7ed;
}

.dialog-header {
  padding: 24px 30px;
}

.header-content {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.header-icon {
  margin-right: 12px;
}

.dialog-title {
  font-size: 20px;
  font-weight: 600;
}

.header-subtitle {
  font-size: 14px;
  margin-left: 36px;
}

.dialog-content {
  padding: 30px;
}

.info-section {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f2f5;
}

.section-icon {
  margin-right: 8px;
  color: #409eff;
  font-size: 18px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.item-form {
  background: #fafbfc;
  padding: 24px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

:deep(.item-form .el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.item-form .el-input__wrapper) {
  border-radius: 6px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.2s;
}

:deep(.item-form .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

:deep(.item-form .el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 0 0 1px #409eff inset;
}

:deep(.item-form .el-textarea__inner) {
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  transition: all 0.2s;
}

:deep(.item-form .el-textarea__inner:hover) {
  border-color: #c0c4cc;
}

:deep(.item-form .el-textarea.is-focus .el-textarea__inner) {
  border-color: #409eff;
}

/* 物料规格区域样式 */
.sku-section {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  background: #ffffff;
}

.sku-section .section-header {
  background: linear-gradient(90deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 16px 20px;
  margin-bottom: 0;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.add-sku-btn {
  border-radius: 6px;
  font-weight: 500;
}

.sku-content {
  padding: 16px;
}

.sku-table {
  border-radius: 6px;
  overflow: hidden;
}

:deep(.sku-table .el-table__header) {
  background: #f8f9fa;
}

:deep(.sku-table .el-table__header th) {
  background: #f8f9fa !important;
  color: #606266;
  font-weight: 600;
}

.price-text {
  color: #f56c6c;
  font-weight: 500;
}

.empty-sku {
  padding: 40px 20px;
  text-align: center;
  background: #fafbfc;
  border-radius: 6px;
  margin-top: 16px;
}

.empty-icon {
  color: #c0c4cc;
}

/* 弹窗底部样式 */
.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  background: #fafbfc;
}

.footer-info {
  display: flex;
  align-items: center;
  color: #909399;
  font-size: 14px;
}

.footer-info .el-icon {
  margin-right: 6px;
  color: #409eff;
}

.footer-actions {
  display: flex;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .layout-container {
    flex-direction: column;
    height: auto;
  }
  
  .left-panel {
    width: 100%;
    height: 400px;
  }
  
  .right-panel {
    width: 100%;
  }
  
  :deep(.item-dialog) {
    width: 95% !important;
    margin: 20px auto !important;
  }
  
  .dialog-content {
    padding: 20px;
  }
  
  .item-form {
    padding: 16px;
  }
}

@media (max-width: 768px) {
  :deep(.item-dialog) {
    width: 98% !important;
    margin: 10px auto !important;
  }
  
  .dialog-header {
    padding: 20px;
  }
  
  .dialog-content {
    padding: 16px;
  }
  
  .dialog-footer {
    padding: 16px 20px;
    flex-direction: column;
    gap: 12px;
  }
  
  .footer-actions {
    width: 100%;
    justify-content: center;
  }
}
</style>