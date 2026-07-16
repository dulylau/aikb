package com.aikb.service;

import com.aikb.dto.*;
import com.aikb.entity.Project;
import com.aikb.entity.ProjectMember;
import com.aikb.entity.Product;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface ProjectService {

    Page<Project> listProjects(int page, int size);

    ProjectDetailVO getProjectDetail(Long id);

    Project createProject(ProjectCreateDTO dto);

    Project updateProject(Long id, ProjectUpdateDTO dto);

    void deleteProject(Long id);

    Product addProduct(Long projectId, AddProductRequest request);

    void removeProduct(Long projectId, Long productId);

    List<Product> listProducts(Long projectId);

    ProjectMember addMember(Long projectId, AddMemberRequest request);

    void updateMemberRole(Long projectId, Long userId, UpdateMemberRoleRequest request);

    void removeMember(Long projectId, Long userId);

    List<ProjectMember> listMembers(Long projectId);

    List<Product> listAvailableProducts();
}
