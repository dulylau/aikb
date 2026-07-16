package com.aikb.service.impl;

import com.aikb.dto.VersionCreateDTO;
import com.aikb.entity.Product;
import com.aikb.entity.ProductVersion;
import com.aikb.exception.BusinessException;
import com.aikb.mapper.ProductMapper;
import com.aikb.mapper.ProductVersionMapper;
import com.aikb.service.ProductVersionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductVersionServiceImpl implements ProductVersionService {

    private final ProductVersionMapper productVersionMapper;
    private final ProductMapper productMapper;

    @Override
    public List<ProductVersion> listVersions(Long productId) {
        return productVersionMapper.selectList(
                new LambdaQueryWrapper<ProductVersion>()
                        .eq(ProductVersion::getProductId, productId)
                        .orderByDesc(ProductVersion::getSortOrder));
    }

    @Override
    @Transactional
    public ProductVersion createVersion(Long productId, VersionCreateDTO dto) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND.value(),
                    "PRODUCT_NOT_FOUND", "未找到 id 为 " + productId + " 的产品");
        }

        Long count = productVersionMapper.selectCount(
                new LambdaQueryWrapper<ProductVersion>()
                        .eq(ProductVersion::getProductId, productId)
                        .eq(ProductVersion::getVersionNumber, dto.getVersionNumber()));
        if (count > 0) {
            throw new BusinessException(HttpStatus.CONFLICT.value(),
                    "VERSION_DUPLICATE", "该版本号已存在");
        }

        Integer maxSort = productVersionMapper.selectList(
                new LambdaQueryWrapper<ProductVersion>()
                        .eq(ProductVersion::getProductId, productId)
                        .orderByDesc(ProductVersion::getSortOrder)
                        .last("LIMIT 1"))
                .stream()
                .findFirst()
                .map(ProductVersion::getSortOrder)
                .orElse(-1);

        ProductVersion version = new ProductVersion();
        version.setProductId(productId);
        version.setVersionNumber(dto.getVersionNumber());
        version.setSortOrder(maxSort + 1);
        productVersionMapper.insert(version);

        product.setVersion(dto.getVersionNumber());
        productMapper.updateById(product);

        return version;
    }
}
