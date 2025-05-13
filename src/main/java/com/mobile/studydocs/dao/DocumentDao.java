package com.mobile.studydocs.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.mobile.studydocs.model.entity.DocumentEntity;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Repository
public class DocumentDao {

    private final Firestore firestore;

    public DocumentDao(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Lấy DocumentEntity theo ID, bao gồm subcollection "likes"
     */
    public DocumentEntity findById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("documents").document(documentId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot snapshot = future.get();
        if (!snapshot.exists()) {
            return null;
        }
        // Map dữ liệu chính
        DocumentEntity doc = snapshot.toObject(DocumentEntity.class);

        // Lấy subcollection likes
        ApiFuture<QuerySnapshot> likesSnap = docRef.collection("likes").get();
        likesSnap.get().getDocuments().forEach(ds -> {
            DocumentEntity.Like like = ds.toObject(DocumentEntity.Like.class);
            doc.getLikes().add(like);
        });
        return doc;
    }
}
