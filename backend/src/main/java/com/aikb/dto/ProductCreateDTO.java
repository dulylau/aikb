package com.aikb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductCreateDTO {

    @NotBlank(message = "产品名称不能为空")
    private String name;

    @NotBlank(message = "产品编码不能为空")
    private String code;

    @NotBlank(message = "版本号不能为空")
    private String version;

    private String description;
}
