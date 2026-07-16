<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="project-form">
    <el-form-item label="项目名称" prop="name">
      <el-input v-model="form.name" placeholder="请输入项目名称" />
    </el-form-item>
    <el-form-item label="项目编码" prop="code">
      <el-input v-model="form.code" placeholder="请输入项目编码" :disabled="mode === 'edit'" />
    </el-form-item>
    <el-form-item label="项目描述">
      <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入项目描述" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        {{ mode === 'create' ? '创建' : '保存' }}
      </el-button>
      <el-button @click="$emit('cancel')">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { Project } from '../types'

const props = defineProps<{ project?: Project; mode: 'create' | 'edit' }>()
const emit = defineEmits<{ submit: [data: { name: string; code: string; description: string }]; cancel: [] }>()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive({ name: '', code: '', description: '' })
const rules: FormRules = {
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入项目编码', trigger: 'blur' }],
}

onMounted(() => {
  if (props.project) {
    form.name = props.project.name
    form.code = props.project.code
    form.description = props.project.description || ''
  }
})

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) { submitting.value = true; try { emit('submit', { ...form }) } finally { submitting.value = false } }
  })
}
</script>

<style scoped>
.project-form { max-width: 600px; }
</style>
