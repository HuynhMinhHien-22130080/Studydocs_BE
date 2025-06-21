package com.mobile.studydocs.service;

import com.mobile.studydocs.dao.DocumentDao;
import com.mobile.studydocs.exception.BusinessException;
import com.mobile.studydocs.model.dto.DocumentDTO;
import com.mobile.studydocs.model.dto.SearchDTO;
import com.mobile.studydocs.model.entity.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
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

    public SearchDTO searchByTitle(String title)  {
        List<Document>res= new ArrayList<>();
        try{
        res.addAll(documentDao.getDocumentsByTitle(title));}
        catch(ExecutionException | InterruptedException e){
            throw new BusinessException("Error while searching by title",e.getCause());
        }
        return new SearchDTO(res);
    }public SearchDTO searchByUniversity(String university)  {
        List<Document>res= new ArrayList<>();
        try{
            res.addAll(documentDao.getDocumentsByUniversity(university));}
        catch(ExecutionException | InterruptedException e){
            throw new BusinessException("Error while searching by title",e.getCause());
        }
        return new SearchDTO(res);
    }

    public SearchDTO searchBySubject(String subject)  {
        List<Document>res= new ArrayList<>();
        try{
            res.addAll(documentDao.getDocumentsBySubject(subject));}
        catch(ExecutionException | InterruptedException e){
            throw new BusinessException("Error while searching by title",e.getCause());
        }
        return new SearchDTO(res);
    }
    public SearchDTO getAll(String userID){
        List<Document>res= new ArrayList<>();
        try{
        res.addAll(documentDao.getAllDocuments());}
         catch(ExecutionException | InterruptedException e){
             throw new BusinessException("Error while searching by title",e.getCause());
            }
        return new SearchDTO(res);
    }

    /**
     * Lấy tất cả tài liệu của một user cụ thể
     * @param userId ID của user cần lấy tài liệu
     * @return SearchDTO chứa danh sách tài liệu
     */
    public SearchDTO getDocumentsByUserId(String userId) {
        List<Document> res = new ArrayList<>();
        try {
            res.addAll(documentDao.getDocumentsByUserId(userId));
        } catch (ExecutionException | InterruptedException e) {
            throw new BusinessException("Error while getting documents by user ID", e.getCause());
        }
        return new SearchDTO(res);
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
                                    .createAt(like.getCreateAt())
                                    .build())
                            .collect(Collectors.toList()) : null)
                    .build();

            return Optional.of(dto);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Thêm like cho tài liệu
     * @param documentId ID của tài liệu
     * @param userId ID của người dùng
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean likeDocument(String documentId, String userId) {
        try {
            Document entity = documentDao.findById(documentId);
            if (entity == null) return false;
            return documentDao.addLike(documentId, userId);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Xóa like khỏi tài liệu
     * @param documentId ID của tài liệu
     * @param userId ID của người dùng
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean unlikeDocument(String documentId, String userId) {
        try {
            Document entity = documentDao.findById(documentId);
            if (entity == null) return false;
            return documentDao.removeLike(documentId, userId);
        } catch (Exception e) {
            return false;
        }
    }

    // ===== hao lam phần này (upload document + file) =====
    public Document uploadDocument(Document document, MultipartFile file) throws Exception {
        // 1. Upload file lên Firebase Storage
        String fileName = firebaseStorageService.uploadFile(file);
        document.setFileUrl(fileName);
        document.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        document.setIsDelete(false);
        // 2. Lưu document vào Firestore
        documentDao.save(document);
        return document;
    }
    // ===== end hao lam phần này =====
}