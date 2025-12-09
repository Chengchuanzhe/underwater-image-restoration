import service from './index'

// 上传图片
export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return service.post('/image/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 修复图片
export const restoreImage = (imageId) => service.post(`/image/restore?imageId=${imageId}`)

// 获取修复记录
export const getImageRecords = () => service.get('/image/records')

// 下载图片（前端处理）
export const downloadImage = (url, name) => {
  const link = document.createElement('a')
  link.href = url
  link.download = name || '修复后的图片'
  link.click()
}