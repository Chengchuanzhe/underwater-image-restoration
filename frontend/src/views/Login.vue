<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="title">
        <svg-icon icon-class="water" class="title-icon"></svg-icon>
        水下图片修复系统  -  登录
      </h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0" class="login-form">
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
        <el-form-item class="button-container">
          <el-button
            type="primary"
            @click="handleLogin"
            class="login-btn"
            :loading="loading"
          >登录</el-button>
          <el-button
            @click="$router.push('/register')"
            class="register-btn"
          >注册</el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <a href="#" class="footer-link">忘记密码?</a>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { login } from '@/api/user'
import { Message, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = ref({
  email: '',
  password: ''
})

const rules = ref({
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
})

const handleLogin = async () => {
  if (!formRef.value) return

  try {
    loading.value = true
    await formRef.value.validate()
    const res = await login(form.value)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))
    ElMessage.success('登录成功')
    router.push('/restoration')
  } catch (error) {
    console.error('登录失败：', error)
    ElMessage.error('登录失败，请检查邮箱和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-image: url('/images/underwater-bg.jpg'); /* 可选：添加水下背景图 */
  background-size: cover;
  background-position: center;
  position: relative;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3); /* 遮罩层增强可读性 */
}

.login-card {
  width: 420px;
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

.login-form {
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
  padding-left: 150px;
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

.button-container {
  margin-top: 30px;
  margin-bottom: 0;
}

:deep(.el-form-item__content) {
  display: flex;
  gap: 12px;
  justify-content: space-between;
}

.login-btn, .register-btn {
  flex: 1;
  height: 44px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  border: none;
}

.login-btn {
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.4);
}

.register-btn {
  background: transparent;
  color: #3498db;
  border: 1px solid #3498db;
}

.register-btn:hover {
  background: #3498db;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.3);
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ecf0f1;
}

.footer-link {
  color: #7f8c8d;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.footer-link:hover {
  color: #3498db;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    width: 90vw;
    margin: 20px;
    padding: 30px 20px;
  }

  .title {
    font-size: 20px;
  }
}
</style>