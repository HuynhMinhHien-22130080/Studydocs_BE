package com.mobile.studydocs.service;

import com.mobile.studydocs.dao.UserDao;
import com.mobile.studydocs.model.dto.request.RegisterRequest;
import com.mobile.studydocs.model.dto.request.UpdateFcmToken;
import com.mobile.studydocs.model.dto.request.UpdateUserRequest;
import com.mobile.studydocs.model.dto.response.UserResponse;
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

    public UserResponse getById(String userId) {
        User user = userDao.findUserById(userId);
       return new UserResponse(user.getFullName(), user.getAvatarUrl(), user.getEmail());
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
        userDao.saveUserToFirestore(user);
    }

    public void addFcmToken(String userId, UpdateFcmToken updateFcmToken) {
        userDao.addFcmToken(userId, updateFcmToken.fcmToken());
    }

    public void removeFcmToken(String userId, String fcmToken) {
        userDao.removeFcmToken(userId, fcmToken);
    }

    public void updateUser(String userId, UpdateUserRequest updateUserRequest) {
        userDao.updateUserInFirestore(userId, updateUserRequest.fullName(), updateUserRequest.avatarUrl());
    }
}
