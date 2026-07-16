<template>
  <el-input
    v-model="keyword"
    :placeholder="placeholder"
    clearable
    @input="handleInput"
    @clear="handleClear"
  >
    <template #prefix>
      <el-icon><Search /></el-icon>
    </template>
  </el-input>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Search } from '@element-plus/icons-vue'

const props = defineProps<{
  placeholder?: string
}>()

const emit = defineEmits<{
  search: [keyword: string]
}>()

const keyword = ref('')
let timer: ReturnType<typeof setTimeout> | null = null

function handleInput() {
  if (timer) clearTimeout(timer)
  timer = setTimeout(() => {
    emit('search', keyword.value)
  }, 300)
}

function handleClear() {
  keyword.value = ''
  emit('search', '')
}
</script>
