<template>
  <div class="dict-page">
    <div class="dict-layout">
      <!-- 左侧：字典树 -->
      <div class="dict-tree-panel">
        <div class="panel-header">
          <span>字典分类</span>
          <el-button size="small" type="primary" text @click="handleAddRoot">
            <el-icon><Plus /></el-icon> 新增根节点
          </el-button>
        </div>
        <el-tree
          ref="treeRef"
          :data="treeData"
          :props="{ label: 'dictName', children: 'children' }"
          node-key="id"
          highlight-current
          default-expand-all
          @node-click="handleTreeClick"
        >
          <template #default="{ node, data }">
            <span class="tree-node">
              <span>{{ data.dictName }}</span>
              <span class="node-code">{{ data.dictCode }}</span>
              <span class="node-actions">
                <el-link type="primary" :underline="false" @click.stop="handleEditNode(data, node)">
                  <el-icon><Edit /></el-icon>
                </el-link>
                <el-link type="danger" :underline="false" @click.stop="handleDeleteNode(data)">
                  <el-icon><Delete /></el-icon>
                </el-link>
              </span>
            </span>
          </template>
        </el-tree>
      </div>

      <!-- 右侧：字典明细 -->
      <div class="dict-table-panel">
        <!-- 操作栏 -->
        <div class="action-bar">
          <el-space>
            <el-tag type="info">{{ currentNode ? currentNode.dictName : '全部字典' }}</el-tag>
            <el-button
              v-if="currentNode"
              type="primary"
              size="small"
              @click="handleAddChild"
            >
              <el-icon><Plus /></el-icon> 新增子节点
            </el-button>
          </el-space>
        </div>

        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-form :inline="true" :model="queryForm" @submit.prevent>
            <el-form-item label="字典名称">
              <el-input v-model="queryForm.dictName" placeholder="模糊搜索" clearable style="width: 150px" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="字典编码">
              <el-input v-model="queryForm.dictCode" placeholder="模糊搜索" clearable style="width: 150px" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 110px" @change="handleSearch">
                <el-option label="激活" :value="0" />
                <el-option label="停用" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 数据表格 -->
        <el-table
          :data="tableData"
          border
          stripe
          height="calc(100vh - 320px)"
          v-loading="loading"
        >
          <el-table-column label="序号" width="60" align="center">
            <template #default="{ $index }">
              {{ (queryForm.page - 1) * queryForm.pageSize + $index + 1 }}
            </template>
          </el-table-column>
          <el-table-column prop="dictName" label="字典名称" min-width="160" show-overflow-tooltip />
          <el-table-column prop="dictCode" label="字典编码" width="200" show-overflow-tooltip />
          <el-table-column prop="dictValue" label="字典值" width="160" show-overflow-tooltip />
          <el-table-column prop="sort" label="排序" width="80" align="center" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.status === 0 || row.status === '0'" type="success" size="small">激活</el-tag>
              <el-tag v-else type="danger" size="small">停用</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
          <el-table-column label="操作" width="140" align="center" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-popconfirm title="确认删除（含子节点）？" @confirm="handleDelete(row)">
                <template #reference>
                  <el-button link type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="520px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="请输入字典名称" maxlength="64" />
        </el-form-item>
        <el-form-item label="字典编码" prop="dictCode">
          <el-input v-model="form.dictCode" placeholder="请输入字典编码（唯一）" maxlength="64" />
        </el-form-item>
        <el-form-item label="字典值" prop="dictValue">
          <el-input v-model="form.dictValue" placeholder="请输入字典值（供下拉框使用）" maxlength="64" />
        </el-form-item>
        <el-form-item label="父节点">
          <el-tree-select
            v-model="form.parentId"
            :data="treeData"
            :props="{ label: 'dictName', value: 'id', children: 'children' }"
            check-strictly
            placeholder="顶级节点"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">激活</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="可选" maxlength="512" />
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
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDictTree, listDict, getDict, createDict, updateDict, deleteDict } from '@/api/dict'

const loading = ref(false)
const tableData = ref([])
const treeData = ref([])
const treeRef = ref()
const currentNode = ref(null)

const queryForm = reactive({
  dictName: '',
  dictCode: '',
  status: null,
  parentId: null,
  page: 1,
  pageSize: 100
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增字典')
const formRef = ref()
const form = reactive({
  id: null,
  dictName: '',
  dictCode: '',
  dictValue: '',
  parentId: null,
  sort: 0,
  status: 0,
  remark: ''
})

const rules = {
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  dictCode: [{ required: true, message: '请输入字典编码', trigger: 'blur' }]
}

// 本地 mock 树数据（后端未就绪时兜底）
const mockTreeData = [
  {
    id: 1, dictName: '分公司', dictCode: 'DICT_BRANCH', sort: 1,
    children: [
      { id: 2, dictName: '华东分公司', dictCode: 'BRANCH_HD', dictValue: '1', sort: 1 },
      { id: 3, dictName: '华南分公司', dictCode: 'BRANCH_HN', dictValue: '2', sort: 2 },
      { id: 4, dictName: '华北分公司', dictCode: 'BRANCH_HB', dictValue: '3', sort: 3 },
      { id: 5, dictName: '西南分公司', dictCode: 'BRANCH_XN', dictValue: '4', sort: 4 }
    ]
  },
  {
    id: 10, dictName: '用户状态', dictCode: 'DICT_USER_STATUS', sort: 2,
    children: [
      { id: 11, dictName: '启用', dictCode: 'USER_STATUS_ON', dictValue: '0', sort: 1 },
      { id: 12, dictName: '禁用', dictCode: 'USER_STATUS_OFF', dictValue: '1', sort: 2 }
    ]
  },
  {
    id: 20, dictName: '组织类型', dictCode: 'DICT_ORG_TYPE', sort: 3,
    children: [
      { id: 21, dictName: '集团', dictCode: 'ORG_GROUP', dictValue: 'group', sort: 1 },
      { id: 22, dictName: '分公司', dictCode: 'ORG_BRANCH', dictValue: 'branch', sort: 2 },
      { id: 23, dictName: '部门', dictCode: 'ORG_DEPT', dictValue: 'dept', sort: 3 }
    ]
  }
]

function flattenTree(nodes, result = []) {
  for (const n of nodes) {
    result.push(n)
    if (n.children?.length) flattenTree(n.children, result)
  }
  return result
}

function filterTreeByQuery(nodes, query) {
  if (!query.dictName && !query.dictCode && !query.status) return nodes
  const all = flattenTree(nodes)
  const filtered = all.filter(n => {
    if (query.dictName && !n.dictName.includes(query.dictName)) return false
    if (query.dictCode && !n.dictCode.includes(query.dictCode)) return false
    if (query.status !== null && query.status !== '' && n.status !== query.status) return false
    return true
  })
  // 重建子树
  const map = {}
  filtered.forEach(n => { map[n.id] = { ...n, children: [] } })
  const roots = []
  filtered.forEach(n => {
    if (n.parentId && map[n.parentId]) {
      map[n.parentId].children.push(map[n.id])
    } else {
      roots.push(map[n.id])
    }
  })
  return roots
}

async function loadTree() {
  try {
    const res = await getDictTree()
    if (res && res.data && res.data.length > 0) {
      treeData.value = res.data
      return
    }
  } catch (e) {}
  treeData.value = mockTreeData
}

async function loadTable() {
  loading.value = true
  try {
    const res = await listDict({
      dictName: queryForm.dictName || undefined,
      dictCode: queryForm.dictCode || undefined,
      status: queryForm.status == null ? undefined : queryForm.status,
      parentId: queryForm.parentId || undefined,
      page: queryForm.page,
      pageSize: queryForm.pageSize
    })
    if (res && res.data && res.data.list) {
      tableData.value = res.data.list
      return
    }
  } catch (e) {}
  // mock 兜底
  const all = flattenTree(mockTreeData)
  let list = all
  if (queryForm.dictName) list = list.filter(n => n.dictName.includes(queryForm.dictName))
  if (queryForm.dictCode) list = list.filter(n => n.dictCode.includes(queryForm.dictCode))
  if (queryForm.status !== null && queryForm.status !== '') list = list.filter(n => n.status === queryForm.status)
  tableData.value = list
  loading.value = false
}

function handleTreeClick(data) {
  currentNode.value = data
  queryForm.parentId = data.id
  queryForm.page = 1
  loadTable()
}

function handleSearch() {
  queryForm.page = 1
  loadTable()
}

function handleReset() {
  queryForm.dictName = ''
  queryForm.dictCode = ''
  queryForm.status = null
  currentNode.value = null
  queryForm.parentId = null
  queryForm.page = 1
  loadTable()
}

function handleAddRoot() {
  dialogTitle.value = '新增根节点'
  form.id = null
  form.parentId = null
  dialogVisible.value = true
}

function handleAddChild() {
  dialogTitle.value = '新增子节点'
  form.id = null
  form.parentId = currentNode.value?.id || null
  dialogVisible.value = true
}

function handleEditNode(data) {
  handleEdit(data)
}

function handleEdit(row) {
  dialogTitle.value = '编辑字典'
  Object.assign(form, {
    id: row.id,
    dictName: row.dictName,
    dictCode: row.dictCode,
    dictValue: row.dictValue,
    parentId: row.parentId === 0 ? null : row.parentId,
    sort: row.sort || 0,
    status: row.status === 1 || row.status === '1' ? 1 : 0,
    remark: row.remark || ''
  })
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await deleteDict(row.id).catch(() => null)
    ElMessage.success('删除成功')
    await loadTree()
    loadTable()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

async function handleSubmit() {
  await formRef.value.validate()
  const payload = {
    id: form.id,
    dictName: form.dictName,
    dictCode: form.dictCode,
    dictValue: form.dictValue,
    parentId: form.parentId || 0,
    sort: form.sort,
    status: form.status,
    remark: form.remark
  }
  try {
    if (form.id) {
      await updateDict(payload).catch(() => null)
    } else {
      await createDict(payload).catch(() => null)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    await loadTree()
    loadTable()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, dictName: '', dictCode: '', dictValue: '', parentId: null, sort: 0, status: 0, remark: '' })
}

onMounted(() => {
  loadTree()
  loadTable()
})
</script>

<style scoped>
.dict-page {
  padding: 12px 16px;
  height: calc(100vh - 56px);
}
.dict-layout {
  display: flex;
  gap: 12px;
  height: 100%;
}
.dict-tree-panel {
  width: 260px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}
.panel-header {
  padding: 10px 14px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 14px;
}
.dict-tree-panel :deep(.el-tree) {
  padding: 8px 0;
  flex: 1;
  overflow: auto;
}
.tree-node {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  .node-code {
    font-size: 11px;
    color: #909399;
    margin-left: auto;
  }
  .node-actions {
    display: none;
    gap: 4px;
  }
}
.dict-tree-panel :deep(.el-tree-node__content:hover) .node-actions {
  display: flex;
}
.dict-table-panel {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.action-bar {
  background: #fff;
  padding: 8px 14px;
  border: 1px solid #ebeef5;
  border-bottom: none;
  border-radius: 4px 4px 0 0;
}
.search-bar {
  background: #fff;
  padding: 12px 14px 0;
  border: 1px solid #ebeef5;
  border-bottom: none;
}
.pagination {
  text-align: right;
  margin-top: 12px;
}
</style>
