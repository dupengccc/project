<template>
  <div class="page-wrapper">
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" class="search-form">
          <el-form-item label="仓库编码">
            <el-input v-model="queryForm.code" placeholder="请输入仓库编码" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="仓库名称">
            <el-input v-model="queryForm.name" placeholder="请输入仓库名称" clearable style="width: 180px" @keyup.enter="loadData" />
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
          <el-icon><Plus /></el-icon>新增仓库
        </el-button>
      </div>

      <el-table :data="pageList" border stripe style="width: 100%; margin-top: 12px">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="仓库编码" prop="code" width="130" />
        <el-table-column label="仓库名称" prop="name" width="160" />
        <el-table-column label="仓库类型" prop="type" width="120">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.type)" size="small" effect="plain">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="负责人" prop="manager" width="110" align="center" />
        <el-table-column label="联系电话" prop="phone" width="140" align="center" />
        <el-table-column label="地址" prop="address" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'info'" size="small">{{ row.status ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该仓库？" @confirm="handleDelete(row)">
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
      :title="dialogMode === 'add' ? '新增仓库' : '编辑仓库'"
      width="760px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="仓库编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入仓库编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入仓库名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="仓库类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
                <el-option v-for="o in typeOptions" :key="o" :label="o" :value="o" />
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
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-switch v-model="form.status" active-text="启用" inactive-text="禁用" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="地址" prop="address">
              <el-input v-model="form.address" type="textarea" :rows="3" placeholder="请输入地址" />
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

const typeOptions = ['原料仓', '成品仓', '半成品仓', '辅料仓']

const typeTag = (t) => {
  const map = { 原料仓: 'primary', 成品仓: 'success', 半成品仓: 'warning', 辅料仓: 'info' }
  return map[t] || ''
}

const dataList = ref([
  { id: 1, code: 'WH001', name: '一号原料仓', type: '原料仓', manager: '张三', phone: '13800138001', address: '上海市松江区工业区 1 号', status: true },
  { id: 2, code: 'WH002', name: '二号原料仓', type: '原料仓', manager: '李四', phone: '13800138002', address: '上海市松江区工业区 2 号', status: true },
  { id: 3, code: 'WH003', name: '成品仓 A', type: '成品仓', manager: '王五', phone: '13800138003', address: '上海市松江区工业区 3 号 A 区', status: true },
  { id: 4, code: 'WH004', name: '半成品仓', type: '半成品仓', manager: '赵六', phone: '13800138004', address: '上海市松江区工业区 4 号', status: true },
  { id: 5, code: 'WH005', name: '辅料仓', type: '辅料仓', manager: '孙七', phone: '13800138005', address: '上海市松江区工业区 5 号', status: true },
  { id: 6, code: 'WH006', name: '备用成品仓', type: '成品仓', manager: '周八', phone: '13800138006', address: '上海市松江区工业区 6 号', status: false }
])

const queryForm = reactive({
  code: '',
  name: '',
  page: 1,
  pageSize: 10
})

const total = ref(dataList.value.length)

const filterList = computed(() => {
  const codeKw = normalize(queryForm.code)
  const nameKw = normalize(queryForm.name)
  const list = dataList.value.filter((r) => {
    const ok1 = codeKw ? normalize(r.code).includes(codeKw) : true
    const ok2 = nameKw ? normalize(r.name).includes(nameKw) : true
    return ok1 && ok2
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
  queryForm.page = 1
}

const form = reactive({
  id: null,
  code: '',
  name: '',
  type: '原料仓',
  manager: '',
  phone: '',
  address: '',
  status: true
})

const rules = {
  code: [{ required: true, message: '请输入仓库编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }]
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, code: '', name: '', type: '原料仓', manager: '', phone: '', address: '', status: true })
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
        ElMessage.success('新增仓库成功')
      } else {
        const idx = dataList.value.findIndex((u) => u.id === form.id)
        if (idx !== -1) {
          dataList.value[idx] = { ...dataList.value[idx], ...JSON.parse(JSON.stringify(form)) }
        }
        ElMessage.success('修改仓库成功')
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
