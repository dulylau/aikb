package com.aikb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int status;
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private T data;
    private Object details;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "SUCCESS", "操作成功", LocalDateTime.now(), data, null);
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> error(int status, String code, String message, Object details) {
        return new ApiResponse<>(status, code, message, LocalDateTime.now(), null, details);
    }
}
