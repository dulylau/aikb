package com.aikb.controller;

import com.aikb.dto.ApiResponse;
import com.aikb.dto.PermissionUpdateDTO;
import com.aikb.entity.DocumentPermission;
import com.aikb.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents/{documentId}/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public ApiResponse<List<DocumentPermission>> getPermissions(@PathVariable Long documentId) {
        List<DocumentPermission> permissions = permissionService.getPermissions(documentId);
        return ApiResponse.success(permissions);
    }

    @PutMapping
    public ApiResponse<Void> updatePermissions(@PathVariable Long documentId,
                                                @RequestBody PermissionUpdateDTO dto) {
        permissionService.updatePermissions(documentId, dto);
        return ApiResponse.success();
    }
}
