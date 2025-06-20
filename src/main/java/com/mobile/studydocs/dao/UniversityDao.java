package com.mobile.studydocs.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.mobile.studydocs.model.entity.University;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class UniversityDao {
    private static final String UNIVERSITIES_COLLECTION = "universities";
    private final Firestore firestore;

    public UniversityDao(Firestore firestore) {
        this.firestore = firestore;
    }

    // ===== hao lam phần này (upload university) =====
    public void save(University university) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        String uniId = university.getId() != null ? university.getId() : db.collection(UNIVERSITIES_COLLECTION).document().getId();
        university.setId(uniId);
        db.collection(UNIVERSITIES_COLLECTION).document(uniId).set(university).get();
    }
    // ===== end hao lam phần này =====

    // ===== hao lam phần này (get all universities) =====
    public List<University> getAll() throws ExecutionException, InterruptedException {
        List<University> result = new ArrayList<>();
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(UNIVERSITIES_COLLECTION).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot docSnap : documents) {
            University uni = docSnap.toObject(University.class);
            result.add(uni);
        }
        return result;
    }
    // ===== end hao lam phần này =====
}
