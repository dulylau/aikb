import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Document, DocumentVersion, PageResult } from '../types'
import api from '../api'

export const useDocumentStore = defineStore('document', () => {
  const documents = ref<Document[]>([])
  const currentDocument = ref<Document | null>(null)
  const versions = ref<DocumentVersion[]>([])
  const loading = ref(false)
  const total = ref(0)

  async function fetchDocumentsByVersion(productId: number, versionId: number) {
    loading.value = true
    try {
      const res = await api.get<{ data: PageResult<Document> }>('/documents/search', {
        params: { productId, page: 1, size: 100 },
      })
      documents.value = res.data.data.records
        .filter((d) => d.versionId === versionId)
        .sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime())
    } finally {
      loading.value = false
    }
  }

  async function uploadDocument(
    file: File,
    productId: number,
    versionId: number,
    category: string,
    featurePoint?: string
  ) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('productId', String(productId))
    formData.append('versionId', String(versionId))
    formData.append('category', category)
    if (featurePoint) {
      formData.append('featurePoint', featurePoint)
    }
    const res = await api.post<{ data: Document }>('/documents/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    return res.data.data
  }

  async function searchDocuments(keyword: string, productId?: number, category?: string, page = 1, size = 10) {
    loading.value = true
    try {
      const res = await api.get<{ data: PageResult<Document> }>('/documents/search', {
        params: { keyword, productId, category, page, size },
      })
      documents.value = res.data.data.records
      total.value = res.data.data.total
      return res.data.data
    } finally {
      loading.value = false
    }
  }

  async function fetchDocumentDetail(id: number) {
    const res = await api.get<{ data: Document }>(`/documents/${id}`)
    currentDocument.value = res.data.data
    return res.data.data
  }

  async function deleteDocument(id: number) {
    await api.delete(`/documents/${id}`)
  }

  async function fetchDocumentVersions(id: number) {
    const res = await api.get<{ data: DocumentVersion[] }>(`/documents/${id}/versions`)
    versions.value = res.data.data
    return res.data.data
  }

  async function uploadDocumentVersion(id: number, file: File) {
    const formData = new FormData()
    formData.append('file', file)
    const res = await api.post<{ data: DocumentVersion }>(`/documents/${id}/versions`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    return res.data.data
  }

  function getPreviewUrl(documentId: number, version?: number) {
    let url = `/api/documents/${documentId}/preview`
    if (version) {
      url += `?version=${version}`
    }
    return url
  }

  return {
    documents,
    currentDocument,
    versions,
    loading,
    total,
    fetchDocumentsByVersion,
    uploadDocument,
    searchDocuments,
    fetchDocumentDetail,
    deleteDocument,
    fetchDocumentVersions,
    uploadDocumentVersion,
    getPreviewUrl,
  }
})
