<template>
  <div class="app-container">
    <el-card>
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="handleAdd">新增用户</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe style="width: 100%">
        <el-table-column label="ID" prop="id" width="70" />
        <el-table-column label="用户名" prop="username" />
        <el-table-column label="昵称" prop="nickName" />
        <el-table-column label="手机号" prop="phone" />
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const queryForm = reactive({
  username: '',
  phone: '',
  page: 1,
  pageSize: 10
})

const total = ref(35)

const tableData = ref([
  { id: 1, username: 'admin', nickName: '超级管理员', phone: '13800000000', status: 0, createTime: '2024-01-01 12:00:00' },
  { id: 2, username: 'zhangsan', nickName: '张三', phone: '13800000001', status: 0, createTime: '2024-01-02 10:20:00' },
  { id: 3, username: 'lisi', nickName: '李四', phone: '13800000002', status: 1, createTime: '2024-01-03 09:30:00' }
])

function loadData() {
  ElMessage.success('已刷新（示例项目，无后端）')
}

function resetQuery() {
  queryForm.username = ''
  queryForm.phone = ''
  queryForm.page = 1
  loadData()
}

function handleAdd() {
  ElMessage.info('打开新增用户表单（示例）')
}

function handleEdit(row) {
  ElMessage.info('编辑：' + row.username)
}

function handleDelete(row) {
  ElMessageBox.confirm('确认删除用户 ' + row.username + ' 吗？', '提示', {
    type: 'warning'
  }).then(() => {
    tableData.value = tableData.value.filter((r) => r.id !== row.id)
    ElMessage.success('删除成功')
  }).catch(() => {})
}

onMounted(loadData)
</script>
