package com.mobile.studydocs.dao;

import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.mobile.studydocs.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDao {
    private final Firestore firestore;
    private static final String USER_COLLECTION = "users";

    public User findUserById(String userId) {
        try {
            return firestore.collection(USER_COLLECTION).document(userId).get().get().toObject(User.class);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error getting user {}: {}", userId, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void saveUserToFirestore(User user) {
        firestore.collection("users")
                .document(user.getUserId())
                .set(user);
    }

    /**
     * Cập nhật thông tin người dùng trong Firestore
     * @param userId UID của người dùng (lấy từ Firebase)
     * @param fullName Họ tên mới
     * @param avatarUrl Ảnh đại diện mới
     * @param email Email mới
     */
    public void updateUserInFirestore(String userId, String fullName, String avatarUrl, String email) {
        firestore.collection("users").document(userId)
                .update("fullName", fullName,
                        "avatarUrl", avatarUrl,
                        "email", email);
    }

    public void addFcmToken(String userId, String fcmToken) {
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            return;
        }
        firestore.collection("users").document(userId)
                .update("fcmTokens", FieldValue.arrayUnion(fcmToken));
    }

    public void removeFcmToken(String userId, String fcmToken) {
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            return;
        }
        firestore.collection("users").document(userId)
                .update("fcmTokens", FieldValue.arrayRemove(fcmToken));
    }
}
