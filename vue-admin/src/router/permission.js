import router from './index'
import NProgress from 'nprogress'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/store/modules/user'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/404']

router.beforeEach(async (to, from, next) => {
  NProgress.start()
  document.title = (to.meta?.title ? to.meta.title + ' - ' : '') + 'Vue Admin'

  const hasToken = getToken()
  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      const userStore = useUserStore()
      if (!userStore.name) {
        try {
          await userStore.getInfo()
        } catch (e) {
          await userStore.logout()
          next(`/login?redirect=${to.path}`)
          NProgress.done()
          return
        }
      }
      next()
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
