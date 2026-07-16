<template>
  <div class="page"><div class="page-header"><h2>编辑项目</h2></div>
    <ProjectForm v-if="p" mode="edit" :project="p" @submit="handleSubmit" @cancel="$router.push(`/projects/${id}`)" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProjectStore } from '../stores/project'
import type { Project } from '../types'
import ProjectForm from '../components/ProjectForm.vue'
const route = useRoute(); const router = useRouter(); const store = useProjectStore()
const id = Number(route.params.id); const p = ref<Project | null>(null)
onMounted(async () => { const d = await store.fetchProjectDetail(id); p.value = { id: d.id, name: d.name, code: d.code, description: d.description, createdAt: '', updatedAt: '' } })
async function handleSubmit(data: { name: string; code: string; description: string }) { await store.updateProject(id, data); router.push(`/projects/${id}`) }
</script>

<style scoped>
.page { max-width: 800px; margin: 0 auto; padding: 24px; }
.page-header { margin-bottom: 24px; }
</style>
