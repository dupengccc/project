<template>
  <div class="job-page">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- ============ 任务管理 ============ -->
      <el-tab-pane label="任务管理" name="job">
        <div class="search-bar">
          <el-form :inline="true" :model="jobQuery" @submit.prevent>
            <el-form-item label="任务名称">
              <el-input v-model="jobQuery.jobName" placeholder="模糊搜索" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item label="任务组">
              <el-input v-model="jobQuery.jobGroup" placeholder="任务组" clearable style="width: 140px" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="jobQuery.status" placeholder="全部" clearable style="width: 110px">
                <el-option label="正常" :value="0" />
                <el-option label="暂停" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleJobSearch">查询</el-button>
              <el-button @click="resetJobQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <div>
            <el-button type="success" @click="handleAdd">
              <el-icon><Plus /></el-icon>新增任务
            </el-button>
          </div>
        </div>

        <el-table :data="jobList" border stripe v-loading="jobLoading" height="calc(100vh - 320px)">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="jobName" label="任务名称" width="140" show-overflow-tooltip />
          <el-table-column prop="jobGroup" label="任务组" width="110" align="center">
            <template #default="{ row }">
              <el-tag size="small" type="info">{{ row.jobGroup || 'DEFAULT' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="invokeTarget" label="调用目标" min-width="220" show-overflow-tooltip />
          <el-table-column prop="cronExpression" label="cron表达式" width="170" />
          <el-table-column prop="status" label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-switch :model-value="row.status === 0" @change="handleChangeStatus(row)"
                         active-text="正常" inactive-text="暂停" inline-prompt />
            </template>
          </el-table-column>
          <el-table-column prop="prevTime" label="上次执行" width="160" align="center">
            <template #default="{ row }">{{ formatTime(row.prevTime) }}</template>
          </el-table-column>
          <el-table-column prop="nextTime" label="下次执行" width="160" align="center">
            <template #default="{ row }">{{ formatTime(row.nextTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="230" align="center" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button link type="success" size="small" @click="handleRun(row)">执行一次</el-button>
              <el-popconfirm title="确认删除该任务？" @confirm="handleDelete(row)">
                <template #reference>
                  <el-button link type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="jobQuery.page"
            v-model:page-size="jobQuery.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="jobTotal"
            layout="total, sizes, prev, pager, next, jumper"
            background
            @current-change="loadJobs"
            @size-change="loadJobs"
          />
        </div>
      </el-tab-pane>

      <!-- ============ 执行日志 ============ -->
      <el-tab-pane label="执行日志" name="log">
        <div class="search-bar">
          <el-form :inline="true" :model="logQuery" @submit.prevent>
            <el-form-item label="任务名称">
              <el-input v-model="logQuery.jobName" placeholder="模糊搜索" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="logQuery.status" placeholder="全部" clearable style="width: 110px">
                <el-option label="成功" :value="0" />
                <el-option label="失败" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleLogSearch">查询</el-button>
              <el-button @click="resetLogQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <div>
            <el-button type="danger" plain @click="handleCleanLog">
              <el-icon><Delete /></el-icon>清空日志
            </el-button>
          </div>
        </div>

        <el-table :data="logList" border stripe v-loading="logLoading" height="calc(100vh - 320px)">
          <el-table-column label="序号" width="60" align="center">
            <template #default="{ $index }">{{ (logQuery.page - 1) * logQuery.pageSize + $index + 1 }}</template>
          </el-table-column>
          <el-table-column prop="jobName" label="任务名称" width="140" show-overflow-tooltip />
          <el-table-column prop="invokeTarget" label="调用目标" min-width="200" show-overflow-tooltip />
          <el-table-column prop="jobMessage" label="日志信息" min-width="200" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
                {{ row.status === 0 ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="costTime" label="耗时(ms)" width="100" align="center" />
          <el-table-column prop="startTime" label="开始时间" width="160" align="center">
            <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
          </el-table-column>
          <el-table-column prop="exceptionInfo" label="异常信息" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <span v-if="row.exceptionInfo" class="error-text">{{ row.exceptionInfo }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90" align="center" fixed="right">
            <template #default="{ row }">
              <el-popconfirm title="确认删除该日志？" @confirm="handleDeleteLog(row)">
                <template #reference>
                  <el-button link type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="logQuery.page"
            v-model:page-size="logQuery.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="logTotal"
            layout="total, sizes, prev, pager, next, jumper"
            background
            @current-change="loadLogs"
            @size-change="loadLogs"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- ============ 新增/编辑弹窗 ============ -->
    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '新增定时任务' : '编辑定时任务'"
               width="680px" @closed="resetForm">
      <el-form ref="formRef" :model="jobForm" :rules="jobRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="任务名称" prop="jobName">
              <el-input v-model="jobForm.jobName" maxlength="64" placeholder="请输入任务名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务组" prop="jobGroup">
              <el-select v-model="jobForm.jobGroup" placeholder="请选择" style="width: 100%" allow-create filterable>
                <el-option label="默认" value="DEFAULT" />
                <el-option label="系统" value="SYSTEM" />
                <el-option label="业务" value="BIZ" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="调用目标" prop="invokeTarget">
          <el-input v-model="jobForm.invokeTarget" maxlength="500" placeholder="格式：beanName.method(参数) 如 sampleTask.withParams('测试', 100)">
            <template #append>
              <el-tooltip content="点击查看示例" placement="top">
                <el-button @click="showExample = !showExample">
                  <el-icon><QuestionFilled /></el-icon>
                </el-button>
              </el-tooltip>
            </template>
          </el-input>
        </el-form-item>
        <div v-if="showExample" class="example-tip">
          示例：<br />
          1. 无参：sampleTask.noParams<br />
          2. 带参：sampleTask.withParams('内容', 100)<br />
          3. 布尔：sampleTask.singleParam('消息')
        </div>
        <el-form-item label="cron表达式" prop="cronExpression">
          <el-input v-model="jobForm.cronExpression" maxlength="255" placeholder="如 0/10 * * * * ?">
            <template #append>
              <el-dropdown @command="handleCronPreset" trigger="click">
                <el-button>常用<el-icon><ArrowDown /></el-icon></el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="0/10 * * * * ?">每10秒</el-dropdown-item>
                    <el-dropdown-item command="0/30 * * * * ?">每30秒</el-dropdown-item>
                    <el-dropdown-item command="0 * * * * ?">每分钟</el-dropdown-item>
                    <el-dropdown-item command="0 0/5 * * * ?">每5分钟</el-dropdown-item>
                    <el-dropdown-item command="0 0/30 * * * ?">每30分钟</el-dropdown-item>
                    <el-dropdown-item command="0 0 * * * ?">每小时</el-dropdown-item>
                    <el-dropdown-item command="0 0 0 * * ?">每天0点</el-dropdown-item>
                    <el-dropdown-item command="0 0 8 * * ?">每天8点</el-dropdown-item>
                    <el-dropdown-item command="0 0 0 1 * ?">每月1号0点</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="错误策略" prop="misfirePolicy">
              <el-select v-model="jobForm.misfirePolicy" style="width: 100%">
                <el-option label="立即执行" :value="1" />
                <el-option label="执行一次" :value="2" />
                <el-option label="放弃执行" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否并发" prop="concurrent">
              <el-radio-group v-model="jobForm.concurrent">
                <el-radio :label="0">允许</el-radio>
                <el-radio :label="1">禁止</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="jobForm.status">
                <el-radio :label="0">正常</el-radio>
                <el-radio :label="1">暂停</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="jobForm.remark" type="textarea" :rows="2" maxlength="500" placeholder="任务说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saveLoading" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listJobs, addJob, updateJob, deleteJob, changeJobStatus, runJob,
  listJobLogs, deleteJobLog, cleanJobLogs
} from '@/api/job'

// ==================== Tab ====================
const activeTab = ref('job')

// ==================== 任务管理 ====================
const jobLoading = ref(false)
const jobList = ref([])
const jobTotal = ref(0)
const jobQuery = reactive({ jobName: '', jobGroup: '', status: null, page: 1, pageSize: 10 })

function handleTabChange(tab) {
  if (tab === 'log') {
    loadLogs()
  }
}

async function loadJobs() {
  jobLoading.value = true
  try {
    const res = await listJobs(jobQuery)
    if (res && res.code === 0 && res.data) {
      jobList.value = res.data.content || []
      jobTotal.value = res.data.total || 0
      jobLoading.value = false
      return
    }
  } catch (e) {}
  // mock 兜底
  jobList.value = mockJobs()
  jobTotal.value = jobList.value.length
  jobLoading.value = false
}

function handleJobSearch() {
  jobQuery.page = 1
  loadJobs()
}

function resetJobQuery() {
  jobQuery.jobName = ''
  jobQuery.jobGroup = ''
  jobQuery.status = null
  jobQuery.page = 1
  loadJobs()
}

async function handleChangeStatus(row) {
  const newStatus = row.status === 0 ? 1 : 0
  try {
    await changeJobStatus(row.id, newStatus)
    ElMessage.success(newStatus === 0 ? '已启用' : '已暂停')
    loadJobs()
    return
  } catch (e) {}
  // mock 兜底
  row.status = newStatus
  ElMessage.success(newStatus === 0 ? '已启用' : '已暂停')
}

async function handleRun(row) {
  try {
    await runJob(row.id)
    ElMessage.success('任务已触发执行')
    loadJobs()
    return
  } catch (e) {}
  ElMessage.success('任务已触发执行')
}

async function handleDelete(row) {
  try {
    await deleteJob(row.id)
    ElMessage.success('删除成功')
    loadJobs()
    return
  } catch (e) {}
  jobList.value = jobList.value.filter(j => j.id !== row.id)
  jobTotal.value = jobList.value.length
  ElMessage.success('删除成功')
}

// ==================== 弹窗表单 ====================
const dialogVisible = ref(false)
const dialogMode = ref('add')
const formRef = ref()
const saveLoading = ref(false)
const showExample = ref(false)
const jobForm = reactive({
  id: null, jobName: '', jobGroup: 'DEFAULT', invokeTarget: '',
  cronExpression: '', misfirePolicy: 3, concurrent: 1, status: 0, remark: ''
})
const jobRules = {
  jobName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  invokeTarget: [{ required: true, message: '请输入调用目标', trigger: 'blur' }],
  cronExpression: [{ required: true, message: '请输入cron表达式', trigger: 'blur' }]
}

function handleAdd() {
  dialogMode.value = 'add'
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogMode.value = 'edit'
  Object.assign(jobForm, {
    id: row.id, jobName: row.jobName, jobGroup: row.jobGroup || 'DEFAULT',
    invokeTarget: row.invokeTarget, cronExpression: row.cronExpression,
    misfirePolicy: row.misfirePolicy, concurrent: row.concurrent,
    status: row.status, remark: row.remark || ''
  })
  dialogVisible.value = true
}

function handleCronPreset(cmd) {
  jobForm.cronExpression = cmd
}

async function handleSave() {
  await formRef.value.validate(async valid => {
    if (!valid) return
    saveLoading.value = true
    try {
      const fn = dialogMode.value === 'add' ? addJob : updateJob
      const res = await fn(jobForm)
      if (res && res.code === 0) {
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadJobs()
        saveLoading.value = false
        return
      }
    } catch (e) {}
    // mock 兜底
    if (dialogMode.value === 'add') {
      jobList.value.unshift({ ...jobForm, id: Date.now() })
      jobTotal.value = jobList.value.length
    } else {
      const idx = jobList.value.findIndex(j => j.id === jobForm.id)
      if (idx > -1) Object.assign(jobList.value[idx], { ...jobForm })
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    saveLoading.value = false
  })
}

function resetForm() {
  Object.assign(jobForm, {
    id: null, jobName: '', jobGroup: 'DEFAULT', invokeTarget: '',
    cronExpression: '', misfirePolicy: 3, concurrent: 1, status: 0, remark: ''
  })
  showExample.value = false
  formRef.value && formRef.value.clearValidate()
}

// ==================== 执行日志 ====================
const logLoading = ref(false)
const logList = ref([])
const logTotal = ref(0)
const logQuery = reactive({ jobName: '', status: null, page: 1, pageSize: 10 })

async function loadLogs() {
  logLoading.value = true
  try {
    const res = await listJobLogs(logQuery)
    if (res && res.code === 0 && res.data) {
      logList.value = res.data.content || []
      logTotal.value = res.data.total || 0
      logLoading.value = false
      return
    }
  } catch (e) {}
  logList.value = mockLogs()
  logTotal.value = logList.value.length
  logLoading.value = false
}

function handleLogSearch() {
  logQuery.page = 1
  loadLogs()
}

function resetLogQuery() {
  logQuery.jobName = ''
  logQuery.status = null
  logQuery.page = 1
  loadLogs()
}

async function handleDeleteLog(row) {
  try {
    await deleteJobLog(row.id)
    ElMessage.success('删除成功')
    loadLogs()
    return
  } catch (e) {}
  logList.value = logList.value.filter(l => l.id !== row.id)
  logTotal.value = logList.value.length
  ElMessage.success('删除成功')
}

async function handleCleanLog() {
  try {
    await ElMessageBox.confirm('确认清空所有任务日志？此操作不可恢复', '警告', { type: 'warning' })
  } catch (e) {
    return
  }
  try {
    await cleanJobLogs()
    ElMessage.success('清空成功')
    loadLogs()
    return
  } catch (e) {}
  logList.value = []
  logTotal.value = 0
  ElMessage.success('清空成功')
}

// ==================== 工具 ====================
function formatTime(t) {
  if (!t) return '-'
  const d = new Date(t)
  if (isNaN(d.getTime())) return '-'
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

// ==================== Mock ====================
function mockJobs() {
  return [
    { id: 1001, jobName: '系统通知', jobGroup: 'SYSTEM', invokeTarget: 'sampleTask.noParams',
      cronExpression: '0/10 * * * * ?', misfirePolicy: 3, concurrent: 1, status: 0,
      prevTime: new Date(Date.now() - 10000), nextTime: new Date(Date.now() + 10000), remark: '系统巡检' },
    { id: 1002, jobName: '数据同步', jobGroup: 'BIZ', invokeTarget: "sampleTask.withParams('同步', 100)",
      cronExpression: '0 0/5 * * * ?', misfirePolicy: 3, concurrent: 1, status: 1,
      prevTime: new Date(Date.now() - 300000), nextTime: new Date(Date.now() + 120000), remark: '业务数据同步' }
  ]
}

function mockLogs() {
  return [
    { id: 2001, jobName: '系统通知', invokeTarget: 'sampleTask.noParams',
      jobMessage: '任务执行成功', status: 0, costTime: 52,
      startTime: new Date(Date.now() - 60000), endTime: new Date(Date.now() - 59948), exceptionInfo: '' },
    { id: 2002, jobName: '数据同步', invokeTarget: "sampleTask.withParams('同步', 100)",
      jobMessage: '任务执行失败：连接超时', status: 1, costTime: 30000,
      startTime: new Date(Date.now() - 300000), endTime: new Date(Date.now() - 270000),
      exceptionInfo: 'java.net.SocketTimeoutException: connect timed out' }
  ]
}

onMounted(() => { loadJobs() })
</script>

<style scoped>
.job-page { padding: 12px 16px; }
.search-bar { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; flex-wrap: wrap; gap: 8px; }
.pagination { display: flex; justify-content: flex-end; margin-top: 12px; }
.error-text { color: #f56c6c; }
.example-tip {
  background: #f5f7fa;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 8px 12px;
  margin-bottom: 12px;
  font-size: 12px;
  color: #606266;
  line-height: 1.8;
}
</style>
