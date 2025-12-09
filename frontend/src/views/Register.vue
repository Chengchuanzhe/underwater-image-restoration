<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="title">水下图片修复系统 - 注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-input v-model="form.code" placeholder="请输入验证码"></el-input>
          <el-button type="primary" @click="getCode" style="margin-top: 10px">获取验证码</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" class="btn">注册</el-button>
          <el-button @click="$router.push('/login')" class="btn">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { register, getEmailCode } from '@/api/user'

const router = useRouter()
const formRef = ref(null)
const form = ref({
  username: '',
  email: '',
  password: '',
  code: ''
})

const rules = ref({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
})

// 获取验证码（可选，后端简化后可跳过）
const getCode = async () => {
  if (!form.value.email) {
    ElMessage.warning('请先输入邮箱')
    return
  }
  try {
    await getEmailCode(form.value.email, 1)
    ElMessage.success('验证码已发送')
  } catch (error) {
    console.error('获取验证码失败：', error)
  }
}

const handleRegister = async () => {
  try {
    await formRef.value.validate()
    await register(form.value)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error('注册失败：', error)
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f5f5f5;
}
.register-card {
  width: 400px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.title {
  text-align: center;
  margin-bottom: 20px;
  color: #1989fa;
}
.btn {
  width: 48%;
  margin: 0 1%;
}
</style>