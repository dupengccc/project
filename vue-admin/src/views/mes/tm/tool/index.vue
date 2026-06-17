<template>
  <div class="page-wrapper">
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" class="search-form">
          <el-form-item label="工具编码">
            <el-input v-model="queryForm.code" placeholder="请输入工具编码" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="工具名称">
            <el-input v-model="queryForm.name" placeholder="请输入工具名称" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="工具类型">
            <el-select v-model="queryForm.type" placeholder="请选择" clearable style="width: 160px">
              <el-option v-for="t in typeOptions" :key="t" :label="t" :value="t" />
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
          <el-icon><Plus /></el-icon>新增工具
        </el-button>
      </div>

      <el-table :data="pageList" border stripe style="width: 100%; margin-top: 12px">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="工具编码" prop="code" width="130" />
        <el-table-column label="工具名称" prop="name" width="180" />
        <el-table-column label="工具类型" prop="type" width="110" />
        <el-table-column label="规格型号" prop="spec" width="160" />
        <el-table-column label="所属车间" prop="workshop" width="140" />
        <el-table-column label="存放位置" prop="location" width="140" />
        <el-table-column label="当前状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="入库日期" prop="inDate" width="120" align="center" />
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该工具？" @confirm="handleDelete(row)">
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
      :title="dialogMode === 'add' ? '新增工具' : '编辑工具'"
      width="760px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="工具编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入工具编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工具名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入工具名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="工具类型" prop="type">
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
            <el-form-item label="存放位置" prop="location">
              <el-input v-model="form.location" placeholder="请输入存放位置" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="当前状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
                <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入库日期" prop="inDate">
              <el-date-picker v-model="form.inDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
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

const typeOptions = ['刀具', '夹具', '量具', '模具', '其他']
const workshopOptions = ['一车间', '二车间', '三车间', '装配车间', '检测车间']
const statusOptions = ['在用', '闲置', '维修', '报废']

const statusTag = (s) => {
  const map = { 在用: 'success', 闲置: 'info', 维修: 'warning', 报废: 'danger' }
  return map[s] || ''
}

const dataList = ref([
  { id: 1, code: 'TL-001', name: '硬质合金铣刀', type: '刀具', spec: 'Φ10×100mm', workshop: '一车间', location: 'A区-01-03', status: '在用', inDate: '2024-03-15', remark: '' },
  { id: 2, code: 'TL-002', name: '车床三爪卡盘', type: '夹具', spec: '250mm', workshop: '一车间', location: 'A区-02-05', status: '在用', inDate: '2023-11-20', remark: '' },
  { id: 3, code: 'TL-003', name: '数显游标卡尺', type: '量具', spec: '0-300mm', workshop: '检测车间', location: 'B区-01-01', status: '在用', inDate: '2024-01-10', remark: '' },
  { id: 4, code: 'TL-004', name: '注塑模具 A1', type: '模具', spec: '500×400mm', workshop: '二车间', location: 'C区-03-02', status: '维修', inDate: '2022-08-08', remark: '定期维护' },
  { id: 5, code: 'TL-005', name: '千分尺', type: '量具', spec: '0-25mm', workshop: '检测车间', location: 'B区-01-02', status: '闲置', inDate: '2024-05-22', remark: '' },
  { id: 6, code: 'TL-006', name: '钻头套装', type: '刀具', spec: 'Φ1-13mm', workshop: '三车间', location: 'A区-03-01', status: '在用', inDate: '2024-07-12', remark: '' },
  { id: 7, code: 'TL-007', name: '装配夹具 B2', type: '夹具', spec: '定制', workshop: '装配车间', location: 'D区-01-04', status: '报废', inDate: '2020-06-30', remark: '已报废登记' },
  { id: 8, code: 'TL-008', name: '丝锥套装', type: '刀具', spec: 'M3-M12', workshop: '一车间', location: 'A区-02-01', status: '在用', inDate: '2024-09-01', remark: '' }
])

const queryForm = reactive({
  code: '',
  name: '',
  type: '',
  page: 1,
  pageSize: 10
})

const total = ref(dataList.value.length)

const filterList = computed(() => {
  const codeKw = normalize(queryForm.code)
  const nameKw = normalize(queryForm.name)
  const typeKw = normalize(queryForm.type)
  const list = dataList.value.filter((r) => {
    const ok1 = codeKw ? normalize(r.code).includes(codeKw) : true
    const ok2 = nameKw ? normalize(r.name).includes(nameKw) : true
    const ok3 = typeKw ? normalize(r.type) === typeKw : true
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
  queryForm.type = ''
  queryForm.page = 1
}

const form = reactive({
  id: null,
  code: '',
  name: '',
  type: '',
  spec: '',
  workshop: '',
  location: '',
  status: '在用',
  inDate: '',
  remark: ''
})

const rules = {
  code: [{ required: true, message: '请输入工具编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入工具名称', trigger: 'blur' }]
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, code: '', name: '', type: '', spec: '', workshop: '', location: '', status: '在用', inDate: '', remark: '' })
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
        ElMessage.success('新增工具成功')
      } else {
        const idx = dataList.value.findIndex((u) => u.id === form.id)
        if (idx !== -1) {
          dataList.value[idx] = { ...dataList.value[idx], ...JSON.parse(JSON.stringify(form)) }
        }
        ElMessage.success('修改工具成功')
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
