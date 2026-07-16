<template>
  <div class="page">
    <div class="page-header"><h2>项目管理</h2><el-button type="primary" @click="$router.push('/projects/create')">创建项目</el-button></div>
    <el-empty v-if="!loading && projects.length === 0" description="暂无项目"><el-button type="primary" @click="$router.push('/projects/create')">创建第一个项目</el-button></el-empty>
    <el-row v-loading="loading" :gutter="16">
      <el-col v-for="p in projects" :key="p.id" :xs="24" :sm="12" :md="8" :lg="6">
        <ProjectCard :project="p" style="margin-bottom:16px" @click="$router.push(`/projects/${p.id}`)" />
      </el-col>
    </el-row>
    <div v-if="total > 12" class="pagination"><el-pagination v-model:current-page="cp" :page-size="12" :total="total" layout="prev,pager,next" @current-change="(v:number)=>store.fetchProjects(v)" /></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useProjectStore } from '../stores/project'
import ProjectCard from '../components/ProjectCard.vue'
const store = useProjectStore()
const { projects, loading, total } = storeToRefs(store)
const cp = ref(1)
onMounted(() => store.fetchProjects())
</script>

<style scoped>
.page { max-width: 1200px; margin: 0 auto; padding: 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h2 { margin: 0; }
.pagination { display: flex; justify-content: center; margin-top: 24px; }
</style>
