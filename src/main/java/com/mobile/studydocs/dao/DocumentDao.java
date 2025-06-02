package com.mobile.studydocs.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import com.google.firebase.cloud.FirestoreClient;
import com.mobile.studydocs.model.dto.SearchDTO;
import com.mobile.studydocs.model.entity.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class DocumentDao {
    // tên collection
    private static final String DOCUMENTS_COLLECTION ="documents";



    private final Firestore firestore;
    public DocumentDao(Firestore firestore) {

        this.firestore = firestore;
    }

/*
Lấy document theo user id
*/
    public List<Document> getAllDocuments() throws ExecutionException, InterruptedException {
        List<Document> result = new ArrayList<>();
        Firestore db= FirestoreClient.getFirestore();
        // Truy vấn tất cả document có isDelete = false
        ApiFuture<QuerySnapshot> future = db.collection("documents")
                .whereEqualTo("isDelete", false)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        for (QueryDocumentSnapshot docSnap : documents) {
            Document doc = docSnap.toObject(Document.class);
            result.add(doc);
        }

        return result;
    }
    /*
    Lấy document theo id doccument
    */
    public List<Document> getDocumentsByUserID(String userID) throws ExecutionException, InterruptedException {
        List<Document> result = new ArrayList<>();
        Firestore db= FirestoreClient.getFirestore();
        // Truy vấn tất cả document có isDelete = false và userID trùng khớp
        ApiFuture<QuerySnapshot> future = db.collection("documents")
                .whereEqualTo("isDelete", false).whereEqualTo("userId",userID)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        for (QueryDocumentSnapshot docSnap : documents) {
            Document doc = docSnap.toObject(Document.class);
            result.add(doc);
        }

        return result;
    }
    /*
    Lấy document theo title
    */
    public  List<Document>getDocumentsByTitle(String title) throws ExecutionException, InterruptedException {
        List<Document> result = new ArrayList<>();
        Firestore db= FirestoreClient.getFirestore();
        // Truy vấn tất cả document có isDelete = false và userID trùng khớp
        ApiFuture<QuerySnapshot> future = db.collection("documents")
                .whereEqualTo("isDelete", false).whereEqualTo("title",title)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        for (QueryDocumentSnapshot docSnap : documents) {
            Document doc = docSnap.toObject(Document.class);
            result.add(doc);
        }
        return result;
    }
    /*
Lấy document theo subject
*/
    public   List<Document>  getDocumentsBySubject(String subject) throws ExecutionException, InterruptedException {
        List<Document> result = new ArrayList<>();
        Firestore db= FirestoreClient.getFirestore();
        // Truy vấn tất cả document có isDelete = false và userID trùng khớp
        ApiFuture<QuerySnapshot> future = db.collection("documents")
                .whereEqualTo("isDelete", false).whereEqualTo("subject",subject)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        for (QueryDocumentSnapshot docSnap : documents) {
            Document doc = docSnap.toObject(Document.class);
            result.add(doc);
        }
        return result;
    }
    /*
Lấy document theo university
*/
    public List<Document> getDocumentsByUniversity(String university) throws ExecutionException, InterruptedException {
        List<Document> result = new ArrayList<>();
        Firestore db= FirestoreClient.getFirestore();
        // Truy vấn tất cả document có isDelete = false và userID trùng khớp
        ApiFuture<QuerySnapshot> future = db.collection("documents")
                .whereEqualTo("isDelete", false).whereEqualTo("university",university)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        for (QueryDocumentSnapshot docSnap : documents) {
            Document doc = docSnap.toObject(Document.class);
            result.add(doc);
        }
        return result;
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

    /**
     * Thêm một like vào subcollection "likes" của tài liệu
     * @param documentId ID của tài liệu
     * @param userId ID của người dùng thực hiện like
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean addLike(String documentId, String userId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("documents").document(documentId);
        CollectionReference likesRef = docRef.collection("likes");
        DocumentReference likeRef = likesRef.document(userId);

        DocumentEntity.Like like = DocumentEntity.Like.builder()
                .userId(userId)
                .type("LIKE")
                .createAt(Instant.now())
                .build();

        ApiFuture<WriteResult> future = likeRef.set(like);
        return future.get() != null;
    }

    /**
     * Xóa một like khỏi subcollection "likes" của tài liệu
     * @param documentId ID của tài liệu
     * @param userId ID của người dùng thực hiện unlike
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean removeLike(String documentId, String userId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("documents").document(documentId);
        DocumentReference likeRef = docRef.collection("likes").document(userId);

        ApiFuture<WriteResult> future = likeRef.delete();
        return future.get() != null;
    }

    public boolean addFollower(String documentId, String userId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("documents").document(documentId);
        CollectionReference followersRef = docRef.collection("followers");
        DocumentReference followerRef = followersRef.document(userId);

        Follower follower = new Follower(userId, Instant.now());
        ApiFuture<WriteResult> future = followerRef.set(follower);
        return future.get() != null;
    }

    public boolean removeFollower(String documentId, String userId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("documents").document(documentId);
        DocumentReference followerRef = docRef.collection("followers").document(userId);

        ApiFuture<WriteResult> future = followerRef.delete();
        return future.get() != null;
    }
    @Data
    public static class Follower {
        private String userId;
        private Instant followAt;

        public Follower(String userId, Instant followAt) {
            this.userId = userId;
            this.followAt = followAt;
        }
    }
}
