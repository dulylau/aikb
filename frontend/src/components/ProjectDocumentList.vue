<template>
  <div class="component">
    <div class="header"><h3>项目文档</h3><el-button type="primary" size="small" @click="showUpload = true">上传文档</el-button></div>
    <div class="search-bar"><el-input v-model="keyword" placeholder="搜索文档" clearable @clear="loadDocs" @keyup.enter="handleSearch" style="width:260px" /></div>
    <el-table v-loading="loading" :data="docs" style="width:100%">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="fileType" label="类型" width="80" />
      <el-table-column prop="fileSize" label="大小" width="80"><template #default="{row}">{{ formatSize(row.fileSize) }}</template></el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button type="primary" size="small" link @click="handlePreview(row.id)">预览</el-button>
          <el-button type="danger" size="small" link @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="showUpload" title="上传文档" width="500px">
      <el-form><el-form-item label="分类"><el-input v-model="uploadCategory" placeholder="请输入文档分类" /></el-form-item>
        <el-form-item label="文件"><el-upload :auto-upload="false" :limit="1" :on-change="(f:any)=>{uploadFile=f.raw}"><el-button plain>选择文件</el-button></el-upload></el-form-item>
      </el-form>
      <template #footer><el-button @click="showUpload = false">取消</el-button><el-button type="primary" @click="handleUpload">上传</el-button></template>
    </el-dialog>
    <el-dialog v-model="showPreview" title="文档预览" width="80%" :close-on-click-modal="false">
      <iframe v-if="previewUrl" :src="previewUrl" style="width:100%;height:70vh;border:none" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useProjectStore } from '../stores/project'
import type { ProjectDocument } from '../types'

const props = defineProps<{ projectId: number }>()
const store = useProjectStore()
const docs = ref<ProjectDocument[]>([])
const loading = ref(false); const keyword = ref('')
const showUpload = ref(false); const uploadCategory = ref(''); const uploadFile = ref<File | null>(null)
const showPreview = ref(false); const previewUrl = ref('')

async function loadDocs() {
  loading.value = true
  try { docs.value = await store.fetchDocuments(props.projectId) } finally { loading.value = false }
}
async function handleSearch() { await store.searchDocuments(props.projectId, keyword.value); docs.value = store.documents }
async function handleUpload() {
  if (!uploadFile.value) return
  await store.uploadDocument(props.projectId, uploadFile.value, uploadCategory.value)
  ElMessage.success('上传成功')
  showUpload.value = false
  uploadFile.value = null
  uploadCategory.value = ''
  loadDocs()
}
function handlePreview(id: number) { previewUrl.value = store.getPreviewUrl(id); showPreview.value = true }
async function handleDelete(id: number) {
  await ElMessageBox.confirm('确认删除该文档？', '提示', { type: 'warning' })
  await store.deleteDocument(id)
  ElMessage.success('已删除')
  loadDocs()
}
function formatSize(bytes: number) {
  if (!bytes) return '0 B'
  const units = ['B','KB','MB','GB']; let i = 0
  let size = bytes
  while (size >= 1024 && i < units.length - 1) { size /= 1024; i++ }
  return size.toFixed(1) + ' ' + units[i]
}
onMounted(loadDocs)
</script>

<style scoped>
.component { margin-bottom: 24px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.header h3 { margin: 0; }
.search-bar { margin-bottom: 12px; }
</style>
