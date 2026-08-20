import { http } from '@/utils/request'
import type { PageParams, PageResult } from '@/types/api'
import type {
  InventoryItem,
  InventoryHistory,
  InventoryDetail,
  Warehouse,
  WarehouseArea,
  Area,
  Item,
  ItemCategory,
  ItemSku,
  Merchant,
  ItemBrand,
  InboundOrder,
  OutboundOrder,
  MovementOrder,
  MovementOrderDetail,
  CheckOrder,
  CheckOrderDetail,
  ReceiptOrder,
  ReceiptOrderDetail,
  ShipmentOrder,
  ShipmentOrderDetail
} from '@/types/inventory'

// 库存管理
export const getInventoryListApi = (params: {
  pageNo?: number
  pageSize?: number
  warehouseId?: number
  areaId?: number
  itemName?: string
  itemNo?: string
  skuName?: string
  skuNo?: string
}): Promise<PageResult<InventoryItem>> => {
  return http.get('/inventory/list', { params })
}

// 仓库管理
export const getWarehouseListApi = (params: PageParams & {
  warehouseName?: string
  warehouseCode?: string
  status?: string
}): Promise<PageResult<Warehouse>> => {
  return http.get('/warehouse/list', { params })
}

export const getWarehouseByIdApi = (id: number): Promise<Warehouse> => {
  return http.get(`/warehouse/getWarehouseById?id=${id}`)
}

export const addWarehouseApi = (warehouse: Partial<Warehouse>): Promise<void> => {
  return http.post('/warehouse/addWarehouse', warehouse)
}

export const updateWarehouseApi = (warehouse: Partial<Warehouse>): Promise<void> => {
  return http.put('/warehouse/updateWarehouse', warehouse)
}

export const deleteWarehouseApi = (ids: string): Promise<void> => {
  return http.delete(`/warehouse/deleteWarehouse?ids=${ids}`)
}

// 库区管理
export const getAreaListApi = (params: {
  pageNum?: number
  pageSize?: number
  areaName?: string
  areaCode?: string
  warehouseId?: number
}): Promise<PageResult<WarehouseArea>> => {
  return http.get('/area/list', { params })
}

export const getAreaByIdApi = (id: number): Promise<WarehouseArea> => {
  return http.get(`/area/getAreaById?id=${id}`)
}

export const addAreaApi = (area: Partial<WarehouseArea>): Promise<void> => {
  return http.post('/area/addArea', area)
}

export const updateAreaApi = (area: Partial<WarehouseArea>): Promise<void> => {
  return http.put('/area/updateArea', area)
}

export const deleteAreaApi = (ids: string): Promise<void> => {
  return http.delete(`/area/deleteArea?ids=${ids}`)
}

// 物料管理
export const getItemListApi = (params: PageParams & {
  itemName?: string
  itemCode?: string
  categoryId?: number
  itemCategory?: string
  brandId?: number
}): Promise<PageResult<Item>> => {
  return http.get('/item/list', { params })
}

export const getItemByIdApi = (id: number): Promise<Item> => {
  return http.get(`/item/getItemById?id=${id}`)
}

export const addItemApi = (item: Partial<Item>): Promise<Item> => {
  return http.post('/item/addItem', item)
}

export const updateItemApi = (item: Partial<Item>): Promise<void> => {
  return http.put('/item/updateItem', item)
}

export const deleteItemApi = (ids: string): Promise<void> => {
  return http.delete(`/item/deleteItem?ids=${ids}`)
}

// 物料分类管理
export const getItemCategoryListApi = (params: PageParams & {
  categoryName?: string
  categoryCode?: string
  parentId?: number
}): Promise<PageResult<ItemCategory>> => {
  return http.get('/itemCategory/list', { params })
}

export const getItemCategoryByIdApi = (id: number): Promise<ItemCategory> => {
  return http.get(`/itemCategory/getItemCategoryById?id=${id}`)
}

export const addItemCategoryApi = (category: Partial<ItemCategory>): Promise<void> => {
  return http.post('/itemCategory/addItemCategory', category)
}

export const updateItemCategoryApi = (category: Partial<ItemCategory>): Promise<void> => {
  return http.put('/itemCategory/updateItemCategory', category)
}

export const deleteItemCategoryApi = (ids: string): Promise<void> => {
  return http.delete(`/itemCategory/deleteItemCategory?ids=${ids}`)
}

/**
 * 根据父类型ID查询物料类型
 */
export const getItemCategoryByParentIdApi = (parentId: number | null = 0): Promise<ItemCategory[]> => {
  // 如果 parentId 为 null 或 undefined，使用 0 作为根节点
  const parentIdParam = parentId ?? 0
  return http.get(`/itemCategory/getByParentId?parentId=${parentIdParam}`)
}

/**
 * 递归构建物料分类树形结构
 */
const buildCategoryTree = async (parentId: number | null = 0): Promise<ItemCategory[]> => {
  try {
    const children = await getItemCategoryByParentIdApi(parentId)
    
    // 如果没有子节点，返回空数组
    if (!children || children.length === 0) {
      return []
    }
    
    // 递归获取每个子节点的子节点
    const tree = await Promise.all(
      children.map(async (category) => {
        // 确保 category.id 存在才递归
        if (category.id !== undefined && category.id !== null) {
          const subTree = await buildCategoryTree(category.id)
          return {
            ...category,
            children: subTree.length > 0 ? subTree : undefined
          }
        }
        // 如果 id 不存在，返回原始 category，不添加 children
        return category
      })
    )
    
    return tree
  } catch (error) {
    console.error(`获取parentId=${parentId}的子分类失败:`, error)
    return []
  }
}

/**
 * 获取物料分类树形结构（用于树形控件）
 * 通过递归调用 getByParentId 接口构建树结构
 * 从根节点（parentId=0）开始构建
 */
export const getItemCategoryTreeApi = async (): Promise<ItemCategory[]> => {
  return buildCategoryTree(0)
}



// 商品规格管理
export const getItemSkuListApi = (params: PageParams & {
  itemId?: number
  categoryId?: number
  itemName?: string
  skuName?: string
  skuCode?: string
}): Promise<PageResult<ItemSku>> => {
  return http.get('/itemSku/list', { params })
}

export const getItemSkuByIdApi = (id: number): Promise<ItemSku> => {
  return http.get(`/itemSku/getItemSkuById?id=${id}`)
}

export const addItemSkuApi = (sku: Partial<ItemSku>): Promise<void> => {
  return http.post('/itemSku/addItemSku', sku)
}

export const updateItemSkuApi = (sku: Partial<ItemSku>): Promise<void> => {
  return http.put('/itemSku/updateItemSku', sku)
}

export const deleteItemSkuApi = (ids: string): Promise<void> => {
  return http.delete(`/itemSku/deleteItemSku?ids=${ids}`)
}

// 往来单位管理
export const getMerchantListApi = (params: PageParams & {
  merchantName?: string
  merchantCode?: string
  merchantType?: string
}): Promise<PageResult<Merchant>> => {
  return http.get('/merchant/list', { params })
}

export const getMerchantByIdApi = (id: number): Promise<Merchant> => {
  return http.get(`/merchant/getMerchantById?id=${id}`)
}

export const addMerchantApi = (merchant: Partial<Merchant>): Promise<void> => {
  return http.post('/merchant/addMerchant', merchant)
}

export const updateMerchantApi = (merchant: Partial<Merchant>): Promise<void> => {
  return http.put('/merchant/updateMerchant', merchant)
}

export const deleteMerchantApi = (ids: string): Promise<void> => {
  return http.delete(`/merchant/deleteMerchant?ids=${ids}`)
}

// 商品品牌管理
export const getItemBrandListApi = (params: PageParams & {
  brandName?: string
}): Promise<PageResult<ItemBrand>> => {
  return http.get('/itemBrand/list', { params })
}

export const getItemBrandByIdApi = (id: number): Promise<ItemBrand> => {
  return http.get(`/itemBrand/getItemBrandById?id=${id}`)
}

export const addItemBrandApi = (itemBrand: Partial<ItemBrand>): Promise<void> => {
  return http.post('/itemBrand/addItemBrand', itemBrand)
}

export const updateItemBrandApi = (itemBrand: Partial<ItemBrand>): Promise<void> => {
  return http.put('/itemBrand/updateItemBrand', itemBrand)
}

export const deleteItemBrandApi = (ids: string): Promise<void> => {
  return http.delete(`/itemBrand/deleteItemBrand?ids=${ids}`)
}

// 入库单管理
export const getInboundOrderListApi = (params: PageParams & {
  orderNo?: string
  orderType?: string
  orderStatus?: string
  supplierId?: number
}): Promise<PageResult<InboundOrder>> => {
  return http.get('/inboundOrder/list', { params })
}

export const getInboundOrderByIdApi = (id: number): Promise<InboundOrder> => {
  return http.get(`/inboundOrder/getInboundOrderById?id=${id}`)
}

export const addInboundOrderApi = (order: Partial<InboundOrder>): Promise<void> => {
  return http.post('/inboundOrder/addInboundOrder', order)
}

export const updateInboundOrderApi = (order: Partial<InboundOrder>): Promise<void> => {
  return http.put('/inboundOrder/updateInboundOrder', order)
}

export const deleteInboundOrderApi = (ids: string): Promise<void> => {
  return http.delete(`/inboundOrder/deleteInboundOrder?ids=${ids}`)
}

// 入库单详情管理
export const getInboundOrderDetailListApi = (params: PageParams & {
  orderId?: number
}): Promise<PageResult<any>> => {
  return http.get('/inboundOrderDetail/list', { params })
}

export const addInboundOrderDetailApi = (detail: any): Promise<void> => {
  return http.post('/inboundOrderDetail/addInboundOrderDetail', detail)
}

export const updateInboundOrderDetailApi = (detail: any): Promise<void> => {
  return http.put('/inboundOrderDetail/updateInboundOrderDetail', detail)
}

export const deleteInboundOrderDetailApi = (ids: string): Promise<void> => {
  return http.delete(`/inboundOrderDetail/deleteInboundOrderDetail?ids=${ids}`)
}

// 出库单管理
export const getOutboundOrderListApi = (params: PageParams & {
  orderNo?: string
  orderType?: string
  orderStatus?: string
  customerId?: number
}): Promise<PageResult<OutboundOrder>> => {
  return http.get('/outboundOrder/list', { params })
}

export const getOutboundOrderByIdApi = (id: number): Promise<OutboundOrder> => {
  return http.get(`/outboundOrder/getOutboundOrderById?id=${id}`)
}

export const addOutboundOrderApi = (order: Partial<OutboundOrder>): Promise<void> => {
  return http.post('/outboundOrder/addOutboundOrder', order)
}

export const updateOutboundOrderApi = (order: Partial<OutboundOrder>): Promise<void> => {
  return http.put('/outboundOrder/updateOutboundOrder', order)
}

export const deleteOutboundOrderApi = (ids: string): Promise<void> => {
  return http.delete(`/outboundOrder/deleteOutboundOrder?ids=${ids}`)
}

// ========== 库存历史控制器 ==========

/**
 * 分页查询库存历史列表
 */
export const getInventoryHistoryListApi = (params: PageParams & {
  warehouseId?: number
  areaId?: number
  itemName?: string
  itemNo?: string
  skuName?: string
  skuNo?: string
  orderType?: number // 1.入库 2.出库 3.移库 4.盘库
  orderNo?: string
}): Promise<PageResult<InventoryHistory>> => {
  return http.get('/inventoryHistory/list', { params })
}

// ========== 移库单控制器 ==========

/**
 * 分页查询移库单列表
 */
export const getMovementOrderListApi = (params: PageParams & {
  movementOrderNo?: string
  movementOrderStatus?: number
}): Promise<PageResult<MovementOrder>> => {
  return http.get('/movementOrder/list', { params })
}

/**
 * 根据id获取移库单详细信息
 */
export const getMovementOrderByIdApi = (id: number): Promise<MovementOrder> => {
  return http.get(`/movementOrder/getMovementOrderById?id=${id}`)
}

/**
 * 新增移库单
 * 返回移库单号
 */
export const addMovementOrderApi = (order: Partial<MovementOrder>): Promise<string> => {
  return http.post('/movementOrder/addMovementOrder', order)
}

/**
 * 修改移库单
 */
export const updateMovementOrderApi = (order: Partial<MovementOrder>): Promise<void> => {
  return http.put('/movementOrder/updateMovementOrder', order)
}

/**
 * 删除移库单
 */
export const deleteMovementOrderApi = (id: string): Promise<void> => {
  return http.delete(`/movementOrder/deleteMovementOrder?id=${id}`)
}

/**
 * 完成移库
 */
export const completeMovementApi = (id: number): Promise<void> => {
  return http.post(`/movementOrder/completeMovement?id=${id}`)
}

// ========== 移库单详情控制器 ==========

/**
 * 分页查询移库单详情列表
 */
export const getMovementOrderDetailListApi = (params: PageParams & {
  movementOrderId?: number
}): Promise<PageResult<MovementOrderDetail>> => {
  return http.get('/movementOrderDetail/list', { params })
}

/**
 * 根据id获取移库单详情详细信息
 */
export const getMovementOrderDetailByIdApi = (id: number): Promise<MovementOrderDetail> => {
  return http.get(`/movementOrderDetail/getMovementOrderDetailById?id=${id}`)
}

/**
 * 新增移库单详情
 */
export const addMovementOrderDetailApi = (detail: Partial<MovementOrderDetail>): Promise<void> => {
  return http.post('/movementOrderDetail/addMovementOrderDetail', detail)
}

/**
 * 修改移库单详情
 */
export const updateMovementOrderDetailApi = (detail: Partial<MovementOrderDetail>): Promise<void> => {
  return http.put('/movementOrderDetail/updateMovementOrderDetail', detail)
}

/**
 * 删除移库单详情
 */
export const deleteMovementOrderDetailApi = (ids: string): Promise<void> => {
  return http.delete(`/movementOrderDetail/deleteMovementOrderDetail?ids=${ids}`)
}

// ========== 盘库单控制器 ==========

/**
 * 分页查询盘库单列表
 */
export const getCheckOrderListApi = (params: {
  pageNo?: number
  pageSize?: number
  checkOrderNo?: string
  checkOrderStatus?: number
}): Promise<PageResult<CheckOrder>> => {
  return http.get('/checkOrder/list', { params })
}

/**
 * 根据id获取盘库单详细信息
 */
export const getCheckOrderByIdApi = (id: number): Promise<CheckOrder> => {
  return http.get(`/checkOrder/getCheckOrderById?id=${id}`)
}

/**
 * 新增盘库单
 */
export const addCheckOrderApi = (order: Partial<CheckOrder>): Promise<string> => {
  return http.post('/checkOrder/addCheckOrder', order)
}

/**
 * 修改盘库单
 */
export const updateCheckOrderApi = (order: Partial<CheckOrder>): Promise<void> => {
  return http.put('/checkOrder/updateCheckOrder', order)
}

/**
 * 删除盘库单
 */
export const deleteCheckOrderApi = (id: number): Promise<void> => {
  return http.delete(`/checkOrder/deleteCheckOrder?id=${id}`)
}

/**
 * 完成盘库
 */
export const completeCheckApi = (id: number): Promise<void> => {
  return http.post(`/checkOrder/completeCheck?id=${id}`)
}

// ========== 盘库单详情控制器 ==========

/**
 * 分页查询盘库单详情列表
 */
export const getCheckOrderDetailListApi = (params: PageParams & {
  checkOrderId?: number
}): Promise<PageResult<CheckOrderDetail>> => {
  return http.get('/checkOrderDetail/list', { params })
}

/**
 * 根据id获取盘库单详情详细信息
 */
export const getCheckOrderDetailByIdApi = (id: number): Promise<CheckOrderDetail> => {
  return http.get(`/checkOrderDetail/getCheckOrderDetailById?id=${id}`)
}

/**
 * 新增盘库单详情
 */
export const addCheckOrderDetailApi = (detail: Partial<CheckOrderDetail>): Promise<void> => {
  return http.post('/checkOrderDetail/addCheckOrderDetail', detail)
}

/**
 * 修改盘库单详情
 */
export const updateCheckOrderDetailApi = (detail: Partial<CheckOrderDetail>): Promise<void> => {
  return http.put('/checkOrderDetail/updateCheckOrderDetail', detail)
}

/**
 * 删除盘库单详情
 */
export const deleteCheckOrderDetailApi = (ids: string): Promise<void> => {
  return http.delete(`/checkOrderDetail/deleteCheckOrderDetail?ids=${ids}`)
}

// ========== 库存详情控制器 ==========

/**
 * 分页查询库存详情列表
 */
export const getInventoryDetailListApi = (params: {
  pageNum?: number
  pageNo?: number
  pageSize?: number
  warehouseId?: number
  areaId?: number
  orderNo?: string
  itemName?: string
  itemNo?: string
  skuName?: string
  skuNo?: string
  receiptDate?: string
}): Promise<PageResult<InventoryDetail>> => {
  const { pageNo, ...rest } = params
  const queryParams = {
    ...rest,
    pageNum: params.pageNum ?? pageNo
  }
  return http.get('/inventoryDetail/list', { params: queryParams })
}

export const getInventoryDetailByIdApi = (id: number): Promise<InventoryDetail> => {
  return http.get(`/inventoryDetail/getInventoryDetailById?id=${id}`)
}

// ========== 入库单管理（基于实际后端控制器） ==========

/**
 * 分页查询入库单列表
 */
export const getReceiptOrderListApi = (params: PageParams & {
  receiptOrderNo?: string
  receiptOrderStatus?: number
}): Promise<PageResult<ReceiptOrder>> => {
  return http.get('/receiptOrder/list', { params })
}

/**
 * 根据id获取入库单详细信息
 */
export const getReceiptOrderByIdApi = (id: number): Promise<ReceiptOrder> => {
  return http.get(`/receiptOrder/getReceiptOrderById?id=${id}`)
}

/**
 * 新增入库单
 */
// 后端返回 BaseResult<String>，响应拦截器会提取 data 字段，所以返回的是入库单号（字符串）
export const addReceiptOrderApi = (order: Partial<ReceiptOrder>): Promise<string> => {
  return http.post('/receiptOrder/addReceiptOrder', order)
}

/**
 * 修改入库单
 */
export const updateReceiptOrderApi = (order: Partial<ReceiptOrder>): Promise<void> => {
  return http.put('/receiptOrder/updateReceiptOrder', order)
}

/**
 * 删除入库单
 */
export const deleteReceiptOrderApi = (id: number): Promise<void> => {
  return http.delete(`/receiptOrder/deleteReceiptOrder?id=${id}`)
}

/**
 * 完成入库
 */
export const completeReceiptApi = (id: number): Promise<void> => {
  return http.post(`/receiptOrder/completeReceipt?id=${id}`)
}

// ========== 入库单详情管理 ==========

/**
 * 分页查询入库单详情列表
 */
export const getReceiptOrderDetailListApi = (params: PageParams & {
  receiptOrderId?: number
}): Promise<PageResult<ReceiptOrderDetail>> => {
  return http.get('/receiptOrderDetail/list', { params })
}

/**
 * 根据id获取入库单详情
 */
export const getReceiptOrderDetailByIdApi = (id: number): Promise<ReceiptOrderDetail> => {
  return http.get(`/receiptOrderDetail/getReceiptOrderDetailById?id=${id}`)
}

/**
 * 新增入库单详情
 */
export const addReceiptOrderDetailApi = (detail: Partial<ReceiptOrderDetail>): Promise<void> => {
  return http.post('/receiptOrderDetail/addReceiptOrderDetail', detail)
}

/**
 * 修改入库单详情
 */
export const updateReceiptOrderDetailApi = (detail: Partial<ReceiptOrderDetail>): Promise<void> => {
  return http.put('/receiptOrderDetail/updateReceiptOrderDetail', detail)
}

/**
 * 删除入库单详情
 */
export const deleteReceiptOrderDetailApi = (ids: string): Promise<void> => {
  return http.delete(`/receiptOrderDetail/deleteReceiptOrderDetail?ids=${ids}`)
}

// ========== 获取选项数据（基于现有API实现） ==========

/**
 * 获取仓库选项（基于分页接口，仅用于下拉选择）
 */
export const getWarehouseOptionsApi = (): Promise<Array<{
  id: number
  warehouseName: string
}>> => {
  return http.get('/warehouse/list', { 
    params: { pageNum: 1, pageSize: 1000 }
  }).then((res: PageResult<Warehouse>) => 
    res.records?.map(item => ({
      id: item.id,
      warehouseName: item.warehouseName
    })) || []
  )
}

/**
 * 获取库区选项（基于分页接口，仅用于下拉选择）
 */
export const getWarehouseAreaOptionsApi = (warehouseId?: number): Promise<Array<{
  id: number
  areaName: string
  warehouseId: number
}>> => {
  return http.get('/area/list', { 
    params: { pageNum: 1, pageSize: 1000, warehouseId }
  }).then((res: PageResult<WarehouseArea>) => 
    res.records?.map(item => ({
      id: item.id,
      areaName: item.areaName,
      warehouseId: item.warehouseId
    })) || []
  )
}

/**
 * 获取供应商选项（基于分页接口，仅用于下拉选择）
 */
export const getMerchantOptionsApi = (): Promise<Array<{
  id: number
  merchantName: string
}>> => {
  return http.get('/merchant/list', {
    params: { pageNum: 1, pageSize: 1000 }
  }).then((res: PageResult<Merchant>) => {
    const records = res.records || []
    return records
      .filter((item): item is Merchant & { id: number } => item.id !== undefined)
      .map(item => ({
        id: item.id,
        merchantName: item.merchantName
      }))
  })
}

// ========== 出库单管理（基于实际后端控制器） ==========

/**
 * 分页查询出库单列表
 */
export const getShipmentOrderListApi = (params: PageParams & {
  shipmentOrderNo?: string
  shipmentOrderStatus?: number
}): Promise<PageResult<ShipmentOrder>> => {
  return http.get('/shipmentOrder/list', { params })
}

/**
 * 根据id获取出库单详细信息
 */
export const getShipmentOrderByIdApi = (id: number): Promise<ShipmentOrder> => {
  return http.get(`/shipmentOrder/getShipmentOrderById?id=${id}`)
}

/**
 * 新增出库单
 */
export const addShipmentOrderApi = (order: Partial<ShipmentOrder>): Promise<string> => {
  return http.post('/shipmentOrder/addShipmentOrder', order)
}

/**
 * 修改出库单
 */
export const updateShipmentOrderApi = (order: Partial<ShipmentOrder>): Promise<void> => {
  return http.put('/shipmentOrder/updateShipmentOrder', order)
}

/**
 * 删除出库单
 */
export const deleteShipmentOrderApi = (id: number): Promise<void> => {
  return http.delete(`/shipmentOrder/deleteShipmentOrder?id=${id}`)
}

/**
 * 完成出库
 */
export const completeShipmentApi = (id: number): Promise<void> => {
  return http.post(`/shipmentOrder/completeShipment?id=${id}`)
}

// ========== 出库单详情管理 ==========

/**
 * 分页查询出库单详情列表
 */
export const getShipmentOrderDetailListApi = (params: PageParams & {
  shipmentOrderId?: number
}): Promise<PageResult<ShipmentOrderDetail>> => {
  return http.get('/shipmentOrderDetail/list', { params })
}

/**
 * 根据id获取出库单详情
 */
export const getShipmentOrderDetailByIdApi = (id: number): Promise<ShipmentOrderDetail> => {
  return http.get(`/shipmentOrderDetail/getShipmentOrderDetailById?id=${id}`)
}

/**
 * 新增出库单详情
 */
export const addShipmentOrderDetailApi = (detail: Partial<ShipmentOrderDetail>): Promise<void> => {
  return http.post('/shipmentOrderDetail/addShipmentOrderDetail', detail)
}

/**
 * 修改出库单详情
 */
export const updateShipmentOrderDetailApi = (detail: Partial<ShipmentOrderDetail>): Promise<void> => {
  return http.put('/shipmentOrderDetail/updateShipmentOrderDetail', detail)
}

/**
 * 删除出库单详情
 */
export const deleteShipmentOrderDetailApi = (ids: string): Promise<void> => {
  return http.delete(`/shipmentOrderDetail/deleteShipmentOrderDetail?ids=${ids}`)
}