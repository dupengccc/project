<template>
  <div class="app-container">
    <el-card>
      <div style="margin-bottom: 12px">
        <el-button type="success">新增菜单</el-button>
      </div>
      <el-table :data="menuList" row-key="id" border stripe style="width: 100%">
        <el-table-column label="菜单名称" prop="name" />
        <el-table-column label="图标" width="80">
          <template #default="{ row }">
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column label="路径" prop="path" />
        <el-table-column label="类型" prop="type" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 'M' ? 'primary' : 'success'" size="small">
              {{ row.type === 'M' ? '目录' : '菜单' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default>
            <el-button size="small" type="primary">编辑</el-button>
            <el-button size="small" type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const menuList = ref([
  { id: 1, name: '首页', icon: 'HomeFilled', path: '/dashboard', type: 'C' },
  { id: 2, name: '系统管理', icon: 'Setting', path: '/system', type: 'M' }
])
</script>
