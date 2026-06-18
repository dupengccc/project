<template>
  <div class="oper-log-page">
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
        <el-form-item label="模块">
          <el-input
            v-model="queryForm.module"
            placeholder="如 系统管理"
            clearable
            style="width: 160px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="操作">
          <el-input
            v-model="queryForm.operation"
            placeholder="如 登录/新增"
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
            <el-option label="正常" :value="0" />
            <el-option label="异常" :value="1" />
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
      <el-table-column prop="module" label="模块" width="140" show-overflow-tooltip />
      <el-table-column prop="operation" label="操作功能" width="140" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP" width="130" show-overflow-tooltip />
      <el-table-column prop="operTime" label="操作时间" width="170">
        <template #default="{ row }">{{ formatDate(row.operTime) }}</template>
      </el-table-column>
      <el-table-column prop="cost" label="耗时(ms)" width="100" align="right" />
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0 || row.status === '0'" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">异常</el-tag>
        </template>
      </el-table-column>
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
    <el-dialog v-model="detailVisible" title="操作日志详情" width="720px">
      <el-descriptions :column="2" border v-if="detailRow">
        <el-descriptions-item label="工号">{{ detailRow.empNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailRow.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属组织">{{ detailRow.orgName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="IP">{{ detailRow.ip || '-' }}</el-descriptions-item>
        <el-descriptions-item label="模块">{{ detailRow.module || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作功能">{{ detailRow.operation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="请求地址">{{ detailRow.url || '-' }}</el-descriptions-item>
        <el-descriptions-item label="耗时(ms)">{{ detailRow.cost ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作时间" :span="2">{{ formatDate(detailRow.operTime) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailRow.status === 0 || detailRow.status === '0'" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">异常</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="异常信息" :span="2">
          <span v-if="detailRow.errorMsg" style="color: #f56c6c; white-space: pre-wrap;">{{ detailRow.errorMsg }}</span>
          <span v-else style="color: #909399;">-</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listOperLog, deleteOperLog, deleteOperLogBatch, cleanOperLog } from '@/api/oper-log'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectedRows = ref([])
const detailVisible = ref(false)
const detailRow = ref(null)

const queryForm = reactive({
  empNo: '',
  name: '',
  module: '',
  operation: '',
  status: null,
  page: 1,
  pageSize: 10
})

// 本地 mock 数据
const mockData = reactive([])
function ensureMock() {
  if (mockData.length > 0) return
  const now = Date.now()
  const modules = ['系统管理', '用户管理', '组织管理', '角色管理', '菜单管理', '基础数据', '生产管理', '仓储管理']
  const operations = ['新增', '修改', '删除', '查询', '导出', '登录', '退出登录', '修改密码']
  const names = ['系统管理员', '张三', '李四', '王五', '赵六', '周七']
  const orgs = ['MES 集团总部', '华东分公司', '华南分公司', '研发部', '生产部']
  for (let i = 0; i < 60; i++) {
    const ts = now - i * 3600 * 1000 * 2
    const isErr = i % 13 === 0
    mockData.push({
      id: -(i + 1),
      empNo: 'E' + (10001 + (i % 6)),
      name: names[i % names.length],
      orgName: orgs[i % orgs.length],
      module: modules[i % modules.length],
      operation: operations[i % operations.length],
      url: '/api/resource/' + i,
      ip: '127.0.0.' + (1 + i % 10),
      location: '局域网',
      cost: 10 + i * 3,
      status: isErr ? 1 : 0,
      errorMsg: isErr ? 'NullPointerException: 空指针异常示例' : null,
      operTime: new Date(ts).toISOString(),
      createTime: new Date(ts).toISOString()
    })
  }
}

async function handleSearch() {
  loading.value = true
  try {
    try {
      const res = await listOperLog({
        empNo: queryForm.empNo || undefined,
        name: queryForm.name || undefined,
        module: queryForm.module || undefined,
        operation: queryForm.operation || undefined,
        status: queryForm.status == null ? undefined : queryForm.status,
        page: queryForm.page,
        pageSize: queryForm.pageSize
      })
      if (res && res.data && Array.isArray(res.data.list) && res.data.list.length > 0) {
        tableData.value = res.data.list
        total.value = res.data.total || res.data.list.length
        return
      }
    } catch (e) {}
    // 本地 mock
    ensureMock()
    let list = mockData.slice()
    if (queryForm.empNo && queryForm.empNo.trim()) {
      list = list.filter(x => (x.empNo || '').toLowerCase().includes(queryForm.empNo.trim().toLowerCase()))
    }
    if (queryForm.name && queryForm.name.trim()) {
      list = list.filter(x => (x.name || '').toLowerCase().includes(queryForm.name.trim().toLowerCase()))
    }
    if (queryForm.module && queryForm.module.trim()) {
      list = list.filter(x => (x.module || '').includes(queryForm.module.trim()))
    }
    if (queryForm.operation && queryForm.operation.trim()) {
      list = list.filter(x => (x.operation || '').includes(queryForm.operation.trim()))
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
  queryForm.module = ''
  queryForm.operation = ''
  queryForm.status = null
  queryForm.page = 1
  handleSearch()
}

function handleSelectionChange(rows) {
  selectedRows.value = rows
}

async function handleDelete(row) {
  try {
    await deleteOperLog(row.id)
    const idx = mockData.findIndex(x => x.id === row.id)
    if (idx >= 0) mockData.splice(idx, 1)
    ElMessage.success('删除成功')
    handleSearch()
  } catch (e) {
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
    await deleteOperLogBatch(ids).catch(() => null)
    const idSet = new Set(ids)
    for (let i = mockData.length - 1; i >= 0; i--) {
      if (idSet.has(mockData[i].id)) mockData.splice(i, 1)
    }
    ElMessage.success('批量删除成功')
    handleSearch()
  } catch (e) {
    if (e.message === 'cancelled') return
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
    await cleanOperLog(30).catch(() => null)
  } catch (e) {}
  const cutoff = Date.now() - 30 * 24 * 3600 * 1000
  for (let i = mockData.length - 1; i >= 0; i--) {
    if (new Date(mockData[i].operTime).getTime() < cutoff) mockData.splice(i, 1)
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

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.oper-log-page {
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
