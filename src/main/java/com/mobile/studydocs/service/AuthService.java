package com.mobile.studydocs.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public boolean isAuthenticated(String userId) {
        // Logic xác thực giả lập
        return userId != null && !userId.trim().isEmpty();
    }
}
