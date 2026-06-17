<template>
  <div class="page-wrapper">
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" class="search-form">
          <el-form-item label="车间名称">
            <el-input v-model="queryForm.name" placeholder="请输入车间名称" clearable style="width: 200px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="车间编码">
            <el-input v-model="queryForm.code" placeholder="请输入车间编码" clearable style="width: 200px" @keyup.enter="loadData" />
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
          <el-icon><Plus /></el-icon>新增车间
        </el-button>
      </div>

      <el-table :data="pageList" border stripe style="width: 100%; margin-top: 12px">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="车间编码" prop="code" width="130" />
        <el-table-column label="车间名称" prop="name" width="180" />
        <el-table-column label="负责人" prop="manager" width="120" />
        <el-table-column label="联系电话" prop="phone" width="150" />
        <el-table-column label="面积(㎡)" prop="area" width="110" align="center" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'info'" size="small">{{ row.status ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="170" />
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该车间？" @confirm="handleDelete(row)">
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
      :title="dialogMode === 'add' ? '新增车间' : '编辑车间'"
      width="720px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="车间编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入车间编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车间名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入车间名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="负责人" prop="manager">
              <el-input v-model="form.manager" placeholder="请输入负责人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="面积(㎡)" prop="area">
              <el-input-number v-model="form.area" :min="0" style="width: 100%" />
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

const dataList = ref([
  { id: 1, code: 'WS001', name: '一号加工车间', manager: '李明', phone: '13911112222', area: 1200, status: true, remark: '主营产品主车间', createTime: '2024-08-01 09:12:00' },
  { id: 2, code: 'WS002', name: '二号装配车间', manager: '张伟', phone: '13922223333', area: 1500, status: true, remark: '', createTime: '2024-09-10 10:30:00' },
  { id: 3, code: 'WS003', name: '三号检测车间', manager: '王芳', phone: '13933334444', area: 600, status: true, remark: '', createTime: '2024-10-15 11:20:00' },
  { id: 4, code: 'WS004', name: '四号包装车间', manager: '陈强', phone: '13944445555', area: 800, status: true, remark: '', createTime: '2024-11-08 14:45:00' },
  { id: 5, code: 'WS005', name: '五号仓储车间', manager: '刘涛', phone: '13955556666', area: 2000, status: false, remark: '暂停使用', createTime: '2024-12-02 16:05:00' }
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
  manager: '',
  phone: '',
  area: 0,
  status: true,
  remark: ''
})

const rules = {
  code: [{ required: true, message: '请输入车间编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入车间名称', trigger: 'blur' }]
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, code: '', name: '', manager: '', phone: '', area: 0, status: true, remark: '' })
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
        ElMessage.success('操作成功')
      } else {
        const idx = dataList.value.findIndex((u) => u.id === form.id)
        if (idx !== -1) {
          dataList.value[idx] = { ...dataList.value[idx], ...JSON.parse(JSON.stringify(form)) }
        }
        ElMessage.success('操作成功')
      }
      dialogVisible.value = false
    } finally {
      submitLoading.value = false
    }
  })
}

function handleDelete(row) {
  dataList.value = dataList.value.filter((u) => u.id !== row.id)
  ElMessage.success('操作成功')
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
