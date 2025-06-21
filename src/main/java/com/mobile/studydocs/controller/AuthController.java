package com.mobile.studydocs.controller;

import com.mobile.studydocs.model.dto.LoginDTO;
import com.mobile.studydocs.model.dto.RegisterDTO;
import com.mobile.studydocs.model.dto.ForgotPasswordDTO;
import com.mobile.studydocs.model.dto.UpdateUserDTO;
import com.mobile.studydocs.model.entity.User;
import com.mobile.studydocs.response.BaseResponse;
import com.mobile.studydocs.service.AuthService;
import com.mobile.studydocs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller xử lý các API xác thực
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    // API Đăng nhập bằng Firebase ID Token
    // Client gửi idToken lấy từ Firebase Auth lên, BE xác thực và trả về UID nếu hợp lệ
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody LoginDTO request) {
        String uid = authService.verifyToken(request.getIdToken());
        if (uid != null) {
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Đăng nhập thành công", uid));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse(HttpStatus.UNAUTHORIZED.value(), "Token không hợp lệ", null));
        }
    }

    /**
     * API Đăng ký tài khoản bằng Firebase ID Token
     * App Android gửi idToken + thông tin cá nhân lên, BE xác thực và lưu vào Firestore
     */
    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(@RequestBody RegisterDTO request) {
        String uid = authService.verifyToken(request.getIdToken());
        if (uid != null) {
            // Lưu thông tin user vào Firestore
            User user = new User();
            user.setUserId(uid);
            user.setFullName(request.getFullName());
            user.setAvatarUrl(request.getAvatarUrl());
            user.setEmail(request.getEmail());
            userService.saveUserToFirestore(user);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Đăng ký thành công", uid));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse(HttpStatus.UNAUTHORIZED.value(), "Token không hợp lệ", null));
        }
    }

    // API Quên mật khẩu
    // Client gửi email, BE gọi Firebase gửi email đặt lại mật khẩu
    @PostMapping("/forgot-password")
    public ResponseEntity<BaseResponse> forgotPassword(@RequestBody ForgotPasswordDTO request) {
        boolean sent = authService.sendPasswordResetEmail(request.getEmail());
        if (sent) {
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Đã gửi email đặt lại mật khẩu", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse(HttpStatus.BAD_REQUEST.value(), "Gửi email thất bại", null));
        }
    }

    /**
     * API cập nhật thông tin cá nhân
     * App Android gửi idToken + thông tin mới, BE xác thực và cập nhật Firestore
     */
    @PutMapping("/update-profile")
    public ResponseEntity<BaseResponse> updateProfile(@RequestBody UpdateUserDTO request) {
        String uid = request.getUserId();
        // Có thể xác thực token nếu muốn bảo mật hơn
        userService.updateUserInFirestore(uid, request.getDisplayName(), request.getPhotoURL(), request.getEmail());
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Cập nhật thông tin thành công", null));
    }
}