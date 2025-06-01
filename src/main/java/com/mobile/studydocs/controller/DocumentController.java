package com.mobile.studydocs.controller;

import com.mobile.studydocs.model.dto.SearchDTO;
import com.mobile.studydocs.response.BaseResponse;
import com.mobile.studydocs.service.DocumentService;
import com.mobile.studydocs.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/controller")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse> search(@RequestParam("keyword") String keyword ) throws ExecutionException, InterruptedException {
        SearchDTO searchDTO = documentService.searchByTitle(keyword);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách thành công", searchDTO));
    }
}
