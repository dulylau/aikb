<template>
  <div class="document-list">
    <div class="list-toolbar">
      <SearchBar placeholder="搜索文档..." @search="handleSearch" style="width: 300px" />
      <el-button type="primary" @click="showUpload = true">
        上传文档
      </el-button>
    </div>

    <div v-if="technicalDocs.length > 0 || businessDocs.length > 0">
      <div v-if="technicalDocs.length > 0" class="doc-group">
        <h4 class="group-title">技术类文档</h4>
        <div
          v-for="doc in technicalDocs"
          :key="doc.id"
          class="doc-item"
        >
          <div class="doc-info" @click="handlePreview(doc)">
            <span class="doc-icon">{{ getFileIcon(doc.fileType) }}</span>
            <span class="doc-title">{{ doc.title }}</span>
            <el-tag size="small" type="info">v{{ doc.currentVersion }}</el-tag>
          </div>
          <div class="doc-actions">
            <el-button size="small" text @click="showVersions(doc)">版本历史</el-button>
            <el-button size="small" text type="danger" @click="handleDelete(doc)">删除</el-button>
          </div>
        </div>
      </div>

      <div v-if="businessDocs.length > 0" class="doc-group">
        <h4 class="group-title">业务类文档</h4>
        <div
          v-for="doc in businessDocs"
          :key="doc.id"
          class="doc-item"
        >
          <div class="doc-info" @click="handlePreview(doc)">
            <span class="doc-icon">{{ getFileIcon(doc.fileType) }}</span>
            <span class="doc-title">{{ doc.title }}</span>
            <el-tag size="small" type="info">v{{ doc.currentVersion }}</el-tag>
          </div>
          <div class="doc-actions">
            <el-button size="small" text @click="showVersions(doc)">版本历史</el-button>
            <el-button size="small" text type="danger" @click="handleDelete(doc)">删除</el-button>
          </div>
        </div>
      </div>
    </div>

    <el-empty v-else description="暂无文档" />

    <el-dialog v-model="versionDialogVisible" title="版本历史" width="500px">
      <el-timeline v-if="docVersions.length > 0">
        <el-timeline-item
          v-for="v in docVersions"
          :key="v.id"
          :timestamp="v.createdAt"
          placement="top"
        >
          <div style="display: flex; justify-content: space-between; align-items: center">
            <span>版本 {{ v.versionNumber }}</span>
            <el-button size="small" text type="primary" @click="previewVersion(v)">预览</el-button>
          </div>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无版本历史" />
    </el-dialog>

    <DocumentUpload
      v-model="showUpload"
      :product-id="productId"
      :version-id="versionId"
      @uploaded="handleUploaded"
    />

    <DocumentPreview
      v-model="previewVisible"
      :document="previewDocument"
      @close="previewDocument = null"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Document, DocumentVersion } from '../types'
import { useDocumentStore } from '../stores/document'
import SearchBar from './SearchBar.vue'
import DocumentUpload from './DocumentUpload.vue'
import DocumentPreview from './DocumentPreview.vue'

const props = defineProps<{
  productId: number
  versionId: number
}>()

const docStore = useDocumentStore()

const showUpload = ref(false)
const previewVisible = ref(false)
const previewDocument = ref<Document | null>(null)
const versionDialogVisible = ref(false)
const docVersions = ref<DocumentVersion[]>([])
const searchKeyword = ref('')

const technicalDocs = computed(() =>
  docStore.documents.filter((d) => d.category === 'TECHNICAL')
)
const businessDocs = computed(() =>
  docStore.documents.filter((d) => d.category === 'BUSINESS')
)

function getFileIcon(fileType: string): string {
  const icons: Record<string, string> = { pdf: '[PDF]', doc: '[DOC]', docx: '[DOC]', md: '[MD]' }
  return icons[fileType.toLowerCase()] || '[FILE]'
}

async function handleSearch(keyword: string) {
  searchKeyword.value = keyword
  if (keyword) {
    await docStore.searchDocuments(keyword, props.productId)
  } else {
    await docStore.fetchDocumentsByVersion(props.productId, props.versionId)
  }
}

function handlePreview(doc: Document) {
  previewDocument.value = doc
  previewVisible.value = true
}

async function handleDelete(doc: Document) {
  try {
    await ElMessageBox.confirm(`确定要删除文档 "${doc.title}" 吗？`, '确认删除', {
      type: 'warning',
    })
    await docStore.deleteDocument(doc.id)
    ElMessage.success('删除成功')
    await loadDocuments()
  } catch {
    // cancelled
  }
}

async function showVersions(doc: Document) {
  previewDocument.value = doc
  docVersions.value = await docStore.fetchDocumentVersions(doc.id)
  versionDialogVisible.value = true
}

function previewVersion(version: DocumentVersion) {
  if (previewDocument.value) {
    const previewUrl = docStore.getPreviewUrl(previewDocument.value.id, version.versionNumber)
    window.open(previewUrl, '_blank')
  }
}

async function handleUploaded() {
  await loadDocuments()
}

async function loadDocuments() {
  if (props.productId && props.versionId) {
    await docStore.fetchDocumentsByVersion(props.productId, props.versionId)
  }
}

watch(
  () => [props.productId, props.versionId],
  () => loadDocuments(),
  { immediate: true }
)
</script>

<style scoped>
.document-list {
  margin-top: 16px;
}
.list-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.doc-group {
  margin-bottom: 24px;
}
.group-title {
  font-size: 15px;
  color: #303133;
  margin: 0 0 8px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}
.doc-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-radius: 6px;
  background: #fafafa;
  margin-bottom: 8px;
}
.doc-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex: 1;
}
.doc-icon {
  color: #409eff;
  font-size: 12px;
  font-weight: 600;
}
.doc-title {
  font-size: 14px;
}
.doc-actions {
  display: flex;
  gap: 4px;
}
</style>
