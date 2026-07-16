<template>
  <el-dialog
    v-model="visible"
    :title="document?.title || '文档预览'"
    fullscreen
    destroy-on-close
    @close="$emit('close')"
  >
    <div class="preview-toolbar">
      <el-button :icon="FullScreen ? 'Close' : 'FullScreen'" @click="FullScreen = !FullScreen">
        {{ FullScreen ? '还原' : '最大化' }}
      </el-button>
      <span v-if="document" class="file-info">
        {{ document.fileType.toUpperCase() }} | {{ formatFileSize(document.fileSize) }}
      </span>
    </div>
    <div class="preview-content" :class="{ maximized: FullScreen }">
      <iframe
        v-if="document?.fileType === 'pdf'"
        :src="previewUrl"
        class="preview-iframe"
      />
      <div v-else-if="document?.fileType === 'md'" class="markdown-preview">
        <pre>{{ markdownContent }}</pre>
      </div>
      <div v-else class="generic-preview">
        <p>文件类型: {{ document?.fileType?.toUpperCase() }}</p>
        <p>点击下方链接可直接下载查看：</p>
        <a :href="previewUrl" target="_blank">下载文件</a>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import type { Document } from '../types'
import { useDocumentStore } from '../stores/document'

const props = defineProps<{
  modelValue: boolean
  document: Document | null
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  close: []
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v),
})

const docStore = useDocumentStore()
const FullScreen = ref(true)
const markdownContent = ref('')

const previewUrl = computed(() => {
  if (!props.document) return ''
  return docStore.getPreviewUrl(props.document.id)
})

onMounted(async () => {
  if (props.document?.fileType === 'md') {
    try {
      const response = await fetch(previewUrl.value)
      markdownContent.value = await response.text()
    } catch {
      markdownContent.value = '无法加载 Markdown 内容'
    }
  }
})

function formatFileSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}
</script>

<style scoped>
.preview-toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}
.file-info {
  color: #909399;
  font-size: 13px;
}
.preview-content {
  height: calc(100vh - 160px);
  overflow: auto;
}
.preview-content.maximized {
  height: calc(100vh - 120px);
}
.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
}
.markdown-preview {
  padding: 16px;
  background: #fafafa;
  border-radius: 4px;
}
.markdown-preview pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 0;
  font-family: monospace;
  font-size: 14px;
}
.generic-preview {
  padding: 32px;
  text-align: center;
}
</style>
