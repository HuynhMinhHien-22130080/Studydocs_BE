package com.mobile.studydocs.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import com.google.firebase.cloud.FirestoreClient;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.mobile.studydocs.model.dto.SearchDTO;
import com.mobile.studydocs.model.entity.Document;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.mobile.studydocs.exception.BusinessException;
import com.mobile.studydocs.exception.ResourceNotFoundException;

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
     * Lấy Document theo ID, bao gồm subcollection "likes"
     */
    public Document findById(String documentId) throws ExecutionException, InterruptedException {
        if (documentId == null || documentId.trim().isEmpty()) {
            throw new BusinessException("Document ID is required", "DOCUMENT_ID_REQUIRED");
        }
        
        DocumentReference docRef = firestore.collection("documents").document(documentId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot snapshot = future.get();
        
        if (!snapshot.exists()) {
            throw new ResourceNotFoundException("Document", "id", documentId);
        }
        
        // Map dữ liệu chính
        Document doc = snapshot.toObject(Document.class);

        // Lấy subcollection likes
        ApiFuture<QuerySnapshot> likesSnap = docRef.collection("likes").get();
        likesSnap.get().getDocuments().forEach(ds -> {
            Document.Like like = ds.toObject(Document.Like.class);
            doc.getLikes().add(like);
        });
        return doc;
    }
    //===== Phần này của hao =====
    //lấy tất cả document của user
    public List<Document> getDocumentsByUserId(String userId) throws ExecutionException, InterruptedException {
        List<Document> result = new ArrayList<>();
        Firestore db= FirestoreClient.getFirestore();
        // Truy vấn tất cả document có isDelete = false và userID trùng khớp
        ApiFuture<QuerySnapshot> future = db.collection("documents")
                .whereEqualTo("isDelete", false).whereEqualTo("userId",userId)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot docSnap : documents) {
            Document doc = docSnap.toObject(Document.class);
            result.add(doc);
        }
        return result;
    }
    //===== end phần này của hao =====

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

        if (likeRef.get().get().exists()) {
            return true;
        }

        Document.Like like = Document.Like.builder()
                .userId(userId)
                .type("LIKE")
                .createAt(Timestamp.from(Instant.now()))
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

    // ===== hao lam phần này (upload document + file) =====
    /**
     * Lưu document mới vào Firestore
     */
    public void save(Document document) throws Exception {
        // Validation với BusinessException
        if (document == null) {
            throw new BusinessException("Document cannot be null", "DOCUMENT_NULL");
        }
        
        if (document.getTitle() == null || document.getTitle().trim().isEmpty()) {
            throw new BusinessException("Document title is required", "DOCUMENT_TITLE_REQUIRED");
        }
        
        if (document.getUserId() == null || document.getUserId().trim().isEmpty()) {
            throw new BusinessException("User ID is required", "USER_ID_REQUIRED");
        }
        
        // Tự động tạo ID nếu chưa có
        String docId = document.getId() != null ? document.getId() : firestore.collection(DOCUMENTS_COLLECTION).document().getId();
        
        // Kiểm tra trùng lặp với BusinessException
        if (document.getId() != null && firestore.collection(DOCUMENTS_COLLECTION).document(docId).get().get().exists()) {
            throw new BusinessException("Document with ID " + docId + " already exists", "DOCUMENT_ALREADY_EXISTS");
        }
        
        document.setId(docId);
        
        // Tự động set timestamp
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (document.getCreatedAt() == null) {
            document.setCreatedAt(now);
        }
        document.setUpdatedAt(now);
        
        // Đảm bảo isDelete = false cho document mới
        if (document.getIsDelete() == null) {
            document.setIsDelete(false);
        }
        
        try {
            firestore.collection(DOCUMENTS_COLLECTION).document(docId).set(document).get();
        } catch (Exception e) {
            throw new BusinessException("Failed to save document: " + e.getMessage(), e);
        }
    }
    // ===== end hao lam phần này =====
}
