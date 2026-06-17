<template>
  <div class="page-wrapper">
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" class="search-form">
          <el-form-item label="物料编码">
            <el-input v-model="queryForm.code" placeholder="请输入物料编码" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="物料名称">
            <el-input v-model="queryForm.name" placeholder="请输入物料名称" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="物料类型">
            <el-select v-model="queryForm.type" placeholder="请选择" clearable style="width: 160px">
              <el-option label="原材料" value="原材料" />
              <el-option label="半成品" value="半成品" />
              <el-option label="成品" value="成品" />
              <el-option label="辅料" value="辅料" />
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
          <el-icon><Plus /></el-icon>新增物料
        </el-button>
      </div>

      <el-table :data="pageList" border stripe style="width: 100%; margin-top: 12px">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="物料编码" prop="code" width="130" />
        <el-table-column label="物料名称" prop="name" width="160" />
        <el-table-column label="规格型号" prop="spec" width="140" />
        <el-table-column label="物料类型" prop="type" width="100">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.type)" size="small" effect="plain">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="计量单位" prop="unit" width="100" align="center" />
        <el-table-column label="安全库存" prop="safeStock" width="100" align="center" />
        <el-table-column label="当前库存" prop="stock" width="100" align="center" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'info'" size="small">{{ row.status ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="160" show-overflow-tooltip />
        <el-table-column label="创建时间" prop="createTime" width="170" />
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该物料？" @confirm="handleDelete(row)">
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
      :title="dialogMode === 'add' ? '新增物料' : '编辑物料'"
      width="760px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="物料编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入物料编码" />
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
            <el-form-item label="规格型号" prop="spec">
              <el-input v-model="form.spec" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="物料类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
                <el-option label="原材料" value="原材料" />
                <el-option label="半成品" value="半成品" />
                <el-option label="成品" value="成品" />
                <el-option label="辅料" value="辅料" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="计量单位" prop="unit">
              <el-input v-model="form.unit" placeholder="如：件 / kg" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="安全库存" prop="safeStock">
              <el-input-number v-model="form.safeStock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="当前库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
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

const typeTag = (t) => {
  const map = { 原材料: 'primary', 半成品: 'warning', 成品: 'success', 辅料: 'info' }
  return map[t] || ''
}

const dataList = ref([
  { id: 1, code: 'M00001', name: '不锈钢板', spec: '1220*2440*2mm', type: '原材料', unit: '张', safeStock: 50, stock: 200, status: true, remark: '常用原材料', createTime: '2025-01-03 09:12:00' },
  { id: 2, code: 'M00002', name: '铝合金型材', spec: '6063-T5 2m', type: '原材料', unit: '根', safeStock: 100, stock: 80, status: true, remark: '', createTime: '2025-01-05 10:30:00' },
  { id: 3, code: 'B00001', name: '半成品装配A', spec: 'A100', type: '半成品', unit: '件', safeStock: 30, stock: 45, status: true, remark: '标准半成品', createTime: '2025-02-01 14:20:00' },
  { id: 4, code: 'F00001', name: '工控机箱', spec: 'IPC-610L', type: '成品', unit: '台', safeStock: 10, stock: 25, status: true, remark: '标准产品', createTime: '2025-03-10 08:45:00' },
  { id: 5, code: 'A00001', name: '内六角螺丝', spec: 'M4x8', type: '辅料', unit: '包', safeStock: 500, stock: 320, status: true, remark: '', createTime: '2025-03-15 16:05:00' },
  { id: 6, code: 'F00002', name: '触控一体机', spec: '15寸 工业级', type: '成品', unit: '台', safeStock: 5, stock: 3, status: false, remark: '暂停销售', createTime: '2025-04-02 11:10:00' }
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
  spec: '',
  type: '原材料',
  unit: '',
  safeStock: 0,
  stock: 0,
  status: true,
  remark: ''
})

const rules = {
  code: [{ required: true, message: '请输入物料编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入物料名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择物料类型', trigger: 'change' }]
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, code: '', name: '', spec: '', type: '原材料', unit: '', safeStock: 0, stock: 0, status: true, remark: '' })
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
        ElMessage.success('新增物料成功')
      } else {
        const idx = dataList.value.findIndex((u) => u.id === form.id)
        if (idx !== -1) {
          dataList.value[idx] = { ...dataList.value[idx], ...JSON.parse(JSON.stringify(form)) }
        }
        ElMessage.success('修改物料成功')
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
