package com.mobile.studydocs.controller;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.mobile.studydocs.exception.BusinessException;
import com.mobile.studydocs.model.dto.DocumentDTO;
import com.mobile.studydocs.response.BaseResponse;
import com.mobile.studydocs.service.DocumentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/document")
public class UserDocumentController {

    private final DocumentService documentService;
    private final Storage storage;
    private final String bucketName;

    public UserDocumentController(DocumentService documentService, Storage storage,
                                  @Value("${firebase.bucket-name}") String bucketName) {
        this.documentService = documentService;
        this.storage = storage;
        this.bucketName = bucketName;
    }

    @GetMapping("/detail/{documentId}")
    public ResponseEntity<BaseResponse> getDocumentDetail(@PathVariable String documentId, @RequestParam("userId") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse(HttpStatus.BAD_REQUEST.value(), "userId không được để trống", null));
        }
        DocumentDTO dto = documentService.getDocumentById(documentId);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Lấy chi tiết tài liệu thành công", dto));
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<BaseResponse> getDownloadUrl(@PathVariable String documentId, @RequestParam("userId") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse(HttpStatus.BAD_REQUEST.value(), "userId không được để trống", null));
        }
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
    public ResponseEntity<BaseResponse> likeDocument(@PathVariable String documentId, @RequestParam("userId") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse(HttpStatus.BAD_REQUEST.value(), "userId không được để trống", null));
        }
        documentService.likeDocument(documentId, userId);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Thích tài liệu thành công", true));
    }

    @DeleteMapping("/{documentId}/like")
    public ResponseEntity<BaseResponse> unlikeDocument(@PathVariable String documentId, @RequestParam("userId") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse(HttpStatus.BAD_REQUEST.value(), "userId không được để trống", null));
        }
        documentService.unlikeDocument(documentId, userId);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Bỏ thích tài liệu thành công", true));
    }
}