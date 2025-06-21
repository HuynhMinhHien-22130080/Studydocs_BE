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
    public SearchDTO getAll(){
        List<Document>res= new ArrayList<>();
        try{
        res.addAll(documentDao.getAllDocuments());}
         catch(ExecutionException | InterruptedException e){
             throw new BusinessException("Error while searching by title",e.getCause());
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
                                    .createdAt(like.getCreatedAt())
                                    .build())
                            .collect(Collectors.toList()) : null)
                    .build();

            return Optional.of(dto);
        } catch (Exception e) {
            return Optional.empty();
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

    // ===== hao lam phần này (upload document + file) =====
    public Document uploadDocument(Document document, MultipartFile file) throws Exception {
        // 1. Upload file lên Firebase Storage
        String fileName = firebaseStorageService.uploadFile(file);
        document.setFileUrl(fileName);
        document.setCreatedAt(com.google.cloud.Timestamp.of(new Timestamp(System.currentTimeMillis())));
        document.setIsDelete(false);
        // 2. Lưu document vào Firestore
        documentDao.save(document);
        return document;
    }
    // ===== end hao lam phần này =====
}