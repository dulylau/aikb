<template>
  <div class="product-create-page">
    <div class="page-header">
      <h2>创建产品</h2>
    </div>
    <ProductForm mode="create" @submit="handleSubmit" @cancel="$router.push('/products')" />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useProductStore } from '../stores/product'
import ProductForm from '../components/ProductForm.vue'

const router = useRouter()
const store = useProductStore()

async function handleSubmit(data: { name: string; code: string; version: string; description: string }) {
  const product = await store.createProduct(data)
  router.push(`/products/${product.id}`)
}
</script>

<style scoped>
.product-create-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}
.page-header {
  margin-bottom: 24px;
}
</style>
