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
@RequestMapping("/controller")
public class DocumentController {
    private final DocumentService documentService;
    private final Storage storage; // Thêm Storage để xử lý download
    private final String bucketName;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse> search(@RequestParam("keyword") String keyword ){
        SearchDTO searchDTO = documentService.searchByTitle(keyword);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách thành công", searchDTO));
    }
    public DocumentController(DocumentService documentService, Storage storage, @Value("${firebase.bucket-name}") String bucketName) {
        this.documentService = documentService;
        this.storage = storage;
        this.bucketName = bucketName;
    }
    /**
     * Xem chi tiết tài liệu theo ID
     * @param documentId Mã tài liệu
     * @return BaseResponse chứa DocumentDTO nếu tồn tại, 404 nếu không
     */
    @GetMapping("/detail/{documentId}")
    public ResponseEntity<BaseResponse> getDocumentDetail(@PathVariable String documentId) {
        return documentService.getDocumentById(documentId)
                .map(dto -> ResponseEntity.status(HttpStatus.OK)
                        .body(new BaseResponse(HttpStatus.OK.value(), "Lấy chi tiết tài liệu thành công", dto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "Tài liệu không tồn tại", null)));
    }

    /**
     * Lấy URL download tạm thời cho tài liệu từ Firebase Storage
     * @param documentId Mã tài liệu
     * @param userId ID của người dùng
     * @return BaseResponse chứa URL download nếu thành công, 404 nếu không tìm thấy
     */
    @GetMapping("/download/{documentId}")
    public ResponseEntity<BaseResponse> getDownloadUrl(@PathVariable String documentId, @RequestParam String userId) {
        return documentService.getDocumentById(documentId)
                .map(dto -> {
                    String fileUrl = dto.getFileUrl();
                    if (fileUrl == null || fileUrl.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new BaseResponse(HttpStatus.BAD_REQUEST.value(), "Tài liệu không có file để tải", null));
                    }
                    BlobId blobId = BlobId.of(bucketName, fileUrl);
                    String downloadUrl = storage.signUrl(
                            BlobInfo.newBuilder(blobId).build(),
                            60, java.util.concurrent.TimeUnit.MINUTES
                    ).toString();
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new BaseResponse(HttpStatus.OK.value(), "Lấy URL tải xuống thành công", downloadUrl));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "Tài liệu không tồn tại", null)));
    }

    /**
     * Thêm "like" cho tài liệu
     * @param documentId Mã tài liệu
     * @param userId ID của người dùng thực hiện like
     * @return BaseResponse chứa boolean (true nếu thành công, false nếu thất bại)
     */
    @PostMapping("/{documentId}/like")
    public ResponseEntity<BaseResponse> likeDocument(@PathVariable String documentId, @RequestParam String userId) {
        boolean success = documentService.likeDocument(documentId, userId);
        if (success) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse(HttpStatus.OK.value(), "Thích tài liệu thành công", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "Tài liệu không tồn tại", false));
        }
    }

    /**
     * Xóa "like" khỏi tài liệu
     * @param documentId Mã tài liệu
     * @param userId ID của người dùng thực hiện unlike
     * @return BaseResponse chứa boolean (true nếu thành công, false nếu thất bại)
     */
    @DeleteMapping("/{documentId}/like")
    public ResponseEntity<BaseResponse> unlikeDocument(@PathVariable String documentId, @RequestParam String userId) {
        boolean success = documentService.unlikeDocument(documentId, userId);
        if (success) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse(HttpStatus.OK.value(), "Bỏ thích tài liệu thành công", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "Tài liệu không tồn tại", false));
        }
    }
}
