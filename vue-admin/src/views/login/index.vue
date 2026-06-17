<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">
        <el-icon :size="24"><UserFilled /></el-icon>
        <span>Vue Admin 后台管理系统</span>
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        auto-complete="on"
        label-position="left"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            prefix-icon="User"
            auto-complete="on"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            :type="passwordType"
            placeholder="密码"
            prefix-icon="Lock"
            auto-complete="on"
            size="large"
            @keyup.enter="handleLogin"
          >
            <template #suffix>
              <el-icon class="cursor-pointer" @click="togglePassword">
                <component :is="passwordType === 'password' ? 'View' : 'Hide'" />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-button
          :loading="loading"
          type="primary"
          size="large"
          style="width: 100%"
          @click.prevent="handleLogin"
        >
          登 录
        </el-button>
      </el-form>
      <div class="login-tips">默认账号 / 密码：admin / admin</div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)
const passwordType = ref('password')

const loginForm = reactive({
  username: 'admin',
  password: 'admin'
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function togglePassword() {
  passwordType.value = passwordType.value === 'password' ? 'text' : 'password'
}

async function handleLogin() {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await userStore.login({
        username: loginForm.username,
        password: loginForm.password
      })
      ElMessage.success('登录成功')
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    } catch (e) {
      // 若后端未就绪，也可直接进入系统
      ElMessage.warning('后端接口未就绪，使用模拟数据登录')
      userStore.token = 'mock-token'
      localStorage.setItem('Admin-Token', 'mock-token')
      userStore.name = loginForm.username
      userStore.roles = ['admin']
      router.push(route.query.redirect || '/')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1890ff 0%, #409eff 50%, #69c0ff 100%);
}
.login-box {
  width: 400px;
  padding: 32px 36px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
}
.login-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 24px;
}
.login-tips {
  margin-top: 16px;
  text-align: center;
  font-size: 12px;
  color: #909399;
}
.cursor-pointer {
  cursor: pointer;
}
</style>
