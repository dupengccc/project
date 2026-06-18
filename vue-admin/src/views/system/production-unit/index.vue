<template>
  <div class="production-unit-page">
    <!-- 左侧组织树 -->
    <div class="left-tree">
      <div class="tree-header">
        <span>组织架构</span>
        <el-button link type="primary" size="small" @click="handleRefreshTree">
          <el-icon><Refresh /></el-icon>
        </el-button>
      </div>
      <el-input
        v-model="filterText"
        placeholder="搜索组织"
        clearable
        style="margin-bottom: 10px"
      />
      <el-tree
        ref="orgTreeRef"
        :data="orgTree"
        :props="{ label: 'label', children: 'children' }"
        node-key="id"
        highlight-current
        default-expand-all
        :filter-node-method="filterNode"
        @node-click="handleOrgClick"
        style="max-height: calc(100vh - 260px); overflow: auto"
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            <el-icon v-if="data.children && data.children.length"><OfficeBuilding /></el-icon>
            <el-icon v-else><Document /></el-icon>
            <span style="margin-left: 4px">{{ node.label }}</span>
          </span>
        </template>
      </el-tree>
    </div>

    <!-- 右侧内容区 -->
    <div class="right-content">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" @submit.prevent>
          <el-form-item label="单元编码">
            <el-input v-model="queryForm.unitCode" placeholder="模糊搜索" clearable style="width: 140px" />
          </el-form-item>
          <el-form-item label="单元名称">
            <el-input v-model="queryForm.unitName" placeholder="模糊搜索" clearable style="width: 140px" />
          </el-form-item>
          <el-form-item label="单元类型">
            <el-select v-model="queryForm.unitType" placeholder="全部" clearable style="width: 120px">
              <el-option label="产线" value="产线" />
              <el-option label="工作中心" value="工作中心" />
              <el-option label="工段" value="工段" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 100px">
              <el-option label="启用" :value="0" />
              <el-option label="停用" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadList">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <div>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增
          </el-button>
          <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
            <el-icon><Delete /></el-icon>批量删除
          </el-button>
        </div>
      </div>

      <!-- 当前选中组织提示 -->
      <div v-if="currentOrg" class="org-tip">
        <el-tag type="info" closable @close="handleClearOrg">
          当前组织：{{ currentOrg.label }}
        </el-tag>
      </div>

      <!-- 数据表格 -->
      <el-table
        :data="tableData"
        border
        stripe
        v-loading="loading"
        height="calc(100vh - 320px)"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="unitCode" label="单元编码" width="140" />
        <el-table-column prop="unitName" label="单元名称" width="160" />
        <el-table-column prop="unitType" label="单元类型" width="100">
          <template #default="{ row }">
            <el-tag :type="unitTypeTag(row.unitType)" size="small">{{ row.unitType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orgName" label="所属组织" width="150" />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="success" size="small">启用</el-tag>
            <el-tag v-else type="info" size="small">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '新增生产单元' : '编辑生产单元'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="单元编码" prop="unitCode">
          <el-input v-model="form.unitCode" maxlength="64" placeholder="如 PU001" />
        </el-form-item>
        <el-form-item label="单元名称" prop="unitName">
          <el-input v-model="form.unitName" maxlength="128" placeholder="如 总装产线" />
        </el-form-item>
        <el-form-item label="单元类型" prop="unitType">
          <el-select v-model="form.unitType" placeholder="请选择" style="width: 100%">
            <el-option label="产线" value="产线" />
            <el-option label="工作中心" value="工作中心" />
            <el-option label="工段" value="工段" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属组织" prop="orgId">
          <el-tree-select
            v-model="form.orgId"
            :data="orgTree"
            :props="{ label: 'label', children: 'children', value: 'id' }"
            placeholder="请选择组织"
            check-strictly
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">启用</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" maxlength="500" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listProductionUnits, createProductionUnit, updateProductionUnit,
  deleteProductionUnit, deleteProductionUnitBatch
} from '@/api/production-unit'
import { getOrgTree } from '@/api/org'

const loading = ref(false)
const tableData = ref([])
const selectedIds = ref([])
const orgTree = ref([])
const orgTreeRef = ref()
const filterText = ref('')
const currentOrg = ref(null)

const queryForm = reactive({
  unitCode: '',
  unitName: '',
  unitType: '',
  status: null,
  orgId: null
})

const dialogVisible = ref(false)
const dialogMode = ref('add')
const formRef = ref()
const form = reactive({
  id: null,
  unitCode: '',
  unitName: '',
  unitType: '',
  orgId: null,
  sort: 0,
  status: 0,
  remark: ''
})

const rules = {
  unitCode: [{ required: true, message: '请输入单元编码', trigger: 'blur' }],
  unitName: [{ required: true, message: '请输入单元名称', trigger: 'blur' }],
  unitType: [{ required: true, message: '请选择单元类型', trigger: 'change' }],
  orgId: [{ required: true, message: '请选择所属组织', trigger: 'change' }]
}

function unitTypeTag(type) {
  const map = { '产线': 'primary', '工作中心': 'success', '工段': 'warning' }
  return map[type] || ''
}

// 过滤组织树
watch(filterText, (val) => {
  orgTreeRef.value?.filter(val)
})

function filterNode(value, data) {
  if (!value) return true
  return data.label.includes(value)
}

// 初始化
onMounted(async () => {
  await loadOrgTree()
  loadList()
})

async function loadOrgTree() {
  try {
    const res = await getOrgTree()
    if (res && res.code === 0 && res.data && res.data.length > 0) {
      orgTree.value = res.data
      return
    }
  } catch (e) {}
  // Mock 数据
  orgTree.value = [
    {
      id: 1, label: 'MES 集团总部',
      children: [
        {
          id: 2, label: '华东分公司',
          children: [
            { id: 4, label: '研发部', children: [] },
            { id: 5, label: '生产部', children: [] }
          ]
        },
        {
          id: 3, label: '华南分公司',
          children: [
            { id: 6, label: '销售部', children: [] },
            { id: 7, label: '售后部', children: [] }
          ]
        }
      ]
    }
  ]
}

async function loadList() {
  loading.value = true
  const params = { ...queryForm }
  if (currentOrg.value) {
    params.orgId = currentOrg.value.id
  }
  try {
    const res = await listProductionUnits(params)
    if (res && res.code === 0 && Array.isArray(res.data)) {
      tableData.value = res.data
      loading.value = false
      return
    }
  } catch (e) {}
  // Mock 数据
  let mock = [
    { id: 1, unitCode: 'PU001', unitName: '总装产线', unitType: '产线', orgId: 2, orgName: '华东分公司', status: 0, sort: 1, remark: '主装配线' },
    { id: 2, unitCode: 'PU002', unitName: '焊接产线', unitType: '产线', orgId: 2, orgName: '华东分公司', status: 0, sort: 2, remark: '焊接作业线' },
    { id: 3, unitCode: 'PU003', unitName: '喷涂产线', unitType: '产线', orgId: 2, orgName: '华东分公司', status: 0, sort: 3, remark: '表面处理线' },
    { id: 4, unitCode: 'PU004', unitName: '检测中心', unitType: '工作中心', orgId: 2, orgName: '华东分公司', status: 0, sort: 4, remark: '质量检测' },
    { id: 5, unitCode: 'PU005', unitName: '包装工段', unitType: '工段', orgId: 3, orgName: '华南分公司', status: 0, sort: 1, remark: '成品包装' },
    { id: 6, unitCode: 'PU006', unitName: 'SMT产线', unitType: '产线', orgId: 3, orgName: '华南分公司', status: 0, sort: 2, remark: '贴片线' }
  ]
  if (params.orgId) {
    mock = mock.filter(m => m.orgId === params.orgId)
  }
  if (params.unitCode) {
    mock = mock.filter(m => m.unitCode.includes(params.unitCode))
  }
  if (params.unitName) {
    mock = mock.filter(m => m.unitName.includes(params.unitName))
  }
  if (params.unitType) {
    mock = mock.filter(m => m.unitType === params.unitType)
  }
  if (params.status !== null && params.status !== undefined) {
    mock = mock.filter(m => m.status === params.status)
  }
  tableData.value = mock
  loading.value = false
}

function handleOrgClick(data) {
  currentOrg.value = data
  queryForm.orgId = data.id
  loadList()
}

function handleClearOrg() {
  currentOrg.value = null
  queryForm.orgId = null
  loadList()
}

function handleRefreshTree() {
  loadOrgTree()
}

function handleReset() {
  queryForm.unitCode = ''
  queryForm.unitName = ''
  queryForm.unitType = ''
  queryForm.status = null
  queryForm.orgId = currentOrg.value ? currentOrg.value.id : null
  loadList()
}

function handleSelectionChange(val) {
  selectedIds.value = val.map(item => item.id)
}

function handleAdd() {
  dialogMode.value = 'add'
  Object.assign(form, {
    id: null, unitCode: '', unitName: '', unitType: '',
    orgId: currentOrg.value ? currentOrg.value.id : null,
    sort: 0, status: 0, remark: ''
  })
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogMode.value = 'edit'
  Object.assign(form, {
    id: row.id,
    unitCode: row.unitCode,
    unitName: row.unitName,
    unitType: row.unitType,
    orgId: row.orgId,
    sort: row.sort ?? 0,
    status: row.status,
    remark: row.remark || ''
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      const res = dialogMode.value === 'add'
        ? await createProductionUnit(form)
        : await updateProductionUnit(form)
      if (res && res.code === 0) {
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadList()
        return
      }
    } catch (e) {}
    // Mock 保存
    if (dialogMode.value === 'add') {
      tableData.value.unshift({ ...form, id: Date.now(), orgName: '模拟组织' })
    } else {
      const idx = tableData.value.findIndex(m => m.id === form.id)
      if (idx >= 0) {
        tableData.value[idx] = { ...tableData.value[idx], ...form }
      }
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
  })
}

async function handleDelete(row) {
  try {
    const res = await deleteProductionUnit(row.id)
    if (res && res.code === 0) {
      ElMessage.success('删除成功')
      loadList()
      return
    }
  } catch (e) {}
  tableData.value = tableData.value.filter(m => m.id !== row.id)
  ElMessage.success('删除成功')
}

async function handleBatchDelete() {
  if (selectedIds.value.length === 0) return
  try {
    const res = await deleteProductionUnitBatch(selectedIds.value)
    if (res && res.code === 0) {
      ElMessage.success('批量删除成功')
      loadList()
      return
    }
  } catch (e) {}
  tableData.value = tableData.value.filter(m => !selectedIds.value.includes(m.id))
  ElMessage.success('批量删除成功')
}
</script>

<style scoped>
.production-unit-page {
  display: flex;
  height: calc(100vh - 100px);
  gap: 12px;
  padding: 12px 16px;
}
.left-tree {
  width: 260px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 12px;
  flex-shrink: 0;
}
.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}
.tree-node {
  display: flex;
  align-items: center;
}
.right-content {
  flex: 1;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 12px;
  overflow: hidden;
}
.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}
.org-tip {
  margin-bottom: 10px;
}
</style>
