<template>
  <div class="org-container">
    <!-- 左侧：组织树 -->
    <div class="org-tree-panel">
      <div class="tree-header">
        <span>组织架构</span>
        <el-button type="primary" size="small" @click="openAddDialog(null)">
          <el-icon><Plus /></el-icon>
          新增
        </el-button>
      </div>
      <div class="tree-filter">
        <el-input
          v-model="filterText"
          placeholder="搜索组织名称 / 组织编码"
          prefix-icon="Search"
          clearable
          size="small"
        />
      </div>
      <el-scrollbar class="tree-scrollbar">
        <el-tree
          ref="treeRef"
          :data="filteredTree"
          :props="{ label: 'name', children: 'children' }"
          node-key="id"
          :expand-on-click-node="false"
          :default-expand-all="true"
          highlight-current
          @node-click="handleNodeClick"
        >
          <template #default="{ node, data }">
            <div class="tree-node">
              <span class="node-name">
                <el-icon>
                  <OfficeBuilding v-if="data.type === 'group'" />
                  <Office v-else-if="data.type === 'branch'" />
                  <Grid v-else />
                </el-icon>
                {{ node.label }}
                <span class="node-code">{{ data.code ? '(' + data.code + ')' : '' }}</span>
              </span>
              <span class="node-actions" @click.stop>
                <el-tooltip content="新增子组织" placement="top">
                  <el-icon @click="openAddChildDialog(data)"><Plus /></el-icon>
                </el-tooltip>
                <el-tooltip content="编辑" placement="top">
                  <el-icon @click="openEditDialog(data)"><Edit /></el-icon>
                </el-tooltip>
                <el-tooltip content="删除" placement="top">
                  <el-icon @click="handleDelete(data)"><Delete /></el-icon>
                </el-tooltip>
              </span>
            </div>
          </template>
        </el-tree>
      </el-scrollbar>
    </div>

    <!-- 右侧：详情面板 -->
    <div class="org-detail-panel">
      <el-card v-if="selectedNode">
        <template #header>
          <div class="detail-header">
            <span>组织详情</span>
            <div>
              <el-button size="small" type="primary" @click="openEditDialog(selectedNode)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button size="small" type="success" @click="openAddChildDialog(selectedNode)">
                <el-icon><Plus /></el-icon> 新增子组织
              </el-button>
            </div>
          </div>
        </template>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="组织名称">{{ selectedNode.name }}</el-descriptions-item>
          <el-descriptions-item label="组织编码">{{ selectedNode.code || '—' }}</el-descriptions-item>
          <el-descriptions-item label="组织类型">
            <el-tag :type="typeTag(selectedNode.type)">{{ typeLabel(selectedNode.type) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="排序">{{ selectedNode.sort }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="selectedNode.status === 0 ? 'success' : 'info'">
              {{ selectedNode.status === 0 ? '启用' : '停用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="上级组织">{{ selectedNode.parentName || '无' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ selectedNode.createTime || '—' }}</el-descriptions-item>
          <el-descriptions-item label="创建人">{{ selectedNode.creator || '—' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      <el-empty v-else description="请从左侧选择一个组织" />
    </div>

    <!-- 新增 / 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '新增组织' : '编辑组织'"
      width="500px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="上级组织">
          <el-tree-select
            v-model="form.parentId"
            :data="treeSelectData"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            check-strictly
            clearable
            placeholder="顶级组织（不选则为根节点）"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="组织名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入组织名称" />
        </el-form-item>
        <el-form-item label="组织编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入组织编码" />
        </el-form-item>
        <el-form-item label="组织类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择组织类型" style="width: 100%">
            <el-option label="集团" value="group" />
            <el-option label="分公司" value="branch" />
            <el-option label="部门" value="dept" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">启用</el-radio>
            <el-radio :value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useOrgStore } from '@/store/modules/org'
import { createOrg, updateOrg, deleteOrg } from '@/api/org'

const orgStore = useOrgStore()
const treeRef = ref()
const orgTree = computed(() => orgStore.tree)

const selectedNode = ref(null)
const dialogVisible = ref(false)
const dialogMode = ref('add')
const submitLoading = ref(false)
const formRef = ref()
const filterText = ref('')

const form = reactive({
  id: null,
  parentId: null,
  name: '',
  code: '',
  type: 'dept',
  sort: 0,
  status: 0,
  creator: ''
})

const rules = {
  name: [{ required: true, message: '请输入组织名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择组织类型', trigger: 'change' }]
}

// 将树数据转为一维数组（用于 el-tree-select）
const treeSelectData = computed(() => {
  return [{ id: null, name: '无（顶级）', children: orgTree.value }]
})

// 筛选：按名称或编码过滤（递归保留匹配节点的完整祖先链）
const filteredTree = computed(() => {
  if (!filterText.value) return orgTree.value
  const kw = filterText.value.trim().toLowerCase()
  const filterNode = (nodes) => {
    return nodes
      .map((n) => {
        const matched = !!(n.name || '').toLowerCase().includes(kw) || !!(n.code || '').toLowerCase().includes(kw)
        const filteredChildren = filterNode(n.children || [])
        if (matched || filteredChildren.length) {
          return { ...n, children: filteredChildren }
        }
        return null
      })
      .filter(Boolean)
  }
  return filterNode(orgTree.value)
})

function handleNodeClick(data) {
  selectedNode.value = data
}

function findParentPath(targetId, nodes, path = null) {
  for (const node of nodes) {
    if (node.id === targetId) return true
    if (node.children?.length) {
      const found = findParentPath(targetId, node.children, path)
      if (found) {
        selectedNode.value = node
        return true
      }
    }
  }
  return false
}

function openAddDialog(parent) {
  dialogMode.value = 'add'
  form.id = null
  form.parentId = parent ? parent.id : null
  form.name = ''
  form.code = ''
  form.type = parent ? 'dept' : 'group'
  form.sort = 0
  form.status = 0
  form.creator = '当前用户'
  dialogVisible.value = true
}

function openAddChildDialog(parent) {
  dialogMode.value = 'add'
  form.id = null
  form.parentId = parent.id
  form.name = ''
  form.code = ''
  form.type = 'dept'
  form.sort = (parent.children?.length || 0) + 1
  form.status = 0
  form.creator = '当前用户'
  dialogVisible.value = true
}

function openEditDialog(node) {
  dialogMode.value = 'edit'
  form.id = node.id
  form.parentId = node.parentId
  form.name = node.name
  form.code = node.code || ''
  form.type = node.type
  form.sort = node.sort
  form.status = node.status
  form.creator = node.creator || ''
  dialogVisible.value = true
}

function resetForm() {
  formRef.value?.resetFields()
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (dialogMode.value === 'add') {
        await createOrg(form)
        ElMessage.success('新增成功')
      } else {
        await updateOrg(form)
        ElMessage.success('修改成功')
      }
      dialogVisible.value = false
      await orgStore.loadTree()
    } catch (e) {
      // 后端未就绪，本地模拟操作
      const tree = JSON.parse(JSON.stringify(orgStore.tree))
      if (dialogMode.value === 'add') {
        const newNode = {
          id: Date.now(),
          name: form.name,
          code: form.code,
          type: form.type,
          sort: form.sort,
          status: form.status,
          createTime: formatTime(new Date()),
          creator: form.creator,
          children: []
        }
        if (form.parentId) {
          const addToParent = (nodes) => {
            for (const n of nodes) {
              if (n.id === form.parentId) {
                n.children = n.children || []
                n.children.push(newNode)
                return true
              }
              if (n.children?.length && addToParent(n.children)) return true
            }
            return false
          }
          addToParent(tree)
        } else {
          tree.push(newNode)
        }
        orgStore.tree = tree
        ElMessage.success(dialogMode.value === 'add' ? '新增成功' : '修改成功')
        dialogVisible.value = false
      } else {
        const updateNode = (nodes) => {
          for (const n of nodes) {
            if (n.id === form.id) {
              Object.assign(n, {
                name: form.name,
                code: form.code,
                type: form.type,
                sort: form.sort,
                status: form.status,
                creator: form.creator
              })
              return true
            }
            if (n.children?.length && updateNode(n.children)) return true
          }
          return false
        }
        updateNode(tree)
        orgStore.tree = tree
        ElMessage.success('修改成功')
        dialogVisible.value = false
        selectedNode.value = { ...selectedNode.value, ...form }
      }
    } finally {
      submitLoading.value = false
    }
  })
}

async function handleDelete(node) {
  const hasChildren = node.children?.length > 0
  const msg = hasChildren
    ? `该组织下存在子组织（${node.children.length} 个），确认删除整棵子树吗？`
    : `确认删除组织「${node.name}」吗？`

  await ElMessageBox.confirm(msg, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  try {
    await deleteOrg(node.id)
  } catch (e) {
    // 本地模拟删除
  }

  const removeNode = (nodes) => {
    const idx = nodes.findIndex((n) => n.id === node.id)
    if (idx !== -1) {
      nodes.splice(idx, 1)
      return true
    }
    for (const n of nodes) {
      if (n.children?.length && removeNode(n.children)) return true
    }
    return false
  }
  const tree = JSON.parse(JSON.stringify(orgStore.tree))
  removeNode(tree)
  orgStore.tree = tree

  if (selectedNode.value?.id === node.id) {
    selectedNode.value = null
  }

  ElMessage.success('删除成功')
}

function typeLabel(type) {
  return { group: '集团', branch: '分公司', dept: '部门' }[type] || type
}

function typeTag(type) {
  return { group: 'primary', branch: 'success', dept: 'warning' }[type] || ''
}

function formatTime(date) {
  const pad = (n) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

onMounted(() => {
  orgStore.loadTree()
})
</script>

<style lang="scss" scoped>
.org-container {
  display: flex;
  height: calc(100vh - 120px);
  gap: 12px;
}
.org-tree-panel {
  width: 320px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
}
.tree-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
  font-weight: 600;
  color: #303133;
}
.tree-filter {
  padding: 8px 12px;
  border-bottom: 1px solid #ebeef5;
}
.tree-scrollbar {
  flex: 1;
  padding: 8px;
}
.tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding-right: 4px;
}
.node-name {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}
.node-code {
  color: #909399;
  font-size: 12px;
}
.node-actions {
  display: flex;
  align-items: center;
  gap: 4px;
  .el-icon {
    cursor: pointer;
    color: #409eff;
    padding: 2px;
    border-radius: 3px;
    &:hover {
      background: #ecf5ff;
    }
    &:nth-child(2) {
      color: #67c23a;
    }
    &:nth-child(3) {
      color: #f56c6c;
    }
  }
}
.org-detail-panel {
  flex: 1;
  overflow: auto;
}
.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
}
</style>
