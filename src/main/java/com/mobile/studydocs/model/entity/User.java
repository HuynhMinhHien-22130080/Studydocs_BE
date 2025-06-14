package com.mobile.studydocs.model.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;

import java.util.List;

@Data
public class User {
    @DocumentId
    private String userId;
    private String fullName;
    private String avatarUrl;
    private String email;
    private List<String> fcmTokens;
}
