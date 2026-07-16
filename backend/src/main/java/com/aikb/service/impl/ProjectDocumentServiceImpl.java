package com.aikb.service.impl;

import com.aikb.dto.DocumentSearchQuery;
import com.aikb.entity.Project;
import com.aikb.entity.ProjectDocument;
import com.aikb.entity.ProjectDocumentVersion;
import com.aikb.exception.BusinessException;
import com.aikb.mapper.ProjectDocumentMapper;
import com.aikb.mapper.ProjectDocumentVersionMapper;
import com.aikb.mapper.ProjectMapper;
import com.aikb.service.FileStorageService;
import com.aikb.service.ProjectDocumentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectDocumentServiceImpl implements ProjectDocumentService {

    private final ProjectDocumentMapper projectDocumentMapper;
    private final ProjectDocumentVersionMapper projectDocumentVersionMapper;
    private final ProjectMapper projectMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public ProjectDocument upload(Long projectId, MultipartFile file, String category) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PROJECT_NOT_FOUND", "未找到该项目");
        }

        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.') + 1) : "";

        ProjectDocument document = new ProjectDocument();
        document.setProjectId(projectId);
        document.setTitle(originalFilename != null ? originalFilename : "未命名文档");
        document.setCategory(category);
        document.setFileType(fileType);
        document.setFileSize(file.getSize());
        document.setCurrentVersion(1);
        projectDocumentMapper.insert(document);

        String filePath = fileStorageService.store(file, project.getCode(), document.getId(), 1);
        document.setFilePath(filePath);
        projectDocumentMapper.updateById(document);

        ProjectDocumentVersion docVersion = new ProjectDocumentVersion();
        docVersion.setDocumentId(document.getId());
        docVersion.setVersionNumber(1);
        docVersion.setFilePath(filePath);
        docVersion.setFileSize(file.getSize());
        projectDocumentVersionMapper.insert(docVersion);

        return document;
    }

    @Override
    public List<ProjectDocument> listDocuments(Long projectId) {
        return projectDocumentMapper.selectList(
                new LambdaQueryWrapper<ProjectDocument>()
                        .eq(ProjectDocument::getProjectId, projectId)
                        .orderByDesc(ProjectDocument::getUpdatedAt));
    }

    @Override
    public Page<ProjectDocument> search(Long projectId, DocumentSearchQuery query, int page, int size) {
        LambdaQueryWrapper<ProjectDocument> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectDocument::getProjectId, projectId);

        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(ProjectDocument::getTitle, query.getKeyword());
        }
        if (StringUtils.hasText(query.getCategory())) {
            wrapper.eq(ProjectDocument::getCategory, query.getCategory());
        }
        wrapper.orderByDesc(ProjectDocument::getUpdatedAt);

        Page<ProjectDocument> pageParam = new Page<>(page, size);
        return projectDocumentMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public ProjectDocument getDocument(Long id) {
        ProjectDocument document = projectDocumentMapper.selectById(id);
        if (document == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PROJECT_DOCUMENT_NOT_FOUND", "未找到该项目文档");
        }
        return document;
    }

    @Override
    public InputStream preview(Long id, Integer version) {
        String filePath;
        if (version != null) {
            ProjectDocumentVersion docVersion = projectDocumentVersionMapper.selectOne(
                    new LambdaQueryWrapper<ProjectDocumentVersion>()
                            .eq(ProjectDocumentVersion::getDocumentId, id)
                            .eq(ProjectDocumentVersion::getVersionNumber, version));
            if (docVersion == null) {
                throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                        "DOCUMENT_VERSION_NOT_FOUND", "未找到该版本的文档");
            }
            filePath = docVersion.getFilePath();
        } else {
            ProjectDocument document = projectDocumentMapper.selectById(id);
            if (document == null) {
                throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                        "PROJECT_DOCUMENT_NOT_FOUND", "未找到该项目文档");
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

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        ProjectDocument document = projectDocumentMapper.selectById(id);
        if (document == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PROJECT_DOCUMENT_NOT_FOUND", "未找到该项目文档");
        }
        projectDocumentMapper.deleteById(id);
    }

    @Override
    public List<ProjectDocumentVersion> listVersions(Long documentId) {
        return projectDocumentVersionMapper.selectList(
                new LambdaQueryWrapper<ProjectDocumentVersion>()
                        .eq(ProjectDocumentVersion::getDocumentId, documentId)
                        .orderByDesc(ProjectDocumentVersion::getVersionNumber));
    }

    @Override
    @Transactional
    public ProjectDocumentVersion uploadNewVersion(Long documentId, MultipartFile file) {
        ProjectDocument document = projectDocumentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PROJECT_DOCUMENT_NOT_FOUND", "未找到该项目文档");
        }

        int newVersion = document.getCurrentVersion() + 1;
        Project project = projectMapper.selectById(document.getProjectId());

        String filePath = fileStorageService.store(file, project.getCode(), documentId, newVersion);

        ProjectDocumentVersion docVersion = new ProjectDocumentVersion();
        docVersion.setDocumentId(documentId);
        docVersion.setVersionNumber(newVersion);
        docVersion.setFilePath(filePath);
        docVersion.setFileSize(file.getSize());
        projectDocumentVersionMapper.insert(docVersion);

        document.setCurrentVersion(newVersion);
        document.setFilePath(filePath);
        document.setFileSize(file.getSize());
        projectDocumentMapper.updateById(document);

        return docVersion;
    }
}
