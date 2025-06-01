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
}
