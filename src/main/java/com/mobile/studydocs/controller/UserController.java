package com.mobile.studydocs.controller;

import com.mobile.studydocs.model.dto.request.RegisterRequest;
import com.mobile.studydocs.model.dto.request.UpdateFcmToken;
import com.mobile.studydocs.model.dto.request.UpdateUserRequest;
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

    @GetMapping
    public ResponseEntity<BaseResponse> getUserInfo(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Lấy user thành công", userService.getById(userId)));
    }

    @PutMapping
    public ResponseEntity<BaseResponse> updateUserInfo(@RequestAttribute("userId") String userId, @RequestBody UpdateUserRequest updateUserRequest) {
        userService.updateUser(userId, updateUserRequest);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Lấy user thành công", null));
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(@RequestAttribute("userId") String userId, @RequestBody RegisterRequest registerRequest) {
        userService.registerUser(userId, registerRequest);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Đăng ký thành công", ""));
    }

    @PatchMapping("/fcm-token")
    public ResponseEntity<BaseResponse> addFcmToken(@RequestAttribute("userId") String userId, @RequestBody UpdateFcmToken updateFcmToken) {
        userService.addFcmToken(userId, updateFcmToken);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Thêm thành công", null));
    }

    @DeleteMapping("/delete-fcm-token")
    public ResponseEntity<BaseResponse> removeFcmToken(
            @RequestAttribute("userId") String userId,
            @RequestParam("token") String fcmToken) {
        userService.removeFcmToken(userId, fcmToken);
        return ResponseEntity.ok(new BaseResponse(200, "Xóa thành công", null));
    }
}
