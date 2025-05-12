package com.mobile.studydocs.controller;

import com.mobile.studydocs.service.DocumentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/{documentId}/view")
    public String viewDocument(@RequestParam String userId, @PathVariable String documentId) {
        documentService.viewDocument(userId, documentId);
        return "Document viewed event published!";
    }
}

