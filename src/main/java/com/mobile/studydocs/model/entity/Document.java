package com.mobile.studydocs.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Document {
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
    private List<Like> likes = new ArrayList<>();

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Like {
        private String userId;      // ai like
        private String type;
        private Timestamp createAt;
    }

}
