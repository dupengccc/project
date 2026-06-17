<template>
  <div class="page-wrapper">
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" class="search-form">
          <el-form-item label="检验单号">
            <el-input v-model="queryForm.code" placeholder="请输入检验单号" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="产品名称">
            <el-input v-model="queryForm.name" placeholder="请输入产品名称" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="检验结果">
            <el-select v-model="queryForm.result" placeholder="请选择" clearable style="width: 160px">
              <el-option label="合格" value="合格" />
              <el-option label="不合格" value="不合格" />
            </el-select>
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
          <el-icon><Plus /></el-icon>新增记录
        </el-button>
      </div>

      <el-table :data="pageList" border stripe style="width: 100%; margin-top: 12px">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="检验单号" prop="code" width="150" />
        <el-table-column label="产品名称" prop="name" width="180" />
        <el-table-column label="检验数量" prop="total" width="110" align="center" />
        <el-table-column label="合格数量" prop="pass" width="110" align="center" />
        <el-table-column label="不合格数量" prop="fail" width="120" align="center" />
        <el-table-column label="检验结果" prop="result" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.result === '合格' ? 'success' : 'danger'" size="small">{{ row.result }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="检验人" prop="inspector" width="110" align="center" />
        <el-table-column label="检验时间" prop="date" width="180" align="center" />
        <el-table-column label="关联工单" prop="workorder" width="140" />
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该检验记录？" @confirm="handleDelete(row)">
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
      :title="dialogMode === 'add' ? '新增检验记录' : '编辑检验记录'"
      width="760px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="检验单号" prop="code">
              <el-input v-model="form.code" placeholder="请输入检验单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入产品名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="检验数量" prop="total">
              <el-input-number v-model="form.total" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="合格数量" prop="pass">
              <el-input-number v-model="form.pass" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="不合格数量" prop="fail">
              <el-input-number v-model="form.fail" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="检验结果" prop="result">
              <el-select v-model="form.result" placeholder="请选择" style="width: 100%">
                <el-option label="合格" value="合格" />
                <el-option label="不合格" value="不合格" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检验人" prop="inspector">
              <el-input v-model="form.inspector" placeholder="请输入检验人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="检验时间" prop="date">
              <el-date-picker v-model="form.date" type="datetime" placeholder="选择日期时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联工单" prop="workorder">
              <el-input v-model="form.workorder" placeholder="请输入关联工单号" />
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

const dataList = ref([
  { id: 1, code: 'QR20250101', name: '工控机箱 5U', total: 20, pass: 19, fail: 1, result: '合格', inspector: '张工', date: '2025-01-10 15:30:00', workorder: 'WO20250001', remark: '一台轻微划痕已返工。' },
  { id: 2, code: 'QR20250115', name: '触控一体机 15"', total: 15, pass: 15, fail: 0, result: '合格', inspector: '李工', date: '2025-01-15 10:00:00', workorder: 'WO20250002', remark: '' },
  { id: 3, code: 'QR20250203', name: '伺服电机 3kW', total: 10, pass: 8, fail: 2, result: '不合格', inspector: '王工', date: '2025-02-03 14:20:00', workorder: 'WO20250003', remark: '噪音超差，需返修。' },
  { id: 4, code: 'QR20250301', name: '精密齿轮', total: 50, pass: 48, fail: 2, result: '合格', inspector: '赵工', date: '2025-03-01 09:15:00', workorder: 'WO20250004', remark: '' },
  { id: 5, code: 'QR20250315', name: '电路板 Rev.B', total: 30, pass: 27, fail: 3, result: '不合格', inspector: '孙工', date: '2025-03-15 16:45:00', workorder: 'WO20250005', remark: '焊接不良。' },
  { id: 6, code: 'QR20250401', name: '工控机箱 4U', total: 25, pass: 25, fail: 0, result: '合格', inspector: '周工', date: '2025-04-01 11:10:00', workorder: 'WO20250006', remark: '' }
])

const queryForm = reactive({
  code: '',
  name: '',
  result: '',
  page: 1,
  pageSize: 10
})

const total = ref(dataList.value.length)

const filterList = computed(() => {
  const codeKw = normalize(queryForm.code)
  const nameKw = normalize(queryForm.name)
  const resultKw = normalize(queryForm.result)
  const list = dataList.value.filter((r) => {
    const ok1 = codeKw ? normalize(r.code).includes(codeKw) : true
    const ok2 = nameKw ? normalize(r.name).includes(nameKw) : true
    const ok3 = resultKw ? normalize(r.result) === resultKw : true
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
  queryForm.name = ''
  queryForm.result = ''
  queryForm.page = 1
}

const form = reactive({
  id: null,
  code: '',
  name: '',
  total: 0,
  pass: 0,
  fail: 0,
  result: '合格',
  inspector: '',
  date: '',
  workorder: '',
  remark: ''
})

const rules = {
  code: [{ required: true, message: '请输入检验单号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入产品名称', trigger: 'blur' }]
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, code: '', name: '', total: 0, pass: 0, fail: 0, result: '合格', inspector: '', date: '', workorder: '', remark: '' })
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
