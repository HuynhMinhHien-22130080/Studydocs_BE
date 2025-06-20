package com.mobile.studydocs.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    // Đọc đường dẫn đến file cấu hình Firebase từ file properties
    @Value("${firebase.config.path}")
    private String firebaseConfigPath;
    @Value("${firebase.bucket-name}")
    private String bucketName;

    /**
     * Khởi tạo FirebaseApp sử dụng thông tin cấu hình từ file service account JSON.
     * FirebaseApp là đối tượng chính để kết nối với các dịch vụ của Firebase (Firestore, Storage, v.v.).
     *
     * @return FirebaseApp đã được khởi tạo
     * @throws IOException nếu có lỗi khi đọc file cấu hình
     */
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Đọc file JSON chứa thông tin xác thực
        FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);
        FirestoreOptions firestoreOptions = FirestoreOptions
                .newBuilder()
                .setDatabaseId("studydocs")
                .build();
        // Cấu hình FirebaseOptions với thông tin xác thực
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setFirestoreOptions(firestoreOptions)
                .setStorageBucket(bucketName)
                .build();

        // Khởi tạo FirebaseApp với FirebaseOptions đã cấu hình
        return FirebaseApp.initializeApp(options);
    }

    /**
     * Tạo Bean Storage để sử dụng Firebase Cloud Storage trong ứng dụng.
     * Storage cung cấp các phương thức để làm việc với dữ liệu trong Firebase Cloud Storage (lưu trữ tệp tin, tải lên, tải xuống, v.v.).
     *
     * @return Storage instance từ Firebase Cloud Storage
     */
    @Bean
    public Storage storage(FirebaseApp firebaseApp) {
        // Khởi tạo và trả về Storage client
        return StorageClient.getInstance(firebaseApp).bucket().getStorage();
    }

    /**
     * Tạo Bean Firestore để sử dụng Firebase Firestore trong ứng dụng.
     * Firestore cung cấp các phương thức để làm việc với cơ sở dữ liệu NoSQL của Firebase (thêm, sửa, xóa tài liệu, truy vấn, v.v.).
     *
     * @return Firestore instance từ Firebase Firestore
     */
    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        // Khởi tạo và trả về Firestore client
        return FirestoreClient.getFirestore(firebaseApp);
    }

    /**
     * Bean để sử dụng Firebase Cloud Messaging (FCM).
     * FirebaseMessaging là client để gửi thông báo đến thiết bị người dùng.
     *
     * @return FirebaseMessaging instance
     */
    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
