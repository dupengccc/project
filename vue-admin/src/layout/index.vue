<template>
  <div class="layout-wrapper">
    <div
      class="layout-sidebar"
      :style="{ width: collapsed ? '64px' : '220px' }"
    >
      <div class="logo">
        <el-icon :size="22"><Cpu /></el-icon>
        <span v-if="!collapsed">Vue Admin</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          :collapse="collapsed"
          :unique-opened="true"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          router
        >
          <template v-for="route in menuRoutes" :key="route.path">
            <el-sub-menu v-if="route.children && route.children.length > 1" :index="route.path">
              <template #title>
                <el-icon v-if="route.meta?.icon"><component :is="route.meta.icon" /></el-icon>
                <span>{{ route.meta?.title }}</span>
              </template>
              <el-menu-item
                v-for="child in route.children"
                :key="child.path"
                :index="resolvePath(route.path, child.path)"
              >
                <el-icon v-if="child.meta?.icon"><component :is="child.meta.icon" /></el-icon>
                <template #title>{{ child.meta?.title }}</template>
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item
              v-else
              :index="route.children && route.children.length === 1
                ? resolvePath(route.path, route.children[0].path)
                : route.path"
            >
              <el-icon v-if="(route.children && route.children[0]?.meta?.icon) || route.meta?.icon">
                <component :is="(route.children && route.children[0]?.meta?.icon) || route.meta.icon" />
              </el-icon>
              <template #title>
                {{ (route.children && route.children[0]?.meta?.title) || route.meta?.title }}
              </template>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>
    </div>

    <div class="layout-main">
      <div class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" :size="20" @click="collapsed = !collapsed">
            <Fold v-if="!collapsed" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item
              v-for="(item, idx) in breadcrumbs"
              :key="idx"
              :to="idx === breadcrumbs.length - 1 ? undefined : item"
            >
              {{ item.meta?.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="30" :src="userStore.avatar || ''">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <span class="user-name">{{ userStore.name || 'Admin' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="change-password">
                  <el-icon><Key /></el-icon> 修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <!-- 修改密码弹窗 -->
          <el-dialog v-model="pwdDialogVisible" title="修改密码" width="480px" @closed="resetPwdForm">
            <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
              <el-form-item label="原密码" prop="oldPassword">
                <el-input
                  v-model="pwdForm.oldPassword"
                  type="password"
                  show-password
                  placeholder="请输入原密码"
                  autocomplete="current-password"
                />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="pwdForm.newPassword"
                  type="password"
                  show-password
                  placeholder="请输入新密码（6-20 位）"
                  autocomplete="new-password"
                />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="pwdForm.confirmPassword"
                  type="password"
                  show-password
                  placeholder="请再次输入新密码"
                  autocomplete="new-password"
                />
              </el-form-item>
            </el-form>
            <template #footer>
              <el-button @click="pwdDialogVisible = false">取消</el-button>
              <el-button type="primary" :loading="pwdSubmitting" @click="handleSubmitPwd">
                确认修改
              </el-button>
            </template>
          </el-dialog>
        </div>
      </div>

      <div class="layout-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { constantRoutes } from '@/router'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const collapsed = ref(false)
const menuRoutes = computed(() => constantRoutes.filter((r) => !r.hidden))
const activeMenu = computed(() => route.path)
const breadcrumbs = computed(() => route.matched.filter((m) => m.meta?.title))

// 修改密码相关
const pwdDialogVisible = ref(false)
const pwdSubmitting = ref(false)
const pwdFormRef = ref(null)
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const validateConfirmPwd = (rule, value, callback) => {
  if (!value) return callback(new Error('请再次输入新密码'))
  if (value !== pwdForm.newPassword) return callback(new Error('两次输入的密码不一致'))
  callback()
}
const pwdRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度 6-20 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPwd, trigger: 'blur' }
  ]
}

function resolvePath(parent, child) {
  if (child.startsWith('/')) return child
  return (parent.endsWith('/') ? parent : parent + '/') + child
}

async function handleCommand(command) {
  if (command === 'change-password') {
    pwdDialogVisible.value = true
    return
  }
  if (command === 'logout') {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      await userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  }
}

function resetPwdForm() {
  pwdFormRef.value?.resetFields()
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdForm.confirmPassword = ''
}

async function handleSubmitPwd() {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      pwdSubmitting.value = true
      await userStore.changePassword({
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword
      })
      ElMessage.success('密码修改成功，请重新登录')
      pwdDialogVisible.value = false
      // 密码修改后强制重新登录
      await userStore.logout()
      router.push('/login')
    } catch (e) {
      // userStore.changePassword 内部已做提示，这里只拦截异常
    } finally {
      pwdSubmitting.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.layout-wrapper {
  height: 100vh;
  display: flex;
  background: #f0f2f5;
}
.layout-sidebar {
  background: #304156;
  height: 100vh;
  overflow: hidden;
  transition: width 0.25s;
}
.logo {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  background: #263445;
  overflow: hidden;
  white-space: nowrap;
}
.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.layout-header {
  height: 50px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  border-bottom: 1px solid #ebeef5;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  cursor: pointer;
  color: #606266;
}
.header-right .user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}
.layout-content {
  flex: 1;
  overflow: auto;
}
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
