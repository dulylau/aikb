package com.aikb.controller;

import com.aikb.dto.ApiResponse;
import com.aikb.dto.DocumentSearchQuery;
import com.aikb.entity.Document;
import com.aikb.entity.DocumentVersion;
import com.aikb.service.DocumentPreviewService;
import com.aikb.service.DocumentService;
import com.aikb.service.DocumentVersionService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentVersionService documentVersionService;
    private final DocumentPreviewService documentPreviewService;

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Document> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("productId") Long productId,
            @RequestParam("versionId") Long versionId,
            @RequestParam("category") String category,
            @RequestParam(value = "featurePoint", required = false) String featurePoint) {
        Document document = documentService.upload(file, productId, versionId, category, featurePoint);
        return ApiResponse.success(document);
    }

    @GetMapping("/search")
    public ApiResponse<Page<Document>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        DocumentSearchQuery query = new DocumentSearchQuery(keyword, productId, category);
        Page<Document> result = documentService.search(query, page, size);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Document> getDetail(@PathVariable Long id) {
        Document document = documentService.getDocument(id);
        return ApiResponse.success(document);
    }

    @GetMapping("/{id}/preview")
    public void preview(@PathVariable Long id,
                         @RequestParam(required = false) Integer version,
                         HttpServletResponse response) {
        InputStream inputStream = documentPreviewService.preview(id, version);
        Document document = documentService.getDocument(id);

        String contentType = switch (document.getFileType().toLowerCase()) {
            case "pdf" -> "application/pdf";
            case "md" -> "text/markdown; charset=UTF-8";
            default -> "application/octet-stream";
        };
        response.setContentType(contentType);

        try (OutputStream out = response.getOutputStream()) {
            inputStream.transferTo(out);
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException("文件预览失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ApiResponse.success();
    }

    @GetMapping("/{id}/versions")
    public ApiResponse<List<DocumentVersion>> listVersions(@PathVariable Long id) {
        List<DocumentVersion> versions = documentVersionService.listVersions(id);
        return ApiResponse.success(versions);
    }

    @PostMapping("/{id}/versions")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DocumentVersion> uploadNewVersion(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        DocumentVersion version = documentVersionService.uploadNewVersion(id, file);
        return ApiResponse.success(version);
    }
}
