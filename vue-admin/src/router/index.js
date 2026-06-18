import { createRouter, createWebHashHistory } from 'vue-router'

const Layout = () => import('@/layout/index.vue')

export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index.vue'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled', affix: true }
      }
    ]
  },
  {
    path: '/mes-dashboard',
    component: Layout,
    redirect: '/mes-dashboard/index',
    meta: { title: '生产看板', icon: 'DataBoard' },
    children: [
      {
        path: 'index',
        name: 'MesDashboard',
        component: () => import('@/views/mes/dashboard/index.vue'),
        meta: { title: '数据看板', icon: 'DataLine' }
      }
    ]
  },
  {
    path: '/mes-md',
    component: Layout,
    redirect: '/mes-md/material',
    meta: { title: '基础数据', icon: 'Files' },
    children: [
      {
        path: 'material',
        name: 'MesMdMaterial',
        component: () => import('@/views/mes/md/material/index.vue'),
        meta: { title: '物料管理', icon: 'Box' }
      },
      {
        path: 'customer',
        name: 'MesMdCustomer',
        component: () => import('@/views/mes/md/customer/index.vue'),
        meta: { title: '客户管理', icon: 'User' }
      },
      {
        path: 'vendor',
        name: 'MesMdVendor',
        component: () => import('@/views/mes/md/vendor/index.vue'),
        meta: { title: '供应商管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'workshop',
        name: 'MesMdWorkshop',
        component: () => import('@/views/mes/md/workshop/index.vue'),
        meta: { title: '车间管理', icon: 'Factory' }
      },
      {
        path: 'workstation',
        name: 'MesMdWorkstation',
        component: () => import('@/views/mes/md/workstation/index.vue'),
        meta: { title: '工作站管理', icon: 'Cpu' }
      },
      {
        path: 'process',
        name: 'MesMdProcess',
        component: () => import('@/views/mes/md/process/index.vue'),
        meta: { title: '工序管理', icon: 'Operation' }
      },
      {
        path: 'route',
        name: 'MesMdRoute',
        component: () => import('@/views/mes/md/route/index.vue'),
        meta: { title: '工艺路线', icon: 'Connection' }
      },
      {
        path: 'bom',
        name: 'MesMdBom',
        component: () => import('@/views/mes/md/bom/index.vue'),
        meta: { title: 'BOM 清单', icon: 'Grid' }
      }
    ]
  },
  {
    path: '/mes-pro',
    component: Layout,
    redirect: '/mes-pro/workorder',
    meta: { title: '生产管理', icon: 'Tools' },
    children: [
      {
        path: 'workorder',
        name: 'MesProWorkorder',
        component: () => import('@/views/mes/pro/workorder/index.vue'),
        meta: { title: '生产工单', icon: 'Document' }
      },
      {
        path: 'schedule',
        name: 'MesProSchedule',
        component: () => import('@/views/mes/pro/schedule/index.vue'),
        meta: { title: '生产排产', icon: 'Calendar' }
      },
      {
        path: 'report',
        name: 'MesProReport',
        component: () => import('@/views/mes/pro/report/index.vue'),
        meta: { title: '生产报工', icon: 'Edit' }
      },
      {
        path: 'board',
        name: 'MesProBoard',
        component: () => import('@/views/mes/pro/board/index.vue'),
        meta: { title: '车间看板', icon: 'Monitor' }
      }
    ]
  },
  {
    path: '/mes-wm',
    component: Layout,
    redirect: '/mes-wm/warehouse',
    meta: { title: '仓储管理', icon: 'Collection' },
    children: [
      {
        path: 'warehouse',
        name: 'MesWmWarehouse',
        component: () => import('@/views/mes/wm/warehouse/index.vue'),
        meta: { title: '仓库设置', icon: 'Warehouse' }
      },
      {
        path: 'area',
        name: 'MesWmArea',
        component: () => import('@/views/mes/wm/area/index.vue'),
        meta: { title: '库区设置', icon: 'Grid' }
      },
      {
        path: 'location',
        name: 'MesWmLocation',
        component: () => import('@/views/mes/wm/location/index.vue'),
        meta: { title: '库位设置', icon: 'LocationFilled' }
      },
      {
        path: 'stock',
        name: 'MesWmStock',
        component: () => import('@/views/mes/wm/stock/index.vue'),
        meta: { title: '实时库存', icon: 'PieChart' }
      },
      {
        path: 'in',
        name: 'MesWmIn',
        component: () => import('@/views/mes/wm/in/index.vue'),
        meta: { title: '入库作业', icon: 'Download' }
      },
      {
        path: 'out',
        name: 'MesWmOut',
        component: () => import('@/views/mes/wm/out/index.vue'),
        meta: { title: '出库作业', icon: 'Upload' }
      }
    ]
  },
  {
    path: '/mes-qc',
    component: Layout,
    redirect: '/mes-qc/defect',
    meta: { title: '质量管理', icon: 'Medal' },
    children: [
      {
        path: 'defect',
        name: 'MesQcDefect',
        component: () => import('@/views/mes/qc/defect/index.vue'),
        meta: { title: '不良项管理', icon: 'Warning' }
      },
      {
        path: 'template',
        name: 'MesQcTemplate',
        component: () => import('@/views/mes/qc/template/index.vue'),
        meta: { title: '检验模板', icon: 'DocumentCopy' }
      },
      {
        path: 'record',
        name: 'MesQcRecord',
        component: () => import('@/views/mes/qc/record/index.vue'),
        meta: { title: '检验记录', icon: 'List' }
      }
    ]
  },
  {
    path: '/mes-dv',
    component: Layout,
    redirect: '/mes-dv/device',
    meta: { title: '设备管理', icon: 'Cpu' },
    children: [
      {
        path: 'device',
        name: 'MesDvDevice',
        component: () => import('@/views/mes/dv/device/index.vue'),
        meta: { title: '设备台账', icon: 'Monitor' }
      },
      {
        path: 'check',
        name: 'MesDvCheck',
        component: () => import('@/views/mes/dv/check/index.vue'),
        meta: { title: '点检保养', icon: 'Tools' }
      },
      {
        path: 'repair',
        name: 'MesDvRepair',
        component: () => import('@/views/mes/dv/repair/index.vue'),
        meta: { title: '故障维修', icon: 'Setting' }
      }
    ]
  },
  {
    path: '/mes-tm',
    component: Layout,
    redirect: '/mes-tm/tool',
    meta: { title: '工具管理', icon: 'SetUp' },
    children: [
      {
        path: 'tool',
        name: 'MesTmTool',
        component: () => import('@/views/mes/tm/tool/index.vue'),
        meta: { title: '工装夹具', icon: 'Aim' }
      }
    ]
  },
  {
    path: '/mes-cal',
    component: Layout,
    redirect: '/mes-cal/team',
    meta: { title: '排班管理', icon: 'Calendar' },
    children: [
      {
        path: 'team',
        name: 'MesCalTeam',
        component: () => import('@/views/mes/cal/team/index.vue'),
        meta: { title: '班组管理', icon: 'UserFilled' }
      },
      {
        path: 'shift',
        name: 'MesCalShift',
        component: () => import('@/views/mes/cal/shift/index.vue'),
        meta: { title: '班次设置', icon: 'AlarmClock' }
      },
      {
        path: 'plan',
        name: 'MesCalPlan',
        component: () => import('@/views/mes/cal/plan/index.vue'),
        meta: { title: '排班计划', icon: 'Calendar' }
      },
      {
        path: 'calendar',
        name: 'MesCalCalendar',
        component: () => import('@/views/mes/cal/calendar/index.vue'),
        meta: { title: '排班日历', icon: 'Date' }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'user',
        name: 'SystemUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'role',
        name: 'SystemRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'UserFilled' }
      },
      {
        path: 'menu',
        name: 'SystemMenu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', icon: 'Menu' }
      },
      {
        path: 'org',
        name: 'SystemOrg',
        component: () => import('@/views/system/org/index.vue'),
        meta: { title: '组织管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'login-log',
        name: 'SystemLoginLog',
        component: () => import('@/views/system/login-log/index.vue'),
        meta: { title: '登录日志', icon: 'Document' }
      },
      {
        path: 'oper-log',
        name: 'SystemOperLog',
        component: () => import('@/views/system/oper-log/index.vue'),
        meta: { title: '操作日志', icon: 'Notebook' }
      },
      {
        path: 'dict',
        name: 'SystemDict',
        component: () => import('@/views/system/dict/index.vue'),
        meta: { title: '字典管理', icon: 'Collection' }
      }
    ]
  },
  {
    path: '/404',
    component: () => import('@/views/error/404.vue'),
    hidden: true
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes: constantRoutes
})

export default router
