package com.aikb.service.impl;

import com.aikb.entity.Document;
import com.aikb.entity.DocumentVersion;
import com.aikb.exception.BusinessException;
import com.aikb.mapper.DocumentMapper;
import com.aikb.mapper.DocumentVersionMapper;
import com.aikb.service.DocumentPreviewService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class DocumentPreviewServiceImpl implements DocumentPreviewService {

    private final DocumentMapper documentMapper;
    private final DocumentVersionMapper documentVersionMapper;

    @Override
    public InputStream preview(Long documentId, Integer versionNumber) {
        String filePath;

        if (versionNumber != null) {
            DocumentVersion docVersion = documentVersionMapper.selectOne(
                    new LambdaQueryWrapper<DocumentVersion>()
                            .eq(DocumentVersion::getDocumentId, documentId)
                            .eq(DocumentVersion::getVersionNumber, versionNumber));
            if (docVersion == null) {
                throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                        "DOCUMENT_VERSION_NOT_FOUND", "未找到该版本的文档");
            }
            filePath = docVersion.getFilePath();
        } else {
            Document document = documentMapper.selectById(documentId);
            if (document == null) {
                throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                        "DOCUMENT_NOT_FOUND", "未找到 id 为 " + documentId + " 的文档");
            }
            filePath = document.getFilePath();
        }

        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "FILE_NOT_FOUND", "文件不存在: " + filePath);
        }
    }
}
