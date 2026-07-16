CREATE DATABASE IF NOT EXISTS aikb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aikb;

-- 产品表
CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '产品名称',
    code VARCHAR(50) NOT NULL COMMENT '产品编码',
    version VARCHAR(20) NOT NULL COMMENT '当前版本号',
    description TEXT COMMENT '产品描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE INDEX idx_product_code (code),
    INDEX idx_product_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品表';

-- 产品版本表
CREATE TABLE IF NOT EXISTS product_version (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL COMMENT '所属产品ID',
    version_number VARCHAR(20) NOT NULL COMMENT '版本号',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '展示排序',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE INDEX idx_version_product (product_id, version_number),
    INDEX idx_version_sort (product_id, sort_order),
    CONSTRAINT fk_version_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品版本表';

-- 文档表
CREATE TABLE IF NOT EXISTS document (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL COMMENT '所属产品ID',
    version_id BIGINT NOT NULL COMMENT '关联的产品版本ID',
    title VARCHAR(200) NOT NULL COMMENT '文档标题',
    category VARCHAR(20) NOT NULL COMMENT '文档分类：TECHNICAL 或 BUSINESS',
    file_type VARCHAR(10) NOT NULL COMMENT '文件扩展名',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    file_path VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    feature_point VARCHAR(200) COMMENT '关联的功能点',
    current_version INT NOT NULL DEFAULT 1 COMMENT '最新版本号',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_doc_product (product_id, version_id),
    INDEX idx_doc_category (category),
    FULLTEXT INDEX idx_doc_title_fulltext (title),
    CONSTRAINT fk_doc_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_doc_version FOREIGN KEY (version_id) REFERENCES product_version(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档表';

-- 文档版本表
CREATE TABLE IF NOT EXISTS document_version (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL COMMENT '所属文档ID',
    version_number INT NOT NULL COMMENT '版本序号',
    file_path VARCHAR(500) NOT NULL COMMENT '该版本的存储路径',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE INDEX idx_docver_document (document_id, version_number),
    CONSTRAINT fk_docver_document FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档版本表';

-- 文档权限表
CREATE TABLE IF NOT EXISTS document_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL COMMENT '所属文档ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    permission_type VARCHAR(10) NOT NULL COMMENT '权限类型：READ 或 WRITE',
    granted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    UNIQUE INDEX idx_perm_document_user (document_id, user_id),
    CONSTRAINT fk_perm_document FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档权限表';

-- 扩展产品表：增加项目关联
ALTER TABLE product ADD COLUMN IF NOT EXISTS project_id BIGINT COMMENT '所属项目ID';

-- 项目表
CREATE TABLE IF NOT EXISTS project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '项目名称',
    code VARCHAR(50) NOT NULL COMMENT '项目编码',
    description TEXT COMMENT '项目描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE INDEX idx_project_code (code),
    INDEX idx_project_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

-- 项目成员表
CREATE TABLE IF NOT EXISTS project_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL COMMENT '所属项目ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role VARCHAR(20) NOT NULL COMMENT '角色：OWNER, DEVELOPER, OBSERVER',
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    UNIQUE INDEX idx_member_project_user (project_id, user_id),
    CONSTRAINT fk_member_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目成员表';

-- 项目文档表
CREATE TABLE IF NOT EXISTS project_document (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL COMMENT '所属项目ID',
    title VARCHAR(200) NOT NULL COMMENT '文档标题',
    category VARCHAR(20) NOT NULL COMMENT '文档分类：TECHNICAL 或 BUSINESS',
    file_type VARCHAR(10) NOT NULL COMMENT '文件扩展名',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    file_path VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    current_version INT NOT NULL DEFAULT 1 COMMENT '最新版本号',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_pdoc_project (project_id),
    INDEX idx_pdoc_category (category),
    FULLTEXT INDEX idx_pdoc_title_fulltext (title),
    CONSTRAINT fk_pdoc_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目文档表';

-- 项目文档版本表
CREATE TABLE IF NOT EXISTS project_document_version (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL COMMENT '所属文档ID',
    version_number INT NOT NULL COMMENT '版本序号',
    file_path VARCHAR(500) NOT NULL COMMENT '该版本的存储路径',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE INDEX idx_pdocver_document (document_id, version_number),
    CONSTRAINT fk_pdocver_document FOREIGN KEY (document_id) REFERENCES project_document(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目文档版本表';
