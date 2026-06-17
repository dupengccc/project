<template>
  <div class="app-container">
    <el-row :gutter="16">
      <el-col :xs="24" :sm="12" :md="6" v-for="card in cards" :key="card.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-title">{{ card.title }}</div>
          <div class="stat-value" :style="{ color: card.color }">{{ card.value }}</div>
          <div class="stat-desc">{{ card.desc }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="mt-16">
      <template #header>
        <div style="font-weight: 600">欢迎使用 Vue Admin</div>
      </template>
      <p>这是基于 Vue 3 + Element Plus 构建的前端管理框架骨架，包含以下模块：</p>
      <ul>
        <li>🔐 登录页面 + Token 存取（localStorage）</li>
        <li>🌐 Axios 请求封装（拦截器、401 处理、统一 code/msg/data 结构）</li>
        <li>🛣️ Vue Router + 路由守卫（未登录重定向到登录页）</li>
        <li>🍍 Pinia 状态管理（user / dict 两个模块）</li>
        <li>📐 主布局：侧边栏 + 顶部导航 + 面包屑</li>
        <li>📚 字典工具（按 dictType 缓存，提供 options / label 查询）</li>
      </ul>
      <el-divider />
      <p>接下来你可以：</p>
      <ul>
        <li>在 <code>vite.config.js</code> 中修改 <code>proxy</code> 为你的后端地址</li>
        <li>在 <code>.env</code> 中修改 <code>VITE_APP_BASE_API</code> 前缀</li>
        <li>在 <code>src/api/</code> 目录新增业务接口文件</li>
        <li>在 <code>src/views/</code> 目录新增页面，并在 <code>src/router/index.js</code> 注册路由</li>
      </ul>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const cards = ref([
  { title: '今日访问', value: '1,286', desc: '较昨日 +12%', color: '#409eff' },
  { title: '注册用户', value: '3,204', desc: '累计总数', color: '#67c23a' },
  { title: '待处理工单', value: '28', desc: '3 项高优先级', color: '#e6a23c' },
  { title: '系统健康度', value: '98.5%', desc: '过去 24 小时', color: '#f56c6c' }
])
</script>

<style lang="scss" scoped>
.stat-card {
  margin-bottom: 16px;
}
.stat-title {
  color: #909399;
  font-size: 13px;
}
.stat-value {
  font-size: 28px;
  font-weight: 600;
  margin: 6px 0;
}
.stat-desc {
  font-size: 12px;
  color: #c0c4cc;
}
.mt-16 {
  margin-top: 16px;
}
</style>
