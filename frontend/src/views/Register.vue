<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="title">
        <svg-icon icon-class="water" class="title-icon"></svg-icon>
        水下图片修复系统 - 注册
      </h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0" class="register-form">
        <el-form-item prop="username">
          <div class="input-with-icon">
            <el-icon class="input-icon"><User /></el-icon>
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              size="large"
            ></el-input>
          </div>
        </el-form-item>
        <el-form-item prop="email">
          <div class="input-with-icon">
            <el-icon class="input-icon"><Message /></el-icon>
            <el-input
              v-model="form.email"
              placeholder="请输入邮箱"
              size="large"
            ></el-input>
          </div>
        </el-form-item>
        <el-form-item prop="password">
          <div class="input-with-icon">
            <el-icon class="input-icon"><Lock /></el-icon>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
            ></el-input>
          </div>
        </el-form-item>
        <el-form-item prop="code" class="code-item">
          <div class="code-input-group">
            <div class="input-with-icon code-input">
              <el-icon class="input-icon"><Key /></el-icon>
              <el-input
                v-model="form.code"
                placeholder="请输入验证码"
                size="large"
                maxlength="6"
              ></el-input>
            </div>
            <el-button
              type="primary"
              @click="getCode"
              class="code-btn"
              :disabled="isCounting"
              :loading="codeLoading"
            >
              {{ isCounting ? `${countdown}秒后重新获取` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item class="button-container">
          <el-button
            type="primary"
            @click="handleRegister"
            class="register-btn"
            :loading="loading"
          >注册</el-button>
          <el-button
            @click="$router.push('/login')"
            class="login-btn"
          >返回登录</el-button>
        </el-form-item>
      </el-form>
      <div class="register-footer">
        <el-checkbox v-model="agreed" class="agreement-checkbox">
          我已阅读并同意
          <el-link type="primary" :underline="false">《用户协议》</el-link>
          和
          <el-link type="primary" :underline="false">《隐私政策》</el-link>
        </el-checkbox>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { register, getEmailCode } from '@/api/user'
import { User, Message, Lock, Key } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const codeLoading = ref(false)
const isCounting = ref(false)
const countdown = ref(60)
const agreed = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
  code: ''
})

const rules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 16, message: '用户名长度为2-16个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/, message: '用户名只能包含中文、英文、数字和下划线', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[^]{6,}$/, message: '密码必须包含大小写字母和数字', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ]
})

// 获取验证码
const getCode = async () => {
  if (!form.email) {
    ElMessage.warning('请先输入邮箱')
    return
  }

  // 验证邮箱格式
  const emailRule = rules.email.find(rule => rule.type === 'email')
  if (emailRule && !emailRule.pattern.test(form.email)) {
    ElMessage.warning('请输入正确的邮箱格式')
    return
  }

  if (isCounting.value) return

  try {
    codeLoading.value = true
    await getEmailCode(form.email, 1)
    ElMessage.success('验证码已发送到您的邮箱')

    // 开始倒计时
    isCounting.value = true
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
        isCounting.value = false
        countdown.value = 60
      }
    }, 1000)
  } catch (error) {
    console.error('获取验证码失败：', error)
    ElMessage.error('验证码发送失败，请重试')
  } finally {
    codeLoading.value = false
  }
}

const handleRegister = async () => {
  if (!formRef.value) return

  // 检查用户协议
  if (!agreed.value) {
    ElMessage.warning('请先阅读并同意用户协议和隐私政策')
    return
  }

  try {
    loading.value = true
    await formRef.value.validate()
    await register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error('注册失败：', error)
    ElMessage.error('注册失败，请检查输入信息')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-image: url('/images/underwater-bg.jpg');
  background-size: cover;
  background-position: center;
  position: relative;
}

.register-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
}

.register-card {
  width: 450px;
  padding: 40px 30px;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  position: relative;
  z-index: 1;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.title-icon {
  width: 28px;
  height: 28px;
  color: #3498db;
}

.register-form {
  margin-top: 20px;
}

.input-with-icon {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 2;
  color: #7f8c8d;
  font-size: 18px;
}

:deep(.el-input__wrapper) {
  padding-left: 40px;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s ease;
  background: #f8f9fa;
  box-shadow: none;
}

:deep(.el-input__wrapper:hover) {
  border-color: #3498db;
  background: #fff;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
  background: #fff;
}

:deep(.el-input__inner) {
  color: #2c3e50;
}

:deep(.el-input__inner::placeholder) {
  color: #95a5a6;
}

.code-item {
  margin-bottom: 10px;
}

.code-input-group {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.code-input {
  flex: 1;
}

.code-btn {
  white-space: nowrap;
  height: 40px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.code-btn:disabled {
  background: #bdc3c7;
  border-color: #bdc3c7;
  color: #7f8c8d;
}

.button-container {
  margin-top: 30px;
  margin-bottom: 0;
}

:deep(.el-form-item__content) {
  display: flex;
  gap: 12px;
  justify-content: space-between;
}

.register-btn, .login-btn {
  flex: 1;
  height: 44px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  border: none;
}

.register-btn {
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
}

.register-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.4);
}

.login-btn {
  background: transparent;
  color: #3498db;
  border: 1px solid #3498db;
}

.login-btn:hover {
  background: #3498db;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.3);
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ecf0f1;
}

.agreement-checkbox {
  width: 100%;
  justify-content: center;
  color: #7f8c8d;
  font-size: 14px;
}

:deep(.el-checkbox__label) {
  color: #7f8c8d;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .register-card {
    width: 90vw;
    margin: 20px;
    padding: 30px 20px;
  }

  .title {
    font-size: 20px;
  }

  .code-input-group {
    flex-direction: column;
  }

  .code-btn {
    width: 100%;
    margin-top: 10px;
  }
}
</style>