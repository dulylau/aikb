# 需求实施计划

- [ ] 1. 后端 - 扩展 product 表与新增数据表
  - [x] 1.1 创建数据库迁移脚本：新增 project、project_member、project_document、project_document_version 表，product 表增加 project_id 字段
  - [x] 1.2 创建新实体类：Project、ProjectMember、ProjectDocument、ProjectDocumentVersion
  - [x] 1.3 更新 Product 实体类增加 projectId 字段
  - [x] 1.4 创建新 Mapper 接口：ProjectMapper、ProjectMemberMapper、ProjectDocumentMapper、ProjectDocumentVersionMapper
  - 参考设计文档 Data Models

- [ ] 2. 后端 - 项目管理 API（R1、R2、R3、R4、R5）
  - [x] 2.1 创建 DTO：ProjectCreateDTO、ProjectUpdateDTO、ProjectDetailVO
  - [x] 2.2 实现 ProjectService：CRUD + 编码唯一性校验 + 删除前检查产品关联
  - [x] 2.3 实现 ProjectController：5 个端点
  - [x] 3.1 实现产品关联：添加产品到项目、移除产品、获取项目下产品列表
  - [x] 3.2 实现成员管理：添加成员、修改角色、移除成员、获取成员列表
  - [x] 3.3 在 ProductController 新增 GET /api/products/available 端点
  - [x] 4.1 实现 ProjectDocumentService：上传、列表、搜索、预览、版本管理
  - [x] 4.2 实现 ProjectDocumentController：8 个端点
  - 参考设计文档 项目文档端点

- [ ] 5. 检查点 - 后端编译验证

- [ ] 6. 前端 - 项目路由与 Store
  - [ ] 6.1 新增路由：/projects、/projects/create、/projects/:id、/projects/:id/edit
  - [ ] 6.2 创建 useProjectStore：projects、members、项目文档状态管理
  - [ ] 6.3 更新 App.vue 增加顶部导航切换

- [ ] 7. 前端 - 项目管理页面（R1-R5）
  - [ ] 7.1 实现 ProjectCard 组件与 ProjectList 页面
  - [ ] 7.2 实现 ProjectForm 组件与 ProjectCreate/ProjectEdit 页面
  - [ ] 7.3 实现 ProjectDetail 页面：展示项目信息、产品列表、成员列表、文档列表

- [ ] 8. 前端 - 产品关联与成员管理组件（R6、R7）
  - [ ] 8.1 实现 ProductAssociation 组件：添加/移除产品
  - [ ] 8.2 实现 MemberManager 组件：添加/角色变更/移除成员

- [ ] 9. 前端 - 项目文档组件（R8）
  - [ ] 9.1 实现 ProjectDocumentList 组件：复用 DocumentUpload/DocumentPreview/SearchBar 模式

- [ ] 10. 检查点 - 前端构建验证

- [ ] 11. 集成与收尾
  - [ ] 11.1 更新 GlobalExceptionHandler 新增项目管理错误码
  - [ ] 11.2 更新前端 types/index.ts 新增类型定义
  - [ ] 11.3 整体编译验证
