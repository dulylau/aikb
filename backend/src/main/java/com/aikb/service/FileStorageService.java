package com.aikb.service;

import com.aikb.entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileStorageService {

    String store(MultipartFile file, String productCode, Long documentId, int version);

    InputStream read(String filePath);

    void deleteDirectory(Long documentId);

    void deleteFile(String filePath);
}
