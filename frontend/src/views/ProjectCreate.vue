<template>
  <div class="page"><div class="page-header"><h2>创建项目</h2></div><ProjectForm mode="create" @submit="handleSubmit" @cancel="$router.push('/projects')" /></div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useProjectStore } from '../stores/project'
import ProjectForm from '../components/ProjectForm.vue'
const router = useRouter()
const store = useProjectStore()
async function handleSubmit(data: { name: string; code: string; description: string }) {
  const p = await store.createProject(data)
  router.push(`/projects/${p.id}`)
}
</script>

<style scoped>
.page { max-width: 800px; margin: 0 auto; padding: 24px; }
.page-header { margin-bottom: 24px; }
</style>
