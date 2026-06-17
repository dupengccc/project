<template>
  <div style="text-align: center; padding: 80px 16px;">
    <h1 style="font-size: 80px; margin: 0; color: #409eff;">404</h1>
    <p style="color: #909399; font-size: 16px;">抱歉，您访问的页面不存在</p>
    <el-button type="primary" @click="goHome">返回首页</el-button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
function goHome() {
  router.push('/')
}
</script>
