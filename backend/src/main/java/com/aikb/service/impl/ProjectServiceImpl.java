package com.aikb.service.impl;

import com.aikb.dto.*;
import com.aikb.entity.Project;
import com.aikb.entity.ProjectMember;
import com.aikb.entity.Product;
import com.aikb.exception.BusinessException;
import com.aikb.mapper.ProductMapper;
import com.aikb.mapper.ProjectMapper;
import com.aikb.mapper.ProjectMemberMapper;
import com.aikb.service.ProjectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private static final Set<String> VALID_ROLES = Set.of("OWNER", "DEVELOPER", "OBSERVER");

    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final ProductMapper productMapper;

    @Override
    public Page<Project> listProjects(int page, int size) {
        Page<Project> pageParam = new Page<>(page, size);
        return projectMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Project>().orderByDesc(Project::getUpdatedAt));
    }

    @Override
    public ProjectDetailVO getProjectDetail(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PROJECT_NOT_FOUND", "未找到该项目");
        }

        List<Product> products = productMapper.selectList(
                new LambdaQueryWrapper<Product>().eq(Product::getProjectId, id));
        List<ProjectMember> members = projectMemberMapper.selectList(
                new LambdaQueryWrapper<ProjectMember>().eq(ProjectMember::getProjectId, id));

        return ProjectDetailVO.from(project, products, members);
    }

    @Override
    @Transactional
    public Project createProject(ProjectCreateDTO dto) {
        Long count = projectMapper.selectCount(
                new LambdaQueryWrapper<Project>().eq(Project::getCode, dto.getCode()));
        if (count > 0) {
            Map<String, String> details = new HashMap<>();
            details.put("field", "code");
            details.put("value", dto.getCode());
            throw new BusinessException(HttpStatus.CONFLICT.value(),
                    "PROJECT_CODE_DUPLICATE", "项目编码已存在", details);
        }

        Project project = new Project();
        project.setName(dto.getName());
        project.setCode(dto.getCode());
        project.setDescription(dto.getDescription());
        projectMapper.insert(project);
        return project;
    }

    @Override
    @Transactional
    public Project updateProject(Long id, ProjectUpdateDTO dto) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PROJECT_NOT_FOUND", "未找到该项目");
        }

        if (StringUtils.hasText(dto.getCode()) && !dto.getCode().equals(project.getCode())) {
            Long count = projectMapper.selectCount(
                    new LambdaQueryWrapper<Project>()
                            .eq(Project::getCode, dto.getCode())
                            .ne(Project::getId, id));
            if (count > 0) {
                Map<String, String> details = new HashMap<>();
                details.put("field", "code");
                details.put("value", dto.getCode());
                throw new BusinessException(HttpStatus.CONFLICT.value(),
                        "PROJECT_CODE_DUPLICATE", "项目编码已存在", details);
            }
            project.setCode(dto.getCode());
        }

        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        projectMapper.updateById(project);
        return project;
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PROJECT_NOT_FOUND", "未找到该项目");
        }

        Long productCount = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getProjectId, id));
        if (productCount > 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(),
                    "PROJECT_HAS_PRODUCTS", "项目下仍有产品，无法删除");
        }

        projectMapper.deleteById(id);
    }

    @Override
    @Transactional
    public Product addProduct(Long projectId, AddProductRequest request) {
        if (projectMapper.selectById(projectId) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PROJECT_NOT_FOUND", "未找到该项目");
        }

        Product product = productMapper.selectById(request.getProductId());
        if (product == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PRODUCT_NOT_FOUND", "未找到该产品");
        }

        if (product.getProjectId() != null && !product.getProjectId().equals(projectId)) {
            throw new BusinessException(HttpStatus.CONFLICT.value(),
                    "PRODUCT_ALREADY_ASSIGNED", "该产品已属于其他项目");
        }

        product.setProjectId(projectId);
        productMapper.updateById(product);
        return product;
    }

    @Override
    @Transactional
    public void removeProduct(Long projectId, Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null || !projectId.equals(product.getProjectId())) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PRODUCT_NOT_IN_PROJECT", "该产品不属于此项目");
        }

        product.setProjectId(null);
        productMapper.updateById(product);
    }

    @Override
    public List<Product> listProducts(Long projectId) {
        return productMapper.selectList(
                new LambdaQueryWrapper<Product>().eq(Product::getProjectId, projectId));
    }

    @Override
    @Transactional
    public ProjectMember addMember(Long projectId, AddMemberRequest request) {
        if (projectMapper.selectById(projectId) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PROJECT_NOT_FOUND", "未找到该项目");
        }

        if (!VALID_ROLES.contains(request.getRole())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(),
                    "INVALID_ROLE", "无效的角色: " + request.getRole());
        }

        Long count = projectMemberMapper.selectCount(
                new LambdaQueryWrapper<ProjectMember>()
                        .eq(ProjectMember::getProjectId, projectId)
                        .eq(ProjectMember::getUserId, request.getUserId()));
        if (count > 0) {
            throw new BusinessException(HttpStatus.CONFLICT.value(),
                    "MEMBER_ALREADY_EXISTS", "该用户已是项目成员");
        }

        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(request.getUserId());
        member.setRole(request.getRole());
        projectMemberMapper.insert(member);
        return member;
    }

    @Override
    @Transactional
    public void updateMemberRole(Long projectId, Long userId, UpdateMemberRoleRequest request) {
        if (!VALID_ROLES.contains(request.getRole())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(),
                    "INVALID_ROLE", "无效的角色: " + request.getRole());
        }

        ProjectMember member = projectMemberMapper.selectOne(
                new LambdaQueryWrapper<ProjectMember>()
                        .eq(ProjectMember::getProjectId, projectId)
                        .eq(ProjectMember::getUserId, userId));
        if (member == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "MEMBER_NOT_FOUND", "未找到该成员");
        }

        member.setRole(request.getRole());
        projectMemberMapper.updateById(member);
    }

    @Override
    @Transactional
    public void removeMember(Long projectId, Long userId) {
        projectMemberMapper.delete(
                new LambdaQueryWrapper<ProjectMember>()
                        .eq(ProjectMember::getProjectId, projectId)
                        .eq(ProjectMember::getUserId, userId));
    }

    @Override
    public List<ProjectMember> listMembers(Long projectId) {
        return projectMemberMapper.selectList(
                new LambdaQueryWrapper<ProjectMember>().eq(ProjectMember::getProjectId, projectId));
    }

    @Override
    public List<Product> listAvailableProducts() {
        return productMapper.selectList(
                new LambdaQueryWrapper<Product>().isNull(Product::getProjectId));
    }
}
