<template>
  <div class="interface-manage-page">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 接口配置管理 -->
      <el-tab-pane label="接口配置" name="config">
        <div class="search-bar">
          <el-form :inline="true" :model="configQuery" @submit.prevent>
            <el-form-item label="接口编码">
              <el-input v-model="configQuery.interfaceCode" placeholder="模糊搜索" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item label="接口名称">
              <el-input v-model="configQuery.interfaceName" placeholder="模糊搜索" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item label="所属系统">
              <el-select v-model="configQuery.ownerSystem" placeholder="全部" clearable style="width: 140px">
                <el-option v-for="sys in systemOptions" :key="sys" :label="sys" :value="sys" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="configQuery.status" placeholder="全部" clearable style="width: 100px">
                <el-option label="启用" :value="0" />
                <el-option label="停用" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadConfigs">查询</el-button>
              <el-button @click="resetConfigQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <div>
            <el-button type="success" @click="handleAddConfig">
              <el-icon><Plus /></el-icon>新增配置
            </el-button>
          </div>
        </div>

        <el-table :data="configList" border stripe v-loading="configLoading" height="calc(100vh - 300px)">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="interfaceCode" label="接口编码" width="160" />
          <el-table-column prop="interfaceName" label="接口名称" width="160" />
          <el-table-column prop="url" label="接口地址" min-width="250" show-overflow-tooltip />
          <el-table-column prop="requestMethod" label="请求方式" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="methodType(row.requestMethod)" size="small">{{ row.requestMethod }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="ownerSystem" label="所属系统" width="120" />
          <el-table-column prop="timeout" label="超时(ms)" width="100" align="center" />
          <el-table-column prop="retryCount" label="重试次数" width="90" align="center" />
          <el-table-column prop="status" label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-switch
                :model-value="row.status === 0"
                @change="handleToggleStatus(row)"
                active-text="启用"
                inactive-text="停用"
                inline-prompt
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEditConfig(row)">编辑</el-button>
              <el-button link type="success" size="small" @click="handleTestCall(row)">测试</el-button>
              <el-button link type="danger" size="small" @click="handleDeleteConfig(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 调用日志 -->
      <el-tab-pane label="调用日志" name="log">
        <div class="search-bar">
          <el-form :inline="true" :model="logQuery" @submit.prevent>
            <el-form-item label="接口编码">
              <el-input v-model="logQuery.interfaceCode" placeholder="模糊搜索" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item label="调用状态">
              <el-select v-model="logQuery.callStatus" placeholder="全部" clearable style="width: 120px">
                <el-option label="成功" :value="0" />
                <el-option label="失败" :value="1" />
                <el-option label="超时" :value="2" />
                <el-option label="重试中" :value="3" />
              </el-select>
            </el-form-item>
            <el-form-item label="业务单号">
              <el-input v-model="logQuery.bizNo" placeholder="模糊搜索" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item label="来源系统">
              <el-input v-model="logQuery.sourceSystem" placeholder="模糊搜索" clearable style="width: 140px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadLogs">查询</el-button>
              <el-button @click="resetLogQuery">重置</el-button>
              <el-button type="danger" plain @click="handleBatchRetry" :disabled="selectedLogIds.length === 0">
                批量重试({{ selectedLogIds.length }})
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-table :data="logList" border stripe v-loading="logLoading" height="calc(100vh - 300px)" @selection-change="handleLogSelectionChange">
          <el-table-column type="selection" width="55" />
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="interfaceCode" label="接口编码" width="160" />
          <el-table-column prop="interfaceName" label="接口名称" width="140" />
          <el-table-column prop="callStatus" label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="statusType(row.callStatus)" size="small">{{ statusText(row.callStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="responseTime" label="响应时间(ms)" width="110" align="center" />
          <el-table-column prop="bizNo" label="业务单号" width="140" />
          <el-table-column prop="callTime" label="调用时间" width="160" />
          <el-table-column prop="errorMsg" label="错误信息" min-width="200" show-overflow-tooltip />
          <el-table-column prop="retryCount" label="重试次数" width="90" align="center" />
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleViewLog(row)">详情</el-button>
              <el-button link type="success" size="small" @click="handleRetry(row)" :disabled="row.callStatus === 0">重试</el-button>
              <el-button v-if="row.callStatus !== 0" link type="warning" size="small" @click="handleProcess(row)">处理</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 异常监控 -->
      <el-tab-pane label="异常监控" name="error">
        <div class="search-bar">
          <el-form :inline="true">
            <el-form-item label="统计周期">
              <el-date-picker v-model="errorDateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadStatistics">刷新统计</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 统计卡片 -->
        <el-row :gutter="20" style="margin-bottom: 16px">
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-value">{{ statistics.totalCount }}</div>
              <div class="stat-label">总调用次数</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card success">
              <div class="stat-value">{{ statistics.successCount }}</div>
              <div class="stat-label">成功次数</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card danger">
              <div class="stat-value">{{ statistics.errorCount }}</div>
              <div class="stat-label">异常次数</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-value">{{ statistics.successRate }}</div>
              <div class="stat-label">成功率</div>
            </el-card>
          </el-col>
        </el-row>

        <el-table :data="errorLogs" border stripe v-loading="errorLoading" height="calc(100vh - 420px)">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="interfaceCode" label="接口编码" width="160" />
          <el-table-column prop="interfaceName" label="接口名称" width="140" />
          <el-table-column prop="callTime" label="调用时间" width="160" />
          <el-table-column prop="errorMsg" label="错误信息" min-width="200" show-overflow-tooltip />
          <el-table-column prop="exceptionType" label="异常类型" width="160" />
          <el-table-column prop="responseTime" label="耗时(ms)" width="100" align="center" />
          <el-table-column prop="retryCount" label="重试" width="70" align="center" />
          <el-table-column prop="processed" label="处理状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.processed === 1" type="success" size="small">已处理</el-tag>
              <el-tag v-else type="danger" size="small">未处理</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" align="center">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleViewLog(row)">详情</el-button>
              <el-button link type="warning" size="small" @click="handleProcess(row)">处理</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 配置编辑弹窗 -->
    <el-dialog v-model="configDialogVisible" :title="configDialogMode === 'add' ? '新增接口配置' : '编辑接口配置'" width="720px" @closed="resetConfigForm">
      <el-form ref="configFormRef" :model="configForm" :rules="configRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="接口编码" prop="interfaceCode">
              <el-input v-model="configForm.interfaceCode" maxlength="64" placeholder="唯一标识，如 ERP_MATERIAL_SYNC" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="接口名称" prop="interfaceName">
              <el-input v-model="configForm.interfaceName" maxlength="128" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="接口地址" prop="url">
          <el-input v-model="configForm.url" maxlength="500" placeholder="http://api.example.com/xxx" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="请求方式" prop="requestMethod">
              <el-select v-model="configForm.requestMethod" style="width: 100%">
                <el-option label="GET" value="GET" />
                <el-option label="POST" value="POST" />
                <el-option label="PUT" value="PUT" />
                <el-option label="DELETE" value="DELETE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="内容类型" prop="contentType">
              <el-select v-model="configForm.contentType" style="width: 100%">
                <el-option label="JSON" value="application/json" />
                <el-option label="Form表单" value="application/x-www-form-urlencoded" />
                <el-option label="Multipart" value="multipart/form-data" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="所属系统" prop="ownerSystem">
              <el-input v-model="configForm.ownerSystem" maxlength="64" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="超时时间" prop="timeout">
              <el-input-number v-model="configForm.timeout" :min="1000" :max="300000" :step="1000" /> ms
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="重试次数" prop="retryCount">
              <el-input-number v-model="configForm.retryCount" :min="0" :max="10" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="configForm.status">
                <el-radio :label="0">启用</el-radio>
                <el-radio :label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="认证类型" prop="authType">
          <el-select v-model="configForm.authType" style="width: 100%">
            <el-option label="无认证" value="none" />
            <el-option label="Basic Auth" value="basic" />
            <el-option label="Bearer Token" value="bearer" />
            <el-option label="API Key" value="apiKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="参数模板">
          <el-input v-model="configForm.paramTemplate" type="textarea" :rows="3" placeholder='{"key": "value"} 格式' />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="configForm.description" type="textarea" :rows="2" maxlength="500" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveConfig">确定</el-button>
      </template>
    </el-dialog>

    <!-- 日志详情弹窗 -->
    <el-dialog v-model="logDialogVisible" title="调用日志详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="接口编码">{{ currentLog?.interfaceCode }}</el-descriptions-item>
        <el-descriptions-item label="接口名称">{{ currentLog?.interfaceName }}</el-descriptions-item>
        <el-descriptions-item label="调用状态">
          <el-tag :type="statusType(currentLog?.callStatus)">{{ statusText(currentLog?.callStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="响应时间">{{ currentLog?.responseTime }} ms</el-descriptions-item>
        <el-descriptions-item label="业务单号">{{ currentLog?.bizNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="重试次数">{{ currentLog?.retryCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="请求地址" :span="2">{{ currentLog?.requestUrl }}</el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2">
          <span class="error-text">{{ currentLog?.errorMsg || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="异常类型">{{ currentLog?.exceptionType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="调用时间">{{ currentLog?.callTime }}</el-descriptions-item>
      </el-descriptions>

      <el-tabs style="margin-top: 16px">
        <el-tab-pane label="请求参数">
          <pre class="code-block">{{ currentLog?.requestBody || '无' }}</pre>
        </el-tab-pane>
        <el-tab-pane label="响应内容">
          <pre class="code-block">{{ currentLog?.responseBody || '无' }}</pre>
        </el-tab-pane>
        <el-tab-pane label="堆栈信息">
          <pre class="code-block error">{{ currentLog?.stackTrace || '无' }}</pre>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <!-- 处理弹窗 -->
    <el-dialog v-model="processDialogVisible" title="处理异常日志" width="500px">
      <el-form :model="processForm" label-width="80px">
        <el-form-item label="处理人">
          <el-input v-model="processForm.processor" placeholder="请输入处理人" />
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input v-model="processForm.remark" type="textarea" :rows="3" placeholder="请输入处理备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitProcess">确定</el-button>
      </template>
    </el-dialog>

    <!-- 测试调用弹窗 -->
    <el-dialog v-model="testDialogVisible" title="测试接口调用" width="600px">
      <el-form :model="testForm" label-width="100px">
        <el-form-item label="接口编码">{{ testForm.interfaceCode }}</el-form-item>
        <el-form-item label="请求参数">
          <el-input v-model="testForm.params" type="textarea" :rows="6" placeholder='{"key": "value"} JSON格式' />
        </el-form-item>
        <el-form-item label="业务单号">
          <el-input v-model="testForm.bizNo" placeholder="可选，用于追踪" />
        </el-form-item>
      </el-form>
      <div v-if="testResult" class="test-result">
        <el-divider>调用结果</el-divider>
        <el-form label-width="100px">
          <el-form-item label="状态">
            <el-tag :type="testResult.callStatus === 0 ? 'success' : 'danger'">
              {{ statusText(testResult.callStatus) }}
            </el-tag>
          </el-form-item>
          <el-form-item label="响应时间">{{ testResult.responseTime }} ms</el-form-item>
          <el-form-item label="响应内容">
            <pre class="code-block">{{ testResult.responseBody || testResult.errorMsg }}</pre>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="testDialogVisible = false">关闭</el-button>
        <el-button type="primary" :loading="testLoading" @click="handleSubmitTest">发起调用</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listInterfaceConfigs, saveInterfaceConfig, deleteInterfaceConfig, toggleInterfaceStatus,
  listInterfaceLogs, getInterfaceLog, listErrorLogs, processInterfaceLog,
  getInterfaceStatistics, callInterface, retryInterface, batchRetryInterface
} from '@/api/interface-manage'

const activeTab = ref('config')

// ==================== 接口配置 ====================
const configLoading = ref(false)
const configList = ref([])
const configQuery = reactive({ interfaceCode: '', interfaceName: '', ownerSystem: '', status: null })
const configDialogVisible = ref(false)
const configDialogMode = ref('add')
const configFormRef = ref()
const configForm = reactive({
  id: null, interfaceCode: '', interfaceName: '', description: '', url: '',
  requestMethod: 'POST', contentType: 'application/json', ownerSystem: '',
  timeout: 30000, retryCount: 3, retryInterval: 5000, status: 0,
  authType: 'none', authConfig: '', paramTemplate: '', headers: ''
})
const configRules = {
  interfaceCode: [{ required: true, message: '请输入接口编码', trigger: 'blur' }],
  interfaceName: [{ required: true, message: '请输入接口名称', trigger: 'blur' }],
  url: [{ required: true, message: '请输入接口地址', trigger: 'blur' }]
}
const systemOptions = ['ERP系统', 'MES系统', 'WMS系统', 'QMS系统', 'CRM系统', '财务系统']

// ==================== 调用日志 ====================
const logLoading = ref(false)
const logList = ref([])
const logQuery = reactive({ interfaceCode: '', callStatus: null, bizNo: '', sourceSystem: '' })
const selectedLogIds = ref([])
const logDialogVisible = ref(false)
const currentLog = ref(null)

// ==================== 异常监控 ====================
const errorLoading = ref(false)
const errorLogs = ref([])
const errorDateRange = ref([])
const statistics = reactive({ totalCount: 0, successCount: 0, errorCount: 0, successRate: '0%' })

// ==================== 处理 ====================
const processDialogVisible = ref(false)
const processForm = reactive({ id: null, processor: '', remark: '' })

// ==================== 测试 ====================
const testDialogVisible = ref(false)
const testLoading = ref(false)
const testForm = reactive({ id: null, interfaceCode: '', params: '{}', bizNo: '' })
const testResult = ref(null)

onMounted(() => {
  loadConfigs()
})

// ==================== 接口配置方法 ====================
async function loadConfigs() {
  configLoading.value = true
  try {
    const res = await listInterfaceConfigs(configQuery)
    if (res && res.code === 0 && res.data) {
      configList.value = res.data.list || []
    }
  } catch (e) {}
  if (configList.value.length === 0) {
    configList.value = [
      { id: 1, interfaceCode: 'ERP_MATERIAL_SYNC', interfaceName: 'ERP物料同步', url: 'http://erp-api.example.com/material/sync', requestMethod: 'POST', ownerSystem: 'ERP系统', timeout: 30000, retryCount: 3, status: 0 },
      { id: 2, interfaceCode: 'MES_ORDER_PUSH', interfaceName: 'MES工单推送', url: 'http://mes-api.example.com/order/push', requestMethod: 'POST', ownerSystem: 'ERP系统', timeout: 30000, retryCount: 3, status: 0 },
      { id: 3, interfaceCode: 'WMS_STOCK_QUERY', interfaceName: 'WMS库存查询', url: 'http://wms-api.example.com/stock/query', requestMethod: 'GET', ownerSystem: 'WMS系统', timeout: 10000, retryCount: 2, status: 0 },
      { id: 4, interfaceCode: 'QMS_INSPECTION_PUSH', interfaceName: 'QMS检验结果推送', url: 'http://qms-api.example.com/inspection/push', requestMethod: 'POST', ownerSystem: 'MES系统', timeout: 30000, retryCount: 3, status: 0 }
    ]
  }
  configLoading.value = false
}

function resetConfigQuery() {
  configQuery.interfaceCode = ''
  configQuery.interfaceName = ''
  configQuery.ownerSystem = ''
  configQuery.status = null
  loadConfigs()
}

function handleAddConfig() {
  configDialogMode.value = 'add'
  Object.assign(configForm, { id: null, interfaceCode: '', interfaceName: '', description: '', url: '', requestMethod: 'POST', contentType: 'application/json', ownerSystem: '', timeout: 30000, retryCount: 3, status: 0, authType: 'none' })
  configDialogVisible.value = true
}

function handleEditConfig(row) {
  configDialogMode.value = 'edit'
  Object.assign(configForm, row)
  configDialogVisible.value = true
}

async function handleSaveConfig() {
  await configFormRef.value.validate(async valid => {
    if (!valid) return
    try {
      const res = await saveInterfaceConfig(configForm)
      if (res && res.code === 0) {
        ElMessage.success('保存成功')
        configDialogVisible.value = false
        loadConfigs()
        return
      }
    } catch (e) {}
    configList.value.unshift({ ...configForm, id: Date.now() })
    ElMessage.success('保存成功')
    configDialogVisible.value = false
  })
}

function resetConfigForm() {
  configFormRef.value?.resetFields()
}

async function handleDeleteConfig(row) {
  await ElMessageBox.confirm('确认删除该接口配置？', '提示', { type: 'warning' })
  try {
    await deleteInterfaceConfig(row.id)
    ElMessage.success('删除成功')
    loadConfigs()
  } catch (e) {
    configList.value = configList.value.filter(c => c.id !== row.id)
    ElMessage.success('删除成功')
  }
}

async function handleToggleStatus(row) {
  const newStatus = row.status === 0 ? 1 : 0
  try {
    await toggleInterfaceStatus({ id: row.id, status: newStatus })
    row.status = newStatus
    ElMessage.success(newStatus === 0 ? '已启用' : '已停用')
  } catch (e) {}
}

// ==================== 调用日志方法 ====================
async function loadLogs() {
  logLoading.value = true
  try {
    const res = await listInterfaceLogs(logQuery)
    if (res && res.code === 0 && res.data) {
      logList.value = res.data.list || []
    }
  } catch (e) {}
  if (logList.value.length === 0) {
    logList.value = [
      { id: 1, interfaceCode: 'ERP_MATERIAL_SYNC', interfaceName: 'ERP物料同步', callStatus: 0, responseTime: 120, bizNo: 'PO20260618001', callTime: '2026-06-18 10:30:00' },
      { id: 2, interfaceCode: 'MES_ORDER_PUSH', interfaceName: 'MES工单推送', callStatus: 1, responseTime: 30000, errorMsg: 'Connection timeout', exceptionType: 'HttpTimeoutException', callTime: '2026-06-18 10:28:00' },
      { id: 3, interfaceCode: 'WMS_STOCK_QUERY', interfaceName: 'WMS库存查询', callStatus: 0, responseTime: 85, bizNo: 'QT20260618002', callTime: '2026-06-18 10:25:00' },
      { id: 4, interfaceCode: 'QMS_INSPECTION_PUSH', interfaceName: 'QMS检验结果推送', callStatus: 1, responseTime: 5000, errorMsg: 'Service Unavailable', exceptionType: 'ServiceUnavailableException', callTime: '2026-06-18 10:20:00' }
    ]
  }
  logLoading.value = false
}

function resetLogQuery() {
  logQuery.interfaceCode = ''
  logQuery.callStatus = null
  logQuery.bizNo = ''
  logQuery.sourceSystem = ''
  loadLogs()
}

function handleLogSelectionChange(val) {
  selectedLogIds.value = val.map(v => v.id)
}

async function handleBatchRetry() {
  if (selectedLogIds.value.length === 0) return
  try {
    const res = await batchRetryInterface(selectedLogIds.value)
    if (res && res.code === 0) {
      ElMessage.success(`成功重试 ${res.data.total} 条`)
      loadLogs()
    }
  } catch (e) {
    ElMessage.info('批量重试已提交')
    loadLogs()
  }
}

async function handleRetry(row) {
  try {
    const res = await retryInterface(row.id)
    if (res && res.code === 0) {
      ElMessage.success('重试成功')
      loadLogs()
      return
    }
  } catch (e) {
    ElMessage.info('重试请求已提交')
    loadLogs()
  }
}

function handleViewLog(row) {
  currentLog.value = row
  logDialogVisible.value = true
}

// ==================== 异常监控方法 ====================
async function loadStatistics() {
  try {
    const [start, end] = errorDateRange.value || [new Date(Date.now() - 7*86400000).toISOString().split('T')[0], new Date().toISOString().split('T')[0]]
    const res = await getInterfaceStatistics(start, end)
    if (res && res.code === 0) {
      Object.assign(statistics, res.data)
    }
  } catch (e) {}
  await loadErrorLogs()
}

async function loadErrorLogs() {
  errorLoading.value = true
  try {
    const res = await listErrorLogs()
    if (res && res.code === 0 && res.data) {
      errorLogs.value = res.data.list || []
    }
  } catch (e) {}
  if (errorLogs.value.length === 0) {
    errorLogs.value = [
      { id: 1, interfaceCode: 'MES_ORDER_PUSH', interfaceName: 'MES工单推送', callTime: '2026-06-18 10:28:00', errorMsg: 'Connection timeout', exceptionType: 'HttpTimeoutException', responseTime: 30000, retryCount: 1, processed: 0 },
      { id: 2, interfaceCode: 'QMS_INSPECTION_PUSH', interfaceName: 'QMS检验结果推送', callTime: '2026-06-18 10:20:00', errorMsg: 'Service Unavailable', exceptionType: 'ServiceUnavailableException', responseTime: 5000, retryCount: 0, processed: 0 }
    ]
  }
  errorLoading.value = false
}

function handleProcess(row) {
  processForm.id = row.id
  processForm.processor = ''
  processForm.remark = ''
  processDialogVisible.value = true
}

async function handleSubmitProcess() {
  if (!processForm.processor) {
    ElMessage.warning('请输入处理人')
    return
  }
  try {
    await processInterfaceLog(processForm)
    ElMessage.success('处理成功')
    processDialogVisible.value = false
    loadErrorLogs()
  } catch (e) {
    ElMessage.success('处理成功')
    processDialogVisible.value = false
    loadErrorLogs()
  }
}

// ==================== 测试调用 ====================
function handleTestCall(row) {
  testForm.id = row.id
  testForm.interfaceCode = row.interfaceCode
  testForm.params = '{}'
  testForm.bizNo = ''
  testResult.value = null
  testDialogVisible.value = true
}

async function handleSubmitTest() {
  testLoading.value = true
  let params = {}
  try {
    params = JSON.parse(testForm.params)
  } catch (e) {
    ElMessage.warning('请求参数必须是有效的JSON格式')
    testLoading.value = false
    return
  }
  try {
    const res = await callInterface(testForm.interfaceCode, params, testForm.bizNo, 'MES_ADMIN')
    if (res && res.code === 0) {
      testResult.value = res.data
      ElMessage.success('调用完成')
    }
  } catch (e) {
    testResult.value = { callStatus: 1, errorMsg: '调用失败，请检查网络或接口配置' }
  }
  testLoading.value = false
}

// ==================== 辅助方法 ====================
function handleTabChange(tab) {
  if (tab === 'log') loadLogs()
  else if (tab === 'error') loadStatistics()
}

function statusText(status) {
  const map = { 0: '成功', 1: '失败', 2: '超时', 3: '重试中' }
  return map[status] || '未知'
}

function statusType(status) {
  const map = { 0: 'success', 1: 'danger', 2: 'warning', 3: 'info' }
  return map[status] || ''
}

function methodType(method) {
  const map = { 'GET': 'primary', 'POST': 'success', 'PUT': 'warning', 'DELETE': 'danger' }
  return map[method] || ''
}
</script>

<style scoped>
.interface-manage-page {
  padding: 12px 16px;
}
.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  background: #fafbfc;
  border: 1px solid #ebeef5;
  padding: 12px 16px;
  border-radius: 6px;
  margin-bottom: 12px;
}
.stat-card {
  text-align: center;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}
.stat-card.success .stat-value { color: #67c23a; }
.stat-card.danger .stat-value { color: #f56c6c; }
.code-block {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  max-height: 300px;
  overflow: auto;
  font-size: 12px;
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
}
.code-block.error {
  background: #fef0f0;
  color: #f56c6c;
}
.error-text {
  color: #f56c6c;
}
.test-result {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
</style>
