
package com.mobile.studydocs.model.dto;

import com.mobile.studydocs.model.dto.DocumentDTO;
import com.mobile.studydocs.model.entity.Document;

import java.util.List;
import java.util.stream.Collectors;

public class DocumentMapper {
    public static DocumentDTO toDTO(Document doc) {
        if (doc == null) return null;

        return DocumentDTO.builder()
                .id(doc.getId())
                .userId(doc.getUserId())
                .title(doc.getTitle())
                .description(doc.getDescription())
                .fileUrl(doc.getFileUrl())
                .subject(doc.getSubject())
                .university(doc.getUniversity())
                .isDelete(doc.getIsDelete())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .deletedAt(doc.getDeletedAt())
                .likes(doc.getLikes() == null ? null :
                        doc.getLikes().stream()
                                .map(like -> DocumentDTO.LikeDTO.builder()
                                        .userId(like.getUserId())
                                        .type(like.getType())
                                        .createAt(like.getCreateAt())
                                        .build())
                                .collect(Collectors.toList()))
                .build();
    }

    public static List<DocumentDTO> toDTOList(List<Document> documents) {
        return documents.stream()
                .map(DocumentMapper::toDTO)
                .collect(Collectors.toList());
    }
}
