<template>
  <div class="page-wrapper">
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" class="search-form">
          <el-form-item label="设备编码">
            <el-input v-model="queryForm.code" placeholder="请输入设备编码" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="设备名称">
            <el-input v-model="queryForm.name" placeholder="请输入设备名称" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="所属车间">
            <el-select v-model="queryForm.workshop" placeholder="请选择" clearable style="width: 160px">
              <el-option v-for="w in workshopOptions" :key="w" :label="w" :value="w" />
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
          <el-icon><Plus /></el-icon>新增设备
        </el-button>
      </div>

      <el-table :data="pageList" border stripe style="width: 100%; margin-top: 12px">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="设备编码" prop="code" width="130" />
        <el-table-column label="设备名称" prop="name" width="180" />
        <el-table-column label="设备类型" prop="type" width="130" />
        <el-table-column label="规格型号" prop="spec" width="160" />
        <el-table-column label="所属车间" prop="workshop" width="140" />
        <el-table-column label="负责人" prop="manager" width="110" align="center" />
        <el-table-column label="购置日期" prop="purchaseDate" width="120" align="center" />
        <el-table-column label="使用年限" prop="years" width="100" align="center" />
        <el-table-column label="状态" prop="status" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该设备？" @confirm="handleDelete(row)">
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
      :title="dialogMode === 'add' ? '新增设备' : '编辑设备'"
      width="760px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入设备编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入设备名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择" style="width: 100%" clearable>
                <el-option v-for="t in typeOptions" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号" prop="spec">
              <el-input v-model="form.spec" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属车间" prop="workshop">
              <el-select v-model="form.workshop" placeholder="请选择" style="width: 100%" clearable>
                <el-option v-for="w in workshopOptions" :key="w" :label="w" :value="w" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="manager">
              <el-input v-model="form.manager" placeholder="请输入负责人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="购置日期" prop="purchaseDate">
              <el-date-picker v-model="form.purchaseDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用年限" prop="years">
              <el-input-number v-model="form.years" :min="0" style="width: 100%" />
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

const typeOptions = ['机床', '注塑机', '检测设备', '装配线', '动力设备']
const workshopOptions = ['一车间', '二车间', '三车间', '装配车间', '检测车间']
const statusOptions = ['运行', '停机', '维修']

const statusTag = (s) => {
  const map = { 运行: 'success', 停机: 'info', 维修: 'warning' }
  return map[s] || ''
}

const dataList = ref([
  { id: 1, code: 'EQ-001', name: 'CNC 数控车床', type: '机床', spec: 'CK6140', workshop: '一车间', manager: '张三', purchaseDate: '2022-03-15', years: 3, status: '运行' },
  { id: 2, code: 'EQ-002', name: '立式加工中心', type: '机床', spec: 'VMC850', workshop: '一车间', manager: '李四', purchaseDate: '2021-07-20', years: 4, status: '运行' },
  { id: 3, code: 'EQ-003', name: '精密注塑机', type: '注塑机', spec: 'HT120', workshop: '二车间', manager: '王五', purchaseDate: '2020-11-08', years: 5, status: '维修' },
  { id: 4, code: 'EQ-004', name: '三坐标测量机', type: '检测设备', spec: 'CMM-543', workshop: '检测车间', manager: '赵六', purchaseDate: '2023-02-10', years: 2, status: '运行' },
  { id: 5, code: 'EQ-005', name: '自动装配线', type: '装配线', spec: 'ASM-A1', workshop: '装配车间', manager: '孙七', purchaseDate: '2024-01-05', years: 1, status: '运行' },
  { id: 6, code: 'EQ-006', name: '空压机', type: '动力设备', spec: '75kW', workshop: '三车间', manager: '周八', purchaseDate: '2019-05-18', years: 6, status: '停机' }
])

const queryForm = reactive({
  code: '',
  name: '',
  workshop: '',
  page: 1,
  pageSize: 10
})

const total = ref(dataList.value.length)

const filterList = computed(() => {
  const codeKw = normalize(queryForm.code)
  const nameKw = normalize(queryForm.name)
  const whKw = normalize(queryForm.workshop)
  const list = dataList.value.filter((r) => {
    const ok1 = codeKw ? normalize(r.code).includes(codeKw) : true
    const ok2 = nameKw ? normalize(r.name).includes(nameKw) : true
    const ok3 = whKw ? normalize(r.workshop) === whKw : true
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
  queryForm.workshop = ''
  queryForm.page = 1
}

const form = reactive({
  id: null,
  code: '',
  name: '',
  type: '',
  spec: '',
  workshop: '',
  manager: '',
  purchaseDate: '',
  years: 0,
  status: '运行'
})

const rules = {
  code: [{ required: true, message: '请输入设备编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }]
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, code: '', name: '', type: '', spec: '', workshop: '', manager: '', purchaseDate: '', years: 0, status: '运行' })
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
        ElMessage.success('新增设备成功')
      } else {
        const idx = dataList.value.findIndex((u) => u.id === form.id)
        if (idx !== -1) {
          dataList.value[idx] = { ...dataList.value[idx], ...JSON.parse(JSON.stringify(form)) }
        }
        ElMessage.success('修改设备成功')
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
