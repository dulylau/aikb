package com.aikb.controller;

import com.aikb.dto.ApiResponse;
import com.aikb.dto.VersionCreateDTO;
import com.aikb.entity.ProductVersion;
import com.aikb.service.ProductVersionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/versions")
@RequiredArgsConstructor
public class ProductVersionController {

    private final ProductVersionService productVersionService;

    @GetMapping
    public ApiResponse<List<ProductVersion>> list(@PathVariable Long productId) {
        List<ProductVersion> versions = productVersionService.listVersions(productId);
        return ApiResponse.success(versions);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductVersion> create(@PathVariable Long productId,
                                               @Valid @RequestBody VersionCreateDTO dto) {
        ProductVersion version = productVersionService.createVersion(productId, dto);
        return ApiResponse.success(version);
    }
}
