<template>
  <div class="page-wrapper">
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" class="search-form">
          <el-form-item label="出库单号">
            <el-input v-model="queryForm.code" placeholder="请输入出库单号" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="物料名称">
            <el-input v-model="queryForm.name" placeholder="请输入物料名称" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="出库日期">
            <el-date-picker v-model="queryForm.date" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 180px" />
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
          <el-icon><Plus /></el-icon>新增出库
        </el-button>
      </div>

      <el-table :data="pageList" border stripe style="width: 100%; margin-top: 12px">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="出库单号" prop="code" width="150" />
        <el-table-column label="物料名称" prop="name" width="160" />
        <el-table-column label="出库数量" prop="qty" width="110" align="center" />
        <el-table-column label="单位" prop="unit" width="100" align="center" />
        <el-table-column label="领用部门" prop="department" width="140" />
        <el-table-column label="出库仓库" prop="warehouse" width="140" />
        <el-table-column label="出库日期" prop="date" width="120" align="center" />
        <el-table-column label="出库人" prop="operator" width="100" align="center" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该出库单？" @confirm="handleDelete(row)">
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
      :title="dialogMode === 'add' ? '新增出库' : '编辑出库'"
      width="760px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="出库单号" prop="code">
              <el-input v-model="form.code" placeholder="请输入出库单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="物料名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入物料名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="数量" prop="qty">
              <el-input-number v-model="form.qty" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="form.unit" placeholder="如：件 / kg" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="领用部门" prop="department">
              <el-select v-model="form.department" placeholder="请选择" style="width: 100%" clearable>
                <el-option v-for="d in departmentOptions" :key="d" :label="d" :value="d" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出库仓库" prop="warehouse">
              <el-select v-model="form.warehouse" placeholder="请选择" style="width: 100%" clearable>
                <el-option v-for="w in warehouseOptions" :key="w" :label="w" :value="w" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="出库日期" prop="date">
              <el-date-picker v-model="form.date" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出库人" prop="operator">
              <el-input v-model="form.operator" placeholder="请输入出库人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择" style="width: 200px">
                <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
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

const departmentOptions = ['生产部', '研发部', '销售部', '装配车间', '质检部']
const warehouseOptions = ['一号原料仓', '二号原料仓', '成品仓 A', '半成品仓', '辅料仓']
const statusOptions = ['待出库', '已出库', '已取消']

const statusTag = (s) => {
  const map = { 待出库: 'warning', 已出库: 'success', 已取消: 'info' }
  return map[s] || ''
}

const dataList = ref([
  { id: 1, code: 'OUT20250001', name: '不锈钢板', qty: 20, unit: '张', department: '生产部', warehouse: '一号原料仓', date: '2025-01-12', operator: '张三', status: '已出库' },
  { id: 2, code: 'OUT20250002', name: '铝合金型材', qty: 15, unit: '根', department: '装配车间', warehouse: '一号原料仓', date: '2025-01-18', operator: '李四', status: '已出库' },
  { id: 3, code: 'OUT20250003', name: '铜线圈', qty: 50, unit: 'kg', department: '生产部', warehouse: '二号原料仓', date: '2025-02-08', operator: '王五', status: '待出库' },
  { id: 4, code: 'OUT20250004', name: '内六角螺丝', qty: 100, unit: '包', department: '装配车间', warehouse: '辅料仓', date: '2025-02-25', operator: '赵六', status: '已出库' },
  { id: 5, code: 'OUT20250005', name: '工控机箱', qty: 5, unit: '台', department: '销售部', warehouse: '成品仓 A', date: '2025-03-10', operator: '孙七', status: '已出库' },
  { id: 6, code: 'OUT20250006', name: 'PLC 控制器', qty: 3, unit: '台', department: '研发部', warehouse: '辅料仓', date: '2025-03-20', operator: '周八', status: '待出库' }
])

const queryForm = reactive({
  code: '',
  name: '',
  date: '',
  page: 1,
  pageSize: 10
})

const total = ref(dataList.value.length)

const filterList = computed(() => {
  const codeKw = normalize(queryForm.code)
  const nameKw = normalize(queryForm.name)
  const dateKw = normalize(queryForm.date)
  const list = dataList.value.filter((r) => {
    const ok1 = codeKw ? normalize(r.code).includes(codeKw) : true
    const ok2 = nameKw ? normalize(r.name).includes(nameKw) : true
    const ok3 = dateKw ? normalize(r.date) === dateKw : true
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
  queryForm.date = ''
  queryForm.page = 1
}

const form = reactive({
  id: null,
  code: '',
  name: '',
  qty: 0,
  unit: '',
  department: '',
  warehouse: '',
  date: '',
  operator: '',
  status: '待出库'
})

const rules = {
  code: [{ required: true, message: '请输入出库单号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入物料名称', trigger: 'blur' }]
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, code: '', name: '', qty: 0, unit: '', department: '', warehouse: '', date: '', operator: '', status: '待出库' })
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
        ElMessage.success('新增出库成功')
      } else {
        const idx = dataList.value.findIndex((u) => u.id === form.id)
        if (idx !== -1) {
          dataList.value[idx] = { ...dataList.value[idx], ...JSON.parse(JSON.stringify(form)) }
        }
        ElMessage.success('修改出库成功')
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
