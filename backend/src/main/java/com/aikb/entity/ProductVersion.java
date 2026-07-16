package com.aikb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("product_version")
public class ProductVersion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private String versionNumber;

    private Integer sortOrder;

    private LocalDateTime createdAt;
}
