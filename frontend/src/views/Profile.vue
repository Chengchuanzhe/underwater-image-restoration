<template>
  <div class="profile-container">
    <div class="nav-header">
      <div class="logo" @click="$router.push('/restoration')" style="cursor: pointer">
        🌊 水下图像修复
      </div>
      <div class="user-info">
        <el-button type="text" style="color: white" @click="$router.push('/restoration')">
          <i class="el-icon-back"></i> 返回修复台
        </el-button>
      </div>
    </div>

    <div class="main-content">
      <el-row :gutter="20" style="width: 100%; max-width: 1200px;">
        <el-col :span="6">
          <el-card class="user-card">
            <div class="avatar-wrapper">
              <el-avatar :size="80" icon="el-icon-user-solid" class="big-avatar"></el-avatar>
            </div>
            <h3 class="username">{{ userInfo.username }}</h3>
            <p class="role">普通用户</p>
            <div class="stats">
              <div class="stat-item">
                <span class="count">{{ records.length }}</span>
                <span class="label">修复次数</span>
              </div>
            </div>
            <el-divider></el-divider>
            <el-button type="danger" plain style="width: 100%" @click="logout">退出登录</el-button>
          </el-card>
        </el-col>

        <el-col :span="18">
          <el-card class="history-card">
            <div slot="header" class="clearfix">
              <span style="font-weight: bold; font-size: 16px">🖼️ 历史修复记录</span>
            </div>

            <el-table :data="records" style="width: 100%" v-loading="loading">
              <el-table-column prop="id" label="ID" width="60"></el-table-column>

              <el-table-column label="原图预览" width="120">
                <template #default="scope">
                  <el-image
                    style="width: 50px; height: 50px; border-radius: 4px"
                    :src="scope.row.originalFileUrl"
                    :preview-src-list="[scope.row.originalFileUrl]"
                  ></el-image>
                </template>
              </el-table-column>

              <el-table-column label="修复后预览" width="120">
                <template #default="scope">
                  <el-image
                    v-if="scope.row.restoredFileUrl"
                    style="width: 50px; height: 50px; border-radius: 4px"
                    :src="scope.row.restoredFileUrl"
                    :preview-src-list="[scope.row.restoredFileUrl]"
                  ></el-image>
                  <span v-else class="text-gray">未修复</span>
                </template>
              </el-table-column>

              <el-table-column prop="originalFilename" label="文件名" show-overflow-tooltip></el-table-column>

              <el-table-column label="操作" width="150" fixed="right">
                <template #default="scope">
                  <el-button
                    v-if="scope.row.restoredFileUrl"
                    size="mini"
                    type="primary"
                    icon="el-icon-download"
                    @click="downloadImage(scope.row.restoredFileUrl)"
                  >
                    下载
                  </el-button>
                  <el-tag v-else type="info" size="mini">处理中</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getImageRecords, downloadImage as downloadApi } from '@/api/image'

const router = useRouter()
const userInfo = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const records = ref([])
const loading = ref(false)

// 获取历史记录
const fetchRecords = async () => {
  loading.value = true
  try {
    const res = await getImageRecords()
    // 假设后端返回 Result<List<ImageRecord>>，数据在 res.data.data 或 res.data 里
    records.value = res.data.data || res.data || []
  } catch (error) {
    console.error('获取记录失败', error)
    // 如果是 401，可能是 token 过期
    // ElMessage.warning('请先登录')
  } finally {
    loading.value = false
  }
}

const downloadImage = (url) => {
  downloadApi(url, `history_${Date.now()}.png`)
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/login')
  ElMessage.success('退出成功')
}

onMounted(() => {
  fetchRecords()
})
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background-color: #f0f2f5;
  display: flex;
  flex-direction: column;
}

.nav-header {
  height: 60px;
  background: #203a43;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.main-content {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 40px 20px;
}

.user-card {
  text-align: center;
  padding: 20px 0;
}
.big-avatar {
  background: #409EFF;
  font-size: 40px;
}
.username {
  margin: 15px 0 5px;
  color: #303133;
}
.role {
  color: #909399;
  font-size: 14px;
  margin-bottom: 20px;
}
.stats {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}
.stat-item {
  text-align: center;
}
.count {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
.label {
  font-size: 12px;
  color: #909399;
}

.history-card {
  min-height: 500px;
}
.text-gray {
  color: #ccc;
  font-size: 12px;
}
</style>