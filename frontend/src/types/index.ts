export interface Product {
  id: number
  name: string
  code: string
  version: string
  description: string
  createdAt: string
  updatedAt: string
}

export interface ProductVersion {
  id: number
  productId: number
  versionNumber: string
  sortOrder: number
  createdAt: string
}

export interface Document {
  id: number
  productId: number
  versionId: number
  title: string
  category: string
  fileType: string
  fileSize: number
  filePath: string
  featurePoint: string
  currentVersion: number
  createdAt: string
  updatedAt: string
}

export interface DocumentVersion {
  id: number
  documentId: number
  versionNumber: number
  filePath: string
  fileSize: number
  createdAt: string
}

export interface ProductDetail extends Product {
  currentVersion: ProductVersion | null
  documents: Document[]
  versions: ProductVersion[]
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface Project {
  id: number
  name: string
  code: string
  description: string
  createdAt: string
  updatedAt: string
}

export interface ProjectMember {
  id: number
  projectId: number
  userId: number
  role: string
  joinedAt: string
}

export interface ProjectDetail extends Project {
  productCount: number
  products: Product[]
  members: ProjectMember[]
}

export interface ProjectDocument {
  id: number
  projectId: number
  title: string
  category: string
  fileType: string
  fileSize: number
  filePath: string
  currentVersion: number
  createdAt: string
  updatedAt: string
}

export interface ProjectDocumentVersion {
  id: number
  documentId: number
  versionNumber: number
  filePath: string
  fileSize: number
  createdAt: string
}
