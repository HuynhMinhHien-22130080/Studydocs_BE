package com.mobile.studydocs.controller;

import com.mobile.studydocs.event.model.NotificationCreateEvent;
import com.mobile.studydocs.event.rabbitmq.producer.NotificationEventProducer;
import com.mobile.studydocs.response.BaseResponse;
import com.mobile.studydocs.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationEventProducer notificationEventProducer;


    @GetMapping
    public BaseResponse getNotifications(@RequestAttribute("userId") String userId) {
        return new BaseResponse(HttpStatus.OK.value(), "Lấy thông báo thành công", notificationService.getNotifications(userId));
    }

    @PatchMapping("/read/{notificationId}")
    public BaseResponse readNotification(@RequestAttribute("userId") String userId, @PathVariable String notificationId) {
        notificationService.markAsRead(userId, notificationId);
        return new BaseResponse(HttpStatus.OK.value(), "Đánh dấu đọc thông báo thành công", true);
    }

    @PatchMapping()
    public BaseResponse readAllNotifications(@RequestAttribute("userId") String userId) {
        notificationService.markAllAsRead(userId);
        return new BaseResponse(HttpStatus.OK.value(), "Đánh dấu đọc tất cả thông báo thành công", true);
    }

    @DeleteMapping("/{notificationId}")
    public BaseResponse deleteNotification(@RequestAttribute("userId") String userId, @PathVariable String notificationId) {
        notificationService.deleteNotification(userId, notificationId);
        return new BaseResponse(HttpStatus.OK.value(), "Xóa thông báo thành công", true);
    }

    @DeleteMapping()
    public BaseResponse deleteAllNotifications(@RequestAttribute("userId") String userId) {
        notificationService.deleteAllNotifications(userId);
        return new BaseResponse(HttpStatus.OK.value(), "Xoá tất cả thông báo thành công", true);
    }
}


