package com.aikb.service;

import com.aikb.dto.DocumentSearchQuery;
import com.aikb.entity.Document;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    Document upload(MultipartFile file, Long productId, Long versionId, String category, String featurePoint);

    Document getDocument(Long id);

    Page<Document> search(DocumentSearchQuery query, int page, int size);

    void deleteDocument(Long id);

    List<Document> listByVersion(Long productId, Long versionId);
}
