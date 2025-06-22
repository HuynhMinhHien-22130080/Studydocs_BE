package com.mobile.studydocs.controller;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.mobile.studydocs.exception.BusinessException;
import com.mobile.studydocs.model.dto.DocumentDTO;
import com.mobile.studydocs.model.dto.SearchDTO;
import com.mobile.studydocs.model.entity.Document;
import com.mobile.studydocs.response.BaseResponse;
import com.mobile.studydocs.service.DocumentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

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


    @GetMapping("/download/{documentId}")
    public ResponseEntity<BaseResponse> getDownloadUrl(@PathVariable String documentId, @RequestAttribute("userId") String userId) {
        Optional<DocumentDTO> dto = documentService.getDocumentById(documentId);
        String fileUrl = dto.get().getFileUrl();
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
    public ResponseEntity<BaseResponse> likeDocument(@PathVariable String documentId, @RequestAttribute("userId") String userId) {
        documentService.likeDocument(documentId, userId);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Thích tài liệu thành công", true));
    }

    @DeleteMapping("/{documentId}/like")
    public ResponseEntity<BaseResponse> unlikeDocument(@PathVariable String documentId, @RequestAttribute("userId") String userId) {
        documentService.unlikeDocument(documentId, userId);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Bỏ thích tài liệu thành công", true));
    }

    @GetMapping("/getDocSaveInLibrary")
    public ResponseEntity<BaseResponse> getDocSaveInLibrary(@RequestAttribute("userId") String userid) {

        SearchDTO searchDTO = documentService.getDocSaveInLibrary(userid);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách thành công", searchDTO));
    }

    @GetMapping("/saveToLibrary")
    public ResponseEntity<BaseResponse> saveDocument(@RequestParam("keyword") String idDocument, @RequestAttribute("userId") String userid) {
        boolean success = documentService.saveToLibrary(idDocument, userid);
        System.out.println("save doc: " + idDocument);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách thành công", success));
    }
    // ===== hao lam phần này (upload document + file) =====

    /**
     * Upload document metadata + file to Firebase Storage and Firestore
     */
    @PostMapping("/upload")
    public ResponseEntity<BaseResponse> uploadDocument(
            @RequestPart("document") Document document,
            @RequestPart("file") MultipartFile file,
            @RequestAttribute("userId") String userId) {
        Document savedDoc = documentService.uploadDocument(document, file, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Upload tài liệu thành công", savedDoc));
    }

    // ===== end hao lam phần này =====
    // ===== phần này của Hảo =====
    @GetMapping("/my-documents")
    public BaseResponse getMyDocuments(@RequestAttribute("userId") String userId) {
        SearchDTO searchDTO = documentService.getDocumentsByUserId(userId);
        return new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách tài liệu thành công", searchDTO);
    }
    // ===== end phần này của Hảo =====
}
