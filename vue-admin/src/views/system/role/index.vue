<template>
  <div class="app-container">
    <el-card>
      <div style="margin-bottom: 12px">
        <el-button type="success">新增角色</el-button>
      </div>
      <el-table :data="roleList" border stripe style="width: 100%">
        <el-table-column label="角色名称" prop="roleName" />
        <el-table-column label="角色编码" prop="roleCode" />
        <el-table-column label="排序" prop="sort" width="80" />
        <el-table-column label="状态" prop="status">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'info'">
              {{ row.status === 0 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" width="180">
          <template #default>
            <el-button size="small" type="primary">编辑</el-button>
            <el-button size="small" type="warning">分配权限</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const roleList = ref([
  { id: 1, roleName: '超级管理员', roleCode: 'super_admin', sort: 1, status: 0, createTime: '2024-01-01 10:00:00' },
  { id: 2, roleName: '普通用户', roleCode: 'user', sort: 2, status: 0, createTime: '2024-01-02 10:00:00' },
  { id: 3, roleName: '访客', roleCode: 'guest', sort: 3, status: 1, createTime: '2024-01-03 10:00:00' }
])
</script>
