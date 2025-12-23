<template>
  <div class="restoration-container">
    <div class="nav-header">
      <div class="logo">🌊 水下图像修复</div>
      <div class="user-menu">
        <el-dropdown trigger="click">
          <span class="el-dropdown-link">
            <el-avatar :size="32" icon="el-icon-user-solid" class="avatar"></el-avatar>
            <span class="username">{{ userInfo.username }}</span>
            <i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
              <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="main-content">
      <el-card class="glass-card" :body-style="{ padding: '30px' }">
        <div slot="header" class="card-header">
          <h2>智能水下图像修复</h2>
          <p class="subtitle">上传模糊、偏色的水下照片， 一键修复</p>
        </div>

        <transition name="el-fade-in-linear">
          <div v-if="!originalImage" class="upload-section">
            <el-upload
              class="upload-drag"
              drag
              :auto-upload="false"
              :on-change="handleFileChange"
              :show-file-list="false"
              accept="image/jpeg,image/png"
            >
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
              <div class="el-upload__tip" slot="tip">支持 JPG/PNG 格式，大小不超过 5MB</div>
            </el-upload>
          </div>
        </transition>

        <transition name="el-fade-in-linear">
          <div v-if="originalImage" class="preview-section">
            <el-row :gutter="20" type="flex" justify="center" align="middle">
              <el-col :span="11" class="image-col">
                <div class="image-label">原图</div>
                <div class="image-wrapper">
                  <el-image
                    :src="originalImage"
                    fit="contain"
                    :preview-src-list="[originalImage]"
                  ></el-image>
                </div>
              </el-col>

              <el-col :span="2" class="action-col">
                <div v-if="!restoredImage">
                  <el-button
                    type="primary"
                    circle
                    :loading="loading"
                    @click="handleRestore"
                    class="restore-btn"
                  >
                    <i class="el-icon-magic-stick" v-if="!loading"></i>
                  </el-button>
                  <div class="action-text">{{ loading ? '修复中...' : '开始修复' }}</div>
                </div>
                <div v-else class="arrow-icon">
                  <i class="el-icon-right"></i>
                </div>
              </el-col>

              <el-col :span="11" class="image-col">
                 <div class="image-label">修复后</div>
                 <div class="image-wrapper result-wrapper" v-loading="loading" element-loading-text="正在智能降噪与色彩还原...">
                    <div v-if="!restoredImage && !loading" class="placeholder">
                      <span>等待修复...</span>
                    </div>
                    <el-image
                      v-if="restoredImage"
                      :src="restoredImage"
                      fit="contain"
                      :preview-src-list="[restoredImage]"
                    ></el-image>
                 </div>
              </el-col>
            </el-row>

            <div class="footer-actions">
              <el-button @click="resetUpload" icon="el-icon-refresh-left">重新上传</el-button>
              <el-button
                v-if="restoredImage"
                type="success"
                icon="el-icon-download"
                @click="downloadImage(restoredImage)"
              >
                下载高清原图
              </el-button>
            </div>
          </div>
        </transition>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { uploadImage, restoreImage, downloadImage as downloadApi } from '@/api/image'

const router = useRouter()
// 如果没有用户信息，默认给一个空对象防止报错
const userInfo = ref(JSON.parse(localStorage.getItem('user') || '{"username": "用户"}'))

const file = ref(null)
const originalImage = ref('')
const restoredImage = ref('')
const imageId = ref('')
const loading = ref(false)

// 监听文件选择
const handleFileChange = (fileObj) => {
  const isLt5M = fileObj.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('上传图片大小不能超过 5MB!')
    return
  }

  file.value = fileObj.raw
  originalImage.value = URL.createObjectURL(fileObj.raw)
  restoredImage.value = '' // 清空之前的修复结果
}

// 重新上传
const resetUpload = () => {
  file.value = null
  originalImage.value = ''
  restoredImage.value = ''
  imageId.value = ''
}

// 修复图片
const handleRestore = async () => {
  if (!file.value) return

  loading.value = true
  try {
    // 1. 上传
    const uploadRes = await uploadImage(file.value)
    if(uploadRes.data.code === 200 || uploadRes.code === 200 || uploadRes.data) {
       // 兼容不同的返回结构，确保取到数据
       const data = uploadRes.data.data || uploadRes.data || uploadRes
       imageId.value = data.id
       // 更新原图URL为服务器地址（可选，保持本地预览更快）
    } else {
       throw new Error(uploadRes.msg || '上传失败')
    }

    // 2. 修复
    const restoreRes = await restoreImage(imageId.value)
    const restoreData = restoreRes.data.data || restoreRes.data || restoreRes

    // 给图片URL加时间戳，防止浏览器缓存
    restoredImage.value = `${restoreData.restoredFileUrl}?t=${new Date().getTime()}`

    ElMessage.success('✨ 图片修复成功！')
  } catch (error) {
    ElMessage.error(error.message || '修复失败，请稍后重试')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 下载图片
const downloadImage = (url) => {
  downloadApi(url, `restored_${Date.now()}.png`)
}

// 退出登录
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('已安全退出')
  router.push('/login')
}
</script>

<style scoped>
/* 全局容器：深海渐变背景 */
.restoration-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f2027 0%, #203a43 50%, #2c5364 100%);
  display: flex;
  flex-direction: column;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

/* 顶部导航 */
.nav-header {
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  letter-spacing: 1px;
}

.el-dropdown-link {
  cursor: pointer;
  color: white;
  display: flex;
  align-items: center;
}
.username {
  margin: 0 8px;
  font-weight: 500;
}
.avatar {
  background: #409EFF;
}

/* 主体内容 */
.main-content {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center; /* 垂直居中 */
  padding: 40px 20px;
}

/* 毛玻璃卡片 */
.glass-card {
  width: 1000px;
  min-height: 500px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  border: none;
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.card-header h2 {
  margin: 0;
  font-size: 24px;
  color: #2c3e50;
}
.subtitle {
  margin: 10px 0 0;
  color: #7f8c8d;
  font-size: 14px;
}

/* 上传区域 */
.upload-section {
  padding: 40px 0;
  text-align: center;
}
.upload-drag :deep(.el-upload-dragger) {
  width: 100%;
  height: 250px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f8f9fa;
  border: 2px dashed #dcdfe6;
  border-radius: 12px;
  transition: all 0.3s;
}
.upload-drag :deep(.el-upload-dragger:hover) {
  border-color: #409EFF;
  background-color: #ecf5ff;
}
.el-icon-upload {
  font-size: 60px;
  color: #c0c4cc;
  margin-bottom: 20px;
}

/* 图片预览区域 */
.preview-section {
  margin-top: 20px;
}
.image-col {
  text-align: center;
}
.image-label {
  margin-bottom: 10px;
  font-weight: bold;
  color: #606266;
}
.image-wrapper {
  height: 300px;
  background: #f0f2f5;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e4e7ed;
}
.result-wrapper {
  background: #e1f3d8; /* 修复成功后的淡绿色背景暗示 */
  border-color: #67c23a;
}
.placeholder {
  color: #909399;
  font-size: 14px;
}
.el-image {
  width: 100%;
  height: 100%;
}

/* 中间操作按钮 */
.action-col {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.restore-btn {
  width: 60px;
  height: 60px;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}
.action-text {
  margin-top: 8px;
  font-size: 12px;
  color: #409EFF;
}
.arrow-icon {
  font-size: 30px;
  color: #909399;
}

/* 底部按钮 */
.footer-actions {
  margin-top: 40px;
  text-align: center;
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
}
</style>