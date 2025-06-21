package com.mobile.studydocs.model.dto;

import lombok.*;
import com.google.cloud.Timestamp;
import java.util.ArrayList;
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
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
    @Builder.Default
    private List<LikeDTO> likes = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LikeDTO {
        private String userId;
        private String type;
        private Timestamp createdAt;
    }
}