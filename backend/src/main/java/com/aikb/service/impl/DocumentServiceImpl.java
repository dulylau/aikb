package com.aikb.service.impl;

import com.aikb.dto.DocumentSearchQuery;
import com.aikb.entity.Document;
import com.aikb.entity.DocumentVersion;
import com.aikb.entity.Product;
import com.aikb.entity.ProductVersion;
import com.aikb.exception.BusinessException;
import com.aikb.mapper.DocumentMapper;
import com.aikb.mapper.DocumentVersionMapper;
import com.aikb.mapper.ProductMapper;
import com.aikb.mapper.ProductVersionMapper;
import com.aikb.service.DocumentService;
import com.aikb.service.FileStorageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentMapper documentMapper;
    private final DocumentVersionMapper documentVersionMapper;
    private final ProductMapper productMapper;
    private final ProductVersionMapper productVersionMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public Document upload(MultipartFile file, Long productId, Long versionId, String category, String featurePoint) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PRODUCT_NOT_FOUND", "未找到 id 为 " + productId + " 的产品");
        }

        ProductVersion version = productVersionMapper.selectById(versionId);
        if (version == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "VERSION_NOT_FOUND", "未找到 id 为 " + versionId + " 的版本");
        }

        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.') + 1) : "";

        Document document = new Document();
        document.setProductId(productId);
        document.setVersionId(versionId);
        document.setTitle(originalFilename != null ? originalFilename : "未命名文档");
        document.setCategory(category);
        document.setFileType(fileType);
        document.setFileSize(file.getSize());
        document.setFeaturePoint(featurePoint);
        document.setCurrentVersion(1);
        documentMapper.insert(document);

        String filePath = fileStorageService.store(file, product.getCode(), document.getId(), 1);
        document.setFilePath(filePath);
        documentMapper.updateById(document);

        DocumentVersion docVersion = new DocumentVersion();
        docVersion.setDocumentId(document.getId());
        docVersion.setVersionNumber(1);
        docVersion.setFilePath(filePath);
        docVersion.setFileSize(file.getSize());
        documentVersionMapper.insert(docVersion);

        return document;
    }

    @Override
    public Document getDocument(Long id) {
        Document document = documentMapper.selectById(id);
        if (document == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "DOCUMENT_NOT_FOUND", "未找到 id 为 " + id + " 的文档");
        }
        return document;
    }

    @Override
    public Page<Document> search(DocumentSearchQuery query, int page, int size) {
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Document::getTitle, query.getKeyword());
        }
        if (query.getProductId() != null) {
            wrapper.eq(Document::getProductId, query.getProductId());
        }
        if (StringUtils.hasText(query.getCategory())) {
            wrapper.eq(Document::getCategory, query.getCategory());
        }
        wrapper.orderByDesc(Document::getUpdatedAt);

        Page<Document> pageParam = new Page<>(page, size);
        return documentMapper.selectPage(pageParam, wrapper);
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        Document document = documentMapper.selectById(id);
        if (document == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "DOCUMENT_NOT_FOUND", "未找到 id 为 " + id + " 的文档");
        }

        fileStorageService.deleteDirectory(id);
        documentMapper.deleteById(id);
    }

    @Override
    public List<Document> listByVersion(Long productId, Long versionId) {
        return documentMapper.selectList(
                new LambdaQueryWrapper<Document>()
                        .eq(Document::getProductId, productId)
                        .eq(Document::getVersionId, versionId)
                        .orderByDesc(Document::getUpdatedAt));
    }
}
