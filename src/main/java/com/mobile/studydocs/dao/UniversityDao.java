package com.mobile.studydocs.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.mobile.studydocs.model.entity.University;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import com.mobile.studydocs.exception.ResourceNotFoundException;

@Component
public class UniversityDao {
    private static final String UNIVERSITIES_COLLECTION = "university";
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
            uni.setId(docSnap.getId());
            result.add(uni);
        }
        return result;
    }
    // ===== end hao lam phần này =====

    // ===== hao lam phần này (add subject to university) =====
    /**
     * Thêm một môn học vào danh sách môn học của một trường đại học.
     * Hàm này sẽ kiểm tra nếu môn học đã tồn tại để tránh trùng lặp.
     *
     * @param universityId ID của trường đại học trên Firebase.
     * @param subjectName Tên của môn học cần thêm.
     * @return Trả về true nếu môn học được thêm thành công (vì nó là môn mới).
     *         Trả về false nếu môn học đã tồn tại và không được thêm vào.
     * @throws Exception Nếu không tìm thấy trường đại học hoặc có lỗi khi giao tiếp với Firebase.
     */
    public boolean addSubject(String universityId, String subjectName) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference universityRef = db.collection(UNIVERSITIES_COLLECTION).document(universityId);

        // Bước 1: Đọc dữ liệu hiện tại của trường đại học
        DocumentSnapshot documentSnapshot = universityRef.get().get();

        if (!documentSnapshot.exists()) {
            throw new ResourceNotFoundException("Trường đại học", "id", universityId);
        }

        University university = documentSnapshot.toObject(University.class);
        List<String> subjects = university.getSubjects();

        // Bước 2: Kiểm tra xem môn học đã tồn tại chưa
        if (subjects != null && subjects.contains(subjectName)) {
            return false; // Trả về false vì môn học đã tồn tại
        }

        // Bước 3: Nếu chưa tồn tại, thực hiện cập nhật và trả về true
        universityRef.update("subjects", FieldValue.arrayUnion(subjectName)).get();
        return true; // Trả về true vì đã thêm thành công
    }
    // ===== end hao lam phần này =====

    // ===== hao lam phần này (get university by id) =====
    public University getUniversityById(String universityId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference universityRef = db.collection(UNIVERSITIES_COLLECTION).document(universityId);
        DocumentSnapshot documentSnapshot = universityRef.get().get();
        return documentSnapshot.toObject(University.class);
    }
    // ===== end hao lam phần này =====
}
