<template>
  <div class="page-wrapper">
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" class="search-form">
          <el-form-item label="班组编码">
            <el-input v-model="queryForm.code" placeholder="请输入班组编码" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="班组名称">
            <el-input v-model="queryForm.name" placeholder="请输入班组名称" clearable style="width: 180px" @keyup.enter="loadData" />
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
          <el-icon><Plus /></el-icon>新增班组
        </el-button>
      </div>

      <el-table :data="pageList" border stripe style="width: 100%; margin-top: 12px">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="班组编码" prop="code" width="130" />
        <el-table-column label="班组名称" prop="name" width="160" />
        <el-table-column label="班组长" prop="leader" width="110" align="center" />
        <el-table-column label="所属车间" prop="workshop" width="140" />
        <el-table-column label="班组人数" prop="count" width="100" align="center" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '启用' ? 'success' : 'info'" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="170" align="center" />
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该班组？" @confirm="handleDelete(row)">
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
      :title="dialogMode === 'add' ? '新增班组' : '编辑班组'"
      width="720px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="班组编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入班组编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班组名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入班组名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="班组长" prop="leader">
              <el-select v-model="form.leader" placeholder="请选择" style="width: 100%" clearable>
                <el-option v-for="l in leaderOptions" :key="l" :label="l" :value="l" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属车间" prop="workshop">
              <el-select v-model="form.workshop" placeholder="请选择" style="width: 100%" clearable>
                <el-option v-for="w in workshopOptions" :key="w" :label="w" :value="w" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="班组人数" prop="count">
              <el-input-number v-model="form.count" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
                <el-option label="启用" value="启用" />
                <el-option label="禁用" value="禁用" />
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

const leaderOptions = ['张三', '李四', '王五', '赵六', '孙七', '周八', '吴九']
const workshopOptions = ['一车间', '二车间', '三车间', '装配车间', '检测车间']

const dataList = ref([
  { id: 1, code: 'TM-A01', name: '甲班第一组', leader: '张三', workshop: '一车间', count: 12, status: '启用', createTime: '2024-08-01 09:00:00' },
  { id: 2, code: 'TM-A02', name: '甲班第二组', leader: '李四', workshop: '一车间', count: 10, status: '启用', createTime: '2024-08-02 10:00:00' },
  { id: 3, code: 'TM-B01', name: '乙班第一组', leader: '王五', workshop: '二车间', count: 15, status: '启用', createTime: '2024-09-10 09:30:00' },
  { id: 4, code: 'TM-C01', name: '丙班装配组', leader: '赵六', workshop: '装配车间', count: 8, status: '启用', createTime: '2024-09-15 14:00:00' },
  { id: 5, code: 'TM-D01', name: '检测质检组', leader: '孙七', workshop: '检测车间', count: 6, status: '启用', createTime: '2024-10-05 11:00:00' },
  { id: 6, code: 'TM-E01', name: '夜班维修组', leader: '周八', workshop: '三车间', count: 5, status: '禁用', createTime: '2024-11-01 16:00:00' }
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
  leader: '',
  workshop: '',
  count: 0,
  status: '启用'
})

const rules = {
  code: [{ required: true, message: '请输入班组编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入班组名称', trigger: 'blur' }]
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, code: '', name: '', leader: '', workshop: '', count: 0, status: '启用' })
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
        dataList.value.unshift({ ...JSON.parse(JSON.stringify(form)), id: Date.now(), createTime: ts })
        ElMessage.success('新增班组成功')
      } else {
        const idx = dataList.value.findIndex((u) => u.id === form.id)
        if (idx !== -1) {
          dataList.value[idx] = { ...dataList.value[idx], ...JSON.parse(JSON.stringify(form)) }
        }
        ElMessage.success('修改班组成功')
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
