<template>
  <el-container style="height: 100vh">
    <!-- 顶部导航 -->
    <el-header style="text-align: right; font-size: 12px">
      <el-dropdown>
        <i class="el-icon-setting" style="margin-right: 15px"></i>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="$router.push('/restoration')">返回修复页</el-dropdown-item>
            <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <span>{{ userInfo.username }}</span>
    </el-header>

    <!-- 主体内容 -->
    <el-main>
      <el-card title="个人信息">
        <el-form :model="userInfo" label-width="80px">
          <el-form-item label="用户名">
            <el-input v-model="userInfo.username" disabled></el-input>
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="userInfo.email" disabled></el-input>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card title="修复记录" style="margin-top: 20px">
        <el-table :data="records" border>
          <el-table-column prop="originalFilename" label="文件名"></el-table-column>
          <el-table-column prop="fileSize" label="文件大小(KB)">
            <template #default="scope">
              {{ (scope.row.fileSize / 1024).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="restorationTime" label="修复时间"></el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button type="primary" size="small" @click="previewRecord(scope.row)">预览</el-button>
              <el-button type="success" size="small" @click="downloadImage(scope.row.restoredFileUrl)">导出</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElDialog, ElImage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getImageRecords, downloadImage } from '@/api/image'

const router = useRouter()
const userInfo = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const records = ref([])
const dialogVisible = ref(false)
const previewImage = ref({
  original: '',
  restored: ''
})

// 获取修复记录
onMounted(async () => {
  try {
    const res = await getImageRecords()
    records.value = res.data
  } catch (error) {
    ElMessage.error('获取记录失败')
    console.error('获取记录失败：', error)
  }
})

// 预览记录
const previewRecord = (row) => {
  previewImage.value = {
    original: row.originalFileUrl,
    restored: row.restoredFileUrl
  }
  dialogVisible.value = true
}

// 退出登录
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('退出成功')
  router.push('/login')
}
</script>

<style scoped>
.el-header {
  background-color: #fff;
  color: #333;
  line-height: 60px;
  border-bottom: 1px solid #e6e6e6;
}
.el-main {
  padding: 20px;
  background: #f5f5f5;
}
</style>