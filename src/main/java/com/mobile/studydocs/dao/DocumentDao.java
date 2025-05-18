package com.mobile.studydocs.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.mobile.studydocs.model.entity.DocumentEntity;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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

    /**
     * Thêm một bookmark vào subcollection "bookmarks" của tài liệu
     * @param documentId ID của tài liệu
     * @param userId ID của người dùng thực hiện bookmark
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean addBookmark(String documentId, String userId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("documents").document(documentId);
        CollectionReference bookmarksRef = docRef.collection("bookmarks");
        DocumentReference bookmarkRef = bookmarksRef.document(userId);

        // Tạo một đối tượng bookmark đơn giản
        ApiFuture<WriteResult> future = bookmarkRef.set(new Bookmark(userId, Instant.now()));
        return future.get() != null;
    }

    /**
     * Xóa một bookmark khỏi subcollection "bookmarks" của tài liệu
     * @param documentId ID của tài liệu
     * @param userId ID của người dùng thực hiện unbookmark
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean removeBookmark(String documentId, String userId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("documents").document(documentId);
        DocumentReference bookmarkRef = docRef.collection("bookmarks").document(userId);

        ApiFuture<WriteResult> future = bookmarkRef.delete();
        return future.get() != null;
    }

    // Class Bookmark để lưu trữ dữ liệu bookmark
    @Data
    public static class Bookmark {
        private String userId;
        private Instant createAt;

        public Bookmark(String userId, Instant createAt) {
            this.userId = userId;
            this.createAt = createAt;
        }
    }
}