package com.mobile.studydocs.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.mobile.studydocs.dao.UserDao;
import com.mobile.studydocs.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;
    public User findUserById(String userId){
        return userDao.getUser(userId);
    }

    public void saveUserToFirestore(User user) {
        Firestore db = FirestoreClient.getFirestore();
        db.collection("users").document(user.getUserId()).set(user);
    }

    /**
     * Cập nhật thông tin người dùng trong Firestore
     * @param userId UID của người dùng (lấy từ Firebase)
     * @param fullName Họ tên mới
     * @param avatarUrl Ảnh đại diện mới
     * @param email Email mới
     */
    public void updateUserInFirestore(String userId, String fullName, String avatarUrl, String email) {
        Firestore db = FirestoreClient.getFirestore();
        db.collection("users").document(userId)
            .update("fullName", fullName,
                    "avatarUrl", avatarUrl,
                    "email", email);

    }
}
