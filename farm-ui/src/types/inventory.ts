// 库存相关类型定义

// 库存信息
export interface InventoryItem {
  id: number
  warehouseId: number
  warehouseName: string
  areaId: number
  areaName: string
  itemName: string
  itemNo: string
  skuName: string
  skuNo: string
  quantity: number
  availableQuantity: number
  lockedQuantity: number
  unit: string
  unitPrice: number
  totalValue: number
  lastInTime: string
  lastOutTime: string
  createTime: string
  updateTime: string
  itemImage?: string
}

// 仓库信息
export interface Warehouse {
  id: number
  warehouseName: string
  warehouseCode: string
  remark: string
  orderNum: number
  createBy: string | null
  createTime: string
  updateBy: string | null
  updateTime: string
}

// 库区信息
export interface WarehouseArea {
  id: number
  warehouseId: number
  areaCode: string
  areaName: string
  remark: string
  createBy: string | null
  createTime: string
  updateBy: string | null
  updateTime: string
  // 关联信息
  warehouseName?: string
}

// 库区信息（与后端Area实体完全对应）
export interface Area {
  id: number
  warehouseId: number
  areaCode: string
  areaName: string
  remark: string
  createBy: string | null
  createTime: string
  updateBy: string | null
  updateTime: string
}

// 物料分类信息（对应后端ItemCategory实体）
export interface ItemCategory {
  id?: number
  parentId?: number
  categoryCode: string
  categoryName: string
  orderNum?: number
  remark?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  // 扩展字段（用于前端树形显示）
  children?: ItemCategory[]
  label?: string // 用于树形控件显示
}

// 物料信息（对应后端Item实体）
export interface Item {
  id?: number
  itemCode: string
  itemName: string
  categoryId?: number
  itemBrand?: number
  unit: string
  remark?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  // 扩展字段（用于前端显示）
  brandName?: string
  categoryName?: string
}

// 物料规格（与后端ItemSku实体对应）
export interface ItemSku {
  id?: number
  itemId?: number
  skuCode: string
  skuName: string
  // 扩展展示字段（后端可能未返回，前端可选）
  itemName?: string
  unit?: string
  barcode?: string
  sellingPrice?: number
  costPrice?: number
  grossWeight?: number
  netWeight?: number
  length?: number
  width?: number
  height?: number
  remark?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
}

// 往来单位
export interface Merchant {
  id?: number
  merchantCode: string
  merchantName: string
  merchantType: number // 1-供应商 2-客户 3-供应商客户
  bankName?: string
  bankAccount?: string
  address?: string
  mobile?: string
  tel?: string
  contactPerson?: string
  email?: string
  remark?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
}

// 商品品牌
export interface ItemBrand {
  id?: number
  brandName: string
  brandCode?: string
  sort?: number
  status?: number // 0-禁用 1-启用
  remark?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
}

// 入库单
export interface InboundOrder {
  orderId: number
  orderNo: string
  orderType: string
  warehouseId: number
  supplierId: number
  supplierName: string
  totalAmount: number
  orderStatus: string
  planInTime: string
  actualInTime: string
  operator: string
  remark: string
  createTime: string
  updateTime: string
  items: InboundOrderItem[]
}

// 入库单明细
export interface InboundOrderItem {
  itemId: number
  skuId: number
  itemName: string
  skuName: string
  planQuantity: number
  actualQuantity: number
  unitPrice: number
  totalAmount: number
  productionDate: string
  expirationDate: string
  batchNo: string
  remark: string
}

// 出库单
export interface OutboundOrder {
  orderId: number
  orderNo: string
  orderType: string
  warehouseId: number
  customerId: number
  customerName: string
  totalAmount: number
  orderStatus: string
  planOutTime: string
  actualOutTime: string
  operator: string
  remark: string
  createTime: string
  updateTime: string
  items: OutboundOrderItem[]
}

// 出库单明细
export interface OutboundOrderItem {
  itemId: number
  skuId: number
  itemName: string
  skuName: string
  planQuantity: number
  actualQuantity: number
  unitPrice: number
  totalAmount: number
  batchNo: string
  remark: string
}

// 库存历史
export interface InventoryHistory {
  id: number
  warehouseId: number
  warehouseName: string
  areaId: number
  areaName: string
  itemName: string
  skuName: string
  skuId: number
  quantity: number
  amount: number
  orderId: number
  orderNo: string
  orderType: number // 1.入库 2.出库 3.移库 4.盘库
  productionDate: string
  expirationDate: string
  remark: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
}

// 移库单
export interface MovementOrder {
  id: number
  movementOrderNo: string
  sourceWarehouseId: number
  sourceAreaId: number
  targetWarehouseId: number
  targetAreaId: number
  movementOrderStatus: number
  totalQuantity: number
  remark: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  detailList: MovementOrderDetail[]
  // 前端扩展字段（用于显示）
  sourceWarehouseName?: string
  sourceAreaName?: string
  targetWarehouseName?: string
  targetAreaName?: string
  totalItems?: number
  progress?: number
  creatorName?: string
}

// 移库单详情
export interface MovementOrderDetail {
  id: number
  movementOrderId: number
  skuId: number
  quantity: number
  productionDate: string
  expirationDate: string
  sourceWarehouseId: number
  sourceAreaId: number
  targetWarehouseId: number
  targetAreaId: number
  inventoryDetailId: number
  remark: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  // 前端扩展字段（用于显示）
  itemName?: string
  itemImage?: string
  skuName?: string
  unit?: string
  transferProgress?: number
  availableQuantity?: number
  transferQuantity?: number
}

// 盘库单
export interface CheckOrder {
  id: number
  checkOrderNo: string
  warehouseId: number
  areaId: number
  checkOrderStatus: number
  totalQuantity: number
  checkOrderTotal?: number // 盈亏数（所有详情的盈亏数之和）
  remark: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  detailList: CheckOrderDetail[]
}

// 盘库单详情
export interface CheckOrderDetail {
  id: number
  checkOrderId: number
  skuId: number
  systemQuantity: number
  actualQuantity: number
  differenceQuantity: number
  productionDate: string
  expirationDate: string
  warehouseId: number
  areaId: number
  inventoryDetailId: number
  remark: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
}

// 库存详情
export interface InventoryDetail {
  id: number
  warehouseId: number
  areaId: number
  skuId: number
  itemId?: number
  orderNo?: string
  quantity: number
  availableQuantity: number
  lockedQuantity: number
  productionDate: string
  expirationDate: string
  unitPrice: number
  totalValue: number
  batchNo: string
  createTime: string
  updateTime: string
  remainQuantity?: number
  amount?: number
  // 关联字段（前端显示用）
  warehouseName?: string
  areaName?: string
  itemName?: string
  itemNo?: string
  skuName?: string
  skuNo?: string
  unit?: string
  itemImage?: string
  receiptDate?: string
}

// 出库单（基于后端ShipmentOrder实体）
export interface ShipmentOrder {
  id: number
  shipmentOrderNo: string
  merchantId: number
  receivableAmount: number
  totalQuantity: number
  shipmentOrderStatus: number // 0:未出库, 1:已出库
  warehouseId: number
  areaId: number
  remark: string
  createBy: string | null
  createTime: string
  updateBy: string | null
  updateTime: string | null
  // 关联信息（前端显示用，需要通过ID获取）
  merchantName?: string
  warehouseName?: string
  areaName?: string
  detailList?: ShipmentOrderDetail[] | null
}

// 出库单详情（基于后端ShipmentOrderDetail实体）
export interface ShipmentOrderDetail {
  id?: number
  shipmentOrderId?: number
  skuId: number
  quantity: number
  amount: number
  warehouseId: number
  areaId: number
  productionDate?: string
  expirationDate?: string
  inventoryDetailId: number
  remark?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  // 关联信息和前端计算字段
  skuName?: string
  itemName?: string
  warehouseName?: string
  areaName?: string
  unit?: string
  unitPrice?: number // 前端计算用单价
  inboundOrderNo?: string
}

// 入库单（基于后端ReceiptOrder实体）
export interface ReceiptOrder {
  id?: number
  receiptOrderNo: string
  merchantId: number
  totalQuantity: number
  payableAmount: number
  receiptOrderStatus: number // 0:待入库, 1:已入库
  warehouseId: number
  areaId: number
  checkOrderId?: number // 关联的盘库单ID
  checkOrderTotal?: number // 盘库单盈亏数（所有物料盈亏数之和）
  remark?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  // 关联信息
  merchantName?: string
  warehouseName?: string
  areaName?: string
  detailList?: ReceiptOrderDetail[]
}

// 入库单详情（基于后端ReceiptOrderDetail实体）
export interface ReceiptOrderDetail {
  id?: number
  receiptOrderId?: number
  skuId: number
  quantity: number
  amount: number
  productionDate?: string
  expirationDate?: string
  warehouseId: number
  areaId: number
  remark?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  // 关联信息和前端计算字段
  skuName?: string
  itemName?: string
  warehouseName?: string
  areaName?: string
  unit?: string
  unitPrice?: number // 前端计算用单价
}