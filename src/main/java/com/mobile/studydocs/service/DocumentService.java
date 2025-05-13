package com.mobile.studydocs.service;

import com.mobile.studydocs.dao.DocumentDao;
import com.mobile.studydocs.model.entity.DocumentEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentDao documentDao;

    public DocumentService(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    public Optional<DocumentEntity> getDocumentById(String documentId) {
        try {
            DocumentEntity doc = documentDao.findById(documentId);
            return Optional.ofNullable(doc);
        } catch (Exception e) {
            // log lỗi nếu cần
            return Optional.empty();
        }
    }
}