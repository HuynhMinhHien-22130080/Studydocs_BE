package com.mobile.studydocs.model.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.mobile.studydocs.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.google.cloud.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @DocumentId
    private String notificationId;        // ID của thông báo (Firestore doc ID)
    private String senderId;               // Ai thực hiện hành động
    private NotificationType type;
    private String title;                  // Tiêu đề hiển thị
    private String message;                // Nội dung phụ/trích dẫn
    private String targetId;               // ID bài viết/bình luận/... liên quan
    @Builder.Default
    private boolean isRead = false;             // Đã đọc chưa
    @Builder.Default
    private Timestamp createdAt = Timestamp.now(); // Thời điểm tạo

    public Boolean getIsRead() {
        return this.isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }


}
