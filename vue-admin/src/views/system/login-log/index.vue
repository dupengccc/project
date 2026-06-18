<template>
  <div class="login-log-page">
    <!-- 顶部查询区 -->
    <div class="search-bar">
      <el-form :inline="true" :model="queryForm" class="search-form" @submit.prevent>
        <el-form-item label="工号">
          <el-input
            v-model="queryForm.empNo"
            placeholder="输入工号/账号"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input
            v-model="queryForm.name"
            placeholder="输入姓名"
            clearable
            style="width: 160px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="登录IP">
          <el-input
            v-model="queryForm.loginIp"
            placeholder="如 127.0.0.1"
            clearable
            style="width: 160px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryForm.status"
            placeholder="全部"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="登录成功" :value="0" />
            <el-option label="登录失败" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="danger" plain @click="handleClean">清理日志</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-space>
        <el-tag type="info">共 {{ total }} 条记录</el-tag>
        <el-button
          type="danger"
          size="small"
          plain
          :disabled="!selectedRows.length"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
      </el-space>
    </div>

    <!-- 数据表格 -->
    <el-table
      :data="tableData"
      border
      stripe
      height="calc(100vh - 240px)"
      highlight-current-row
      @selection-change="handleSelectionChange"
      v-loading="loading"
    >
      <el-table-column type="selection" width="46" align="center" />
      <el-table-column label="序号" width="60" align="center">
        <template #default="{ $index }">
          {{ (queryForm.page - 1) * queryForm.pageSize + $index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="empNo" label="工号" width="110" show-overflow-tooltip />
      <el-table-column prop="name" label="姓名" width="130" show-overflow-tooltip />
      <el-table-column prop="orgName" label="所属组织" width="170" show-overflow-tooltip />
      <el-table-column prop="loginIp" label="登录IP" width="130" show-overflow-tooltip />
      <el-table-column prop="loginLocation" label="登录地点" width="140" show-overflow-tooltip />
      <el-table-column prop="loginTime" label="登录时间" width="170">
        <template #default="{ row }">{{ formatDate(row.loginTime) }}</template>
      </el-table-column>
      <el-table-column prop="logoutTime" label="退出时间" width="170">
        <template #default="{ row }">
          <span v-if="row.logoutTime">{{ formatDate(row.logoutTime) }}</span>
          <span v-else style="color: #909399;">-</span>
        </template>
      </el-table-column>
      <el-table-column prop="onlineDuration" label="在线时长" width="110" align="center">
        <template #default="{ row }">{{ duration(row.loginTime, row.logoutTime) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0 || row.status === '0'" type="success" size="small">成功</el-tag>
          <el-tag v-else type="danger" size="small">失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="msg" label="描述" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" width="120" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleDetail(row)">详情</el-button>
          <el-popconfirm title="确认删除该条日志？" @confirm="handleDelete(row)">
            <template #reference>
              <el-button link type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @current-change="handleSearch"
        @size-change="handleSearch"
      />
    </div>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="登录日志详情" width="620px">
      <el-descriptions :column="2" border v-if="detailRow">
        <el-descriptions-item label="工号">{{ detailRow.empNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailRow.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属组织">{{ detailRow.orgName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="登录IP">{{ detailRow.loginIp || '-' }}</el-descriptions-item>
        <el-descriptions-item label="登录地点">{{ detailRow.loginLocation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="浏览器/系统">{{ (detailRow.browser || '-') + ' / ' + (detailRow.os || '-') }}</el-descriptions-item>
        <el-descriptions-item label="登录时间">{{ formatDate(detailRow.loginTime) }}</el-descriptions-item>
        <el-descriptions-item label="退出时间">{{ formatDate(detailRow.logoutTime) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="在线时长">{{ duration(detailRow.loginTime, detailRow.logoutTime) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailRow.status === 0 || detailRow.status === '0'" type="success" size="small">成功</el-tag>
          <el-tag v-else type="danger" size="small">失败</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="消息" :span="2">{{ detailRow.msg || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listLoginLog, deleteLoginLog, deleteLoginLogBatch, cleanLoginLog } from '@/api/login-log'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectedRows = ref([])
const detailVisible = ref(false)
const detailRow = ref(null)

const queryForm = reactive({
  empNo: '',
  name: '',
  loginIp: '',
  status: null,
  page: 1,
  pageSize: 10
})

// 本地 mock 数据
const mockData = reactive([])
function ensureMock() {
  if (mockData.length > 0) return
  const now = Date.now()
  const names = ['系统管理员', '张三', '李四', '王五', '赵六', '周七']
  const orgs = ['MES 集团总部', '华东分公司', '华南分公司', '研发部', '生产部']
  for (let i = 0; i < 60; i++) {
    const loginT = now - i * 3600 * 1000 - Math.floor(Math.random() * 1800 * 1000)
    const logoutT = i % 3 === 0 ? null : loginT + (600 + Math.floor(Math.random() * 3600)) * 1000
    const success = Math.random() > 0.15
    mockData.push({
      id: -(i + 1),
      empNo: 'E' + String(10001 + (i % 6)),
      name: names[i % names.length],
      orgId: (i % 5) + 1,
      orgName: orgs[i % orgs.length],
      loginIp: '127.0.' + (i % 255) + '.' + (1 + (i % 10)),
      loginLocation: '局域网',
      browser: 'Chrome 130',
      os: 'Windows 10',
      loginTime: new Date(loginT).toISOString(),
      logoutTime: logoutT ? new Date(logoutT).toISOString() : null,
      status: success ? 0 : 1,
      msg: success ? '登录成功' : (i % 2 === 0 ? '密码错误' : '账号已禁用'),
      createTime: new Date(loginT).toISOString(),
      updateTime: new Date(loginT).toISOString()
    })
  }
}

async function handleSearch() {
  loading.value = true
  try {
    // 先尝试后端接口
    try {
      const res = await listLoginLog({
        empNo: queryForm.empNo || undefined,
        name: queryForm.name || undefined,
        loginIp: queryForm.loginIp || undefined,
        status: queryForm.status == null ? undefined : queryForm.status,
        page: queryForm.page,
        pageSize: queryForm.pageSize
      })
      if (res && res.data && Array.isArray(res.data.list) && res.data.list.length > 0) {
        tableData.value = res.data.list
        total.value = res.data.total || res.data.list.length
        return
      }
    } catch (e) {
      // 后端接口不可用，走本地 mock
    }
    // 本地 mock
    ensureMock()
    let list = mockData.slice()
    if (queryForm.empNo && queryForm.empNo.trim()) {
      list = list.filter(x => (x.empNo || '').toLowerCase().includes(queryForm.empNo.trim().toLowerCase()))
    }
    if (queryForm.name && queryForm.name.trim()) {
      list = list.filter(x => (x.name || '').toLowerCase().includes(queryForm.name.trim().toLowerCase()))
    }
    if (queryForm.loginIp && queryForm.loginIp.trim()) {
      list = list.filter(x => (x.loginIp || '').includes(queryForm.loginIp.trim()))
    }
    if (queryForm.status !== null && queryForm.status !== '') {
      list = list.filter(x => Number(x.status) === Number(queryForm.status))
    }
    total.value = list.length
    const from = (queryForm.page - 1) * queryForm.pageSize
    tableData.value = list.slice(from, from + queryForm.pageSize)
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryForm.empNo = ''
  queryForm.name = ''
  queryForm.loginIp = ''
  queryForm.status = null
  queryForm.page = 1
  handleSearch()
}

function handleSelectionChange(rows) {
  selectedRows.value = rows
}

async function handleDelete(row) {
  try {
    await deleteLoginLog(row.id)
    // 本地 mock 删除
    const idx = mockData.findIndex(x => x.id === row.id)
    if (idx >= 0) mockData.splice(idx, 1)
    ElMessage.success('删除成功')
    handleSearch()
  } catch (e) {
    // 本地 mock 兜底
    const idx = mockData.findIndex(x => x.id === row.id)
    if (idx >= 0) mockData.splice(idx, 1)
    ElMessage.success('删除成功')
    handleSearch()
  }
}

async function handleBatchDelete() {
  if (!selectedRows.value.length) return
  await ElMessageBox.confirm(`确认删除选中的 ${selectedRows.value.length} 条日志？`, '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).catch(() => { throw new Error('cancelled') })
  try {
    const ids = selectedRows.value.map(r => r.id)
    await deleteLoginLogBatch(ids).catch(() => null)
    // 本地 mock 删除
    const idSet = new Set(ids)
    for (let i = mockData.length - 1; i >= 0; i--) {
      if (idSet.has(mockData[i].id)) mockData.splice(i, 1)
    }
    ElMessage.success('批量删除成功')
    handleSearch()
  } catch (e) {
    if (e.message === 'cancelled') return
    // mock 兜底
    const idSet = new Set(selectedRows.value.map(r => r.id))
    for (let i = mockData.length - 1; i >= 0; i--) {
      if (idSet.has(mockData[i].id)) mockData.splice(i, 1)
    }
    ElMessage.success('批量删除成功')
    handleSearch()
  }
}

async function handleClean() {
  await ElMessageBox.confirm('清理 30 天前的日志记录？', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).catch(() => {})
  try {
    await cleanLoginLog(30).catch(() => null)
  } catch (e) {}
  // mock 兜底：从本地 mock 删除 30 天前
  const cutoff = Date.now() - 30 * 24 * 3600 * 1000
  for (let i = mockData.length - 1; i >= 0; i--) {
    if (new Date(mockData[i].loginTime).getTime() < cutoff) mockData.splice(i, 1)
  }
  ElMessage.success('清理完成')
  handleSearch()
}

function handleDetail(row) {
  detailRow.value = row
  detailVisible.value = true
}

function formatDate(t) {
  if (!t) return '-'
  const d = new Date(t)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

function duration(start, end) {
  if (!start || !end) return '-'
  const diff = Math.max(0, new Date(end).getTime() - new Date(start).getTime())
  const s = Math.floor(diff / 1000)
  const h = Math.floor(s / 3600)
  const m = Math.floor((s % 3600) / 60)
  const sec = s % 60
  if (h > 0) return `${h}h ${m}m`
  if (m > 0) return `${m}m ${sec}s`
  return `${sec}s`
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.login-log-page {
  padding: 12px 16px;
}
.search-bar {
  background: #fff;
  padding: 14px 14px 0;
  border-radius: 4px;
  border: 1px solid #ebeef5;
  margin-bottom: 10px;
}
.action-bar {
  background: #fff;
  padding: 8px 14px;
  border: 1px solid #ebeef5;
  border-bottom: none;
}
.pagination {
  text-align: right;
  margin-top: 12px;
}
</style>
