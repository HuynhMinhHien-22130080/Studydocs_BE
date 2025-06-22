package com.mobile.studydocs.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.mobile.studydocs.dao.UserDao;
import com.mobile.studydocs.model.dto.request.RegisterRequest;
import com.mobile.studydocs.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;

    public User findUserById(String userId) {
        return userDao.findUserById(userId);
    }

    public void saveUserToFirestore(User user) {
        Firestore db = FirestoreClient.getFirestore();
        db.collection("users").document(user.getUserId()).set(user);
    }

    /**
     * Đăng ký người dùng mới: tạo User và lưu vào Firestore
     * @param userId UID lấy từ Firebase
     * @param request Thông tin đăng ký
     */
    public void registerUser(String userId, RegisterRequest request) {
        User user = new User();
        user.setUserId(userId);
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setFcmTokens(List.of(request.fcmToken()));
        userDao.saveUserToFirestore(user);
    }

    public void addFcmToken(String userId, String fcmToken) {
        userDao.addFcmToken(userId, fcmToken);
    }

    public void removeFcmToken(String userId, String fcmToken) {
        userDao.removeFcmToken(userId, fcmToken);
    }
}
