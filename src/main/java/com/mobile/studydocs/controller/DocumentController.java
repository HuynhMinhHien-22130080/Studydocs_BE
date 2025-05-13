package com.mobile.studydocs.controller;

import com.mobile.studydocs.model.dto.DocumentDTO;
import com.mobile.studydocs.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Xem chi tiết tài liệu theo ID
     * @param documentId Mã tài liệu
     * @return DocumentDTO nếu tồn tại, 404 nếu không
     */
    @GetMapping("/detail/{documentId}")
    public ResponseEntity<DocumentDTO> getDocumentDetail(@PathVariable String documentId) {
        return documentService.getDocumentById(documentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
