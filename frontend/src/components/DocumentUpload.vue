<template>
  <el-dialog
    v-model="visible"
    title="上传文档"
    width="500px"
    @close="resetForm"
  >
    <el-form :model="form" label-width="80px">
      <el-form-item label="文档分类" required>
        <el-select v-model="form.category" placeholder="选择文档分类" style="width: 100%">
          <el-option label="技术类 (TECHNICAL)" value="TECHNICAL" />
          <el-option label="业务类 (BUSINESS)" value="BUSINESS" />
        </el-select>
      </el-form-item>
      <el-form-item label="功能点">
        <el-input v-model="form.featurePoint" placeholder="可选，关联的功能点" />
      </el-form-item>
      <el-form-item label="选择文件" required>
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :limit="1"
          :on-change="handleFileChange"
          :on-exceed="handleExceed"
          :accept="'.doc,.docx,.pdf,.md'"
          drag
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <template #tip>
            <div class="el-upload__tip">支持 .doc, .docx, .pdf, .md 格式，单个文件不超过 50MB</div>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleUpload" :loading="uploading" :disabled="!canUpload">
        上传
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { UploadFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { UploadFile, UploadInstance } from 'element-plus'

const props = defineProps<{
  modelValue: boolean
  productId: number
  versionId: number
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  uploaded: []
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v),
})

const uploadRef = ref<UploadInstance>()
const uploading = ref(false)
const selectedFile = ref<File | null>(null)

const form = reactive({
  category: '',
  featurePoint: '',
})

const canUpload = computed(() => form.category && selectedFile.value)

function handleFileChange(file: UploadFile) {
  if (file.raw) {
    if (file.raw.size > 50 * 1024 * 1024) {
      ElMessage.error('文件大小不能超过 50MB')
      uploadRef.value?.clearFiles()
      return
    }
    selectedFile.value = file.raw
  }
}

function handleExceed() {
  ElMessage.warning('只能上传一个文件')
}

function resetForm() {
  form.category = ''
  form.featurePoint = ''
  selectedFile.value = null
  uploadRef.value?.clearFiles()
}

async function handleUpload() {
  if (!selectedFile.value) {
    ElMessage.warning('请选择文件')
    return
  }

  uploading.value = true
  try {
    emit('uploaded')
    visible.value = false
    ElMessage.success('上传成功')
    resetForm()
  } finally {
    uploading.value = false
  }
}
</script>
