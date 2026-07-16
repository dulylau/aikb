<template>
  <div class="page" v-loading="loading">
    <div class="page-header">
      <div><el-button text @click="$router.push('/projects')">← 返回</el-button><h2>{{ p?.name }}</h2></div>
      <div><el-button @click="$router.push(`/projects/${id}/edit`)">编辑</el-button><el-button type="danger" @click="handleDelete">删除</el-button></div>
    </div>
    <el-descriptions v-if="p" :column="2" border style="margin-bottom:24px">
      <el-descriptions-item label="项目编码">{{ p.code }}</el-descriptions-item>
      <el-descriptions-item label="产品数量">{{ p.productCount }}</el-descriptions-item>
      <el-descriptions-item label="描述" :span="2">{{ p.description || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="关联产品" name="products">
        <ProductAssociation v-if="p" :projectId="id" :products="p.products" @refresh="loadDetail" />
      </el-tab-pane>
      <el-tab-pane label="项目成员" name="members">
        <MemberManager v-if="p" :projectId="id" :members="p.members" />
      </el-tab-pane>
      <el-tab-pane label="项目文档" name="documents">
        <ProjectDocumentList :projectId="id" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useProjectStore } from '../stores/project'
import type { ProjectDetail } from '../types'
import ProductAssociation from '../components/ProductAssociation.vue'
import MemberManager from '../components/MemberManager.vue'
import ProjectDocumentList from '../components/ProjectDocumentList.vue'

const route = useRoute(); const router = useRouter(); const store = useProjectStore()
const id = Number(route.params.id); const p = ref<ProjectDetail | null>(null)
const loading = ref(false); const activeTab = ref('products')

async function loadDetail() {
  loading.value = true
  try { p.value = await store.fetchProjectDetail(id) } finally { loading.value = false }
}
async function handleDelete() {
  await ElMessageBox.confirm('确认删除该项目？', '提示', { type: 'warning' })
  await store.deleteProject(id)
  ElMessage.success('项目已删除')
  router.push('/projects')
}
onMounted(loadDetail)
</script>

<style scoped>
.page { max-width: 1100px; margin: 0 auto; padding: 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h2 { margin: 8px 0 0; }
</style>
