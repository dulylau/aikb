package com.aikb.service.impl;

import com.aikb.dto.ProductCreateDTO;
import com.aikb.dto.ProductDetailVO;
import com.aikb.dto.ProductUpdateDTO;
import com.aikb.entity.Document;
import com.aikb.entity.Product;
import com.aikb.entity.ProductVersion;
import com.aikb.exception.BusinessException;
import com.aikb.mapper.DocumentMapper;
import com.aikb.mapper.ProductMapper;
import com.aikb.mapper.ProductVersionMapper;
import com.aikb.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductVersionMapper productVersionMapper;
    private final DocumentMapper documentMapper;

    @Override
    public Page<Product> listProducts(int page, int size) {
        Page<Product> pageParam = new Page<>(page, size);
        return productMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Product>().orderByDesc(Product::getUpdatedAt));
    }

    @Override
    public ProductDetailVO getProductDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PRODUCT_NOT_FOUND", "未找到 id 为 " + id + " 的产品");
        }

        LambdaQueryWrapper<ProductVersion> versionQuery = new LambdaQueryWrapper<ProductVersion>()
                .eq(ProductVersion::getProductId, id)
                .orderByDesc(ProductVersion::getSortOrder);
        List<ProductVersion> versions = productVersionMapper.selectList(versionQuery);

        ProductVersion currentVersion = versions.isEmpty() ? null : versions.get(0);

        List<Document> documents = List.of();
        if (currentVersion != null) {
            documents = documentMapper.selectList(
                    new LambdaQueryWrapper<Document>()
                            .eq(Document::getProductId, id)
                            .eq(Document::getVersionId, currentVersion.getId())
                            .orderByDesc(Document::getUpdatedAt));
        }

        return ProductDetailVO.from(product, currentVersion, documents, versions);
    }

    @Override
    @Transactional
    public Product createProduct(ProductCreateDTO dto) {
        Long count = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getCode, dto.getCode()));
        if (count > 0) {
            Map<String, String> details = new HashMap<>();
            details.put("field", "code");
            details.put("value", dto.getCode());
            throw new BusinessException(HttpStatus.CONFLICT.value(),
                    "PRODUCT_CODE_DUPLICATE", "产品编码已存在", details);
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setCode(dto.getCode());
        product.setVersion(dto.getVersion());
        product.setDescription(dto.getDescription());
        productMapper.insert(product);

        ProductVersion version = new ProductVersion();
        version.setProductId(product.getId());
        version.setVersionNumber(dto.getVersion());
        version.setSortOrder(0);
        productVersionMapper.insert(version);

        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductUpdateDTO dto) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PRODUCT_NOT_FOUND", "未找到 id 为 " + id + " 的产品");
        }

        if (dto.getCode() != null && !dto.getCode().equals(product.getCode())) {
            Long count = productMapper.selectCount(
                    new LambdaQueryWrapper<Product>()
                            .eq(Product::getCode, dto.getCode())
                            .ne(Product::getId, id));
            if (count > 0) {
                Map<String, String> details = new HashMap<>();
                details.put("field", "code");
                details.put("value", dto.getCode());
                throw new BusinessException(HttpStatus.CONFLICT.value(),
                        "PRODUCT_CODE_DUPLICATE", "产品编码已存在", details);
            }
            product.setCode(dto.getCode());
        }

        product.setName(dto.getName());
        if (dto.getVersion() != null) {
            product.setVersion(dto.getVersion());
        }
        product.setDescription(dto.getDescription());
        productMapper.updateById(product);

        return product;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PRODUCT_NOT_FOUND", "未找到 id 为 " + id + " 的产品");
        }
        productMapper.deleteById(id);
    }
}
