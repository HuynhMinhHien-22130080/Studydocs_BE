package com.mobile.studydocs.model.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @DocumentId
    private String userId;
    private String fullName;
    private String avatarUrl;
    private String email;
    private List<String> save;
    private List<String> fcmTokens;
}
