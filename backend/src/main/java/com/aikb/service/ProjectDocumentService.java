package com.aikb.service;

import com.aikb.dto.DocumentSearchQuery;
import com.aikb.entity.ProjectDocument;
import com.aikb.entity.ProjectDocumentVersion;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface ProjectDocumentService {

    ProjectDocument upload(Long projectId, MultipartFile file, String category);

    List<ProjectDocument> listDocuments(Long projectId);

    Page<ProjectDocument> search(Long projectId, DocumentSearchQuery query, int page, int size);

    ProjectDocument getDocument(Long id);

    InputStream preview(Long id, Integer version);

    void deleteDocument(Long id);

    List<ProjectDocumentVersion> listVersions(Long documentId);

    ProjectDocumentVersion uploadNewVersion(Long documentId, MultipartFile file);
}
