package com.aikb.controller;

import com.aikb.dto.ApiResponse;
import com.aikb.dto.DocumentSearchQuery;
import com.aikb.entity.ProjectDocument;
import com.aikb.entity.ProjectDocumentVersion;
import com.aikb.service.ProjectDocumentService;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectDocumentController {

    private final ProjectDocumentService projectDocumentService;

    @PostMapping("/projects/{projectId}/documents/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProjectDocument> upload(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") String category) {
        ProjectDocument document = projectDocumentService.upload(projectId, file, category);
        return ApiResponse.success(document);
    }

    @GetMapping("/projects/{projectId}/documents")
    public ApiResponse<List<ProjectDocument>> list(@PathVariable Long projectId) {
        List<ProjectDocument> documents = projectDocumentService.listDocuments(projectId);
        return ApiResponse.success(documents);
    }

    @GetMapping("/projects/{projectId}/documents/search")
    public ApiResponse<Page<ProjectDocument>> search(
            @PathVariable Long projectId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        DocumentSearchQuery query = new DocumentSearchQuery(keyword, null, category);
        Page<ProjectDocument> result = projectDocumentService.search(projectId, query, page, size);
        return ApiResponse.success(result);
    }

    @GetMapping("/documents/project/{id}")
    public ApiResponse<ProjectDocument> getDetail(@PathVariable Long id) {
        ProjectDocument document = projectDocumentService.getDocument(id);
        return ApiResponse.success(document);
    }

    @GetMapping("/documents/project/{id}/preview")
    public void preview(@PathVariable Long id,
                         @RequestParam(required = false) Integer version,
                         HttpServletResponse response) {
        InputStream inputStream = projectDocumentService.preview(id, version);
        ProjectDocument document = projectDocumentService.getDocument(id);

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

    @DeleteMapping("/documents/project/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable Long id) {
        projectDocumentService.deleteDocument(id);
        return ApiResponse.success();
    }

    @GetMapping("/documents/project/{id}/versions")
    public ApiResponse<List<ProjectDocumentVersion>> listVersions(@PathVariable Long id) {
        List<ProjectDocumentVersion> versions = projectDocumentService.listVersions(id);
        return ApiResponse.success(versions);
    }

    @PostMapping("/documents/project/{id}/versions")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProjectDocumentVersion> uploadNewVersion(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        ProjectDocumentVersion version = projectDocumentService.uploadNewVersion(id, file);
        return ApiResponse.success(version);
    }
}
