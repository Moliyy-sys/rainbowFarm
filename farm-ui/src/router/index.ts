import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// 配置进度条
NProgress.configure({ showSpinner: false })

// 基础路由
const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      hidden: true
    }
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '404',
      hidden: true
    }
  }
]

// 动态路由
const asyncRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '首页',
          icon: 'House',
          affix: true
        }
      }
    ]
  },
  {
    path: '/inventory',
    name: 'Inventory',
    component: () => import('@/layout/index.vue'),
    meta: {
      title: '库存管理',
      icon: 'Box'
    },
    children: [
      {
        path: 'statistics',
        name: 'InventoryStatistics',
        component: () => import('@/views/inventory/statistics/index.vue'),
        redirect: '/inventory/statistics/count',
        meta: {
          title: '库存统计',
          icon: 'DataAnalysis'
        },
        children: [
          {
            path: 'count',
            name: 'InventoryStatisticsCount',
            component: () => import('@/views/inventory/inventory-list/index.vue'),
            meta: {
              title: '库存数量',
              icon: 'List'
            }
          },
          {
            path: 'detail',
            name: 'InventoryStatisticsDetail',
            component: () => import('@/views/inventory/statistics/detail/index.vue'),
            meta: {
              title: '库存详情',
              icon: 'View'
            }
          },
          {
            path: 'flow',
            name: 'InventoryStatisticsFlow',
            component: () => import('@/views/inventory/statistics/flow/index.vue'),
            meta: {
              title: '库存流水',
              icon: 'Document'
            }
          }
        ]
      },
      {
        path: 'detail',
        name: 'InventoryDetail',
        component: () => import('@/views/inventory/detail/index.vue'),
        meta: {
          title: '库存详情',
          icon: 'View'
        }
      },
      {
        path: 'record',
        name: 'InventoryRecord',
        component: () => import('@/views/inventory/record/index.vue'),
        meta: {
          title: '库存记录',
          icon: 'Document'
        }
      },
      {
        path: 'warehouse',
        name: 'Warehouse',
        component: () => import('@/views/inventory/warehouse/index.vue'),
        meta: {
          title: '仓库管理',
          icon: 'OfficeBuilding'
        }
      },
      {
        path: 'warehouse-area',
        name: 'WarehouseArea',
        component: () => import('@/views/inventory/warehouse-area/index.vue'),
        meta: {
          title: '库区管理',
          icon: 'Grid'
        }
      },
      {
        path: 'item',
        name: 'Item',
        component: () => import('@/views/inventory/item/index.vue'),
        meta: {
          title: '物料管理',
          icon: 'Goods'
        }
      },
      {
        path: 'merchant',
        name: 'Merchant',
        component: () => import('@/views/inventory/merchant/index.vue'),
        meta: {
          title: '往来单位',
          icon: 'UserFilled'
        }
      },
      {
        path: 'brand',
        name: 'Brand',
        component: () => import('@/views/inventory/brand/index.vue'),
        meta: {
          title: '商品品牌',
          icon: 'Medal'
        }
      },
      {
        path: 'inbound',
        name: 'Inbound',
        component: () => import('@/views/inventory/inbound/index.vue'),
        meta: {
          title: '入库',
          icon: 'Download'
        }
      },
      {
        path: 'outbound',
        name: 'Outbound',
        component: () => import('@/views/inventory/outbound/index.vue'),
        meta: {
          title: '出库',
          icon: 'Upload'
        }
      },
      {
        path: 'transfer',
        name: 'InventoryTransfer',
        component: () => import('@/views/inventory/transfer/index.vue'),
        meta: {
          title: '移库',
          icon: 'Switch'
        }
      },
      {
        path: 'stocktaking',
        name: 'Stocktaking',
        component: () => import('@/views/inventory/stocktaking/index.vue'),
        meta: {
          title: '盘库',
          icon: 'DataAnalysis'
        }
      }
    ]
  },
  {
    path: '/plant',
    name: 'Plant',
    component: () => import('@/layout/index.vue'),
    meta: {
      title: '种植管理',
      icon: 'Cherry'
    },
    children: [
      {
        path: 'task',
        name: 'PlantTask',
        component: () => import('@/views/plant/task/index.vue'),
        meta: {
          title: '我的批次',
          icon: 'List'
        }
      },
      {
        path: 'batch',
        name: 'TaskBatch',
        component: () => import('@/views/plant/batch/index.vue'),
        meta: {
          title: '种植批次',
          icon: 'Collection'
        }
      },
      {
        path: 'germplasm',
        name: 'Germplasm',
        component: () => import('@/views/plant/germplasm/index.vue'),
        meta: {
          title: '种质管理',
          icon: 'Apple'
        }
      },
      {
        path: 'land',
        name: 'Land',
        component: () => import('@/views/plant/land/index.vue'),
        meta: {
          title: '地块管理',
          icon: 'MapLocation'
        }
      },
      {
        path: 'material',
        name: 'Material',
        component: () => import('@/views/plant/material/index.vue'),
        meta: {
          title: '农资管理',
          icon: 'Box'
        }
      },
      {
        path: 'machine',
        name: 'Machine',
        component: () => import('@/views/plant/machine/index.vue'),
        meta: {
          title: '机械管理',
          icon: 'Tools'
        }
      },
      {
        path: 'employee',
        name: 'Employee',
        component: () => import('@/views/plant/employee/index.vue'),
        meta: {
          title: '雇员管理',
          icon: 'Avatar'
        }
      }
    ]
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/layout/index.vue'),
    meta: {
      title: '个人中心',
      icon: 'User',
      hidden: true // 不在侧边栏显示，通过导航栏头像菜单进入
    },
    children: [
      {
        path: '',
        name: 'UserProfile',
        component: () => import('@/views/profile/index.vue'),
        meta: {
          title: '个人中心',
          icon: 'User'
        }
      }
    ]
  },
  {
    path: '/system',
    name: 'System',
    component: () => import('@/layout/index.vue'),
    meta: {
      title: '系统管理',
      icon: 'Setting'
    },
    children: [
      {
        path: 'user',
        name: 'SystemUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: {
          title: '用户管理',
          icon: 'User'
        }
      },
      {
        path: 'role',
        name: 'SystemRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: {
          title: '角色管理',
          icon: 'Avatar'
        }
      },
      {
        path: 'permission',
        name: 'SystemPermission',
        component: () => import('@/views/system/permission/index.vue'),
        meta: {
          title: '权限管理',
          icon: 'Key'
        }
      },
      {
        path: 'dict',
        name: 'SystemDict',
        component: () => import('@/views/system/dict/index.vue'),
        meta: {
          title: '字典管理',
          icon: 'Collection'
        }
      },
      {
        path: 'login-log',
        name: 'LoginLog',
        component: () => import('@/views/system/login-log/index.vue'),
        meta: {
          title: '登录日志',
          icon: 'Document'
        }
      },
      {
        path: 'operation-log',
        name: 'OperationLog',
        component: () => import('@/views/system/operation-log/index.vue'),
        meta: {
          title: '操作日志',
          icon: 'DocumentCopy'
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
    meta: {
      hidden: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: [...constantRoutes, ...asyncRoutes],
  scrollBehavior: () => ({ top: 0 })
})

//路由守卫
router.beforeEach(async (to, from, next) => {
  NProgress.start()
  
  const userStore = useUserStore()
  
  if (to.path === '/login') {
    if (userStore.isLoggedIn) {
      next('/')
    } else {
      next()
    }
    return
  }
  
  if (!userStore.isLoggedIn) {
    ElMessage.error('请先登录')
    next('/login')
    return
  }
  
  // 如果没有用户信息，尝试获取（失败不影响访问）
  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch (error) {
      console.warn('获取用户信息失败，但允许继续访问:', error)
      // 不强制退出，允许用户继续访问
    }
  }
  
  next()
})

router.afterEach(() => {
  NProgress.done()
})

export default router
