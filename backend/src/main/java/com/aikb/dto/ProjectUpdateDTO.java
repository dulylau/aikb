package com.aikb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectUpdateDTO {

    @NotBlank(message = "项目名称不能为空")
    private String name;

    private String code;

    private String description;
}
