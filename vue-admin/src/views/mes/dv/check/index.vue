<template>
  <div class="page-wrapper">
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" class="search-form">
          <el-form-item label="点检单号">
            <el-input v-model="queryForm.code" placeholder="请输入点检单号" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="设备名称">
            <el-input v-model="queryForm.device" placeholder="请输入设备名称" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="点检人">
            <el-input v-model="queryForm.inspector" placeholder="请输入点检人" clearable style="width: 160px" @keyup.enter="loadData" />
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
          <el-icon><Plus /></el-icon>新增点检
        </el-button>
      </div>

      <el-table :data="pageList" border stripe style="width: 100%; margin-top: 12px">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="点检单号" prop="code" width="150" />
        <el-table-column label="设备名称" prop="device" width="180" />
        <el-table-column label="设备编码" prop="deviceCode" width="130" />
        <el-table-column label="点检类型" prop="type" width="110" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="plain">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="点检人" prop="inspector" width="100" align="center" />
        <el-table-column label="点检日期" prop="date" width="120" align="center" />
        <el-table-column label="点检结果" prop="result" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.result === '正常' ? 'success' : 'warning'" size="small">{{ row.result }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="下次点检日期" prop="nextDate" width="130" align="center" />
        <el-table-column label="状态" prop="status" width="110" align="center" />
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该点检记录？" @confirm="handleDelete(row)">
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
      :title="dialogMode === 'add' ? '新增点检' : '编辑点检'"
      width="760px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="点检单号" prop="code">
              <el-input v-model="form.code" placeholder="请输入点检单号" />
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
            <el-form-item label="点检类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
                <el-option v-for="t in typeOptions" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="点检人" prop="inspector">
              <el-input v-model="form.inspector" placeholder="请输入点检人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="点检日期" prop="date">
              <el-date-picker v-model="form.date" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="点检结果" prop="result">
              <el-select v-model="form.result" placeholder="请选择" style="width: 100%">
                <el-option v-for="r in resultOptions" :key="r" :label="r" :value="r" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="下次点检日期" prop="nextDate">
              <el-date-picker v-model="form.nextDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-input v-model="form.status" placeholder="请输入状态" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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

const typeOptions = ['日常', '定期', '专项']
const resultOptions = ['正常', '异常']

const dataList = ref([
  { id: 1, code: 'CK20250001', device: 'CNC 数控车床', deviceCode: 'EQ-001', type: '日常', inspector: '张三', date: '2025-01-05', result: '正常', nextDate: '2025-01-12', status: '已完成', remark: '各项指标正常。' },
  { id: 2, code: 'CK20250002', device: '立式加工中心', deviceCode: 'EQ-002', type: '定期', inspector: '李四', date: '2025-01-10', result: '异常', nextDate: '2025-02-10', status: '待处理', remark: '润滑油位偏低，已补充。' },
  { id: 3, code: 'CK20250003', device: '精密注塑机', deviceCode: 'EQ-003', type: '专项', inspector: '王五', date: '2025-02-01', result: '异常', nextDate: '2025-03-01', status: '检修中', remark: '加热圈故障，待更换。' },
  { id: 4, code: 'CK20250004', device: '三坐标测量机', deviceCode: 'EQ-004', type: '日常', inspector: '赵六', date: '2025-02-20', result: '正常', nextDate: '2025-02-27', status: '已完成', remark: '' },
  { id: 5, code: 'CK20250005', device: '自动装配线', deviceCode: 'EQ-005', type: '定期', inspector: '孙七', date: '2025-03-05', result: '正常', nextDate: '2025-04-05', status: '已完成', remark: '' },
  { id: 6, code: 'CK20250006', device: '空压机', deviceCode: 'EQ-006', type: '日常', inspector: '周八', date: '2025-03-18', result: '异常', nextDate: '2025-03-25', status: '待处理', remark: '排气温度偏高。' }
])

const queryForm = reactive({
  code: '',
  device: '',
  inspector: '',
  page: 1,
  pageSize: 10
})

const total = ref(dataList.value.length)

const filterList = computed(() => {
  const codeKw = normalize(queryForm.code)
  const deviceKw = normalize(queryForm.device)
  const inspectorKw = normalize(queryForm.inspector)
  const list = dataList.value.filter((r) => {
    const ok1 = codeKw ? normalize(r.code).includes(codeKw) : true
    const ok2 = deviceKw ? normalize(r.device).includes(deviceKw) : true
    const ok3 = inspectorKw ? normalize(r.inspector).includes(inspectorKw) : true
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
  queryForm.inspector = ''
  queryForm.page = 1
}

const form = reactive({
  id: null,
  code: '',
  device: '',
  deviceCode: '',
  type: '日常',
  inspector: '',
  date: '',
  result: '正常',
  nextDate: '',
  status: '',
  remark: ''
})

const rules = {
  code: [{ required: true, message: '请输入点检单号', trigger: 'blur' }],
  device: [{ required: true, message: '请输入设备名称', trigger: 'blur' }]
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, code: '', device: '', deviceCode: '', type: '日常', inspector: '', date: '', result: '正常', nextDate: '', status: '', remark: '' })
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
        ElMessage.success('新增成功')
      } else {
        const idx = dataList.value.findIndex((u) => u.id === form.id)
        if (idx !== -1) {
          dataList.value[idx] = { ...dataList.value[idx], ...JSON.parse(JSON.stringify(form)) }
        }
        ElMessage.success('修改成功')
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
