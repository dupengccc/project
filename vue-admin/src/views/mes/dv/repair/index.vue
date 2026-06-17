<template>
  <div class="page-wrapper">
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" class="search-form">
          <el-form-item label="维修单号">
            <el-input v-model="queryForm.code" placeholder="请输入维修单号" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="设备名称">
            <el-input v-model="queryForm.device" placeholder="请输入设备名称" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="维修人">
            <el-input v-model="queryForm.worker" placeholder="请输入维修人" clearable style="width: 160px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">
              <el-icon><Search /></el-icon>查询
            </el-button>
            <el-button @click="resetQuery">
              <el-icon><RefreshLeft /></el-icon>重置
            </el-button>
          </el-form-item>
        </el-form>
        <el-button type="success" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增维修
        </el-button>
      </div>

      <el-table :data="pageList" border stripe style="width: 100%; margin-top: 12px">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="维修单号" prop="code" width="150" />
        <el-table-column label="设备名称" prop="device" width="180" />
        <el-table-column label="故障描述" prop="issue" min-width="220" show-overflow-tooltip />
        <el-table-column label="报修人" prop="reporter" width="100" align="center" />
        <el-table-column label="报修时间" prop="reportDate" width="180" align="center" />
        <el-table-column label="维修人" prop="worker" width="100" align="center" />
        <el-table-column label="维修时间" prop="fixDate" width="180" align="center" />
        <el-table-column label="维修状态" prop="status" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="维修费用" prop="cost" width="120" align="center">
          <template #default="{ row }">{{ row.cost }} 元</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该维修单？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button size="small" type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '新增维修' : '编辑维修'"
      width="760px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="维修单号" prop="code">
              <el-input v-model="form.code" placeholder="请输入维修单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备名称" prop="device">
              <el-input v-model="form.device" placeholder="请输入设备名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="报修人" prop="reporter">
              <el-input v-model="form.reporter" placeholder="请输入报修人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报修时间" prop="reportDate">
              <el-date-picker v-model="form.reportDate" type="datetime" placeholder="选择日期时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="维修人" prop="worker">
              <el-input v-model="form.worker" placeholder="请输入维修人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维修时间" prop="fixDate">
              <el-date-picker v-model="form.fixDate" type="datetime" placeholder="选择日期时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="维修状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
                <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维修费用" prop="cost">
              <el-input-number v-model="form.cost" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="故障描述" prop="issue">
              <el-input v-model="form.issue" type="textarea" :rows="3" placeholder="请输入故障描述" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

const formRef = ref()
const dialogVisible = ref(false)
const dialogMode = ref('add')
const submitLoading = ref(false)

const statusOptions = ['维修中', '已完成', '待验收']

const statusTag = (s) => {
  const map = { 维修中: 'warning', 已完成: 'success', 待验收: 'info' }
  return map[s] || ''
}

const dataList = ref([
  { id: 1, code: 'RP20250001', device: 'CNC 数控车床', issue: '主轴异响', reporter: '张三', reportDate: '2025-01-12 09:30:00', worker: '李工', fixDate: '2025-01-14 16:00:00', status: '已完成', cost: 3500, remark: '更换轴承。' },
  { id: 2, code: 'RP20250002', device: '立式加工中心', issue: '换刀机构卡刀', reporter: '李四', reportDate: '2025-02-02 10:15:00', worker: '王工', fixDate: '2025-02-05 14:30:00', status: '已完成', cost: 2800, remark: '' },
  { id: 3, code: 'RP20250003', device: '精密注塑机', issue: '加热圈故障', reporter: '王五', reportDate: '2025-02-25 11:00:00', worker: '赵工', fixDate: '', status: '维修中', cost: 1500, remark: '配件待到达。' },
  { id: 4, code: 'RP20250004', device: '三坐标测量机', issue: '测头校准失败', reporter: '赵六', reportDate: '2025-03-10 14:20:00', worker: '孙工', fixDate: '2025-03-12 09:30:00', status: '待验收', cost: 800, remark: '' },
  { id: 5, code: 'RP20250005', device: '自动装配线', issue: '传感器信号异常', reporter: '孙七', reportDate: '2025-03-25 15:45:00', worker: '周工', fixDate: '2025-03-26 11:00:00', status: '已完成', cost: 600, remark: '' },
  { id: 6, code: 'RP20250006', device: '空压机', issue: '排气温度偏高', reporter: '周八', reportDate: '2025-04-08 13:00:00', worker: '吴工', fixDate: '', status: '维修中', cost: 2000, remark: '散热器清洗中。' }
])

const queryForm = reactive({
  code: '',
  device: '',
  worker: '',
  page: 1,
  pageSize: 10
})

const total = ref(dataList.value.length)

const filterList = computed(() => {
  const codeKw = normalize(queryForm.code)
  const deviceKw = normalize(queryForm.device)
  const workerKw = normalize(queryForm.worker)
  const list = dataList.value.filter((r) => {
    const ok1 = codeKw ? normalize(r.code).includes(codeKw) : true
    const ok2 = deviceKw ? normalize(r.device).includes(deviceKw) : true
    const ok3 = workerKw ? normalize(r.worker).includes(workerKw) : true
    return ok1 && ok2 && ok3
  })
  total.value = list.length
  return list
})

const pageList = computed(() => {
  const start = (queryForm.page - 1) * queryForm.pageSize
  return filterList.value.slice(start, start + queryForm.pageSize)
})

function normalize(val) {
  return (val == null ? '' : String(val)).trim().toLowerCase()
}

function loadData() {
  queryForm.page = 1
  ElMessage.success('查询完成')
}

function resetQuery() {
  queryForm.code = ''
  queryForm.device = ''
  queryForm.worker = ''
  queryForm.page = 1
}

const form = reactive({
  id: null,
  code: '',
  device: '',
  issue: '',
  reporter: '',
  reportDate: '',
  worker: '',
  fixDate: '',
  status: '维修中',
  cost: 0,
  remark: ''
})

const rules = {
  code: [{ required: true, message: '请输入维修单号', trigger: 'blur' }],
  device: [{ required: true, message: '请输入设备名称', trigger: 'blur' }]
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, code: '', device: '', issue: '', reporter: '', reportDate: '', worker: '', fixDate: '', status: '维修中', cost: 0, remark: '' })
}

function handleAdd() {
  dialogMode.value = 'add'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogMode.value = 'edit'
  Object.assign(form, JSON.parse(JSON.stringify(row)))
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (dialogMode.value === 'add') {
        dataList.value.unshift({ ...JSON.parse(JSON.stringify(form)), id: Date.now() })
        ElMessage.success('新增维修成功')
      } else {
        const idx = dataList.value.findIndex((u) => u.id === form.id)
        if (idx !== -1) {
          dataList.value[idx] = { ...dataList.value[idx], ...JSON.parse(JSON.stringify(form)) }
        }
        ElMessage.success('修改维修成功')
      }
      dialogVisible.value = false
    } finally {
      submitLoading.value = false
    }
  })
}

function handleDelete(row) {
  dataList.value = dataList.value.filter((u) => u.id !== row.id)
  ElMessage.success('删除成功')
}

onMounted(() => {
  total.value = dataList.value.length
})
</script>

<style lang="scss" scoped>
.page-wrapper {
  padding: 12px;
}
.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #fafbfc;
  border: 1px solid #ebeef5;
  border-radius: 6px;
}
.search-form :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 12px;
}
.search-form :deep(.el-form-item__label) {
  color: #606266;
  font-weight: 500;
}
.pagination-bar {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
