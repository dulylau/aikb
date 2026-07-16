package com.aikb.dto;

import com.aikb.entity.Document;
import com.aikb.entity.Product;
import com.aikb.entity.ProductVersion;
import lombok.Data;

import java.util.List;

@Data
public class ProductDetailVO {

    private Long id;
    private String name;
    private String code;
    private String version;
    private String description;
    private ProductVersion currentVersion;
    private List<Document> documents;
    private List<ProductVersion> versions;

    public static ProductDetailVO from(Product product, ProductVersion currentVersion,
                                        List<Document> documents, List<ProductVersion> versions) {
        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setCode(product.getCode());
        vo.setVersion(product.getVersion());
        vo.setDescription(product.getDescription());
        vo.setCurrentVersion(currentVersion);
        vo.setDocuments(documents);
        vo.setVersions(versions);
        return vo;
    }
}
