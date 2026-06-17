<template>
  <div class="dashboard-wrapper">
    <div class="welcome-banner">
      <div class="banner-left">
        <el-icon :size="28" class="banner-icon"><Sunny /></el-icon>
        <div class="banner-text">
          <div class="welcome-title">{{ greeting }}，欢迎回来 👋</div>
          <div class="welcome-sub">今天是 {{ today }} · {{ weekDay }}</div>
        </div>
      </div>
      <div class="banner-right">
        <el-icon :size="16"><Clock /></el-icon>
        <span>{{ currentTime }}</span>
      </div>
    </div>

    <el-row :gutter="16" class="metrics-row">
      <el-col :span="4">
        <div class="metric-card mc-blue">
          <div class="mc-top">
            <div class="mc-icon"><el-icon><Document /></el-icon></div>
            <div class="mc-delta">较昨日 +{{ deltaOrders }}%</div>
          </div>
          <div class="mc-value">{{ todayOrders }}</div>
          <div class="mc-label">今日工单</div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="metric-card mc-green">
          <div class="mc-top">
            <div class="mc-icon"><el-icon><Box /></el-icon></div>
            <div class="mc-delta">单位 件</div>
          </div>
          <div class="mc-value">{{ todayOutput }}</div>
          <div class="mc-label">今日产量</div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="metric-card mc-purple">
          <div class="mc-top">
            <div class="mc-icon"><el-icon><Medal /></el-icon></div>
            <div class="mc-delta">趋势 <el-icon style="vertical-align:-2px"><TrendCharts /></el-icon></div>
          </div>
          <div class="mc-value">{{ qualifiedRate }}<span class="mc-percent">%</span></div>
          <div class="mc-label">合格率</div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="metric-card mc-cyan">
          <div class="mc-top">
            <div class="mc-icon"><el-icon><Monitor /></el-icon></div>
            <div class="mc-delta">设备利用率</div>
          </div>
          <div class="mc-value">{{ deviceRate }}<span class="mc-percent">%</span></div>
          <div class="mc-progress"><el-progress :percentage="deviceRate" :color="'#fff'" :stroke-width="6" :show-text="false" /></div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="metric-card mc-orange">
          <div class="mc-top">
            <div class="mc-icon"><el-icon><Bell /></el-icon></div>
            <div class="mc-delta">待处理</div>
          </div>
          <div class="mc-value">{{ pendingReports }}</div>
          <div class="mc-label">待处理报工</div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="metric-card mc-red">
          <div class="mc-top">
            <div class="mc-icon"><el-icon><Warning /></el-icon></div>
            <div class="mc-delta">低于安全库存</div>
          </div>
          <div class="mc-value">{{ lowStockItems }}</div>
          <div class="mc-label">低库存物料</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="content-row">
      <el-col :span="16">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title"><el-icon style="color:#409eff"><DataLine /></el-icon> 工单进度</span>
              <el-radio-group v-model="orderFilter" size="small">
                <el-radio-button label="all">全部</el-radio-button>
                <el-radio-button label="active">进行中</el-radio-button>
                <el-radio-button label="done">已完成</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="progress-list">
            <div v-for="(item, idx) in filteredOrders" :key="idx" class="progress-item">
              <div class="progress-info">
                <span class="progress-name">{{ item.name }}</span>
                <span class="progress-code">{{ item.code }}</span>
              </div>
              <div class="progress-bar-row">
                <el-progress :percentage="item.progress" :color="progressColor(item.progress)" :stroke-width="14" />
                <span class="progress-status">{{ item.status }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title"><el-icon style="color:#67c23a"><PieChart /></el-icon> 库存分布</span>
            </div>
          </template>
          <div class="pie-area">
            <div class="pie-center">
              <div class="pie-total-label">总库存</div>
              <div class="pie-total-value">{{ warehouseTotal }}</div>
              <div class="pie-total-unit">件</div>
            </div>
            <div class="pie-list">
              <div v-for="(w, idx) in warehouseList" :key="idx" class="pie-item">
                <span class="pie-dot" :style="{ background: w.color }"></span>
                <span class="pie-name">{{ w.name }}</span>
                <span class="pie-bar-wrapper"><span class="pie-bar" :style="{ width: w.percent + '%', background: w.color }"></span></span>
                <span class="pie-percent">{{ w.percent }}%</span>
                <span class="pie-value">{{ w.value }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="content-row">
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title"><el-icon style="color:#8e44ad"><Histogram /></el-icon> 近期质量趋势</span>
            </div>
          </template>
          <div class="quality-area">
            <div class="quality-chart">
              <div class="chart-grid">
                <div v-for="(item, idx) in qualityData" :key="idx" class="chart-col">
                  <div class="col-value" :style="{ height: item.rate + '%' }">
                    <div class="col-tooltip">{{ item.rate }}%</div>
                  </div>
                  <div class="col-label">{{ item.date }}</div>
                </div>
              </div>
            </div>
            <div class="quality-legend">
              <div class="legend-item"><span class="dot" style="background:#67c23a"></span>合格率趋势 (近 {{ qualityData.length }} 天)</div>
              <div class="legend-item"><span class="dot" style="background:#f56c6c"></span>目标值 ≥ 95%</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title"><el-icon style="color:#e6a23c"><Tools /></el-icon> 设备状态</span>
            </div>
          </template>
          <el-table :data="deviceStatus" border style="width: 100%">
            <el-table-column label="车间" prop="workshop" />
            <el-table-column label="运行" prop="running" align="center">
              <template #default="{ row }">
                <el-tag type="success" size="small">{{ row.running }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="停机" prop="stopped" align="center">
              <template #default="{ row }">
                <el-tag type="info" size="small">{{ row.stopped }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="维修" prop="repair" align="center">
              <template #default="{ row }">
                <el-tag type="warning" size="small">{{ row.repair }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="利用率" align="center">
              <template #default="{ row }">
                <el-progress :percentage="Math.round((row.running / (row.running + row.stopped + row.repair)) * 100)" :color="'#409eff'" :stroke-width="10" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const todayOrders = ref(186)
const deltaOrders = ref(12)
const todayOutput = ref(12580)
const qualifiedRate = ref(97.4)
const deviceRate = ref(82)
const pendingReports = ref(23)
const lowStockItems = ref(5)
const orderFilter = ref('all')

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '凌晨好'
  if (h < 12) return '早上好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const currentTime = ref('')
function updateTime() {
  const d = new Date()
  const pad = (n) => String(n).padStart(2, '0')
  currentTime.value = `${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

const today = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
})
const weekDay = computed(() => {
  const days = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return days[new Date().getDay()]
})

const allOrders = ref([
  { code: 'WO-20241015-001', name: 'A1 型电机外壳加工', progress: 85, status: '进行中', active: true },
  { code: 'WO-20241015-002', name: 'B2 型齿轮组件装配', progress: 100, status: '已完成', active: false },
  { code: 'WO-20241016-003', name: 'C3 型传动轴加工', progress: 62, status: '进行中', active: true },
  { code: 'WO-20241016-004', name: 'D4 型法兰盘批量生产', progress: 45, status: '进行中', active: true },
  { code: 'WO-20241017-005', name: 'E5 型精密连接器', progress: 100, status: '已完成', active: false },
  { code: 'WO-20241017-006', name: 'F6 型控制面板组装', progress: 28, status: '进行中', active: true },
  { code: 'WO-20241018-007', name: 'G7 型散热片批量加工', progress: 15, status: '进行中', active: true },
  { code: 'WO-20241018-008', name: 'H8 型传感器外壳', progress: 100, status: '已完成', active: false }
])

const filteredOrders = computed(() => {
  if (orderFilter.value === 'active') return allOrders.value.filter((o) => o.active)
  if (orderFilter.value === 'done') return allOrders.value.filter((o) => !o.active)
  return allOrders.value
})

const progressColor = (p) => {
  if (p >= 100) return '#67c23a'
  if (p >= 60) return '#409eff'
  if (p >= 30) return '#e6a23c'
  return '#f56c6c'
}

const warehouseList = ref([
  { name: '原料仓', value: 5820, color: '#409eff', percent: 0 },
  { name: '半成品仓', value: 3150, color: '#67c23a', percent: 0 },
  { name: '成品仓', value: 2680, color: '#e6a23c', percent: 0 },
  { name: '辅料仓', value: 1420, color: '#8e44ad', percent: 0 },
  { name: '危化品仓', value: 380, color: '#f56c6c', percent: 0 }
])
const warehouseTotal = computed(() => warehouseList.value.reduce((s, w) => s + w.value, 0))
warehouseList.value.forEach((w) => (w.percent = Math.round((w.value / warehouseTotal.value) * 100)))

const qualityData = ref([
  { date: '10/12', rate: 96.2 },
  { date: '10/13', rate: 97.8 },
  { date: '10/14', rate: 95.5 },
  { date: '10/15', rate: 98.1 },
  { date: '10/16', rate: 97.3 },
  { date: '10/17', rate: 96.8 },
  { date: '10/18', rate: 97.4 }
])

const deviceStatus = ref([
  { workshop: '一车间', running: 18, stopped: 2, repair: 1 },
  { workshop: '二车间', running: 14, stopped: 3, repair: 2 },
  { workshop: '三车间', running: 10, stopped: 1, repair: 1 },
  { workshop: '装配车间', running: 12, stopped: 2, repair: 0 },
  { workshop: '检测车间', running: 8, stopped: 0, repair: 1 }
])

let timer = null
onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})
onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
})
</script>

<style lang="scss" scoped>
.dashboard-wrapper {
  padding: 12px;
  background: #f3f5f9;
  min-height: calc(100vh - 24px);
}

.welcome-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  color: #fff;
  margin-bottom: 16px;
  box-shadow: 0 4px 14px rgba(102, 126, 234, 0.25);
}
.banner-left { display: flex; align-items: center; gap: 14px; }
.banner-icon { opacity: 0.95; }
.welcome-title { font-size: 18px; font-weight: 600; }
.welcome-sub { font-size: 13px; opacity: 0.9; margin-top: 4px; }
.banner-right { display: flex; align-items: center; gap: 6px; font-size: 14px; font-weight: 500; }

.metrics-row { margin-bottom: 16px; }
.metric-card {
  padding: 18px 20px;
  border-radius: 10px;
  color: #fff;
  position: relative;
  overflow: hidden;
  min-height: 118px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
}
.mc-blue { background: linear-gradient(135deg, #42a5f5 0%, #1976d2 100%); }
.mc-green { background: linear-gradient(135deg, #66bb6a 0%, #2e7d32 100%); }
.mc-purple { background: linear-gradient(135deg, #ab47bc 0%, #6a1b9a 100%); }
.mc-cyan { background: linear-gradient(135deg, #26c6da 0%, #00838f 100%); }
.mc-orange { background: linear-gradient(135deg, #ffa726 0%, #ef6c00 100%); }
.mc-red { background: linear-gradient(135deg, #ef5350 0%, #c62828 100%); }
.mc-top { display: flex; justify-content: space-between; align-items: center; }
.mc-icon { font-size: 22px; opacity: 0.9; }
.mc-delta { font-size: 12px; opacity: 0.9; }
.mc-value { font-size: 30px; font-weight: 700; margin-top: 12px; line-height: 1; }
.mc-percent { font-size: 16px; font-weight: 500; margin-left: 2px; }
.mc-label { font-size: 13px; opacity: 0.95; margin-top: 6px; }
.mc-progress { margin-top: 10px; }
.mc-progress :deep(.el-progress__text) { color: #fff; font-size: 12px; }

.content-row { margin-bottom: 16px; }
.chart-card { border-radius: 10px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 15px; font-weight: 600; color: #303133; display: inline-flex; align-items: center; gap: 6px; }

.progress-list { display: flex; flex-direction: column; gap: 12px; padding: 4px 0; }
.progress-item { padding: 8px 4px; }
.progress-info { display: flex; justify-content: space-between; margin-bottom: 6px; }
.progress-name { font-size: 14px; color: #303133; font-weight: 500; }
.progress-code { font-size: 12px; color: #909399; }
.progress-bar-row { display: flex; align-items: center; gap: 14px; }
.progress-bar-row :deep(.el-progress) { flex: 1; }
.progress-status { font-size: 12px; color: #606266; width: 60px; text-align: right; }

.pie-area { padding: 4px 0; }
.pie-center {
  text-align: center;
  padding: 18px 0 20px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.08), rgba(103, 194, 58, 0.08));
  border-radius: 8px;
  margin-bottom: 18px;
}
.pie-total-label { font-size: 13px; color: #606266; }
.pie-total-value { font-size: 32px; font-weight: 700; color: #303133; margin: 4px 0; }
.pie-total-unit { font-size: 12px; color: #909399; }
.pie-list { display: flex; flex-direction: column; gap: 12px; }
.pie-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  padding: 4px 2px;
}
.pie-dot { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }
.pie-name { width: 72px; color: #606266; flex-shrink: 0; }
.pie-bar-wrapper { flex: 1; height: 8px; background: #ecf0f5; border-radius: 4px; overflow: hidden; }
.pie-bar { display: block; height: 100%; border-radius: 4px; transition: width 0.4s; }
.pie-percent { width: 42px; text-align: right; color: #303133; font-weight: 500; }
.pie-value { width: 60px; text-align: right; color: #909399; font-size: 12px; }

.quality-area { padding: 8px 4px; }
.quality-chart { height: 220px; padding: 20px 10px 30px; }
.chart-grid {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  height: 100%;
  gap: 12px;
  border-bottom: 1px dashed #dcdfe6;
  padding-bottom: 8px;
}
.chart-col { flex: 1; display: flex; flex-direction: column; align-items: center; height: 100%; }
.col-value {
  width: 70%;
  background: linear-gradient(180deg, #8e44ad 0%, #c39bd3 100%);
  border-radius: 6px 6px 0 0;
  position: relative;
  min-height: 6px;
  transition: height 0.4s;
}
.col-tooltip {
  position: absolute;
  top: -22px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 11px;
  color: #8e44ad;
  font-weight: 600;
  white-space: nowrap;
}
.col-label { font-size: 12px; color: #909399; margin-top: 8px; }
.quality-legend { display: flex; justify-content: center; gap: 24px; margin-top: 12px; font-size: 12px; color: #606266; }
.legend-item { display: flex; align-items: center; gap: 6px; }
.legend-item .dot { width: 8px; height: 8px; border-radius: 50%; display: inline-block; }

@media (max-width: 1400px) {
  .metrics-row .el-col { margin-bottom: 12px; }
}
</style>
