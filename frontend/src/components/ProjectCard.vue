<template>
  <el-card class="project-card" shadow="hover" @click="$emit('click')">
    <template #header>
      <div class="card-header">
        <span class="project-name">{{ project.name }}</span>
        <el-tag size="small">{{ project.code }}</el-tag>
      </div>
    </template>
    <div class="card-body">
      <p class="description">{{ project.description || '暂无描述' }}</p>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import type { Project } from '../types'

defineProps<{ project: Project }>()
defineEmits<{ click: [] }>()
</script>

<style scoped>
.project-card { cursor: pointer; transition: transform 0.2s; height: 100%; }
.project-card:hover { transform: translateY(-2px); }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.project-name { font-size: 16px; font-weight: 600; }
.description { color: #909399; font-size: 13px; margin: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
