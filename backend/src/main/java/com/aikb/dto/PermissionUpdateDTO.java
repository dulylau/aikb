package com.aikb.dto;

import lombok.Data;

import java.util.List;

@Data
public class PermissionUpdateDTO {

    private List<PermissionEntry> permissions;

    @Data
    public static class PermissionEntry {
        private Long userId;
        private String permissionType;
    }
}
