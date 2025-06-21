package com.mobile.studydocs.controller;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.mobile.studydocs.model.dto.SearchDTO;
import com.mobile.studydocs.response.BaseResponse;
import com.mobile.studydocs.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mobile.studydocs.service.AuthService;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/document")
public class DocumentController {
    private final DocumentService documentService;
    private final Storage storage; // Thêm Storage để xử lý download
    private final String bucketName;


    public DocumentController(DocumentService documentService, Storage storage, @Value("${firebase.bucket-name}") String bucketName) {
        this.documentService = documentService;
        this.storage = storage;
        this.bucketName = bucketName;
    }

    @GetMapping("/detail/{documentId}")
    public ResponseEntity<BaseResponse> getDocumentDetail(@PathVariable String documentId) {
        DocumentDTO dto = documentService.getDocumentById(documentId);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Lấy chi tiết tài liệu thành công", dto));
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<BaseResponse> getDownloadUrl(@PathVariable String documentId, @RequestParam String userId) {
        DocumentDTO dto = documentService.getDocumentById(documentId);
        String fileUrl = dto.getFileUrl();
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new BusinessException("Tài liệu không có file để tải");
        }

        BlobId blobId = BlobId.of(bucketName, fileUrl);
        String downloadUrl = storage.signUrl(
                BlobInfo.newBuilder(blobId).build(),
                60, java.util.concurrent.TimeUnit.MINUTES
        ).toString();

        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Lấy URL tải xuống thành công", downloadUrl));
    }

    @PostMapping("/{documentId}/like")
    public ResponseEntity<BaseResponse> likeDocument(@PathVariable String documentId, @RequestParam String userId) {
        documentService.likeDocument(documentId, userId);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Thích tài liệu thành công", true));
    }

    @DeleteMapping("/{documentId}/like")
    public ResponseEntity<BaseResponse> unlikeDocument(@PathVariable String documentId, @RequestParam String userId) {
        documentService.unlikeDocument(documentId, userId);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Bỏ thích tài liệu thành công", true));
    }

    @GetMapping( "/searchByTitle")
    public ResponseEntity<BaseResponse> searchByTitle(@RequestParam("keyword") String title ){
        SearchDTO searchDTO = documentService.searchByTitle(title);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách thành công", searchDTO));
    }
    @GetMapping( "/searchByUniversity")
    public ResponseEntity<BaseResponse> searchByUniversity(@RequestParam("keyword") String university ){
        SearchDTO searchDTO = documentService.searchByUniversity(university);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách thành công", searchDTO));
    }
    @GetMapping( "/searchBySubject")
    public ResponseEntity<BaseResponse> searchBySubject(@RequestParam("keyword") String subject ){
        SearchDTO searchDTO = documentService.searchBySubject(subject);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách thành công", searchDTO));
    }
}
