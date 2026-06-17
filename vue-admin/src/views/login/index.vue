<template>
  <div class="login-wrapper">
    <!-- 背景轮播 -->
    <div class="bg-carousel">
      <div
        v-for="(img, idx) in bgImages"
        :key="idx"
        class="bg-item"
        :class="{ active: currentBg === idx }"
        :style="{ backgroundImage: `url(${img})` }"
      />
      <div class="bg-overlay" />
    </div>

    <!-- 左上角 Logo -->
    <div class="login-logo">
      <img src="@/assets/logo.svg" alt="logo" class="logo-img" />
      <span class="logo-text">企业管理系统</span>
    </div>

    <!-- 登录表单 -->
    <div class="login-panel">
      <div class="login-header">
        <h2 class="login-title">欢迎登录</h2>
        <p class="login-subtitle">Enterprise Management System</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        label-position="top"
      >
        <!-- 分公司选择 -->
        <el-form-item prop="branchId" label="所属分公司">
          <el-select
            v-model="loginForm.branchId"
            placeholder="请选择所属分公司"
            size="large"
            style="width: 100%"
          >
            <el-option
              v-for="item in branchList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <div class="branch-option">
                <el-icon><Office /></el-icon>
                <span>{{ item.name }}</span>
                <span class="branch-code">{{ item.code }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <!-- 用户名 -->
        <el-form-item prop="username" label="用户名称">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名称"
            prefix-icon="UserFilled"
            size="large"
            clearable
          />
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password" label="登录密码">
          <el-input
            v-model="loginForm.password"
            :type="passwordType"
            placeholder="请输入登录密码"
            prefix-icon="Lock"
            size="large"
            @keyup.enter="handleLogin"
          >
            <template #suffix>
              <el-icon class="pwd-toggle" @click="passwordType = passwordType === 'password' ? 'text' : 'password'">
                <component :is="passwordType === 'password' ? 'View' : 'Hide'" />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 记住密码 -->
        <div class="form-extra">
          <el-checkbox v-model="loginForm.remember">记住密码</el-checkbox>
        </div>

        <!-- 登录按钮 -->
        <el-button
          type="primary"
          size="large"
          :loading="loading"
          class="login-btn"
          @click="handleLogin"
        >
          {{ loading ? '登录中...' : '登 录' }}
        </el-button>
      </el-form>

      <!-- 底部信息 -->
      <div class="login-footer">
        <span>默认演示账号 / 密码：admin / admin</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)
const passwordType = ref('password')

// 背景轮播
const bgImages = [
  'https://images.unsplash.com/photo-1497366216548-37526070297c?w=1920&q=80',
  'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=1920&q=80',
  'https://images.unsplash.com/photo-1497366811353-6870744d04b2?w=1920&q=80',
  'https://images.unsplash.com/photo-1554469384-e58fac16e23a?w=1920&q=80'
]
const currentBg = ref(0)
let bgTimer = null

// 模拟分公司列表
const branchList = [
  { id: 1, name: '华东分公司', code: 'HD001' },
  { id: 2, name: '华南分公司', code: 'HN002' },
  { id: 3, name: '华北分公司', code: 'HB003' },
  { id: 4, name: '西南分公司', code: 'XN004' }
]

// 读取本地记住的账号
function loadRemember() {
  const saved = localStorage.getItem('login_remember')
  if (saved) {
    try {
      const data = JSON.parse(saved)
      loginForm.branchId = data.branchId || ''
      loginForm.username = data.username || ''
      loginForm.password = data.password || ''
      loginForm.remember = true
    } catch (e) {
      // ignore
    }
  }
}

const loginForm = reactive({
  branchId: 1,
  username: 'admin',
  password: 'admin',
  remember: false
})

const loginRules = {
  branchId: [{ required: true, message: '请选择所属分公司', trigger: 'change' }],
  username: [{ required: true, message: '请输入用户名称', trigger: 'blur' }],
  password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }]
}

function startBgCarousel() {
  bgTimer = setInterval(() => {
    currentBg.value = (currentBg.value + 1) % bgImages.length
  }, 5000)
}

async function handleLogin() {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const branch = branchList.find((b) => b.id === loginForm.branchId)
      await userStore.login({
        username: loginForm.username,
        password: loginForm.password,
        branchId: loginForm.branchId,
        branchName: branch?.name
      })

      // 记住密码
      if (loginForm.remember) {
        localStorage.setItem('login_remember', JSON.stringify({
          branchId: loginForm.branchId,
          username: loginForm.username,
          password: loginForm.password
        }))
      } else {
        localStorage.removeItem('login_remember')
      }

      ElMessage.success('登录成功，欢迎 ' + loginForm.username)
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    } catch (e) {
      // 后端未就绪，模拟登录
      const branch = branchList.find((b) => b.id === loginForm.branchId)
      userStore.token = 'mock-token-' + Date.now()
      userStore.name = loginForm.username
      userStore.roles = ['admin']
      localStorage.setItem('Admin-Token', userStore.token)

      if (loginForm.remember) {
        localStorage.setItem('login_remember', JSON.stringify({
          branchId: loginForm.branchId,
          username: loginForm.username,
          password: loginForm.password
        }))
      } else {
        localStorage.removeItem('login_remember')
      }

      ElMessage.success('登录成功（模拟模式）')
      router.push(route.query.redirect || '/')
    } finally {
      loading.value = false
    }
  })
}

onMounted(() => {
  loadRemember()
  startBgCarousel()
})

onUnmounted(() => {
  if (bgTimer) clearInterval(bgTimer)
})
</script>

<style lang="scss" scoped>
.login-wrapper {
  position: fixed;
  inset: 0;
  overflow: hidden;
}

// 背景轮播
.bg-carousel {
  position: absolute;
  inset: 0;
}
.bg-item {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  opacity: 0;
  transition: opacity 1.5s ease-in-out;
  &.active {
    opacity: 1;
  }
}
.bg-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(2px);
}

// 左上角 Logo
.login-logo {
  position: fixed;
  top: 28px;
  left: 32px;
  display: flex;
  align-items: center;
  gap: 12px;
  z-index: 10;
}
.logo-img {
  width: 40px;
  height: 40px;
}
.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 2px;
}

// 登录面板
.login-panel {
  position: relative;
  z-index: 5;
  width: 420px;
  margin: 60px auto;
  padding: 40px 36px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(16px);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}
.login-title {
  margin: 0 0 8px;
  font-size: 26px;
  font-weight: 700;
  color: #1d2129;
}
.login-subtitle {
  margin: 0;
  font-size: 13px;
  color: #86909c;
  letter-spacing: 1px;
}

.login-form {
  :deep(.el-form-item__label) {
    font-weight: 500;
    color: #4e5969;
    padding-bottom: 6px;
  }
}

.branch-option {
  display: flex;
  align-items: center;
  gap: 8px;
  .branch-code {
    margin-left: auto;
    font-size: 12px;
    color: #909399;
  }
}

.pwd-toggle {
  cursor: pointer;
  color: #909399;
  &:hover {
    color: #409eff;
  }
}

.form-extra {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 4px;
  border-radius: 8px;
  background: linear-gradient(135deg, #409eff 0%, #53a8ff 100%);
  border: none;
  transition: opacity 0.3s;
  &:hover {
    opacity: 0.9;
  }
}

.login-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 12px;
  color: #909399;
}
</style>
