package com.mobile.studydocs.service;

import com.mobile.studydocs.dao.DocumentDao;
import com.mobile.studydocs.dao.UserDao;
import com.mobile.studydocs.model.dto.SearchDTO;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {
    private final DocumentDao documentDao;
    public DocumentService(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    public SearchDTO search(String keyword) {
    return documentDao.search(keyword);
    }
}
