<template>
  <div class="calendar-wrapper">
    <el-card shadow="never" class="main-card">
      <div class="card-header">
        <div class="title-area">
          <el-icon :size="22" class="title-icon"><Calendar /></el-icon>
          <span class="title-text">排班日历</span>
        </div>
        <div class="filter-area">
          <el-date-picker
            v-model="selectedMonth"
            type="month"
            placeholder="选择月份"
            value-format="YYYY-MM"
            style="width: 160px"
            @change="refreshData"
          />
          <el-select v-model="selectedTeam" placeholder="班组筛选" clearable style="width: 170px" @change="refreshData">
            <el-option v-for="t in teamOptions" :key="t" :label="t" :value="t" />
          </el-select>
        </div>
      </div>

      <el-calendar v-model="selectedDate" :range="[startDay, endDay]">
        <template #date-cell="{ data }">
          <div class="calendar-cell" :class="{ 'is-today': data.day === today }">
            <div class="cell-day">{{ data.day.split('-').slice(2).join('') }}</div>
            <div class="cell-content">
              <div v-for="(item, idx) in getSchedulesOfDate(data.day)" :key="idx" class="schedule-item" :style="{ background: shiftColor(item.shift) }">
                <span class="shift-label">{{ item.shift }}</span>
                <span class="team-label">{{ item.team }} · {{ item.people }}人</span>
              </div>
              <div v-if="getSchedulesOfDate(data.day).length === 0" class="empty-cell">—</div>
            </div>
          </div>
        </template>
      </el-calendar>

      <el-divider content-position="left" class="stat-divider">
        <span class="divider-title">排班统计 · {{ selectedMonth }}</span>
      </el-divider>

      <el-row :gutter="16" class="stat-row">
        <el-col :span="8">
          <div class="stat-card stat-blue">
            <div class="stat-label">本月总排班天数</div>
            <div class="stat-value">{{ totalScheduledDays }}</div>
            <div class="stat-unit">天</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-green">
            <div class="stat-label">总工时</div>
            <div class="stat-value">{{ totalHours }}</div>
            <div class="stat-unit">小时</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-orange">
            <div class="stat-label">总出勤人次</div>
            <div class="stat-value">{{ totalPeople }}</div>
            <div class="stat-unit">人次</div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="stat-row">
        <el-col :span="24">
          <div class="team-summary-card">
            <div class="summary-title">各班组出勤汇总</div>
            <div class="summary-list">
              <div v-for="(item, idx) in teamSummary" :key="idx" class="summary-item">
                <div class="summary-team">
                  <span class="dot" :style="{ background: teamColors[idx % teamColors.length] }"></span>
                  {{ item.team }}
                </div>
                <div class="summary-bar-wrapper">
                  <div class="summary-bar" :style="{ width: item.percent + '%', background: teamColors[idx % teamColors.length] }"></div>
                </div>
                <div class="summary-value">{{ item.days }}天 · {{ item.hours }}h · {{ item.people }}人</div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const teamOptions = ['甲班第一组', '甲班第二组', '乙班第一组', '丙班装配组', '检测质检组', '夜班维修组']

const now = new Date()
const today = now.toISOString().slice(0, 10)
const selectedMonth = ref(today.slice(0, 7))
const selectedTeam = ref('')
const selectedDate = ref(today)

const startDay = computed(() => selectedMonth.value + '-01')
const endDay = computed(() => {
  const [y, m] = selectedMonth.value.split('-').map(Number)
  const last = new Date(y, m, 0)
  return last.toISOString().slice(0, 10)
})

const shiftColorMap = {
  白班: 'linear-gradient(135deg,#409eff,#79bbff)',
  中班: 'linear-gradient(135deg,#67c23a,#95d475)',
  夜班: 'linear-gradient(135deg,#909399,#a6a9ad)',
  早班: 'linear-gradient(135deg,#e6a23c,#f0c78a)',
  加班班: 'linear-gradient(135deg,#f56c6c,#f89898)'
}
const shiftColor = (s) => shiftColorMap[s] || 'linear-gradient(135deg,#909399,#a6a9ad)'

const teamColors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#8e44ad']

const allSchedules = ref([])

function refreshData() {
  const [y, m] = selectedMonth.value.split('-').map(Number)
  const lastDay = new Date(y, m, 0).getDate()
  const list = []
  const shifts = ['白班', '中班', '夜班', '早班', '加班班']
  let idx = 0
  for (let d = 1; d <= lastDay; d++) {
    const day = `${selectedMonth.value}-${String(d).padStart(2, '0')}`
    const weekday = new Date(y, m - 1, d).getDay()
    const count = weekday === 0 ? 1 : (weekday === 6 ? 2 : 2 + Math.floor(Math.random() * 2))
    const availableTeams = selectedTeam.value ? [selectedTeam.value] : teamOptions
    for (let i = 0; i < count; i++) {
      const team = availableTeams[Math.floor((i + idx) % availableTeams.length)]
      const shift = shifts[(idx + d) % shifts.length]
      const people = 5 + ((d + idx) % 10)
      list.push({ day, team, shift, people, hours: shift === '加班班' ? 4 : 8 })
      idx++
    }
  }
  allSchedules.value = list
}

const getSchedulesOfDate = (day) => {
  return allSchedules.value.filter((s) => s.day === day).slice(0, 3)
}

const totalScheduledDays = computed(() => {
  const days = new Set(allSchedules.value.map((s) => s.day))
  return days.size
})

const totalHours = computed(() => allSchedules.value.reduce((sum, s) => sum + s.hours * s.people, 0))
const totalPeople = computed(() => allSchedules.value.reduce((sum, s) => sum + s.people, 0))

const teamSummary = computed(() => {
  const map = {}
  for (const s of allSchedules.value) {
    if (!map[s.team]) map[s.team] = { team: s.team, days: 0, hours: 0, people: 0 }
    map[s.team].days += 1
    map[s.team].hours += s.hours * s.people
    map[s.team].people += s.people
  }
  const arr = Object.values(map)
  const maxDays = arr.length ? Math.max(...arr.map((a) => a.days)) : 1
  arr.forEach((a) => (a.percent = Math.round((a.days / maxDays) * 100)))
  return arr
})

onMounted(() => {
  refreshData()
})
</script>

<style lang="scss" scoped>
.calendar-wrapper { padding: 12px; }
.main-card { border-radius: 8px; }
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px 8px 16px;
}
.title-area {
  display: flex;
  align-items: center;
  gap: 8px;
}
.title-icon { color: #409eff; }
.title-text {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.filter-area { display: flex; gap: 12px; }

.calendar-cell {
  min-height: 90px;
  padding: 4px 6px;
  display: flex;
  flex-direction: column;
}
.calendar-cell.is-today {
  background: linear-gradient(135deg, rgba(64,158,255,0.08), rgba(64,158,255,0.02));
  border-radius: 4px;
}
.cell-day {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}
.cell-content { flex: 1; display: flex; flex-direction: column; gap: 3px; }
.empty-cell { color: #c0c4cc; font-size: 12px; text-align: center; padding-top: 16px; }
.schedule-item {
  color: #fff;
  padding: 3px 6px;
  border-radius: 4px;
  font-size: 11px;
  line-height: 1.4;
  display: flex;
  flex-direction: column;
}
.shift-label { font-weight: 600; }
.team-label { font-size: 10px; opacity: 0.9; }

.stat-divider { margin: 16px 0 12px; }
.divider-title { color: #409eff; font-weight: 600; font-size: 14px; }

.stat-row { margin-top: 0; }
.stat-card {
  padding: 18px 20px;
  border-radius: 8px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
}
.stat-blue { background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%); }
.stat-green { background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%); }
.stat-orange { background: linear-gradient(135deg, #e6a23c 0%, #f0c78a 100%); }
.stat-label { font-size: 13px; opacity: 0.95; }
.stat-value { font-size: 28px; font-weight: 700; }
.stat-unit { font-size: 12px; opacity: 0.9; }

.team-summary-card {
  padding: 16px 20px;
  background: #fafbfc;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}
.summary-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 14px;
}
.summary-list { display: flex; flex-direction: column; gap: 10px; }
.summary-item {
  display: flex;
  align-items: center;
  gap: 14px;
}
.summary-team {
  width: 110px;
  font-size: 13px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 6px;
}
.summary-team .dot { width: 8px; height: 8px; border-radius: 50%; display: inline-block; }
.summary-bar-wrapper {
  flex: 1;
  height: 10px;
  background: #ecf0f5;
  border-radius: 5px;
  overflow: hidden;
}
.summary-bar { height: 100%; border-radius: 5px; transition: width 0.3s; }
.summary-value {
  width: 150px;
  text-align: right;
  font-size: 13px;
  color: #606266;
}

:deep(.el-calendar__header) { padding: 12px 20px; }
:deep(.el-calendar__body) { padding: 8px 20px 20px; }
:deep(.el-calendar-table thead th) { color: #606266; font-weight: 500; padding: 8px 4px; }
:deep(.el-calendar-table .el-calendar-day) { height: 100px; padding: 4px; }
</style>
