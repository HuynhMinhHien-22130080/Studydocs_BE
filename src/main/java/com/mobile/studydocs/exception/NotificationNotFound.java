package com.mobile.studydocs.exception;

import org.springframework.http.HttpStatus;

public class NotificationNotFound extends BaseException {
    public NotificationNotFound(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    protected NotificationNotFound(String message, HttpStatus status, String errorCode, Throwable cause) {
        super(message, status, errorCode, cause);
    }
}
