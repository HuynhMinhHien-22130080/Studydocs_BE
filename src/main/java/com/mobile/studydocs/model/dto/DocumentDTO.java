package com.mobile.studydocs.model.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDTO {
    private String id;
    private String userId;
    private String title;
    private String description;
    private String fileUrl;
    private String subject;
    private String university;
    private Boolean isDelete;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private List<LikeDTO> likes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LikeDTO {
        private String userId;
        private String type;
        private Instant createAt;
    }
}

