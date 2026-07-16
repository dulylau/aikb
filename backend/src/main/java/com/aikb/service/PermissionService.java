package com.aikb.service;

import com.aikb.dto.PermissionUpdateDTO;
import com.aikb.entity.DocumentPermission;

import java.util.List;

public interface PermissionService {

    List<DocumentPermission> getPermissions(Long documentId);

    void updatePermissions(Long documentId, PermissionUpdateDTO dto);

    boolean hasPermission(Long documentId, Long userId, String permissionType);
}
