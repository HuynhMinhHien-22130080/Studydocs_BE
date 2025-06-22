//package com.mobile.studydocs.service;
//
//import com.mobile.studydocs.model.entity.Document;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.web.context.request.RequestAttributes;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//class DocumentServiceTest {
//
//    @Autowired
//    private DocumentService documentService;
//    @Autowired
//    private RequestAttributes requestAttributes;
//
//    @Test
//    void testUploadDocument_WithRealFile() throws Exception {
//        // Đọc file 123.jpeg
//        Path filePath = Paths.get("src/test/resources/123.jpeg");
//
//        if (!Files.exists(filePath)) {
//            System.out.println("⚠️ Không tìm thấy file test tại: " + filePath);
//            return;
//        }
//
//        // Đọc file thật
//        byte[] fileContent = Files.readAllBytes(filePath);
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "123.jpeg",
//                "image/jpeg",
//                fileContent
//        );
//
//        // Tạo document
//        Document document = Document.builder()
//                .userId("integration_test_user")
//                .title("Integration Test with Real JPEG")
//                .description("Test upload with real JPEG file")
//                .subject("Toán học")
//                .university("Đại học Nông Lâm")
//                .build();
//
//        // Upload
//        Document result = documentService.uploadDocument(document, file,@requestAttributes(Uer));
//
//        // Assert
//        assertNotNull(result);
//        assertNotNull(result.getId());
//        assertNotNull(result.getFileUrl());
//        assertEquals("integration_test_user", result.getUserId());
//        assertEquals("Integration Test with Real JPEG", result.getTitle());
//        assertNotNull(result.getCreatedAt());
//        assertFalse(result.getIsDelete());
//
//        System.out.println("✅ Upload thành công với file thật!");
//        System.out.println("�� File: " + file.getOriginalFilename());
//        System.out.println("�� Size: " + file.getSize() + " bytes");
//        System.out.println("🆔 Document ID: " + result.getId());
//        System.out.println("🔗 File URL: " + result.getFileUrl());
//    }
//}