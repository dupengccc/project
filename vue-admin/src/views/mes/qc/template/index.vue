<template>
  <div class="page-wrapper">
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" class="search-form">
          <el-form-item label="模板编码">
            <el-input v-model="queryForm.code" placeholder="请输入模板编码" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="模板名称">
            <el-input v-model="queryForm.name" placeholder="请输入模板名称" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="产品类型">
            <el-select v-model="queryForm.productType" placeholder="请选择" clearable style="width: 160px">
              <el-option v-for="p in productOptions" :key="p" :label="p" :value="p" />
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
          <el-icon><Plus /></el-icon>新增模板
        </el-button>
      </div>

      <el-table :data="pageList" border stripe style="width: 100%; margin-top: 12px">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="模板编码" prop="code" width="140" />
        <el-table-column label="模板名称" prop="name" width="180" />
        <el-table-column label="产品类型" prop="productType" width="140" />
        <el-table-column label="检验项数" prop="itemCount" width="110" align="center" />
        <el-table-column label="版本" prop="version" width="100" align="center" />
        <el-table-column label="创建人" prop="creator" width="110" align="center" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'info'" size="small">{{ row.status ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该模板？" @confirm="handleDelete(row)">
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
      :title="dialogMode === 'add' ? '新增检验模板' : '编辑检验模板'"
      width="760px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="模板编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入模板编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模板名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入模板名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="产品类型" prop="productType">
              <el-select v-model="form.productType" placeholder="请选择" style="width: 100%">
                <el-option v-for="p in productOptions" :key="p" :label="p" :value="p" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="版本号" prop="version">
              <el-input v-model="form.version" placeholder="如：V1.0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="检验项数" prop="itemCount">
              <el-input-number v-model="form.itemCount" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建人" prop="creator">
              <el-input v-model="form.creator" placeholder="请输入创建人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="24">
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

const productOptions = ['工控机箱', '触控一体机', '伺服电机', '精密零部件', '电路板']

const dataList = ref([
  { id: 1, code: 'TPL-001', name: '工控机箱出厂检验', productType: '工控机箱', itemCount: 12, version: 'V1.2', creator: '张工', status: true, description: '含外观、尺寸、电气性能等综合检验。' },
  { id: 2, code: 'TPL-002', name: '触控一体机装配检验', productType: '触控一体机', itemCount: 8, version: 'V1.0', creator: '李工', status: true, description: '外观、显示、触摸测试。' },
  { id: 3, code: 'TPL-003', name: '伺服电机出厂检验', productType: '伺服电机', itemCount: 15, version: 'V2.0', creator: '王工', status: true, description: '振动、噪音、绝缘、负载测试。' },
  { id: 4, code: 'TPL-004', name: '精密零部件尺寸检验', productType: '精密零部件', itemCount: 6, version: 'V1.1', creator: '赵工', status: true, description: '使用三坐标测量。' },
  { id: 5, code: 'TPL-005', name: '电路板工艺检验', productType: '电路板', itemCount: 10, version: 'V1.0', creator: '孙工', status: true, description: '焊接、导通、绝缘测试。' },
  { id: 6, code: 'TPL-006', name: '机箱外观检验', productType: '工控机箱', itemCount: 5, version: 'V1.0', creator: '周工', status: false, description: '仅外观检查。' }
])

const queryForm = reactive({
  code: '',
  name: '',
  productType: '',
  page: 1,
  pageSize: 10
})

const total = ref(dataList.value.length)

const filterList = computed(() => {
  const codeKw = normalize(queryForm.code)
  const nameKw = normalize(queryForm.name)
  const ptKw = normalize(queryForm.productType)
  const list = dataList.value.filter((r) => {
    const ok1 = codeKw ? normalize(r.code).includes(codeKw) : true
    const ok2 = nameKw ? normalize(r.name).includes(nameKw) : true
    const ok3 = ptKw ? normalize(r.productType) === ptKw : true
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
  queryForm.productType = ''
  queryForm.page = 1
}

const form = reactive({
  id: null,
  code: '',
  name: '',
  productType: '',
  itemCount: 0,
  version: '',
  creator: '',
  description: '',
  status: true
})

const rules = {
  code: [{ required: true, message: '请输入模板编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }]
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, code: '', name: '', productType: '', itemCount: 0, version: '', creator: '', description: '', status: true })
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
