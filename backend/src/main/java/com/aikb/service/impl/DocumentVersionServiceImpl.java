package com.aikb.service.impl;

import com.aikb.entity.Document;
import com.aikb.entity.DocumentVersion;
import com.aikb.entity.Product;
import com.aikb.exception.BusinessException;
import com.aikb.mapper.DocumentMapper;
import com.aikb.mapper.DocumentVersionMapper;
import com.aikb.mapper.ProductMapper;
import com.aikb.service.DocumentVersionService;
import com.aikb.service.FileStorageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentVersionServiceImpl implements DocumentVersionService {

    private final DocumentVersionMapper documentVersionMapper;
    private final DocumentMapper documentMapper;
    private final ProductMapper productMapper;
    private final FileStorageService fileStorageService;

    @Override
    public List<DocumentVersion> listVersions(Long documentId) {
        return documentVersionMapper.selectList(
                new LambdaQueryWrapper<DocumentVersion>()
                        .eq(DocumentVersion::getDocumentId, documentId)
                        .orderByDesc(DocumentVersion::getVersionNumber));
    }

    @Override
    @Transactional
    public DocumentVersion uploadNewVersion(Long documentId, MultipartFile file) {
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "DOCUMENT_NOT_FOUND", "未找到 id 为 " + documentId + " 的文档");
        }

        int newVersion = document.getCurrentVersion() + 1;

        Product product = productMapper.selectById(document.getProductId());
        String filePath = fileStorageService.store(file, product.getCode(), documentId, newVersion);

        DocumentVersion docVersion = new DocumentVersion();
        docVersion.setDocumentId(documentId);
        docVersion.setVersionNumber(newVersion);
        docVersion.setFilePath(filePath);
        docVersion.setFileSize(file.getSize());
        documentVersionMapper.insert(docVersion);

        document.setCurrentVersion(newVersion);
        document.setFilePath(filePath);
        document.setFileSize(file.getSize());
        documentMapper.updateById(document);

        return docVersion;
    }
}
