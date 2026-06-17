<template>
  <div class="page-wrapper">
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" class="search-form">
          <el-form-item label="计划编号">
            <el-input v-model="queryForm.code" placeholder="请输入计划编号" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="班组">
            <el-select v-model="queryForm.team" placeholder="请选择" clearable style="width: 160px">
              <el-option v-for="t in teamOptions" :key="t" :label="t" :value="t" />
            </el-select>
          </el-form-item>
          <el-form-item label="计划日期">
            <el-date-picker v-model="queryForm.date" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 160px" />
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
          <el-icon><Plus /></el-icon>新增排班计划
        </el-button>
      </div>

      <el-table :data="pageList" border stripe style="width: 100%; margin-top: 12px">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="计划编号" prop="code" width="130" />
        <el-table-column label="班组" prop="team" width="160" />
        <el-table-column label="排班日期" prop="planDate" width="120" align="center" />
        <el-table-column label="班次" prop="shift" width="110" align="center" />
        <el-table-column label="人员数" prop="people" width="100" align="center" />
        <el-table-column label="创建人" prop="creator" width="110" align="center" />
        <el-table-column label="创建时间" prop="createTime" width="170" align="center" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该计划？" @confirm="handleDelete(row)">
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
      :title="dialogMode === 'add' ? '新增排班计划' : '编辑排班计划'"
      width="720px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="计划编号" prop="code">
              <el-input v-model="form.code" placeholder="请输入计划编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班组" prop="team">
              <el-select v-model="form.team" placeholder="请选择" style="width: 100%" clearable>
                <el-option v-for="t in teamOptions" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="排班日期" prop="planDate">
              <el-date-picker v-model="form.planDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班次" prop="shift">
              <el-select v-model="form.shift" placeholder="请选择" style="width: 100%" clearable>
                <el-option v-for="s in shiftOptions" :key="s" :label="s" :value="s" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="人员数" prop="people">
              <el-input-number v-model="form.people" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
                <el-option label="草稿" value="草稿" />
                <el-option label="已发布" value="已发布" />
                <el-option label="已完成" value="已完成" />
              </el-select>
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

const teamOptions = ['甲班第一组', '甲班第二组', '乙班第一组', '丙班装配组', '检测质检组', '夜班维修组']
const shiftOptions = ['白班', '中班', '夜班', '早班', '加班班']

const statusTag = (s) => {
  const map = { 草稿: 'info', 已发布: 'success', 已完成: 'warning' }
  return map[s] || ''
}

const dataList = ref([
  { id: 1, code: 'PL-20241001-001', team: '甲班第一组', planDate: '2024-10-01', shift: '白班', people: 12, creator: '管理员', createTime: '2024-09-25 10:00:00', status: '已完成' },
  { id: 2, code: 'PL-20241002-002', team: '甲班第二组', planDate: '2024-10-02', shift: '中班', people: 10, creator: '管理员', createTime: '2024-09-25 10:15:00', status: '已完成' },
  { id: 3, code: 'PL-20241003-003', team: '乙班第一组', planDate: '2024-10-03', shift: '白班', people: 15, creator: '张三', createTime: '2024-09-26 09:30:00', status: '已发布' },
  { id: 4, code: 'PL-20241004-004', team: '丙班装配组', planDate: '2024-10-04', shift: '夜班', people: 8, creator: '张三', createTime: '2024-09-27 14:00:00', status: '已发布' },
  { id: 5, code: 'PL-20241005-005', team: '检测质检组', planDate: '2024-10-05', shift: '白班', people: 6, creator: '李四', createTime: '2024-09-28 11:00:00', status: '已发布' },
  { id: 6, code: 'PL-20241006-006', team: '甲班第一组', planDate: '2024-10-06', shift: '加班班', people: 5, creator: '李四', createTime: '2024-09-29 16:00:00', status: '草稿' },
  { id: 7, code: 'PL-20241007-007', team: '夜班维修组', planDate: '2024-10-07', shift: '夜班', people: 5, creator: '管理员', createTime: '2024-09-30 08:30:00', status: '草稿' }
])

const queryForm = reactive({
  code: '',
  team: '',
  date: '',
  page: 1,
  pageSize: 10
})

const total = ref(dataList.value.length)

const filterList = computed(() => {
  const codeKw = normalize(queryForm.code)
  const teamKw = normalize(queryForm.team)
  const dateKw = normalize(queryForm.date)
  const list = dataList.value.filter((r) => {
    const ok1 = codeKw ? normalize(r.code).includes(codeKw) : true
    const ok2 = teamKw ? normalize(r.team) === teamKw : true
    const ok3 = dateKw ? normalize(r.planDate) === dateKw : true
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
  queryForm.team = ''
  queryForm.date = ''
  queryForm.page = 1
}

const form = reactive({
  id: null,
  code: '',
  team: '',
  planDate: '',
  shift: '',
  people: 0,
  status: '草稿'
})

const rules = {
  code: [{ required: true, message: '请输入计划编号', trigger: 'blur' }]
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, code: '', team: '', planDate: '', shift: '', people: 0, status: '草稿' })
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
        const now = new Date()
        const ts = now.toISOString().slice(0, 10) + ' ' + now.toTimeString().slice(0, 8)
        dataList.value.unshift({ ...JSON.parse(JSON.stringify(form)), id: Date.now(), creator: '管理员', createTime: ts })
        ElMessage.success('新增排班计划成功')
      } else {
        const idx = dataList.value.findIndex((u) => u.id === form.id)
        if (idx !== -1) {
          dataList.value[idx] = { ...dataList.value[idx], ...JSON.parse(JSON.stringify(form)) }
        }
        ElMessage.success('修改排班计划成功')
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
.page-wrapper { padding: 12px; }
.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #fafbfc;
  border: 1px solid #ebeef5;
  border-radius: 6px;
}
.search-form :deep(.el-form-item) { margin-bottom: 0; margin-right: 12px; }
.search-form :deep(.el-form-item__label) { color: #606266; font-weight: 500; }
.pagination-bar { margin-top: 12px; display: flex; justify-content: flex-end; }
</style>
