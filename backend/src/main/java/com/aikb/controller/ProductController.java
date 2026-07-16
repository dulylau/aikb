package com.aikb.controller;

import com.aikb.dto.ApiResponse;
import com.aikb.dto.ProductCreateDTO;
import com.aikb.dto.ProductDetailVO;
import com.aikb.dto.ProductUpdateDTO;
import com.aikb.entity.Product;
import com.aikb.service.ProductService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<Page<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        Page<Product> result = productService.listProducts(page, size);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDetailVO> getDetail(@PathVariable Long id) {
        ProductDetailVO vo = productService.getProductDetail(id);
        return ApiResponse.success(vo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Product> create(@Valid @RequestBody ProductCreateDTO dto) {
        Product product = productService.createProduct(dto);
        return ApiResponse.success(product);
    }

    @PutMapping("/{id}")
    public ApiResponse<Product> update(@PathVariable Long id, @Valid @RequestBody ProductUpdateDTO dto) {
        Product product = productService.updateProduct(id, dto);
        return ApiResponse.success(product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success();
    }
}
