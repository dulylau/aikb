<template>
  <div class="product-detail-page" v-loading="store.loading">
    <div class="page-header">
      <el-button text @click="$router.push('/products')">
        <el-icon><ArrowLeft /></el-icon>
        返回列表
      </el-button>
      <el-button type="primary" @click="$router.push(`/products/${id}/edit`)">
        编辑产品
      </el-button>
    </div>

    <template v-if="store.currentProduct">
      <div class="product-info">
        <h2>{{ store.currentProduct.name }}</h2>
        <div class="info-tags">
          <el-tag>{{ store.currentProduct.code }}</el-tag>
          <el-tag type="success">v{{ store.currentProduct.version }}</el-tag>
        </div>
        <p class="info-desc">{{ store.currentProduct.description || '暂无描述' }}</p>
      </div>

      <div v-if="versions.length > 0" class="version-bar">
        <VersionSelector
          v-model="selectedVersion"
          :versions="versions"
        />
      </div>

      <div v-if="currentVersionId" class="documents-section">
        <h3>产品文档</h3>
        <DocumentList
          :product-id="id"
          :version-id="currentVersionId"
        />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { storeToRefs } from 'pinia'
import type { ProductVersion } from '../types'
import { useProductStore } from '../stores/product'
import VersionSelector from '../components/VersionSelector.vue'
import DocumentList from '../components/DocumentList.vue'

const route = useRoute()
const store = useProductStore()
const { versions } = storeToRefs(store)

const id = Number(route.params.id)
const selectedVersion = ref<ProductVersion | null>(null)

const currentVersionId = computed(() => selectedVersion.value?.id || null)

onMounted(async () => {
  const detail = await store.fetchProductDetail(id)
  if (detail.currentVersion) {
    selectedVersion.value = detail.currentVersion
  } else if (detail.versions?.length) {
    selectedVersion.value = detail.versions[0]
  }
})
</script>

<style scoped>
.product-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.product-info {
  margin-bottom: 24px;
  padding: 24px;
  background: #f5f7fa;
  border-radius: 8px;
}
.product-info h2 {
  margin: 0 0 12px 0;
}
.info-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}
.info-desc {
  color: #606266;
  margin: 0;
}
.version-bar {
  margin-bottom: 16px;
}
.documents-section h3 {
  margin: 0 0 16px 0;
}
</style>
