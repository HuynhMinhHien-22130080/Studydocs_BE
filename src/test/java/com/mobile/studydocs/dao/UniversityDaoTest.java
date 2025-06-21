package com.mobile.studydocs.dao;

import com.mobile.studydocs.model.entity.University;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest
public class UniversityDaoTest {

    @Autowired
    private UniversityDao universityDao;

    @Test
    void testGetAllUniversities() {
        System.out.println("Bắt đầu test lấy danh sách trường đại học...");
        try {
            List<University> universities = universityDao.getAll();

            // In kết quả ra console để bạn xem
            System.out.println("========== KẾT QUẢ TỪ FIREBASE ==========");
            for (University uni : universities) {
                System.out.println("ID: " + uni.getId() + ", Tên: " + uni.getName() + ", Môn học: " + uni.getSubjects());
            }
            System.out.println("==========================================");


            // Kiểm tra để chắc chắn rằng có dữ liệu trả về
            assertNotNull(universities, "Danh sách trả về không được null.");
            assertFalse(universities.isEmpty(), "Danh sách không được rỗng. Hãy chắc chắn có dữ liệu trên Firebase.");

            System.out.println("Test thành công! Đã lấy được " + universities.size() + " trường đại học.");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Test thất bại do có lỗi Exception: " + e.getMessage());
        }
    }

    @Test
    void testAddSubject() {
        // ID của "Đại Học Nông Lâm" trên Firebase của bạn
        String testUniversityId = "9P9J2KI2LeDqHPhKItUb";
        // Sử dụng một tên môn học độc nhất mỗi lần chạy test để đảm bảo nó là mới
        String newSubject = "Kiểm thử phần mềm ";

        try {
            // ---- Kịch bản 1: Thêm môn học mới ----
            System.out.println("\nBắt đầu test thêm môn học MỚI: '" + newSubject + "'");
            boolean result = universityDao.addSubject(testUniversityId, newSubject);
            assertTrue(result, "Hàm phải trả về TRUE khi thêm một môn học mới.");

            // Kiểm tra lại trên DB xem môn học đã được thêm vào chưa
            University updatedUniversity = universityDao.getAll().stream()
                    .filter(u -> testUniversityId.equals(u.getId()))
                    .findFirst().orElseThrow(() -> new AssertionError("Không tìm thấy trường đại học sau khi cập nhật"));
            assertTrue(updatedUniversity.getSubjects().contains(newSubject), "Danh sách môn học phải chứa môn học vừa được thêm.");
            System.out.println("Test kịch bản 1 THÀNH CÔNG.");

            // ---- Kịch bản 2: Thêm lại chính môn học đó ----
            System.out.println("\nBắt đầu test thêm môn học TRÙNG LẶP: '" + newSubject + "'");
            boolean duplicateResult = universityDao.addSubject(testUniversityId, newSubject);
            assertFalse(duplicateResult, "Hàm phải trả về FALSE khi thêm một môn học đã tồn tại.");
            System.out.println("Test kịch bản 2 THÀNH CÔNG.");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Test thêm môn học thất bại: " + e.getMessage());
        }
    }
} 