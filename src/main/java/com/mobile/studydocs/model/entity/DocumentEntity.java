package com.mobile.studydocs.model.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DocumentEntity {
    @DocumentId
    private String id;            // ví dụ: "doc456"

    @PropertyName("userId")
    private String userId;        // tham chiếu đến users/user123

    private String title;
    private String description;
    private String fileUrl;
    private String subject;
    private String university;

    @PropertyName("isDelete")
    private Boolean isDelete;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private List<Like> likes;     // từ subcollection "likes"

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Like {
        private String userId;      // ai like
        private String type;
        private Instant createAt;
    }
}