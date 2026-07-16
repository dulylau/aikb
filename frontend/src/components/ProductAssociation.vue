<template>
  <div class="component">
    <div class="header"><h3>关联产品</h3><el-button type="primary" size="small" @click="showDialog = true; loadAvailable()">添加产品</el-button></div>
    <el-table v-loading="loading" :data="products" style="width:100%">
      <el-table-column prop="name" label="产品名称" />
      <el-table-column prop="code" label="产品编码" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }"><el-button type="danger" size="small" @click="handleRemove(row.id)">移除</el-button></template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="showDialog" title="添加产品" width="500px">
      <el-select v-model="selectedProduct" placeholder="请选择产品" style="width:100%">
        <el-option v-for="p in available" :key="p.id" :label="`${p.name} (${p.code})`" :value="p.id" />
      </el-select>
      <template #footer><el-button @click="showDialog = false">取消</el-button><el-button type="primary" @click="handleAdd" :loading="adding">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useProjectStore } from '../stores/project'
import type { Product } from '../types'

const props = defineProps<{ projectId: number; products: Product[] }>()
const emit = defineEmits<{ refresh: [] }>()
const store = useProjectStore()
const showDialog = ref(false); const selectedProduct = ref<number | null>(null)
const available = ref<Product[]>([]); const loading = ref(false); const adding = ref(false)

async function loadAvailable() {
  loading.value = true
  try { available.value = await store.fetchAvailableProducts() } finally { loading.value = false }
}
async function handleAdd() {
  if (!selectedProduct.value) return
  adding.value = true
  try {
    await store.addProduct(props.projectId, selectedProduct.value)
    ElMessage.success('产品关联成功')
    showDialog.value = false
    emit('refresh')
  } catch { adding.value = false; return }
  adding.value = false
}
async function handleRemove(productId: number) {
  await store.removeProduct(props.projectId, productId)
  ElMessage.success('产品已移除')
  emit('refresh')
}
</script>

<style scoped>
.component { margin-bottom: 24px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.header h3 { margin: 0; }
</style>
