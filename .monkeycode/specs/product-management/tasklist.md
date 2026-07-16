# 需求实施计划

- [ ] 1. 后端项目初始化与基础设施搭建
  - [x] 1.1 创建 Spring Boot 3 + Maven 项目骨架
    - 使用 Spring Initializr 创建项目，包含 Spring Web、MyBatis-Plus、MySQL Driver、Lombok 依赖
    - 配置 `application.yml`：数据源连接、文件上传大小限制（50MB）、MyBatis-Plus 驼峰映射
    - 创建项目包结构：`controller`、`service`、`mapper`、`entity`、`dto`、`config`
    - 参考设计文档 Architecture 中 Backend Module Structure 的分层结构

  - [x] 1.2 创建数据库迁移脚本与 MyBatis-Plus 实体类
    - 编写 `schema.sql` 创建 5 张表：product、product_version、document、document_version、document_permission
    - 创建索引：product.code UNIQUE、product_version(product_id, version_number) UNIQUE、document.title FULLTEXT、document_permission(document_id, user_id) UNIQUE
    - 创建 5 个实体类，字段映射设计文档 Table Definitions 中每列定义
    - 创建 5 个 Mapper 接口继承 `BaseMapper<T>`
    - 参考设计文档 Data Models 中 ER Diagram 和 Table Definitions (R1-R12 数据基础)

  - [ ]* 1.3 为数据层编写单元测试
    - 编写 ProductMapper 和 ProductVersionMapper 测试：验证 CRUD 操作和唯一约束
    - 编写 DocumentMapper 和 DocumentVersionMapper 测试：验证关联查询和版本号递增
    - 编写 DocumentPermissionMapper 测试：验证 (document_id, user_id) 唯一约束
    - 参考设计文档 Correctness Properties 中的不变式 (Invariants 1,2,4,6)

  - [x] 1.4 实现统一异常处理与 API 响应格式
    - 定义统一响应体 `ApiResponse<T>`（code、message、timestamp、details）
    - 实现 `GlobalExceptionHandler` 处理 9 种错误场景：VALIDATION_ERROR、PRODUCT_CODE_DUPLICATE、PRODUCT_NOT_FOUND 等
    - 配置 MyBatis-Plus 分页插件
    - 参考设计文档 Error Handling 中的 HTTP Status Codes 和 API Error Response Format

- [ ] 2. 检查点 - 后端基础设施
  - 确认 Maven 编译通过，数据库表创建成功，异常处理响应格式正确

- [ ] 3. 后端 - 产品管理模块实现
  - [x] 3.1 实现产品 CRUD API（R1、R2、R4）
    - 创建 `ProductCreateDTO` 和 `ProductUpdateDTO`，添加 `@NotBlank` 校验 name 和 code 字段
    - 实现 `ProductService`：创建时校验 code 唯一性（Invariant 1）、事务中创建 product + 初始 product_version（Transactional Boundary 1）
    - 实现 `ProductController`：GET `/api/products` 分页列表、GET `/api/products/{id}` 详情、POST `/api/products` 创建、PUT `/api/products/{id}` 更新、DELETE `/api/products/{id}` 删除
    - 更新 product.version 与 product.updated_at 时间戳实现乐观锁（Concurrency）

  - [x] 3.2 实现产品版本管理 API（R3、R5）
    - 实现 `ProductVersionService`：创建版本时校验同一 product 内 version_number 唯一性（Invariant 2）、按 sort_order 排序返回
    - 实现 `ProductVersionController`：GET `/api/products/{id}/versions` 列表、POST `/api/products/{id}/versions` 创建
    - GET `/api/products/{id}` 返回 `ProductDetailVO` 包含当前版本和关联文档列表

  - [ ]* 3.3 为产品管理 API 编写集成测试
    - 使用 MockMvc 测试：创建产品成功返回 201、重复 code 返回 409、无产品时返回空列表、编辑产品后验证字段更新
    - 测试事务边界：产品创建失败时不产生孤立 product_version 记录
    - 参考设计文档 Test Strategy 中 Key Test Scenarios (1, 2, 9)

- [ ] 4. 检查点 - 产品管理后端
  - 确认产品 CRUD 和版本管理 API 全部可用，唯一约束和事务边界正确

- [ ] 5. 后端 - 文档管理模块实现
  - [x] 5.1 实现文件存储服务（R7）
  - [x] 5.2 实现文档管理 API（R6、R7、R9）
  - [x] 5.3 实现文档版本管理 API（R8）
  - [x] 5.4 实现文档搜索 API（R11）
  - [x] 5.5 实现文档预览 API（R10）
  - [x] 5.6 实现文档权限管理 API（R12）
    - 实现 `PermissionService`：按 (document_id, user_id) 管理 READ/WRITE 权限（Invariant 6）
    - 实现 `PermissionController`：GET `/api/documents/{id}/permissions` 查询权限列表、PUT `/api/documents/{id}/permissions` 批量更新权限
    - 在 `DocumentService` 的查询和上传方法中注入权限校验：READ 权限才能查看文档内容，WRITE 权限才能修改/删除文档

  - [ ]* 5.7 为文档管理 API 编写集成测试
    - 测试文档上传：支持格式上传成功、不支持格式返回 400、超大文件返回 400
    - 测试文档版本：上传新版本后验证 current_version 递增、历史版本可查询
    - 测试文档预览：PDF/Markdown/Word 格式分别正确渲染
    - 测试搜索：关键词匹配返回结果、无匹配返回空列表
    - 测试权限：无权限用户访问返回 403
    - 参考设计文档 Test Strategy 中 Key Test Scenarios (2-8)

- [ ] 6. 检查点 - 文档管理后端
  - 确认文档上传、版本管理、搜索、预览、权限 API 全部可用

- [ ] 7. 前端项目初始化与基础设施
  - [x] 7.1 创建 Vue 3 + Vite + TypeScript 项目
  - [x] 7.2 配置路由、状态管理与 HTTP 客户端
  - [x] 8.1 实现产品卡片组件与产品列表页（R1）
  - [x] 8.2 实现产品表单组件与创建/编辑页（R2、R4）
  - [x] 8.3 实现产品详情页与版本选择器（R3、R5）
  - [x] 10.1 实现文档上传组件（R6、R7、R9）
  - [x] 10.2 实现文档列表组件（R3、R6、R8）
  - [x] 10.3 实现文档预览组件与搜索组件（R10、R11）
  - [x] 10.4 实现文档权限管理界面（R12）
    - 在 DocumentList 中每个文档项增加权限设置按钮
    - 点击后弹出 `ElDialog` 显示当前权限列表（调用 GET `/api/documents/{id}/permissions`）
    - 支持添加用户权限（选择 user、选择 READ/WRITE）和删除已有权限
    - 保存时调用 PUT `/api/documents/{id}/permissions`，Permission denied 时 ElMessage 显示错误

  - [ ]* 10.5 为文档管理组件编写单元测试
    - 测试 DocumentUpload 组件：文件格式过滤、大小校验、上传成功回调
    - 测试 DocumentPreview 组件：最大化/还原切换、PDF/Markdown 渲染
    - 测试 SearchBar 组件：debounce 行为和搜索结果渲染

- [ ] 11. 检查点 - 文档管理前端
  - 确认文档上传、分类展示、在线预览、关键词搜索、权限管理全部功能可用

- [ ] 12. 集成与收尾
  - [x] 12.1 配置 Nginx 反向代理
    - 创建 `nginx.conf`：前端静态文件 root 指向 Vite build 产物目录，`/api/*` 代理到 Spring Boot 8080 端口
    - 配置 CORS 头允许前端跨域请求

  - [x] 12.2 集成 Knife4j API 文档
    - 在 Spring Boot 中引入 `knife4j-openapi3` 依赖
    - 配置 SwaggerUI 路径和分组，自动扫描 Controller 生成 API 文档

  - [x] 12.3 配置前端 Vite 构建输出与 allowedHosts
    - 配置 `vite.config.ts` 的 `server.allowedHosts` 添加 `.monkeycode-ai.online`
    - 配置 `build.outDir` 指向 Nginx 静态文件目录
