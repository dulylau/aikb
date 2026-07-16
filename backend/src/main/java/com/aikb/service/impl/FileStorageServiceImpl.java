package com.aikb.service.impl;

import com.aikb.exception.BusinessException;
import com.aikb.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Set;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("doc", "docx", "pdf", "md");
    private static final long MAX_FILE_SIZE = 50L * 1024 * 1024;

    @Value("${file.storage.base-path:/data/files}")
    private String basePath;

    @Override
    public String store(MultipartFile file, String productCode, Long documentId, int version) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);

        String dir = String.format("%s/products/%s/documents/%d/v%d",
                basePath, productCode, documentId, version);
        String filePath = dir + "/" + originalFilename;

        try {
            Files.createDirectories(Path.of(dir));
            file.transferTo(new File(filePath));
            return filePath;
        } catch (IOException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "FILE_STORAGE_ERROR", "文件存储失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream read(String filePath) {
        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "FILE_NOT_FOUND", "文件不存在: " + filePath);
        }
    }

    @Override
    public void deleteDirectory(Long documentId) {
        try {
            Path docPath = Path.of(basePath, "products");
            java.util.List<Path> docDirs = Files.walk(docPath, 3)
                    .filter(p -> p.getFileName().toString().equals(String.valueOf(documentId)))
                    .toList();
            for (Path dir : docDirs) {
                try (var walk = Files.walk(dir)) {
                    walk.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
            }
        } catch (IOException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "FILE_STORAGE_ERROR", "文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Path.of(filePath));
        } catch (IOException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "FILE_STORAGE_ERROR", "文件删除失败: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(),
                    "VALIDATION_ERROR", "上传文件不能为空");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(),
                    "FILE_SIZE_EXCEEDED", "文件大小超出 50MB 限制");
        }

        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(),
                    "UNSUPPORTED_FILE_TYPE", "不支持的文件类型: " + extension);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
