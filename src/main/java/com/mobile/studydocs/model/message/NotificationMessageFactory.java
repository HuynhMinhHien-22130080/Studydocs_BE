package com.mobile.studydocs.model.message;

import com.mobile.studydocs.model.enums.NotificationType;

public class NotificationMessageFactory {

    public static NotificationMessage createMessage(NotificationType type, String senderName) {
        return switch (type) {
            case NEW_POST -> new NotificationMessage(
                    "Bài viết mới từ người bạn theo dõi",
                    senderName + " vừa đăng một bài viết mới. Nhấn để xem ngay!"
            );
            case POST_LIKED -> new NotificationMessage(
                    "Bài viết của bạn được yêu thích",
                    senderName + " đã thích bài viết của bạn."
            );
        };
    }
}

