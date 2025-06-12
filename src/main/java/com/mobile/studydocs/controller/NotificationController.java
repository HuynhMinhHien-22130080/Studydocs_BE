package com.mobile.studydocs.controller;

import com.mobile.studydocs.response.BaseResponse;
import com.mobile.studydocs.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping
    public BaseResponse getNotifications(@RequestAttribute("userId") String userId) {
        return new BaseResponse(HttpStatus.OK.value(), "Lấy thông báo thành công", notificationService.getNotifications(userId));
    }

    @PatchMapping("/read/{notificationId}")
    public BaseResponse readNotification(@RequestAttribute("userId") String userId, @PathVariable String notificationId) {
        return new BaseResponse(HttpStatus.OK.value(), "Đánh dấu đọc thông báo thành công", notificationService.markAsRead(userId, notificationId));
    }

    @PatchMapping("/readAll")
    public BaseResponse readAllNotifications(@RequestAttribute("userId") String userId) {
        return new BaseResponse(HttpStatus.OK.value(), "Đánh dấu đọc tất cả thông báo thành công", notificationService.markAllAsRead(userId));
    }

    @DeleteMapping("/{notificationId}")
    public BaseResponse deleteNotification(@RequestAttribute("userId") String userId, @PathVariable String notificationId) {
        return new BaseResponse(HttpStatus.OK.value(), "Xóa thông báo thành công", notificationService.deleteNotification(userId, notificationId));
    }

    @DeleteMapping()
    public BaseResponse deleteAllNotifications(@RequestAttribute("userId") String userId) {
        return new BaseResponse(HttpStatus.OK.value(), "Xoá tất cả thông báo thành công", notificationService.deleteAllNotifications(userId));
    }
}

