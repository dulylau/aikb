package com.aikb.service;

import com.aikb.dto.ProductCreateDTO;
import com.aikb.dto.ProductDetailVO;
import com.aikb.dto.ProductUpdateDTO;
import com.aikb.entity.Product;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ProductService {

    Page<Product> listProducts(int page, int size);

    ProductDetailVO getProductDetail(Long id);

    Product createProduct(ProductCreateDTO dto);

    Product updateProduct(Long id, ProductUpdateDTO dto);

    void deleteProduct(Long id);
}
