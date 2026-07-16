package com.aikb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectCreateDTO {

    @NotBlank(message = "项目名称不能为空")
    private String name;

    @NotBlank(message = "项目编码不能为空")
    private String code;

    private String description;
}
