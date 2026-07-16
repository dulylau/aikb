<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    label-width="100px"
    class="product-form"
  >
    <el-form-item label="产品名称" prop="name">
      <el-input v-model="form.name" placeholder="请输入产品名称" />
    </el-form-item>
    <el-form-item label="产品编码" prop="code">
      <el-input
        v-model="form.code"
        placeholder="请输入产品编码"
        :disabled="mode === 'edit'"
      />
    </el-form-item>
    <el-form-item label="版本号" prop="version">
      <el-input v-model="form.version" placeholder="请输入版本号，如 1.0.0" />
    </el-form-item>
    <el-form-item label="产品描述">
      <el-input
        v-model="form.description"
        type="textarea"
        :rows="3"
        placeholder="请输入产品描述"
      />
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
import type { Product } from '../types'

const props = defineProps<{
  product?: Product
  mode: 'create' | 'edit'
}>()

const emit = defineEmits<{
  submit: [data: { name: string; code: string; version: string; description: string }]
  cancel: []
}>()

const formRef = ref<FormInstance>()
const submitting = ref(false)

const form = reactive({
  name: '',
  code: '',
  version: '1.0.0',
  description: '',
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入产品编码', trigger: 'blur' }],
  version: [{ required: true, message: '请输入版本号', trigger: 'blur' }],
}

onMounted(() => {
  if (props.product) {
    form.name = props.product.name
    form.code = props.product.code
    form.version = props.product.version
    form.description = props.product.description || ''
  }
})

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        emit('submit', { ...form })
      } finally {
        submitting.value = false
      }
    }
  })
}
</script>

<style scoped>
.product-form {
  max-width: 600px;
}
</style>
