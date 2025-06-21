package com.mobile.studydocs.controller;

import com.mobile.studydocs.model.dto.request.RegisterRequest;
import com.mobile.studydocs.response.BaseResponse;
import com.mobile.studydocs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(@RequestAttribute("userId") String userId, @RequestBody RegisterRequest registerRequest) {
        userService.registerUser(userId, registerRequest);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Đăng ký thành công", ""));
    }

    @PatchMapping("/fcm-token")
    public ResponseEntity<BaseResponse> addFcmToken(@RequestAttribute("userId") String userId, String fcmToken) {
        userService.addFcmToken(userId, fcmToken);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Thêm thành công", null));
    }
}
