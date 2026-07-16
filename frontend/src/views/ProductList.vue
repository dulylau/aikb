<template>
  <div class="product-list-page">
    <div class="page-header">
      <h2>产品管理</h2>
      <el-button type="primary" @click="$router.push('/products/create')">
        创建产品
      </el-button>
    </div>

    <el-empty v-if="!loading && products.length === 0" description="暂无产品">
      <el-button type="primary" @click="$router.push('/products/create')">
        创建第一个产品
      </el-button>
    </el-empty>

    <el-row v-loading="loading" :gutter="16">
      <el-col v-for="product in products" :key="product.id" :xs="24" :sm="12" :md="8" :lg="6">
        <ProductCard
          :product="product"
          style="margin-bottom: 16px"
          @click="$router.push(`/products/${product.id}`)"
        />
      </el-col>
    </el-row>

    <div v-if="total > 12" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="12"
        :total="total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useProductStore } from '../stores/product'
import ProductCard from '../components/ProductCard.vue'

const store = useProductStore()
const currentPage = ref(1)

const { products, loading, total } = storeToRefs(store)

function handlePageChange(page: number) {
  currentPage.value = page
  store.fetchProducts(page)
}

onMounted(() => {
  store.fetchProducts()
})
</script>

<script lang="ts">
import { storeToRefs } from 'pinia'
export default { inheritAttrs: false }
</script>

<style scoped>
.product-list-page {
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
.page-header h2 {
  margin: 0;
}
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
