package com.mobile.studydocs.dao;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class UserDao {
    private static final String USER_COLLECTION = "users";
    private static final String FOLLOW_COLLECTION = "followers";
    private final Firestore firestore;

    public UserDao(Firestore firestore) {
        this.firestore = firestore;
    }

    public DocumentReference getUserRef(String userId) {
        return firestore.collection(USER_COLLECTION).document(userId);
    }

    public boolean addFollow(String followeeId, String followerId) {
        try {
            DocumentReference followerDocRef = getUserRef(followeeId).collection(FOLLOW_COLLECTION).document(followerId);
            Map<String, Object> data = new HashMap<>();
            data.put("followerId", followerId);
            data.put("notifyOnPost", true);
            followerDocRef.set(data).get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    public boolean removeFollow(String followeeId, String followerId) {
        try {
            DocumentReference followerDocRef = getUserRef(followeeId).collection(FOLLOW_COLLECTION).document(followerId);
            followerDocRef.delete().get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }
}
