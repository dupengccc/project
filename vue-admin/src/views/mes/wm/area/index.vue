<template>
  <div class="page-wrapper">
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" class="search-form">
          <el-form-item label="库区编码">
            <el-input v-model="queryForm.code" placeholder="请输入库区编码" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="库区名称">
            <el-input v-model="queryForm.name" placeholder="请输入库区名称" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="所属仓库">
            <el-select v-model="queryForm.warehouse" placeholder="请选择" clearable style="width: 160px">
              <el-option v-for="w in warehouseOptions" :key="w" :label="w" :value="w" />
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
          <el-icon><Plus /></el-icon>新增库区
        </el-button>
      </div>

      <el-table :data="pageList" border stripe style="width: 100%; margin-top: 12px">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="库区编码" prop="code" width="130" />
        <el-table-column label="库区名称" prop="name" width="160" />
        <el-table-column label="所属仓库" prop="warehouse" width="140" />
        <el-table-column label="库区类型" prop="type" width="120" />
        <el-table-column label="面积(㎡)" prop="area" width="110" align="center" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'info'" size="small">{{ row.status ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该库区？" @confirm="handleDelete(row)">
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
      :title="dialogMode === 'add' ? '新增库区' : '编辑库区'"
      width="760px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="库区编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入库区编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库区名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入库区名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属仓库" prop="warehouse">
              <el-select v-model="form.warehouse" placeholder="请选择" style="width: 100%">
                <el-option v-for="w in warehouseOptions" :key="w" :label="w" :value="w" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库区类型" prop="type">
              <el-input v-model="form.type" placeholder="如：常温区/冷藏区" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="面积" prop="area">
              <el-input-number v-model="form.area" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-switch v-model="form.status" active-text="启用" inactive-text="禁用" />
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

const warehouseOptions = ['一号原料仓', '二号原料仓', '成品仓 A', '半成品仓', '辅料仓']

const dataList = ref([
  { id: 1, code: 'A01', name: '原料A区', warehouse: '一号原料仓', type: '常温区', area: 300, status: true },
  { id: 2, code: 'A02', name: '原料B区', warehouse: '一号原料仓', type: '常温区', area: 280, status: true },
  { id: 3, code: 'B01', name: '原料A区', warehouse: '二号原料仓', type: '冷藏区', area: 200, status: true },
  { id: 4, code: 'C01', name: '成品A区', warehouse: '成品仓 A', type: '常温区', area: 400, status: true },
  { id: 5, code: 'D01', name: '半成品区', warehouse: '半成品仓', type: '常温区', area: 250, status: true },
  { id: 6, code: 'E01', name: '辅料区', warehouse: '辅料仓', type: '常温区', area: 180, status: false }
])

const queryForm = reactive({
  code: '',
  name: '',
  warehouse: '',
  page: 1,
  pageSize: 10
})

const total = ref(dataList.value.length)

const filterList = computed(() => {
  const codeKw = normalize(queryForm.code)
  const nameKw = normalize(queryForm.name)
  const whKw = normalize(queryForm.warehouse)
  const list = dataList.value.filter((r) => {
    const ok1 = codeKw ? normalize(r.code).includes(codeKw) : true
    const ok2 = nameKw ? normalize(r.name).includes(nameKw) : true
    const ok3 = whKw ? normalize(r.warehouse) === whKw : true
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
  queryForm.warehouse = ''
  queryForm.page = 1
}

const form = reactive({
  id: null,
  code: '',
  name: '',
  warehouse: '',
  type: '',
  area: 0,
  status: true
})

const rules = {
  code: [{ required: true, message: '请输入库区编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入库区名称', trigger: 'blur' }],
  warehouse: [{ required: true, message: '请选择所属仓库', trigger: 'change' }]
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, code: '', name: '', warehouse: '', type: '', area: 0, status: true })
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
        ElMessage.success('新增库区成功')
      } else {
        const idx = dataList.value.findIndex((u) => u.id === form.id)
        if (idx !== -1) {
          dataList.value[idx] = { ...dataList.value[idx], ...JSON.parse(JSON.stringify(form)) }
        }
        ElMessage.success('修改库区成功')
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
