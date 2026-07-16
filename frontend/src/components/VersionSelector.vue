<template>
  <el-select
    :model-value="modelValue?.id"
    placeholder="选择版本"
    @update:model-value="handleChange"
    style="width: 200px"
  >
    <el-option
      v-for="v in versions"
      :key="v.id"
      :label="`v${v.versionNumber}`"
      :value="v.id"
    />
  </el-select>
</template>

<script setup lang="ts">
import type { ProductVersion } from '../types'

const props = defineProps<{
  versions: ProductVersion[]
  modelValue: ProductVersion | null
}>()

const emit = defineEmits<{
  'update:modelValue': [version: ProductVersion]
}>()

function handleChange(versionId: number) {
  const version = props.versions.find((v) => v.id === versionId)
  if (version) {
    emit('update:modelValue', version)
  }
}
</script>
