<template>
  <el-card class="product-card" shadow="hover" @click="$emit('click')">
    <template #header>
      <div class="card-header">
        <span class="product-name">{{ product.name }}</span>
        <el-tag size="small">{{ product.code }}</el-tag>
      </div>
    </template>
    <div class="card-body">
      <div class="version-info">
        <el-tag type="success" size="small">v{{ product.version }}</el-tag>
      </div>
      <p class="description">{{ product.description || '暂无描述' }}</p>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import type { Product } from '../types'

defineProps<{
  product: Product
}>()

defineEmits<{
  click: []
}>()
</script>

<style scoped>
.product-card {
  cursor: pointer;
  transition: transform 0.2s;
  height: 100%;
}
.product-card:hover {
  transform: translateY(-2px);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.product-name {
  font-size: 16px;
  font-weight: 600;
}
.card-body {
  min-height: 60px;
}
.version-info {
  margin-bottom: 8px;
}
.description {
  color: #909399;
  font-size: 13px;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
