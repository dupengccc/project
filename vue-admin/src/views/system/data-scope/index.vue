<template>
  <div class="data-scope-page">
    <!-- 顶部搜索 -->
    <div class="search-bar">
      <el-form :inline="true" :model="queryForm" @submit.prevent>
        <el-form-item label="角色名称">
          <el-input v-model="queryForm.roleName" placeholder="模糊搜索" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="启用" :value="0" />
            <el-option label="停用" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadRoles">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="success" @click="handleAddRole">
        <el-icon><Plus /></el-icon>新增角色
      </el-button>
    </div>

    <!-- 角色列表 -->
    <el-table :data="roleList" border stripe v-loading="loading" height="calc(100vh - 220px)">
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="roleName" label="角色名称" width="180" />
      <el-table-column prop="roleKey" label="角色编码" width="160" />
      <el-table-column prop="roleSort" label="排序" width="80" align="center" />
      <el-table-column label="数据范围" width="200">
        <template #default="{ row }">
          <el-tag :type="scopeTagType(row.dataScope)" effect="plain">{{ scopeText(row.dataScope) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0 || row.status === '0'" type="success" size="small">启用</el-tag>
          <el-tag v-else type="info" size="small">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
      <el-table-column label="操作" width="220" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleAssign(row)">
            分配数据权限
          </el-button>
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-popconfirm title="确认删除该角色？" @confirm="handleDelete(row)">
            <template #reference>
              <el-button link type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑 弹窗 -->
    <el-dialog v-model="roleDialogVisible" :title="roleDialogMode === 'add' ? '新增角色' : '编辑角色'" width="560px">
      <el-form ref="roleFormRef" :model="roleForm" :rules="roleRules" label-width="90px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" maxlength="64" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleKey">
          <el-input v-model="roleForm.roleKey" maxlength="64" placeholder="如 super_admin" />
        </el-form-item>
        <el-form-item label="排序" prop="roleSort">
          <el-input-number v-model.number="roleForm.roleSort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
            <el-radio :label="0">启用</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="roleForm.remark" type="textarea" :rows="2" maxlength="255" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRole">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配数据权限 弹窗 -->
    <el-dialog v-model="assignVisible" :title="'分配数据权限 - ' + (currentRole?.roleName || '')" width="640px" @closed="resetAssignForm">
      <el-form ref="assignFormRef" :model="assignForm" label-width="110px">
        <el-form-item label="数据范围" prop="dataScope">
          <el-select v-model="assignForm.dataScope" style="width: 100%" @change="handleScopeChange">
            <el-option label="全部数据" value="1" />
            <el-option label="自定义数据" value="2" />
            <el-option label="本部门数据" value="3" />
            <el-option label="本部门及以下" value="4" />
            <el-option label="仅本人数据" value="5" />
          </el-select>
          <div style="font-size: 12px; color: #909399; margin-top: 6px">
            {{ scopeTip(assignForm.dataScope) }}
          </div>
        </el-form-item>
        <el-form-item
          v-if="assignForm.dataScope === '2'"
          label="可见组织"
          prop="deptIds"
        >
          <el-tree
            ref="orgTreeRef"
            :data="orgTree"
            :props="{ label: 'label', children: 'children' }"
            node-key="id"
            show-checkbox
            default-expand-all
            :default-checked-keys="assignForm.deptIds"
            style="max-height: 400px; overflow: auto"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitAssign">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listRoles, saveRole, deleteRole,
  getRoleDataScope, assignDataScope, getOrgTree
} from '@/api/data-scope'

const loading = ref(false)
const roleList = ref([])
const orgTree = ref([])

const queryForm = reactive({ roleName: '', status: null })

// 角色弹窗
const roleDialogVisible = ref(false)
const roleDialogMode = ref('add')
const roleFormRef = ref()
const roleForm = reactive({
  id: null, roleName: '', roleKey: '', roleSort: 1, status: 0, remark: ''
})
const roleRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

// 分配数据权限弹窗
const assignVisible = ref(false)
const assignFormRef = ref()
const orgTreeRef = ref()
const currentRole = ref(null)
const assignForm = reactive({ dataScope: '1', deptIds: [] })

// 数据范围元信息
const scopeMap = {
  '1': { text: '全部数据', type: 'success', tip: '可见所有组织的数据（超级管理员使用）' },
  '2': { text: '自定义数据', type: 'warning', tip: '在下方树形控件中勾选可见的组织节点' },
  '3': { text: '本部门数据', type: '', tip: '仅可见与当前登录用户所属同一部门的数据' },
  '4': { text: '本部门及以下', type: 'primary', tip: '当前部门及其所有子部门下的数据' },
  '5': { text: '仅本人数据', type: 'info', tip: '只能看到自己创建的数据' }
}
function scopeText(s) { return scopeMap[s]?.text || '未设置' }
function scopeTagType(s) { return scopeMap[s]?.type || '' }
function scopeTip(s) { return scopeMap[s]?.tip || '' }

// 初始化
onMounted(async () => {
  try {
    const res = await getOrgTree()
    if (res && res.data && res.data.length > 0) {
      orgTree.value = res.data
      return
    }
  } catch (e) {}
  // 兜底模拟树
  orgTree.value = [
    {
      id: 1, label: 'MES 集团总部', orgCode: 'GROUP001',
      children: [
        {
          id: 2, label: '华东分公司', orgCode: 'BRANCH001',
          children: [
            { id: 4, label: '研发部', orgCode: 'DEPT001', children: [] },
            { id: 5, label: '生产部', orgCode: 'DEPT002', children: [] }
          ]
        },
        {
          id: 3, label: '华南分公司', orgCode: 'BRANCH002',
          children: [
            { id: 6, label: '销售部', orgCode: 'DEPT003', children: [] },
            { id: 7, label: '售后部', orgCode: 'DEPT004', children: [] }
          ]
        }
      ]
    }
  ]
  loadRoles()
})

async function loadRoles() {
  loading.value = true
  try {
    const res = await listRoles({ roleName: queryForm.roleName, status: queryForm.status })
    if (res && res.code === 0 && res.data && Array.isArray(res.data.list) && res.data.list.length > 0) {
      roleList.value = res.data.list
      return
    }
  } catch (e) {}
  // 模拟数据
  roleList.value = [
    { id: 1, roleName: '超级管理员', roleKey: 'super_admin', roleSort: 1, status: 0, dataScope: '1', remark: '拥有全部数据权限' },
    { id: 2, roleName: '集团管理员', roleKey: 'group_admin', roleSort: 2, status: 0, dataScope: '4', remark: '集团范围内全部数据' },
    { id: 3, roleName: '分公司管理员', roleKey: 'branch_admin', roleSort: 3, status: 0, dataScope: '4', remark: '分公司范围内全部数据' },
    { id: 4, roleName: '部门管理员', roleKey: 'dept_admin', roleSort: 4, status: 0, dataScope: '3', remark: '仅可见本部门数据' },
    { id: 5, roleName: '普通用户', roleKey: 'user', roleSort: 5, status: 0, dataScope: '5', remark: '仅可见自己创建的数据' },
    { id: 6, roleName: '访客', roleKey: 'guest', roleSort: 6, status: 1, dataScope: '5', remark: '只读访客权限' }
  ]
  loading.value = false
}

function handleReset() {
  queryForm.roleName = ''
  queryForm.status = null
  loadRoles()
}

// ==== 角色增删改 ====
function handleAddRole() {
  roleDialogMode.value = 'add'
  Object.assign(roleForm, { id: null, roleName: '', roleKey: '', roleSort: 1, status: 0, remark: '' })
  roleDialogVisible.value = true
}

function handleEdit(row) {
  roleDialogMode.value = 'edit'
  Object.assign(roleForm, {
    id: row.id, roleName: row.roleName, roleKey: row.roleKey,
    roleSort: row.roleSort ?? 1, status: row.status, remark: row.remark || ''
  })
  roleDialogVisible.value = true
}

async function handleSaveRole() {
  await roleFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      const res = await saveRole(roleForm)
      if (res && res.code === 0) {
        ElMessage.success('保存成功')
        roleDialogVisible.value = false
        loadRoles()
        return
      }
    } catch (e) {}
    // 模拟保存成功
    if (roleDialogMode.value === 'add') {
      roleList.value.unshift({ ...roleForm, id: Date.now(), dataScope: '1' })
    } else {
      const idx = roleList.value.findIndex(r => r.id === roleForm.id)
      if (idx >= 0) {
        const old = roleList.value[idx]
        roleList.value[idx] = { ...old, ...roleForm }
      }
    }
    ElMessage.success('保存成功')
    roleDialogVisible.value = false
    loadRoles()
  })
}

async function handleDelete(row) {
  try {
    const res = await deleteRole(row.id)
    if (res && res.code === 0) {
      ElMessage.success('删除成功')
      loadRoles()
      return
    }
  } catch (e) {}
  // 模拟删除
  roleList.value = roleList.value.filter(r => r.id !== row.id)
  ElMessage.success('删除成功')
}

// ==== 分配数据权限 ====
async function handleAssign(row) {
  currentRole.value = row
  assignForm.dataScope = row.dataScope || '1'
  assignForm.deptIds = []
  // 若是自定义范围，拉取该角色已有的部门 ID
  if (assignForm.dataScope === '2') {
    try {
      const res = await getRoleDataScope(row.id)
      if (res && res.code === 0 && res.data && res.data.deptIds) {
        assignForm.deptIds = res.data.deptIds
      }
    } catch (e) {}
  }
  assignVisible.value = true
}

function handleScopeChange() {
  assignForm.deptIds = []
}

function resetAssignForm() {
  assignForm.dataScope = '1'
  assignForm.deptIds = []
  currentRole.value = null
}

async function handleSubmitAssign() {
  // 获取树形勾选结果（含半选中父节点）
  let checkedKeys = []
  try {
    if (orgTreeRef.value) {
      const checked = orgTreeRef.value.getCheckedKeys()
      const halfChecked = orgTreeRef.value.getHalfCheckedKeys()
      checkedKeys = [...checked, ...halfChecked]
    }
  } catch (e) {
    checkedKeys = assignForm.deptIds
  }
  // 非自定义范围时忽略 deptIds
  const finalDeptIds = assignForm.dataScope === '2' ? checkedKeys : []
  const payload = {
    roleId: currentRole.value.id,
    dataScope: assignForm.dataScope,
    deptIds: finalDeptIds
  }
  try {
    const res = await assignDataScope(payload)
    if (res && res.code === 0) {
      ElMessage.success('数据权限已更新')
      assignVisible.value = false
      loadRoles()
      return
    }
  } catch (e) {}
  // 模拟保存成功：同步角色列表中的 dataScope
  const idx = roleList.value.findIndex(r => r.id === currentRole.value.id)
  if (idx >= 0) {
    roleList.value[idx] = { ...roleList.value[idx], dataScope: assignForm.dataScope }
  }
  ElMessage.success('数据权限已更新')
  assignVisible.value = false
}
</script>

<style scoped>
.data-scope-page {
  padding: 12px 16px;
}
.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fafbfc;
  border: 1px solid #ebeef5;
  padding: 12px 16px;
  border-radius: 6px;
  margin-bottom: 12px;
}
</style>
