package com.aikb.service.impl;

import com.aikb.dto.PermissionUpdateDTO;
import com.aikb.entity.DocumentPermission;
import com.aikb.exception.BusinessException;
import com.aikb.mapper.DocumentMapper;
import com.aikb.mapper.DocumentPermissionMapper;
import com.aikb.service.PermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final DocumentPermissionMapper documentPermissionMapper;
    private final DocumentMapper documentMapper;

    @Override
    public List<DocumentPermission> getPermissions(Long documentId) {
        return documentPermissionMapper.selectList(
                new LambdaQueryWrapper<DocumentPermission>()
                        .eq(DocumentPermission::getDocumentId, documentId));
    }

    @Override
    @Transactional
    public void updatePermissions(Long documentId, PermissionUpdateDTO dto) {
        if (documentMapper.selectById(documentId) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "DOCUMENT_NOT_FOUND", "未找到 id 为 " + documentId + " 的文档");
        }

        documentPermissionMapper.delete(
                new LambdaQueryWrapper<DocumentPermission>()
                        .eq(DocumentPermission::getDocumentId, documentId));

        if (dto.getPermissions() != null) {
            for (PermissionUpdateDTO.PermissionEntry entry : dto.getPermissions()) {
                DocumentPermission permission = new DocumentPermission();
                permission.setDocumentId(documentId);
                permission.setUserId(entry.getUserId());
                permission.setPermissionType(entry.getPermissionType());
                documentPermissionMapper.insert(permission);
            }
        }
    }

    @Override
    public boolean hasPermission(Long documentId, Long userId, String permissionType) {
        Long count = documentPermissionMapper.selectCount(
                new LambdaQueryWrapper<DocumentPermission>()
                        .eq(DocumentPermission::getDocumentId, documentId)
                        .eq(DocumentPermission::getUserId, userId)
                        .eq(DocumentPermission::getPermissionType, permissionType));
        return count > 0;
    }
}
