package com.aikb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("document")
public class Document {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private Long versionId;

    private String title;

    private String category;

    private String fileType;

    private Long fileSize;

    private String filePath;

    private String featurePoint;

    private Integer currentVersion;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
