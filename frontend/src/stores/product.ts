import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Product, ProductDetail, ProductVersion, PageResult } from '../types'
import api from '../api'

export const useProductStore = defineStore('product', () => {
  const products = ref<Product[]>([])
  const currentProduct = ref<ProductDetail | null>(null)
  const versions = ref<ProductVersion[]>([])
  const loading = ref(false)
  const total = ref(0)

  async function fetchProducts(page = 1, size = 12) {
    loading.value = true
    try {
      const res = await api.get<{ data: PageResult<Product> }>('/products', {
        params: { page, size },
      })
      products.value = res.data.data.records
      total.value = res.data.data.total
    } finally {
      loading.value = false
    }
  }

  async function fetchProductDetail(id: number) {
    loading.value = true
    try {
      const res = await api.get<{ data: ProductDetail }>(`/products/${id}`)
      currentProduct.value = res.data.data
      versions.value = res.data.data.versions || []
      return res.data.data
    } finally {
      loading.value = false
    }
  }

  async function createProduct(data: Partial<Product>) {
    const res = await api.post<{ data: Product }>('/products', data)
    return res.data.data
  }

  async function updateProduct(id: number, data: Partial<Product>) {
    const res = await api.put<{ data: Product }>(`/products/${id}`, data)
    return res.data.data
  }

  async function deleteProduct(id: number) {
    await api.delete(`/products/${id}`)
  }

  async function fetchVersions(productId: number) {
    const res = await api.get<{ data: ProductVersion[] }>(`/products/${productId}/versions`)
    versions.value = res.data.data
    return res.data.data
  }

  async function createVersion(productId: number, versionNumber: string) {
    const res = await api.post<{ data: ProductVersion }>(`/products/${productId}/versions`, {
      versionNumber,
    })
    return res.data.data
  }

  return {
    products,
    currentProduct,
    versions,
    loading,
    total,
    fetchProducts,
    fetchProductDetail,
    createProduct,
    updateProduct,
    deleteProduct,
    fetchVersions,
    createVersion,
  }
})
