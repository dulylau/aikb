package com.aikb.service;

import com.aikb.dto.VersionCreateDTO;
import com.aikb.entity.ProductVersion;

import java.util.List;

public interface ProductVersionService {

    List<ProductVersion> listVersions(Long productId);

    ProductVersion createVersion(Long productId, VersionCreateDTO dto);
}
