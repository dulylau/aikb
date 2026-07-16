package com.aikb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VersionCreateDTO {

    @NotBlank(message = "版本号不能为空")
    private String versionNumber;
}
