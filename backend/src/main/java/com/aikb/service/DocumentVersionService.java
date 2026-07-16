package com.aikb.service;

import com.aikb.entity.DocumentVersion;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentVersionService {

    List<DocumentVersion> listVersions(Long documentId);

    DocumentVersion uploadNewVersion(Long documentId, MultipartFile file);
}
