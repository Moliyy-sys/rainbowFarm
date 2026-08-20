<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-elements">
        <div class="floating-element" v-for="n in 8" :key="n" :style="getFloatingStyle(n)">
          {{ ['🌾', '🌱', '🌿', '🍃', '🌳', '🌻', '🌺', '🌸'][n-1] }}
        </div>
      </div>
      <div class="gradient-overlay"></div>
    </div>

    <!-- 主登录卡片 -->
    <div class="login-card">
      <!-- 左侧品牌展示区域 -->
      <div class="left-panel">
        <!-- 品牌区域 -->
        <div class="brand-section">
          <div class="brand-logo">
            <div class="logo-icon">🌱</div>
            <div class="brand-info">
              <h1 class="brand-title">智慧农业</h1>
              <p class="brand-subtitle">科技赋能，智慧创造</p>
            </div>
          </div>
        </div>

        <!-- 特色功能展示 -->
        <div class="features-section">
          <div class="feature-card" v-for="(feature, index) in features" :key="index">
            <div class="feature-icon">{{ feature.icon }}</div>
            <div class="feature-content">
              <h3 class="feature-title">{{ feature.title }}</h3>
              <p class="feature-desc">{{ feature.desc }}</p>
            </div>
          </div>
        </div>

        <!-- 装饰图案 -->
        <div class="decoration-pattern">
          <div class="pattern-circle" v-for="n in 3" :key="n"></div>
        </div>
      </div>

      <!-- 右侧登录区域 -->
      <div class="right-panel">
        <div class="login-form-container">
          <div class="login-header">
            <h2 class="login-title">欢迎回来</h2>
            <p class="login-subtitle">请输入您的账户信息</p>
          </div>

          <el-form 
            ref="formRef" 
            :model="form" 
            :rules="rules" 
            class="login-form"
            @keyup.enter="handleLogin"
          >
            <el-form-item prop="username" class="form-item">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                size="large"
                prefix-icon="User"
                class="login-input"
              />
            </el-form-item>

            <el-form-item prop="password" class="form-item">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                prefix-icon="Lock"
                class="login-input"
                show-password
              />
            </el-form-item>

            <el-form-item prop="captcha" class="form-item captcha-item" v-if="showCaptcha">
              <el-input
                v-model="form.captcha"
                placeholder="请输入右侧验证码"
                size="large"
                prefix-icon="Key"
                class="captcha-input"
                maxlength="4"
              />
              <div class="captcha-code" @click="refreshCaptcha" :class="{ 'loading': captchaLoading }" title="点击刷新验证码">
                <img 
                  v-if="captchaImage && !captchaLoading" 
                  :src="captchaImage" 
                  alt="验证码" 
                  class="captcha-img"
                  @error="handleImageError"
                />
                <div v-else-if="captchaLoading" class="captcha-loading">
                  <el-icon class="loading-icon"><Refresh /></el-icon>
                  <span class="loading-text">加载中</span>
                </div>
                <span v-else class="captcha-placeholder">点击获取验证码</span>
                <el-icon class="refresh-icon"><Refresh /></el-icon>
              </div>
            </el-form-item>

            <div class="form-options">
              <el-checkbox v-model="form.rememberMe" class="remember-checkbox">
                记住密码
              </el-checkbox>
              <el-link type="primary" class="forgot-link">忘记密码？</el-link>
            </div>

            <el-button
              type="primary"
              size="large"
              class="login-button"
              :loading="loading"
              @click="handleLogin"
              block
            >
              {{ loading ? '登录中...' : '立即登录' }}
            </el-button>

            <!-- 第三方登录 -->
            <div class="third-party-login">
              <div class="divider">
                <span>其他登录方式</span>
              </div>
              <div class="social-buttons">
                <el-button class="social-btn wechat" circle>
                  <el-icon><ChatDotRound /></el-icon>
                </el-button>
                <el-button class="social-btn qq" circle>
                  <el-icon><User /></el-icon>
                </el-button>
                <el-button class="social-btn phone" circle>
                  <el-icon><Phone /></el-icon>
                </el-button>
              </div>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElForm } from 'element-plus'
import { User, Lock, Key, Refresh, ChatDotRound, Phone } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import { getCaptchaApi } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

// 表单引用
const formRef = ref<InstanceType<typeof ElForm>>()

// 表单数据
const form = reactive({
  username: 'admin',
  password: 'admin123',
  captcha: '',
  rememberMe: false
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码为4位', trigger: 'blur' }
  ]
}

// 状态变量
const loading = ref(false)
const showCaptcha = ref(true)
const captchaImage = ref('')
const captchaLoading = ref(false)

// 特色功能数据
const features = [
  {
    icon: '📊',
    title: '智能监控',
    desc: '实时环境数据监控'
  },
  {
    icon: '📈',
    title: '数据分析',
    desc: '深度数据分析洞察'
  },
  {
    icon: '🎯',
    title: '生产管理',
    desc: '全流程生产管理'
  },
  {
    icon: '⚡',
    title: '智能控制',
    desc: '自动化设备控制'
  }
]

// 登录处理
const handleLogin = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    // 构建登录参数
    const loginParams = {
      username: form.username,
      password: form.password,
      captchaCode: form.captcha
    }

    // 调用store的登录方法
    await userStore.login(loginParams)
    
    ElMessage.success('登录成功')
    
    // 使用replace避免返回登录页
    await router.replace('/')
    
  } catch (error: any) {
    console.error('登录错误:', error)
    
    // 根据错误类型显示不同的提示信息
    let errorMessage = '登录失败'
    if (error?.message) {
      if (error.message.includes('验证码')) {
        errorMessage = '验证码错误，请重新输入'
      } else if (error.message.includes('用户名')) {
        errorMessage = '用户名不存在'
      } else if (error.message.includes('密码')) {
        errorMessage = '密码错误'
      } else {
        errorMessage = error.message
      }
    } else if (error?.response?.data?.msg) {
      errorMessage = error.response.data.msg
    } else {
      errorMessage = '登录失败，请检查用户名和密码'
    }
    
    ElMessage.error(errorMessage)
    
    // 如果是验证码错误或登录失败，刷新验证码
    await refreshCaptcha()
  } finally {
    loading.value = false
  }
}

// 刷新验证码
const refreshCaptcha = async () => {
  // 防止重复请求
  if (captchaLoading.value) {
    return
  }
  
  try {
    captchaLoading.value = true
    const captchaData = await getCaptchaApi()
    
    // 设置验证码图片
    captchaImage.value = `${captchaData.img}`
    
    // 清空验证码输入
    form.captcha = ''
    
    // 重置图片错误计数器
    imageErrorCount = 0
  } catch (error) {
    ElMessage.error('获取验证码失败，请稍后重试')
    console.error('获取验证码失败:', error)
  } finally {
    captchaLoading.value = false
  }
}

// 图片错误重试计数器
let imageErrorCount = 0
const MAX_IMAGE_RETRY = 3

// 处理验证码图片加载错误
const handleImageError = () => {
  imageErrorCount++
  
  if (imageErrorCount <= MAX_IMAGE_RETRY) {
    ElMessage.warning('验证码图片加载失败，正在重新获取...')
    setTimeout(() => {
      refreshCaptcha()
    }, 1000) // 延迟1秒重试
  } else {
    ElMessage.error('验证码图片加载失败次数过多，请刷新页面重试')
  }
}

// 获取飘动元素样式
const getFloatingStyle = (index: number) => {
  const positions = [
    { left: '10%', top: '20%', animationDelay: '0s' },
    { left: '20%', top: '60%', animationDelay: '2s' },
    { left: '80%', top: '30%', animationDelay: '4s' },
    { left: '15%', top: '80%', animationDelay: '1s' },
    { left: '85%', top: '70%', animationDelay: '3s' },
    { left: '75%', top: '15%', animationDelay: '5s' },
    { left: '5%', top: '45%', animationDelay: '2.5s' },
    { left: '90%', top: '85%', animationDelay: '4.5s' }
  ]
  return positions[index - 1]
}

onMounted(() => {
  // 初始化加载验证码
  refreshCaptcha()
})
</script>

<style scoped>
/* ============= 主容器样式 ============= */
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 50%, #f0fdf4 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
}

/* ============= 背景装饰 ============= */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at 30% 20%, rgba(16, 185, 129, 0.1) 0%, transparent 50%),
              radial-gradient(circle at 80% 80%, rgba(5, 150, 105, 0.08) 0%, transparent 50%),
              radial-gradient(circle at 40% 60%, rgba(4, 120, 87, 0.05) 0%, transparent 50%);
}

.floating-elements {
  position: absolute;
  width: 100%;
  height: 100%;
}

.floating-element {
  position: absolute;
  font-size: 24px;
  opacity: 0.6;
  animation: float 6s ease-in-out infinite;
  pointer-events: none;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  33% { transform: translateY(-20px) rotate(120deg); }
  66% { transform: translateY(10px) rotate(240deg); }
}

/* ============= 主登录卡片 ============= */
.login-card {
  width: 100%;
  max-width: 1000px;
  height: 600px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 
    0 25px 50px -12px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.3);
  display: flex;
  overflow: hidden;
  position: relative;
  z-index: 10;
  transition: all 0.3s ease;
}

.login-card:hover {
  box-shadow: 
    0 32px 64px -12px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.4);
  transform: translateY(-2px);
}

/* ============= 左侧品牌展示区域 ============= */
.left-panel {
  flex: 1;
  background: linear-gradient(135deg, #10b981 0%, #059669 50%, #047857 100%);
  position: relative;
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
}

.left-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="20" cy="20" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="80" cy="40" r="0.8" fill="rgba(255,255,255,0.08)"/><circle cx="40" cy="80" r="1.2" fill="rgba(255,255,255,0.12)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
}

/* 品牌区域 */
.brand-section {
  position: relative;
  z-index: 2;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
}

.logo-icon {
  font-size: 48px;
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.brand-info {
  flex: 1;
}

.brand-title {
  font-size: 32px;
  font-weight: 700;
  color: white;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.brand-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  font-weight: 400;
}

/* 特色功能展示 */
.features-section {
  position: relative;
  z-index: 2;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.feature-card {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 16px;
  padding: 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
  cursor: pointer;
}

.feature-card:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
}

.feature-icon {
  font-size: 28px;
  margin-bottom: 12px;
  display: block;
}

.feature-title {
  font-size: 16px;
  font-weight: 600;
  color: white;
  margin: 0 0 8px 0;
}

.feature-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
  line-height: 1.4;
}

/* 装饰图案 */
.decoration-pattern {
  position: absolute;
  bottom: 20px;
  right: 20px;
  display: flex;
  gap: 8px;
  z-index: 1;
}

.pattern-circle {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  animation: pulse 2s ease-in-out infinite;
}

.pattern-circle:nth-child(2) {
  animation-delay: 0.5s;
}

.pattern-circle:nth-child(3) {
  animation-delay: 1s;
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 0.8; transform: scale(1.2); }
}

/* ============= 右侧登录区域 ============= */
.right-panel {
  width: 480px;
  background: white;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-form-container {
  width: 100%;
  max-width: 360px;
  padding: 48px 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-title {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 12px 0;
  letter-spacing: -0.5px;
}

.login-subtitle {
  font-size: 16px;
  color: #6b7280;
  margin: 0;
  font-weight: 400;
}

/* 表单样式 */
.login-form {
  width: 100%;
}

.form-item {
  margin-bottom: 24px;
}

:deep(.el-input) {
  height: 48px;
  border-radius: 12px;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px #e5e7eb;
  transition: all 0.2s ease;
  background: #fafafa;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #10b981;
  background: white;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.2);
  background: white;
}

:deep(.el-input__inner) {
  height: 46px;
  line-height: 46px;
  font-size: 15px;
  color: #374151;
}

:deep(.el-input__inner::placeholder) {
  color: #9ca3af;
  font-weight: 400;
}

/* 验证码样式 */
.captcha-item {
  display: flex;
  gap: 12px;
}

.captcha-input {
  flex: 1;
}

.captcha-code {
  width: 120px;
  height: 48px;
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-weight: 600;
  color: #374151;
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
}

.captcha-code:hover {
  background: #e5e7eb;
  border-color: #10b981;
}

.captcha-code.loading {
  cursor: not-allowed;
}

.captcha-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 11px;
}

.captcha-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  gap: 4px;
}

.loading-text {
  font-size: 12px;
  color: #10b981;
}

.loading-icon {
  animation: rotate 1s linear infinite;
  color: #10b981;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.captcha-placeholder {
  font-size: 12px;
  color: #9ca3af;
}

.refresh-icon {
  position: absolute;
  top: 4px;
  right: 4px;
  font-size: 12px;
  opacity: 0.6;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: opacity 0.2s ease;
}

.captcha-code:hover .refresh-icon {
  opacity: 1;
}

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

:deep(.remember-checkbox .el-checkbox__label) {
  color: #6b7280;
  font-size: 14px;
}

.forgot-link {
  font-size: 14px;
  text-decoration: none;
}

/* 登录按钮 */
.login-button {
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.login-button:hover {
  background: linear-gradient(135deg, #059669 0%, #047857 100%);
  box-shadow: 0 6px 16px rgba(16, 185, 129, 0.4);
  transform: translateY(-1px);
}

.login-button:active {
  transform: translateY(0);
}

/* 第三方登录 */
.third-party-login {
  margin-top: 32px;
}

.divider {
  position: relative;
  text-align: center;
  margin-bottom: 24px;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #e5e7eb;
}

.divider span {
  background: white;
  padding: 0 16px;
  color: #9ca3af;
  font-size: 14px;
  position: relative;
  z-index: 1;
}

.social-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.social-btn {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  background: white;
  color: #6b7280;
  transition: all 0.2s ease;
}

.social-btn:hover {
  border-color: #10b981;
  color: #10b981;
  transform: translateY(-1px);
}

.social-btn.wechat:hover {
  border-color: #07c160;
  color: #07c160;
}

.social-btn.qq:hover {
  border-color: #1296db;
  color: #1296db;
}

.social-btn.phone:hover {
  border-color: #f59e0b;
  color: #f59e0b;
}

/* ============= 响应式设计 ============= */
@media (max-width: 1024px) {
  .login-card {
    max-width: 900px;
    height: 550px;
  }
  
  .right-panel {
    width: 420px;
  }
  
  .login-form-container {
    max-width: 320px;
    padding: 40px 30px;
  }
}

@media (max-width: 768px) {
  .login-container {
    padding: 20px;
  }
  
  .login-card {
    flex-direction: column;
    max-width: 400px;
    height: auto;
  }
  
  .left-panel {
    padding: 32px 24px;
    min-height: 200px;
  }
  
  .brand-title {
    font-size: 24px;
  }
  
  .features-section {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .feature-card {
    padding: 16px;
  }
  
  .right-panel {
    width: 100%;
  }
  
  .login-form-container {
    max-width: 100%;
    padding: 32px 24px;
  }
  
  .login-title {
    font-size: 24px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 16px;
  }
  
  .login-card {
    border-radius: 16px;
  }
  
  .left-panel {
    padding: 24px 20px;
  }
  
  .brand-logo {
    gap: 12px;
  }
  
  .logo-icon {
    width: 60px;
    height: 60px;
    font-size: 36px;
  }
  
  .brand-title {
    font-size: 20px;
  }
  
  .login-form-container {
    padding: 24px 20px;
  }
  
  .login-title {
    font-size: 20px;
  }
}
</style>