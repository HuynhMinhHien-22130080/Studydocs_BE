package com.mobile.studydocs.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

// Service xác thực và xử lý các chức năng auth
@Service
public class AuthService {

    // Xác thực token Firebase, trả về UID nếu hợp lệ
    public String verifyToken(String idToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            return decodedToken.getUid();
        } catch (FirebaseAuthException e) {
            return null;
        }
    }
}
