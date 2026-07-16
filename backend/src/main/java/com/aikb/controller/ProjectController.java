package com.aikb.controller;

import com.aikb.dto.*;
import com.aikb.entity.Product;
import com.aikb.entity.Project;
import com.aikb.entity.ProjectMember;
import com.aikb.service.ProjectService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/projects")
    public ApiResponse<Page<Project>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        Page<Project> result = projectService.listProjects(page, size);
        return ApiResponse.success(result);
    }

    @GetMapping("/projects/{id}")
    public ApiResponse<ProjectDetailVO> getDetail(@PathVariable Long id) {
        ProjectDetailVO vo = projectService.getProjectDetail(id);
        return ApiResponse.success(vo);
    }

    @PostMapping("/projects")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Project> create(@Valid @RequestBody ProjectCreateDTO dto) {
        Project project = projectService.createProject(dto);
        return ApiResponse.success(project);
    }

    @PutMapping("/projects/{id}")
    public ApiResponse<Project> update(@PathVariable Long id, @Valid @RequestBody ProjectUpdateDTO dto) {
        Project project = projectService.updateProject(id, dto);
        return ApiResponse.success(project);
    }

    @DeleteMapping("/projects/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ApiResponse.success();
    }

    @GetMapping("/projects/{id}/products")
    public ApiResponse<List<Product>> listProducts(@PathVariable Long id) {
        List<Product> products = projectService.listProducts(id);
        return ApiResponse.success(products);
    }

    @PostMapping("/projects/{id}/products")
    public ApiResponse<Product> addProduct(@PathVariable Long id, @RequestBody AddProductRequest request) {
        Product product = projectService.addProduct(id, request);
        return ApiResponse.success(product);
    }

    @DeleteMapping("/projects/{id}/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> removeProduct(@PathVariable Long id, @PathVariable Long productId) {
        projectService.removeProduct(id, productId);
        return ApiResponse.success();
    }

    @GetMapping("/products/available")
    public ApiResponse<List<Product>> listAvailableProducts() {
        List<Product> products = projectService.listAvailableProducts();
        return ApiResponse.success(products);
    }

    @GetMapping("/projects/{id}/members")
    public ApiResponse<List<ProjectMember>> listMembers(@PathVariable Long id) {
        List<ProjectMember> members = projectService.listMembers(id);
        return ApiResponse.success(members);
    }

    @PostMapping("/projects/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProjectMember> addMember(@PathVariable Long id, @Valid @RequestBody AddMemberRequest request) {
        ProjectMember member = projectService.addMember(id, request);
        return ApiResponse.success(member);
    }

    @PutMapping("/projects/{id}/members/{userId}")
    public ApiResponse<Void> updateMemberRole(@PathVariable Long id, @PathVariable Long userId,
                                               @RequestBody UpdateMemberRoleRequest request) {
        projectService.updateMemberRole(id, userId, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/projects/{id}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        projectService.removeMember(id, userId);
        return ApiResponse.success();
    }
}
