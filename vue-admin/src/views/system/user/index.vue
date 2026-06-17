<template>
  <div class="user-wrapper">
    <!-- 左侧：组织树 -->
    <div class="org-tree-panel">
      <div class="panel-title">组织架构</div>
      <el-input
        v-model="treeFilter"
        placeholder="搜索组织"
        prefix-icon="Search"
        clearable
        size="small"
        style="margin: 8px 12px"
      />
      <el-scrollbar class="tree-scroll">
        <el-tree
          ref="treeRef"
          :data="orgTree"
          :props="{ label: 'name', children: 'children' }"
          node-key="id"
          :default-expand-all="true"
          highlight-current
          :filter-node-method="filterNode"
          @node-click="handleOrgClick"
        >
          <template #default="{ node, data }">
            <div class="tree-node">
              <el-icon>
                <OfficeBuilding v-if="data.type === 'group'" />
                <Office v-else-if="data.type === 'branch'" />
                <Grid v-else />
              </el-icon>
              <span class="node-name">{{ node.label }}</span>
              <el-tag size="small" type="info" effect="plain">{{ data.count || 0 }}</el-tag>
            </div>
          </template>
        </el-tree>
      </el-scrollbar>
    </div>

    <!-- 右侧：人员信息 -->
    <div class="user-content">
      <el-card shadow="never">
        <div class="content-header">
          <div class="header-title">
            <span>人员管理</span>
            <el-tag size="small" v-if="selectedOrg">当前组织：{{ selectedOrg.name }}</el-tag>
            <el-tag size="small" v-else type="warning">请在左侧选择组织</el-tag>
          </div>
          <div class="header-actions">
            <el-input
              v-model="queryForm.keyword"
              placeholder="搜索姓名/工号/账号/邮箱"
              prefix-icon="Search"
              clearable
              style="width: 260px; margin-right: 8px"
              @keyup.enter="loadData"
            />
            <el-button type="primary" @click="handleAdd" :disabled="!selectedOrg">
              <el-icon><Plus /></el-icon>新增人员
            </el-button>
            <el-button @click="loadData">
              <el-icon><Refresh /></el-icon>刷新
            </el-button>
          </div>
        </div>

        <el-table
          :data="filteredList"
          border
          stripe
          :height="tableHeight"
          style="width: 100%; margin-top: 12px"
          :empty-text="selectedOrg ? '暂无人员数据' : '请先选择左侧组织'"
        >
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column label="工号" prop="empNo" width="110" fixed="left" />
          <el-table-column label="姓名" prop="name" width="100" fixed="left" />
          <el-table-column label="性别" prop="gender" width="70">
            <template #default="{ row }">
              <el-tag :type="row.gender === '男' ? 'primary' : 'success'" size="small" effect="plain">
                {{ row.gender }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="登录账号" prop="username" width="130" />
          <el-table-column label="职位" prop="position" width="120" />
          <el-table-column label="技能" prop="skills" min-width="180">
            <template #default="{ row }">
              <el-tag
                v-for="s in (row.skills || '').split(/[,，]/).filter(Boolean)"
                :key="s"
                size="small"
                type="info"
                effect="plain"
                style="margin-right: 4px"
              >{{ s }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="员工性质" prop="empType" width="110">
            <template #default="{ row }">
              <el-tag size="small">{{ row.empType }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="考核系数" prop="kpiFactor" width="100" align="center" />
          <el-table-column label="身份证号" prop="idCard" width="200" />
          <el-table-column label="邮箱" prop="email" width="180" />
          <el-table-column label="手机号" prop="phone" width="130" />
          <el-table-column label="所属组织" prop="orgName" width="140" />
          <el-table-column label="操作" width="160" fixed="right" align="center">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-popconfirm title="确认删除该人员？" @confirm="handleDelete(row)">
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
    </div>

    <!-- 新增/编辑人员弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '新增人员' : '编辑人员'"
      width="780px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属组织" prop="orgId">
              <el-tree-select
                v-model="form.orgId"
                :data="orgTree"
                :props="{ label: 'name', value: 'id', children: 'children' }"
                check-strictly
                placeholder="请选择所属组织"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工号" prop="empNo">
              <el-input v-model="form.empNo" placeholder="请输入工号" maxlength="20" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录账号" prop="username">
              <el-input v-model="form.username" placeholder="请输入登录账号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio value="男">男</el-radio>
                <el-radio value="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="员工性质" prop="empType">
              <el-select v-model="form.empType" placeholder="请选择" style="width: 100%">
                <el-option label="正式员工" value="正式员工" />
                <el-option label="实习员工" value="实习员工" />
                <el-option label="劳务派遣" value="劳务派遣" />
                <el-option label="外包员工" value="外包员工" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="职位" prop="position">
              <el-select v-model="form.position" placeholder="请选择" style="width: 100%" filterable>
                <el-option label="董事长" value="董事长" />
                <el-option label="总经理" value="总经理" />
                <el-option label="部门经理" value="部门经理" />
                <el-option label="工程师" value="工程师" />
                <el-option label="高级工程师" value="高级工程师" />
                <el-option label="专员" value="专员" />
                <el-option label="助理" value="助理" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请输入18位身份证号" maxlength="18" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="考核系数" prop="kpiFactor">
              <el-input-number v-model="form.kpiFactor" :min="0" :max="3" :step="0.1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="技能标签">
              <el-select
                v-model="form.skills"
                multiple
                filterable
                allow-create
                default-first-option
                placeholder="可多选，支持手动输入，回车确认"
                style="width: 100%"
              >
                <el-option label="Java" value="Java" />
                <el-option label="Vue" value="Vue" />
                <el-option label="React" value="React" />
                <el-option label="Python" value="Python" />
                <el-option label="Go" value="Go" />
                <el-option label="数据库" value="数据库" />
                <el-option label="项目管理" value="项目管理" />
                <el-option label="英语" value="英语" />
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
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

const treeRef = ref()
const formRef = ref()
const dialogVisible = ref(false)
const dialogMode = ref('add')
const submitLoading = ref(false)
const treeFilter = ref('')

// 组织树（包含各组织的人员数）
const orgTree = ref([
  {
    id: 1, name: '集团总部', type: 'group', count: 2, children: [
      {
        id: 2, name: '华东分公司', type: 'branch', count: 3, children: [
          {
            id: 5, name: '研发部', type: 'dept', count: 3, children: [
              { id: 8, name: '前端组', type: 'dept', count: 2, children: [] },
              { id: 9, name: '后端组', type: 'dept', count: 1, children: [] }
            ]
          },
          { id: 6, name: '市场部', type: 'dept', count: 0, children: [] }
        ]
      },
      {
        id: 3, name: '华南分公司', type: 'branch', count: 1, children: [
          { id: 7, name: '运营部', type: 'dept', count: 1, children: [] }
        ]
      },
      { id: 4, name: '财务部', type: 'dept', count: 1, children: [] }
    ]
  }
])

const selectedOrg = ref(null)

// 模拟人员数据
const allUsers = ref([
  { id: 1, orgId: 1, orgName: '集团总部', empNo: 'G0001', name: '张建国', gender: '男', username: 'zhangjg', position: '总经理', skills: '项目管理,英语', empType: '正式员工', kpiFactor: 1.2, idCard: '110101198001011234', email: 'zhangjg@company.com', phone: '13800000001' },
  { id: 2, orgId: 1, orgName: '集团总部', empNo: 'G0002', name: '李淑芬', gender: '女', username: 'lishf', position: '工程师', skills: 'Java,数据库', empType: '正式员工', kpiFactor: 1.0, idCard: '110101198502021235', email: 'lishf@company.com', phone: '13800000002' },
  { id: 3, orgId: 5, orgName: '研发部', empNo: 'HD001', name: '王志强', gender: '男', username: 'wangzq', position: '高级工程师', skills: 'Java,Go,数据库', empType: '正式员工', kpiFactor: 1.15, idCard: '310101198803031236', email: 'wangzq@company.com', phone: '13800000003' },
  { id: 4, orgId: 8, orgName: '前端组', empNo: 'HD002', name: '陈美丽', gender: '女', username: 'chenml', position: '工程师', skills: 'Vue,React', empType: '正式员工', kpiFactor: 1.05, idCard: '310101199204041237', email: 'chenml@company.com', phone: '13800000004' },
  { id: 5, orgId: 8, orgName: '前端组', empNo: 'HD003', name: '刘洋', gender: '男', username: 'liuy', position: '工程师', skills: 'Vue,React', empType: '劳务派遣', kpiFactor: 0.9, idCard: '310101199505051238', email: 'liuy@company.com', phone: '13800000005' },
  { id: 6, orgId: 9, orgName: '后端组', empNo: 'HD004', name: '赵雷', gender: '男', username: 'zhaol', position: '高级工程师', skills: 'Java,数据库', empType: '正式员工', kpiFactor: 1.1, idCard: '310101198706061239', email: 'zhaol@company.com', phone: '13800000006' },
  { id: 7, orgId: 7, orgName: '运营部', empNo: 'HN001', name: '孙晓峰', gender: '男', username: 'sunxf', position: '部门经理', skills: '项目管理,英语', empType: '正式员工', kpiFactor: 1.2, idCard: '440101198607071240', email: 'sunxf@company.com', phone: '13800000007' },
  { id: 8, orgId: 4, orgName: '财务部', empNo: 'G0003', name: '周慧敏', gender: '女', username: 'zhouhm', position: '专员', skills: '财务,英语', empType: '实习员工', kpiFactor: 0.8, idCard: '110101199808081241', email: 'zhouhm@company.com', phone: '13800000008' }
])

const queryForm = reactive({
  keyword: '',
  page: 1,
  pageSize: 10
})

const total = ref(0)
const tableHeight = ref('calc(100vh - 260px)')

// 工具：获取一个组织及其所有子组织的 id 列表
function getOrgAndChildrenIds(node, ids = []) {
  ids.push(node.id)
  if (node.children) {
    for (const c of node.children) {
      getOrgAndChildrenIds(c, ids)
    }
  }
  return ids
}

// 根据选中的组织，筛选出对应人员
const filteredList = computed(() => {
  if (!selectedOrg.value) return []
  const ids = getOrgAndChildrenIds(selectedOrg.value)
  let list = allUsers.value.filter((u) => ids.includes(u.orgId))
  const kw = queryForm.keyword.trim().toLowerCase()
  if (kw) {
    list = list.filter(
      (u) =>
        (u.name || '').toLowerCase().includes(kw) ||
        (u.empNo || '').toLowerCase().includes(kw) ||
        (u.username || '').toLowerCase().includes(kw) ||
        (u.email || '').toLowerCase().includes(kw)
    )
  }
  total.value = list.length
  const start = (queryForm.page - 1) * queryForm.pageSize
  return list.slice(start, start + queryForm.pageSize)
})

watch(treeFilter, (val) => {
  treeRef.value?.filter(val)
})

function filterNode(value, data) {
  if (!value) return true
  return (data.name || '').includes(value)
}

function handleOrgClick(data) {
  selectedOrg.value = data
  queryForm.page = 1
}

function loadData() {
  // 模拟刷新
  ElMessage.success('已刷新')
}

// 表单
const form = reactive({
  id: null,
  orgId: null,
  empNo: '',
  name: '',
  gender: '男',
  username: '',
  position: '',
  skills: '',
  empType: '正式员工',
  kpiFactor: 1.0,
  idCard: '',
  email: '',
  phone: ''
})

const rules = {
  orgId: [{ required: true, message: '请选择所属组织', trigger: 'change' }],
  empNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  username: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  empType: [{ required: true, message: '请选择员工性质', trigger: 'change' }],
  position: [{ required: true, message: '请选择职位', trigger: 'change' }],
  kpiFactor: [{ required: true, message: '请输入考核系数', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }, { pattern: /^\d{17}[\dXx]$/, message: '身份证号格式不正确', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
  phone: [{ pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null, orgId: selectedOrg.value?.id || null, empNo: '', name: '', gender: '男',
    username: '', position: '', skills: '', empType: '正式员工', kpiFactor: 1.0,
    idCard: '', email: '', phone: ''
  })
}

function handleAdd() {
  dialogMode.value = 'add'
  form.id = null
  form.orgId = selectedOrg.value?.id || null
  form.empNo = ''
  form.name = ''
  form.gender = '男'
  form.username = ''
  form.position = ''
  form.skills = ''
  form.empType = '正式员工'
  form.kpiFactor = 1.0
  form.idCard = ''
  form.email = ''
  form.phone = ''
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogMode.value = 'edit'
  Object.assign(form, JSON.parse(JSON.stringify(row)))
  form.skills = (row.skills || '').split(/[,，]/).filter(Boolean)
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      const skillsStr = Array.isArray(form.skills) ? form.skills.join(',') : form.skills
      if (dialogMode.value === 'add') {
        const newUser = {
          id: Date.now(),
          orgId: form.orgId,
          orgName: (findOrg(form.orgId)?.name) || '',
          empNo: form.empNo,
          name: form.name,
          gender: form.gender,
          username: form.username,
          position: form.position,
          skills: skillsStr,
          empType: form.empType,
          kpiFactor: form.kpiFactor,
          idCard: form.idCard,
          email: form.email,
          phone: form.phone
        }
        allUsers.value.push(newUser)
        // 更新组织树的人数
        updateOrgCount(newUser.orgId, 1)
        ElMessage.success('新增人员成功')
      } else {
        const idx = allUsers.value.findIndex((u) => u.id === form.id)
        if (idx !== -1) {
          const oldOrgId = allUsers.value[idx].orgId
          allUsers.value[idx] = {
            ...allUsers.value[idx],
            orgId: form.orgId,
            orgName: (findOrg(form.orgId)?.name) || '',
            empNo: form.empNo,
            name: form.name,
            gender: form.gender,
            username: form.username,
            position: form.position,
            skills: skillsStr,
            empType: form.empType,
            kpiFactor: form.kpiFactor,
            idCard: form.idCard,
            email: form.email,
            phone: form.phone
          }
          if (oldOrgId !== form.orgId) {
            updateOrgCount(oldOrgId, -1)
            updateOrgCount(form.orgId, 1)
          }
        }
        ElMessage.success('修改人员成功')
      }
      dialogVisible.value = false
    } finally {
      submitLoading.value = false
    }
  })
}

function handleDelete(row) {
  allUsers.value = allUsers.value.filter((u) => u.id !== row.id)
  updateOrgCount(row.orgId, -1)
  ElMessage.success('删除成功')
}

function findOrg(id, nodes = orgTree.value) {
  for (const n of nodes) {
    if (n.id === id) return n
    if (n.children?.length) {
      const r = findOrg(id, n.children)
      if (r) return r
    }
  }
  return null
}

function updateOrgCount(id, delta, nodes = orgTree.value) {
  for (const n of nodes) {
    if (n.id === id) {
      n.count = Math.max(0, (n.count || 0) + delta)
      return true
    }
    if (n.children?.length && updateOrgCount(id, delta, n.children)) {
      n.count = (n.count || 0) + delta
      return true
    }
  }
  return false
}

onMounted(() => {
  // 默认选中第一个顶级组织
  selectedOrg.value = orgTree.value[0]
})
</script>

<style lang="scss" scoped>
.user-wrapper {
  display: flex;
  height: calc(100vh - 120px);
  gap: 12px;
}
.org-tree-panel {
  width: 260px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}
.panel-title {
  padding: 14px 16px;
  border-bottom: 1px solid #ebeef5;
  font-size: 15px;
  font-weight: 600;
  color: #1f2d3d;
}
.tree-scroll {
  flex: 1;
  overflow: hidden;
}
.tree-node {
  display: flex;
  align-items: center;
  gap: 6px;
  width: 100%;
  .node-name {
    flex: 1;
  }
}
.user-content {
  flex: 1;
  overflow: hidden;
}
.content-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 8px;
}
.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #1f2d3d;
}
.pagination-bar {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
