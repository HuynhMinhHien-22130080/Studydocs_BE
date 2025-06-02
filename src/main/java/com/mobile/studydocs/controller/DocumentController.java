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
    private final Storage storage; // Thêm Storage để xử lý download
    private final AuthService authService; // Thêm AuthService để kiểm tra người dùng đăng nhập

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse> search(@RequestParam("keyword") String keyword ){
        SearchDTO searchDTO = documentService.searchByTitle(keyword);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách thành công", searchDTO));
    }
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse> getAll() throws ExecutionException, InterruptedException {
        SearchDTO searchDTO = documentService.getAll();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách thành công", searchDTO));
    }
    public DocumentController(DocumentService documentService, Storage storage, AuthService authService) {
        this.documentService = documentService;
        this.storage = storage;
        this.authService = authService;
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
        if (!authService.isAuthenticated(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn cần đăng nhập để tải tài liệu");
        }
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
        if (!authService.isAuthenticated(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
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
        if (!authService.isAuthenticated(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return documentService.unlikeDocument(documentId, userId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/{documentId}/follow")
    public ResponseEntity<Void> followDocument(@PathVariable String documentId, @RequestParam String userId) {
        if (!authService.isAuthenticated(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return documentService.followDocument(documentId, userId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{documentId}/follow")
    public ResponseEntity<Void> unfollowDocument(@PathVariable String documentId, @RequestParam String userId) {
        if (!authService.isAuthenticated(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return documentService.unfollowDocument(documentId, userId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

}
