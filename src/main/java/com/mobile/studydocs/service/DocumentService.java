package com.mobile.studydocs.service;

import com.google.cloud.Timestamp;
import com.mobile.studydocs.dao.DocumentDao;
import com.mobile.studydocs.exception.BusinessException;
import com.mobile.studydocs.model.dto.DocumentDTO;
import com.mobile.studydocs.model.dto.DocumentMapper;
import com.mobile.studydocs.model.dto.SearchDTO;
import com.mobile.studydocs.model.entity.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    private final DocumentDao documentDao;
    private final FirebaseStorageService firebaseStorageService;

    public DocumentService(DocumentDao documentDao, FirebaseStorageService firebaseStorageService) {
        this.documentDao = documentDao;
        this.firebaseStorageService = firebaseStorageService;
    }

    public SearchDTO searchByTitle(String title) {
        try {
            List<Document> docs = documentDao.getDocumentsByTitle(title);
            List<DocumentDTO> dtos = docs.stream()
                    .map(DocumentMapper::toDTO)
                    .toList();
            return new SearchDTO(dtos);
        } catch (ExecutionException | InterruptedException e) {
            throw new BusinessException("Error while searching by title", e.getCause());
        }
    }

    public SearchDTO searchByUniversity(String university) {
        try {
            List<Document> docs = documentDao.getDocumentsByUniversity(university);
            List<DocumentDTO> dtos = docs.stream()
                    .map(DocumentMapper::toDTO)
                    .toList();
            return new SearchDTO(dtos);
        } catch (ExecutionException | InterruptedException e) {
            throw new BusinessException("Error while searching by university", e.getCause());
        }
    }

    public SearchDTO searchBySubject(String subject) {
        try {
            List<Document> docs = documentDao.getDocumentsBySubject(subject);
            List<DocumentDTO> dtos = docs.stream()
                    .map(DocumentMapper::toDTO)
                    .toList();
            return new SearchDTO(dtos);
        } catch (ExecutionException | InterruptedException e) {
            throw new BusinessException("Error while searching by subject", e.getCause());
        }
    }

    public SearchDTO getAll() {
        try {
            List<Document> docs = documentDao.getAllDocuments();
            List<DocumentDTO> dtoList = docs.stream()
                    .map(DocumentMapper::toDTO) // map qua DTO
                    .collect(Collectors.toList());
            return new SearchDTO(dtoList);
        } catch (ExecutionException | InterruptedException e) {
            throw new BusinessException("Error while getting all documents", e.getCause());
        }
    }


    /**
     * Lấy thông tin chi tiết tài liệu theo ID và chuyển thành DTO
     * @param documentId ID của tài liệu cần lấy
     * @return Optional chứa DocumentDTO nếu tồn tại, hoặc empty nếu không
     */
    public Optional<DocumentDTO> getDocumentById(String documentId) {
        try {
            Document entity = documentDao.findById(documentId);
            if (entity == null) return Optional.empty();

            DocumentDTO dto = DocumentDTO.builder()
                    .id(entity.getId())
                    .userId(entity.getUserId())
                    .title(entity.getTitle())
                    .description(entity.getDescription())
                    .fileUrl(entity.getFileUrl())
                    .subject(entity.getSubject())
                    .university(entity.getUniversity())
                    .isDelete(entity.getIsDelete())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .deletedAt(entity.getDeletedAt())
                    .likes(entity.getLikes() != null ? entity.getLikes().stream()
                            .map(like -> DocumentDTO.LikeDTO.builder()
                                    .userId(like.getUserId())
                                    .type(like.getType())
                                    .createdAt(like.getCreatedAt())
                                    .build())
                            .collect(Collectors.toList()) : null)
                    .build();

            return Optional.of(dto);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    public void likeDocument(String documentId, String userId) {
        try {
            Document entity = documentDao.findById(documentId);
            if (entity == null) throw new BusinessException("Tài liệu không tồn tại");

            boolean success = documentDao.addLike(documentId, userId);
            if (!success) throw new BusinessException("Không thể thích tài liệu");
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi thích tài liệu", e);
        }
    }

    public void unlikeDocument(String documentId, String userId) {
        try {
            Document entity = documentDao.findById(documentId);
            if (entity == null) throw new BusinessException("Tài liệu không tồn tại");

            boolean success = documentDao.removeLike(documentId, userId);
            if (!success) throw new BusinessException("Không thể bỏ thích tài liệu");
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi bỏ thích tài liệu", e);
        }
    }

//    public boolean saveDocument(String idDocument) {
//    return documentDao.saveDocument(idDocument);
//    }

    // ===== hao lam phần này (upload document + file) =====
    public Document uploadDocument(Document document, MultipartFile file) throws Exception {
        // 1. Upload file lên Firebase Storage
        String fileName = firebaseStorageService.uploadFile(file);
        document.setFileUrl(fileName);
        long millis = System.currentTimeMillis();
        long seconds = millis / 1000;
        int nanos = (int) ((millis % 1000) * 1_000_000); // phần dư ms chuyển sang nanoseconds
        document.setCreatedAt(Timestamp.ofTimeSecondsAndNanos(seconds, nanos));
        document.setIsDelete(false);
        // 2. Lưu document vào Firestore
        documentDao.save(document);
        return document;
    }
    // ===== end hao lam phần này =====
}