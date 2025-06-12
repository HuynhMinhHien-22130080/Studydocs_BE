package com.mobile.studydocs.model.enums;

public enum NotificationType {
    NEW_POST,
    POST_LIKED;

    public static FollowType getFollowType(NotificationType type) {
        return switch (type) {
            case NEW_POST, POST_LIKED -> FollowType.USER;
        };
    }
}
