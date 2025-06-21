package com.mobile.studydocs.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.mobile.studydocs.model.entity.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class DocumentDao {
    private static final String DOCUMENTS_COLLECTION = "documents";
    private final Firestore firestore;

    public DocumentDao(Firestore firestore) {
        this.firestore = firestore;
    }

    public List<Document> getAllDocuments() throws ExecutionException, InterruptedException {
        List<Document> result = new ArrayList<>();
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(DOCUMENTS_COLLECTION)
                .whereEqualTo("isDelete", false)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot docSnap : documents) {
            Document doc = docSnap.toObject(Document.class);
            result.add(doc);
        }
        return result;
    }

    public List<Document> getDocumentsByUserID(String userID) throws ExecutionException, InterruptedException {
        List<Document> result = new ArrayList<>();
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(DOCUMENTS_COLLECTION)
                .whereEqualTo("isDelete", false)
                .whereEqualTo("userId", userID)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot docSnap : documents) {
            Document doc = docSnap.toObject(Document.class);
            result.add(doc);
        }
        return result;
    }

    public List<Document> getDocumentsByTitle(String title) throws ExecutionException, InterruptedException {
        List<Document> result = new ArrayList<>();
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(DOCUMENTS_COLLECTION)
                .whereEqualTo("isDelete", false)
                .whereEqualTo("title", title)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot docSnap : documents) {
            Document doc = docSnap.toObject(Document.class);
            result.add(doc);
        }
        return result;
    }

    public List<Document> getDocumentsBySubject(String subject) throws ExecutionException, InterruptedException {
        List<Document> result = new ArrayList<>();
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(DOCUMENTS_COLLECTION)
                .whereEqualTo("isDelete", false)
                .whereEqualTo("subject", subject)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot docSnap : documents) {
            Document doc = docSnap.toObject(Document.class);
            result.add(doc);
        }
        return result;
    }

    public List<Document> getDocumentsByUniversity(String university) throws ExecutionException, InterruptedException {
        List<Document> result = new ArrayList<>();
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(DOCUMENTS_COLLECTION)
                .whereEqualTo("isDelete", false)
                .whereEqualTo("university", university)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot docSnap : documents) {
            Document doc = docSnap.toObject(Document.class);
            result.add(doc);
        }
        return result;
    }

    public Document findById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(DOCUMENTS_COLLECTION).document(documentId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot snapshot = future.get();
        if (!snapshot.exists()) {
            return null;
        }
        Document doc = snapshot.toObject(Document.class);
        ApiFuture<QuerySnapshot> likesSnap = docRef.collection("likes").get();
        likesSnap.get().getDocuments().forEach(ds -> {
            Document.Like like = ds.toObject(Document.Like.class);
            doc.getLikes().add(like);
        });
        return doc;
    }

    public boolean addLike(String documentId, String userId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(DOCUMENTS_COLLECTION).document(documentId);
        CollectionReference likesRef = docRef.collection("likes");
        DocumentReference likeRef = likesRef.document(userId);

        if (likeRef.get().get().exists()) {
            return true;
        }

        Document.Like like = Document.Like.builder()
                .userId(userId)
                .type("LIKE")
                .createdAt(com.google.cloud.Timestamp.now())
                .build();
        ApiFuture<WriteResult> future = likeRef.set(like);
        return future.get() != null;
    }

    public boolean removeLike(String documentId, String userId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(DOCUMENTS_COLLECTION).document(documentId);
        DocumentReference likeRef = docRef.collection("likes").document(userId);
        ApiFuture<WriteResult> future = likeRef.delete();
        return future.get() != null;
    }
}