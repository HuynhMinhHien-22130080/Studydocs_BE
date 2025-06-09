package com.mobile.studydocs.controller;

import com.mobile.studydocs.model.dto.LoginDTO;
import com.mobile.studydocs.model.dto.RegisterDTO;
import com.mobile.studydocs.model.dto.ForgotPasswordDTO;
import com.mobile.studydocs.response.BaseResponse;
import com.mobile.studydocs.service.AuthService;
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

    // API Đăng ký bằng Firebase ID Token
    // Client đăng ký qua Firebase Auth, sau đó gửi idToken lên BE để lưu thông tin nếu cần
    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(@RequestBody RegisterDTO request) {
        String uid = authService.verifyToken(request.getIdToken());
        if (uid != null) {
            // Có thể lưu thêm thông tin user vào Firestore tại đây nếu muốn
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Đăng ký thành công", uid));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse(HttpStatus.UNAUTHORIZED.value(), "Token không hợp lệ", null));
        }
    }
}