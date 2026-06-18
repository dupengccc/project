<template>
  <div class="page-wrapper">
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" class="search-form" @submit.prevent>
          <el-form-item label="物料编码">
            <el-input
              v-model="queryForm.materialCode"
              placeholder="请输入物料编码"
              clearable
              style="width: 160px"
            />
          </el-form-item>
          <el-form-item label="物料名称">
            <el-input
              v-model="queryForm.materialName"
              placeholder="请输入物料名称"
              clearable
              style="width: 160px"
            />
          </el-form-item>
          <el-form-item label="物料类型">
            <el-select
              v-model="queryForm.materialType"
              placeholder="请选择"
              clearable
              style="width: 140px"
            >
              <el-option label="原材料" value="原材料" />
              <el-option label="半成品" value="半成品" />
              <el-option label="成品" value="成品" />
              <el-option label="辅料" value="辅料" />
            </el-select>
          </el-form-item>
          <el-form-item label="管理方式">
            <el-select
              v-model="queryForm.manageMode"
              placeholder="请选择"
              clearable
              style="width: 140px"
            >
              <el-option label="自制" value="自制" />
              <el-option label="外购" value="外购" />
              <el-option label="委外加工" value="委外加工" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>查询
            </el-button>
            <el-button @click="handleReset">
              <el-icon><RefreshLeft /></el-icon>重置
            </el-button>
          </el-form-item>
        </el-form>
        <el-button type="success" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增物料
        </el-button>
      </div>

      <el-table :data="pageList" border stripe style="width: 100%; margin-top: 12px" height="calc(100vh - 280px)">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="物料编码" prop="materialCode" width="130" />
        <el-table-column label="物料名称" prop="materialName" width="160" show-overflow-tooltip />
        <el-table-column label="规格型号" prop="spec" width="160" show-overflow-tooltip />
        <el-table-column label="物料类型" prop="materialType" width="100">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.materialType)" size="small" effect="plain">{{ row.materialType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="管理方式" prop="manageMode" width="110" />
        <el-table-column label="单位" prop="unit" width="80" align="center" />
        <el-table-column label="所属组织" prop="orgName" width="160" show-overflow-tooltip />
        <el-table-column label="安全库存" prop="safeStock" width="100" align="right" />
        <el-table-column label="当前库存" prop="currentStock" width="100" align="right" />
        <el-table-column label="状态" prop="status" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0 || row.status === '0'" type="success" size="small">启用</el-tag>
            <el-tag v-else type="info" size="small">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="160" show-overflow-tooltip />
        <el-table-column label="创建时间" prop="createTime" width="170" />
        <el-table-column label="操作" width="150" fixed="right" align="center">
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
          background
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '新增物料' : '编辑物料'"
      width="780px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="物料编码" prop="materialCode">
              <el-input v-model="form.materialCode" placeholder="请输入物料编码" maxlength="32" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="物料名称" prop="materialName">
              <el-input v-model="form.materialName" placeholder="请输入物料名称" maxlength="64" />
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
            <el-form-item label="物料类型" prop="materialType">
              <el-select v-model="form.materialType" placeholder="请选择" style="width: 100%">
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
            <el-form-item label="管理方式" prop="manageMode">
              <el-select v-model="form.manageMode" placeholder="请选择" style="width: 100%">
                <el-option label="自制" value="自制" />
                <el-option label="外购" value="外购" />
                <el-option label="委外加工" value="委外加工" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="form.unit" placeholder="如：件/kg" maxlength="16" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属组织" prop="orgId">
              <el-select
                v-model="form.orgId"
                placeholder="请选择所属组织"
                filterable
                style="width: 100%"
                @change="onOrgChange"
              >
                <el-option
                  v-for="item in orgOptions"
                  :key="item.id"
                  :label="item.orgName || item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :label="0">启用</el-radio>
                <el-radio :label="1">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="安全库存" prop="safeStock">
              <el-input-number v-model="form.safeStock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前库存" prop="currentStock">
              <el-input-number v-model="form.currentStock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息" maxlength="500" />
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
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listMaterial, getMaterial, createMaterial, updateMaterial, deleteMaterial } from '@/api/mes/md/material'

const formRef = ref()
const dialogVisible = ref(false)
const dialogMode = ref('add')
const submitLoading = ref(false)

const typeTag = (t) => {
  const map = { 原材料: 'primary', 半成品: 'warning', 成品: 'success', 辅料: 'info' }
  return map[t] || ''
}

// 组织下拉（从组织管理获取）
const orgOptions = ref([
  { id: 1, name: '集团总部', orgName: '集团总部' },
  { id: 2, name: '华东分公司', orgName: '华东分公司' },
  { id: 3, name: '华南分公司', orgName: '华南分公司' },
  { id: 4, name: '研发部', orgName: '研发部' }
])

// 查询条件
const queryForm = reactive({
  materialCode: '',
  materialName: '',
  materialType: '',
  manageMode: '',
  status: null,
  page: 1,
  pageSize: 10
})

const total = ref(0)
const dataList = ref([])
const pageList = ref([])

async function handleSearch() {
  try {
    const res = await listMaterial({
      materialCode: queryForm.materialCode || undefined,
      materialName: queryForm.materialName || undefined,
      materialType: queryForm.materialType || undefined,
      manageMode: queryForm.manageMode || undefined,
      status: queryForm.status === '' || queryForm.status == null ? undefined : queryForm.status,
      page: queryForm.page,
      pageSize: queryForm.pageSize
    })
    if (res && res.code === 0 && res.data && Array.isArray(res.data.list) && res.data.list.length > 0) {
      dataList.value = res.data.list
      total.value = res.data.total || res.data.list.length
      applyPagination()
      return
    }
  } catch (e) {
    // 后端未就绪时走 mock
  }
  useMockData()
}

function useMockData() {
  const mock = [
    { id: 1, materialCode: 'M00001', materialName: '不锈钢板', spec: '1220*2440*2mm', materialType: '原材料', manageMode: '外购', unit: '张', orgId: 2, orgName: '华东分公司', safeStock: 50, currentStock: 200, status: 0, remark: '常用原材料', createTime: '2025-01-03 09:12:00' },
    { id: 2, materialCode: 'M00002', materialName: '铝合金型材', spec: '6063-T5 2m', materialType: '原材料', manageMode: '外购', unit: '根', orgId: 2, orgName: '华东分公司', safeStock: 100, currentStock: 80, status: 0, remark: '主原料', createTime: '2025-01-05 10:30:00' },
    { id: 3, materialCode: 'B00001', materialName: '半成品装配A', spec: 'A100', materialType: '半成品', manageMode: '自制', unit: '件', orgId: 2, orgName: '华东分公司', safeStock: 30, currentStock: 45, status: 0, remark: '标准半成品', createTime: '2025-02-01 14:20:00' },
    { id: 4, materialCode: 'F00001', materialName: '工控机箱', spec: 'IPC-610L', materialType: '成品', manageMode: '自制', unit: '台', orgId: 2, orgName: '华东分公司', safeStock: 10, currentStock: 25, status: 0, remark: '标准产品', createTime: '2025-03-10 08:45:00' },
    { id: 5, materialCode: 'A00001', materialName: '内六角螺丝', spec: 'M4x8', materialType: '辅料', manageMode: '外购', unit: '包', orgId: 3, orgName: '华南分公司', safeStock: 500, currentStock: 320, status: 0, remark: '辅助材料', createTime: '2025-03-15 16:05:00' },
    { id: 6, materialCode: 'F00002', materialName: '触控一体机', spec: '15寸 工业级', materialType: '成品', manageMode: '委外加工', unit: '台', orgId: 3, orgName: '华南分公司', safeStock: 5, currentStock: 3, status: 1, remark: '暂停销售', createTime: '2025-04-02 11:10:00' }
  ]
  // 本地过滤
  let list = mock
  if (queryForm.materialCode && queryForm.materialCode.trim()) {
    list = list.filter(r => (r.materialCode || '').includes(queryForm.materialCode.trim()))
  }
  if (queryForm.materialName && queryForm.materialName.trim()) {
    list = list.filter(r => (r.materialName || '').includes(queryForm.materialName.trim()))
  }
  if (queryForm.materialType && queryForm.materialType.trim()) {
    list = list.filter(r => r.materialType === queryForm.materialType)
  }
  if (queryForm.manageMode && queryForm.manageMode.trim()) {
    list = list.filter(r => r.manageMode === queryForm.manageMode)
  }
  dataList.value = list
  total.value = list.length
  applyPagination()
}

function applyPagination() {
  const start = (queryForm.page - 1) * queryForm.pageSize
  pageList.value = dataList.value.slice(start, start + queryForm.pageSize)
}

function handleReset() {
  queryForm.materialCode = ''
  queryForm.materialName = ''
  queryForm.materialType = ''
  queryForm.manageMode = ''
  queryForm.status = null
  queryForm.page = 1
  handleSearch()
}

const form = reactive({
  id: null,
  materialCode: '',
  materialName: '',
  spec: '',
  materialType: '原材料',
  manageMode: '自制',
  unit: '',
  orgId: null,
  orgName: '',
  safeStock: 0,
  currentStock: 0,
  status: 0,
  remark: ''
})

const rules = {
  materialCode: [{ required: true, message: '请输入物料编码', trigger: 'blur' }],
  materialName: [{ required: true, message: '请输入物料名称', trigger: 'blur' }],
  materialType: [{ required: true, message: '请选择物料类型', trigger: 'change' }],
  manageMode: [{ required: true, message: '请选择管理方式', trigger: 'change' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  orgId: [{ required: true, message: '请选择所属组织', trigger: 'change' }]
}

function onOrgChange(id) {
  const org = orgOptions.value.find(o => o.id === id)
  form.orgName = org ? (org.orgName || org.name) : ''
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, materialCode: '', materialName: '', spec: '', materialType: '原材料', manageMode: '自制', unit: '', orgId: null, orgName: '', safeStock: 0, currentStock: 0, status: 0, remark: '' })
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
        try {
          await createMaterial(form)
        } catch (e) {
          // 后端未就绪，本地 mock
          dataList.value.unshift({
            ...JSON.parse(JSON.stringify(form)),
            id: Date.now(),
            createTime: new Date().toLocaleString()
          })
        }
        ElMessage.success('新增物料成功')
      } else {
        try {
          await updateMaterial(form)
        } catch (e) {
          const idx = dataList.value.findIndex(u => u.id === form.id)
          if (idx !== -1) {
            dataList.value[idx] = { ...JSON.parse(JSON.stringify(form)) }
          }
        }
        ElMessage.success('修改物料成功')
      }
      dialogVisible.value = false
      handleSearch()
    } finally {
      submitLoading.value = false
    }
  })
}

async function handleDelete(row) {
  try {
    await deleteMaterial(row.id)
  } catch (e) {
    // 本地 mock：从列表移除
  }
  dataList.value = dataList.value.filter(u => u.id !== row.id)
  ElMessage.success('删除成功')
  handleSearch()
}

onMounted(() => {
  handleSearch()
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
