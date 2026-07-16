package com.aikb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentSearchQuery {

    private String keyword;
    private Long productId;
    private String category;
}
