import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Project, ProjectDetail, ProjectMember, ProjectDocument, ProjectDocumentVersion, Product, PageResult } from '../types'
import api from '../api'

export const useProjectStore = defineStore('project', () => {
  const projects = ref<Project[]>([])
  const currentProject = ref<ProjectDetail | null>(null)
  const loading = ref(false)
  const total = ref(0)
  const documents = ref<ProjectDocument[]>([])
  const availableProducts = ref<Product[]>([])

  async function fetchProjects(page = 1, size = 12) {
    loading.value = true
    try {
      const res = await api.get<{ data: PageResult<Project> }>('/projects', { params: { page, size } })
      projects.value = res.data.data.records
      total.value = res.data.data.total
    } finally {
      loading.value = false
    }
  }

  async function fetchProjectDetail(id: number) {
    loading.value = true
    try {
      const res = await api.get<{ data: ProjectDetail }>(`/projects/${id}`)
      currentProject.value = res.data.data
      return res.data.data
    } finally {
      loading.value = false
    }
  }

  async function createProject(data: Partial<Project>) {
    const res = await api.post<{ data: Project }>('/projects', data)
    return res.data.data
  }

  async function updateProject(id: number, data: Partial<Project>) {
    const res = await api.put<{ data: Project }>(`/projects/${id}`, data)
    return res.data.data
  }

  async function deleteProject(id: number) {
    await api.delete(`/projects/${id}`)
  }

  async function addProduct(projectId: number, productId: number) {
    const res = await api.post<{ data: Product }>(`/projects/${projectId}/products`, { productId })
    return res.data.data
  }

  async function removeProduct(projectId: number, productId: number) {
    await api.delete(`/projects/${projectId}/products/${productId}`)
  }

  async function fetchAvailableProducts() {
    const res = await api.get<{ data: Product[] }>('/products/available')
    availableProducts.value = res.data.data
    return res.data.data
  }

  async function fetchMembers(projectId: number) {
    const res = await api.get<{ data: ProjectMember[] }>(`/projects/${projectId}/members`)
    return res.data.data
  }

  async function addMember(projectId: number, userId: number, role: string) {
    const res = await api.post<{ data: ProjectMember }>(`/projects/${projectId}/members`, { userId, role })
    return res.data.data
  }

  async function updateMemberRole(projectId: number, userId: number, role: string) {
    await api.put(`/projects/${projectId}/members/${userId}`, { role })
  }

  async function removeMember(projectId: number, userId: number) {
    await api.delete(`/projects/${projectId}/members/${userId}`)
  }

  async function fetchDocuments(projectId: number) {
    const res = await api.get<{ data: ProjectDocument[] }>(`/projects/${projectId}/documents`)
    documents.value = res.data.data
    return res.data.data
  }

  async function uploadDocument(projectId: number, file: File, category: string) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('category', category)
    const res = await api.post<{ data: ProjectDocument }>(`/projects/${projectId}/documents/upload`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    return res.data.data
  }

  async function searchDocuments(projectId: number, keyword: string, category?: string) {
    const res = await api.get<{ data: PageResult<ProjectDocument> }>(`/projects/${projectId}/documents/search`, {
      params: { keyword, category },
    })
    documents.value = res.data.data.records
    return res.data.data
  }

  async function deleteDocument(documentId: number) {
    await api.delete(`/documents/project/${documentId}`)
  }

  async function fetchDocumentVersions(documentId: number) {
    const res = await api.get<{ data: ProjectDocumentVersion[] }>(`/documents/project/${documentId}/versions`)
    return res.data.data
  }

  async function uploadDocumentVersion(documentId: number, file: File) {
    const formData = new FormData()
    formData.append('file', file)
    const res = await api.post<{ data: ProjectDocumentVersion }>(`/documents/project/${documentId}/versions`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    return res.data.data
  }

  function getPreviewUrl(documentId: number, version?: number) {
    let url = `/api/documents/project/${documentId}/preview`
    if (version) url += `?version=${version}`
    return url
  }

  return {
    projects, currentProject, loading, total, documents, availableProducts,
    fetchProjects, fetchProjectDetail, createProject, updateProject, deleteProject,
    addProduct, removeProduct, fetchAvailableProducts,
    fetchMembers, addMember, updateMemberRole, removeMember,
    fetchDocuments, uploadDocument, searchDocuments, deleteDocument,
    fetchDocumentVersions, uploadDocumentVersion, getPreviewUrl,
  }
})
