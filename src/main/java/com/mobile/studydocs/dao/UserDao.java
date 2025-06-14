package com.mobile.studydocs.dao;

import com.google.cloud.firestore.Firestore;
import com.mobile.studydocs.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDao {
    private final Firestore firestore;
    private static final String USER_COLLECTION = "users";

    public User getUser(String userId) {
        try {
            return firestore.collection(USER_COLLECTION).document(userId).get().get().toObject(User.class);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error getting user {}: {}", userId, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
