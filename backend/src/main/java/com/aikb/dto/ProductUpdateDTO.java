package com.aikb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductUpdateDTO {

    @NotBlank(message = "产品名称不能为空")
    private String name;

    private String code;

    private String version;

    private String description;
}
