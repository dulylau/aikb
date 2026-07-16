package com.aikb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentUploadDTO {

    @NotNull(message = "产品ID不能为空")
    private Long productId;

    @NotNull(message = "产品版本ID不能为空")
    private Long versionId;

    @NotBlank(message = "文档分类不能为空")
    private String category;

    private String featurePoint;
}
