package com.mobile.studydocs.service;

import com.mobile.studydocs.dao.DocumentDao;
import com.mobile.studydocs.exception.BusinessException;
import com.mobile.studydocs.model.dto.DocumentDTO;
import com.mobile.studydocs.model.dto.SearchDTO;
import com.mobile.studydocs.model.entity.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    private final DocumentDao documentDao;

    public DocumentService(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    public SearchDTO searchByTitle(String title)  {
        List<Document>res= new ArrayList<>();
        try{
        res.addAll(documentDao.getDocumentsByTitle(title));}
        catch(ExecutionException | InterruptedException e){
            throw new BusinessException("Error while searching by title",e.getCause());
        }
        return new SearchDTO(res);
    }
    public SearchDTO searchByUniversity(String university)  {
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

    public DocumentDTO getDocumentById(String documentId) {
        try {
            Document entity = documentDao.findById(documentId);
            if (entity == null) {
                throw new BusinessException("Tài liệu không tồn tại");
            }

            return DocumentDTO.builder()
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
        } catch (Exception e) {
            throw new BusinessException("Không thể lấy chi tiết tài liệu", e);
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
}