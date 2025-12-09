<template>
  <el-container style="height: 100vh">
    <!-- 顶部导航 -->
    <el-header style="text-align: right; font-size: 12px">
      <el-dropdown>
        <i class="el-icon-setting" style="margin-right: 15px"></i>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
            <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <span>{{ userInfo.username }}</span>
    </el-header>

    <!-- 主体内容 -->
    <el-main>
      <el-card title="图片修复">
        <!-- 图片上传 -->
        <el-upload
          class="upload-demo"
          drag
          :auto-upload="false"
          :on-change="handleFileChange"
          accept="image/jpeg,image/png"
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">拖拽文件到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">仅支持jpg/png格式，文件大小≤5MB</div>
        </el-upload>

        <el-button type="primary" @click="handleRestore" style="margin-top: 20px">立即修复</el-button>

        <!-- 图片对比 -->
        <el-row style="margin-top: 30px">
          <el-col :span="12">
            <h3>原图</h3>
            <img v-if="originalImage" :src="originalImage" class="image-preview" />
          </el-col>
          <el-col :span="12">
            <h3>修复后</h3>
            <img v-if="restoredImage" :src="restoredImage" class="image-preview" />
            <el-button
              v-if="restoredImage"
              type="primary"
              @click="downloadImage(restoredImage)"
              style="margin-top: 10px"
            >
              导出修复图
            </el-button>
          </el-col>
        </el-row>
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { uploadImage, restoreImage, downloadImage } from '@/api/image'

const router = useRouter()
const userInfo = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const file = ref(null)
const originalImage = ref('')
const restoredImage = ref('')
const imageId = ref('')

// 监听文件选择
const handleFileChange = (fileObj) => {
  file.value = fileObj.raw
  originalImage.value = URL.createObjectURL(fileObj.raw)
  restoredImage.value = ''
}

// 修复图片
const handleRestore = async () => {
  if (!file.value) {
    ElMessage.warning('请先上传图片')
    return
  }
  try {
    // 上传图片
    const uploadRes = await uploadImage(file.value)
    imageId.value = uploadRes.data.id
    originalImage.value = uploadRes.data.originalFileUrl

    // 修复图片
    const restoreRes = await restoreImage(imageId.value)
    restoredImage.value = restoreRes.data.restoredFileUrl
    ElMessage.success('图片修复成功')
  } catch (error) {
    ElMessage.error('图片修复失败')
    console.error('修复失败：', error)
  }
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
.image-preview {
  max-width: 100%;
  max-height: 400px;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
}
</style>