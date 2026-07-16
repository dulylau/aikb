<template>
  <div class="product-edit-page">
    <div class="page-header">
      <h2>编辑产品</h2>
    </div>
    <ProductForm
      v-if="product"
      mode="edit"
      :product="product"
      @submit="handleSubmit"
      @cancel="$router.push(`/products/${id}`)"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '../stores/product'
import type { Product } from '../types'
import ProductForm from '../components/ProductForm.vue'

const route = useRoute()
const router = useRouter()
const store = useProductStore()
const id = Number(route.params.id)
const product = ref<Product | null>(null)

onMounted(async () => {
  const detail = await store.fetchProductDetail(id)
  product.value = {
    id: detail.id,
    name: detail.name,
    code: detail.code,
    version: detail.version,
    description: detail.description,
    createdAt: '',
    updatedAt: '',
  }
})

async function handleSubmit(data: { name: string; code: string; version: string; description: string }) {
  await store.updateProduct(id, data)
  router.push(`/products/${id}`)
}
</script>

<style scoped>
.product-edit-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}
.page-header {
  margin-bottom: 24px;
}
</style>
