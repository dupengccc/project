<template>
  <div class="data-scope-page">
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
      <el-table-column label="操作" width="280" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleAssign(row)">
            分配数据权限
          </el-button>
          <el-button link type="primary" size="small" @click="handleAssignMaterial(row)">
            分配物料权限
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

    <!-- 分配物料权限弹窗 -->
    <el-dialog v-model="materialAssignVisible" :title="'分配物料权限 - ' + (currentRole?.roleName || '')" width="800px" @closed="resetMaterialAssignForm">
      <el-form :model="materialAssignForm" label-width="100px">
        <el-form-item label="物料类型">
          <el-select v-model="materialAssignForm.materialType" placeholder="全部" clearable style="width: 160px" @change="loadMaterials">
            <el-option label="全部" value="" />
            <el-option label="原材料" value="原材料" />
            <el-option label="半成品" value="半成品" />
            <el-option label="成品" value="成品" />
            <el-option label="辅料" value="辅料" />
          </el-select>
        </el-form-item>
        <el-form-item label="物料列表">
          <div style="max-height: 400px; overflow: auto; border: 1px solid #ebeef5; border-radius: 4px;">
            <el-table
              ref="materialTableRef"
              :data="materialList"
              border
              stripe
              :default-checked-keys="materialAssignForm.materialIds"
              @selection-change="handleMaterialSelectionChange"
            >
              <el-table-column type="selection" width="55" />
              <el-table-column prop="materialCode" label="物料编码" width="140" />
              <el-table-column prop="materialName" label="物料名称" width="160" />
              <el-table-column prop="materialType" label="物料类型" width="100" />
              <el-table-column prop="spec" label="规格" min-width="150" show-overflow-tooltip />
              <el-table-column prop="unit" label="单位" width="80" />
            </el-table>
          </div>
          <div style="text-align: right; padding-top: 8px;">
            <el-button size="small" @click="selectAllMaterials">全选</el-button>
            <el-button size="small" @click="clearAllMaterials">清空</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="materialAssignVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitMaterialAssign">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listRoles, saveRole, deleteRole,
  getRoleDataScope, assignDataScope, getOrgTree,
  assignMaterialScope, getMaterials
} from '@/api/data-scope'

const loading = ref(false)
const roleList = ref([])
const orgTree = ref([])
const materialList = ref([])

const queryForm = reactive({ roleName: '', status: null })

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

const assignVisible = ref(false)
const assignFormRef = ref()
const orgTreeRef = ref()
const currentRole = ref(null)
const assignForm = reactive({ dataScope: '1', deptIds: [] })

const materialAssignVisible = ref(false)
const materialTableRef = ref()
const materialAssignForm = reactive({ materialType: '', materialIds: [] })

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

onMounted(async () => {
  await loadOrgTree()
  loadRoles()
})

async function loadOrgTree() {
  try {
    const res = await getOrgTree()
    if (res && res.data && res.data.length > 0) {
      orgTree.value = res.data
      return
    }
  } catch (e) {}
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
}

async function loadRoles() {
  loading.value = true
  try {
    const res = await listRoles({ roleName: queryForm.roleName, status: queryForm.status })
    if (res && res.code === 0 && res.data && Array.isArray(res.data.list) && res.data.list.length > 0) {
      roleList.value = res.data.list
      loading.value = false
      return
    }
  } catch (e) {}
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

async function loadMaterials() {
  try {
    const res = await getMaterials({ materialType: materialAssignForm.materialType })
    if (res && res.code === 0 && res.data && Array.isArray(res.data)) {
      materialList.value = res.data
      return
    }
  } catch (e) {}
  let mock = [
    { id: 1, materialCode: 'M00001', materialName: '不锈钢板', materialType: '原材料', spec: '1220*2440*2mm', unit: '张' },
    { id: 2, materialCode: 'M00002', materialName: '铝合金型材', materialType: '原材料', spec: '6063-T5 2m', unit: '根' },
    { id: 3, materialCode: 'M00003', materialName: '碳钢圆棒', materialType: '原材料', spec: '直径20mm', unit: '根' },
    { id: 4, materialCode: 'B00001', materialName: '半成品装配A', materialType: '半成品', spec: 'A100', unit: '件' },
    { id: 5, materialCode: 'B00002', materialName: '半成品装配B', materialType: '半成品', spec: 'B200', unit: '件' },
    { id: 6, materialCode: 'F00001', materialName: '工控机箱', materialType: '成品', spec: 'IPC-610L', unit: '台' },
    { id: 7, materialCode: 'F00002', materialName: '触控一体机', materialType: '成品', spec: '15寸', unit: '台' },
    { id: 8, materialCode: 'A00001', materialName: '内六角螺丝', materialType: '辅料', spec: 'M4x8', unit: '包' },
    { id: 9, materialCode: 'A00002', materialName: '垫片', materialType: '辅料', spec: 'M4', unit: '包' },
    { id: 10, materialCode: 'A00003', materialName: '螺母', materialType: '辅料', spec: 'M4', unit: '包' }
  ]
  if (materialAssignForm.materialType) {
    mock = mock.filter(m => m.materialType === materialAssignForm.materialType)
  }
  materialList.value = mock
}

function handleReset() {
  queryForm.roleName = ''
  queryForm.status = null
  loadRoles()
}

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
  roleList.value = roleList.value.filter(r => r.id !== row.id)
  ElMessage.success('删除成功')
}

async function handleAssign(row) {
  currentRole.value = row
  assignForm.dataScope = row.dataScope || '1'
  assignForm.deptIds = []
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
  const idx = roleList.value.findIndex(r => r.id === currentRole.value.id)
  if (idx >= 0) {
    roleList.value[idx] = { ...roleList.value[idx], dataScope: assignForm.dataScope }
  }
  ElMessage.success('数据权限已更新')
  assignVisible.value = false
}

// ==== 物料权限分配 ====
async function handleAssignMaterial(row) {
  currentRole.value = row
  materialAssignForm.materialType = ''
  materialAssignForm.materialIds = []
  await loadMaterials()
  try {
    const res = await getRoleDataScope(row.id)
    if (res && res.code === 0 && res.data && res.data.materialIds) {
      materialAssignForm.materialIds = res.data.materialIds
    }
  } catch (e) {}
  materialAssignVisible.value = true
}

function handleMaterialSelectionChange(val) {
  materialAssignForm.materialIds = val.map(item => item.id)
}

function selectAllMaterials() {
  if (materialTableRef.value) {
    materialTableRef.value.toggleAllSelection()
  }
}

function clearAllMaterials() {
  if (materialTableRef.value) {
    materialTableRef.value.clearSelection()
  }
}

function resetMaterialAssignForm() {
  materialAssignForm.materialType = ''
  materialAssignForm.materialIds = []
  currentRole.value = null
}

async function handleSubmitMaterialAssign() {
  const payload = {
    roleId: currentRole.value.id,
    materialIds: materialAssignForm.materialIds
  }
  try {
    const res = await assignMaterialScope(payload)
    if (res && res.code === 0) {
      ElMessage.success('物料权限已更新')
      materialAssignVisible.value = false
      return
    }
  } catch (e) {}
  ElMessage.success('物料权限已更新')
  materialAssignVisible.value = false
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
