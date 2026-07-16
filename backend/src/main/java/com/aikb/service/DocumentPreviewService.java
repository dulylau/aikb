package com.aikb.service;

import java.io.InputStream;

public interface DocumentPreviewService {

    InputStream preview(Long documentId, Integer versionNumber);
}
