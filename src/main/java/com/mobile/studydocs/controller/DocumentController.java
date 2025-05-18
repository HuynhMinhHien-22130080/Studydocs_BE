package com.mobile.studydocs.controller;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.mobile.studydocs.model.dto.DocumentDTO;
import com.mobile.studydocs.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final Storage storage; // Thêm Storage để xử lý download

    public DocumentController(DocumentService documentService, Storage storage) {
        this.documentService = documentService;
        this.storage = storage;
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

    /**
     * Lấy URL download tạm thời cho tài liệu từ Firebase Storage
     * @param documentId Mã tài liệu
     * @return URL download nếu thành công, 404 nếu không tìm thấy
     */
    @GetMapping("/download/{documentId}")
    public ResponseEntity<String> getDownloadUrl(@PathVariable String documentId) {
        return documentService.getDocumentById(documentId)
                .map(dto -> {
                    String fileUrl = dto.getFileUrl();
                    // Giả định fileUrl là path trong Storage (e.g., "documents/doc456/file.pdf")
                    BlobId blobId = BlobId.of("your-bucket-name", fileUrl);
                    // Tạo URL có thời hạn (1 giờ)
                    String downloadUrl = storage.signUrl(
                            BlobInfo.newBuilder(blobId).build(),
                            60, java.util.concurrent.TimeUnit.MINUTES
                    ).toString();
                    return ResponseEntity.ok(downloadUrl);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Thêm "like" cho tài liệu
     * @param documentId Mã tài liệu
     * @param userId ID của người dùng thực hiện like
     * @return 200 nếu thành công, 404 nếu tài liệu không tồn tại
     */
    @PostMapping("/{documentId}/like")
    public ResponseEntity<Void> likeDocument(@PathVariable String documentId, @RequestParam String userId) {
        return documentService.likeDocument(documentId, userId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Xóa "like" khỏi tài liệu
     * @param documentId Mã tài liệu
     * @param userId ID của người dùng thực hiện unlike
     * @return 200 nếu thành công, 404 nếu tài liệu không tồn tại
     */
    @DeleteMapping("/{documentId}/like")
    public ResponseEntity<Void> unlikeDocument(@PathVariable String documentId, @RequestParam String userId) {
        return documentService.unlikeDocument(documentId, userId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Thêm "bookmark" cho tài liệu
     * @param documentId Mã tài liệu
     * @param userId ID của người dùng thực hiện bookmark
     * @return 200 nếu thành công, 404 nếu tài liệu không tồn tại
     */
    @PostMapping("/{documentId}/bookmark")
    public ResponseEntity<Void> bookmarkDocument(@PathVariable String documentId, @RequestParam String userId) {
        return documentService.bookmarkDocument(documentId, userId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Xóa "bookmark" khỏi tài liệu
     * @param documentId Mã tài liệu
     * @param userId ID của người dùng thực hiện unbookmark
     * @return 200 nếu thành công, 404 nếu tài liệu không tồn tại
     */
    @DeleteMapping("/{documentId}/bookmark")
    public ResponseEntity<Void> unbookmarkDocument(@PathVariable String documentId, @RequestParam String userId) {
        return documentService.unbookmarkDocument(documentId, userId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}