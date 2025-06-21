package com.mobile.studydocs.controller;

import com.mobile.studydocs.model.entity.University;
import com.mobile.studydocs.response.BaseResponse;
import com.mobile.studydocs.service.UniversityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.AllArgsConstructor;
import com.mobile.studydocs.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/university")
@AllArgsConstructor
public class UniversityController {
    private final UniversityService universityService;

    // ===== hao lam phần này (upload university) =====
    /**
     * Upload university (tên trường và list môn)
     */
    @PostMapping("/upload")
    public ResponseEntity<BaseResponse> uploadUniversity(@RequestBody University university) {
        // Thêm validation
        if (university.getName() == null || university.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new BaseResponse(400, "Tên trường đại học không được để trống", null));
        }
        
        if (university.getSubjects() == null || university.getSubjects().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new BaseResponse(400, "Danh sách môn học không được để trống", null));
        }
        
        try {
            universityService.uploadUniversity(university);
            return ResponseEntity.ok(new BaseResponse(200, "Upload university thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new BaseResponse(500, "Lỗi upload university: " + e.getMessage(), null));
        }
    }
    // ===== end hao lam phần này =====

    // ===== hao lam phần này (get all universities) =====
    /**
     * Lấy danh sách tất cả university (bao gồm list môn)
     */
    @GetMapping("/all")
    public ResponseEntity<BaseResponse> getAllUniversities() {
        try {
            List<University> universities = universityService.getAllUniversities();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách university thành công", universities));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi lấy danh sách university", null));
        }
    }
    // ===== end hao lam phần này =====

    // ===== hao lam phần này (add subject) =====
    /**
     * Thêm môn học vào university
     */
    @PostMapping("/{id}/subject")
    public ResponseEntity<BaseResponse> addSubject(@PathVariable String id, @RequestBody String subject) {
        try {
            boolean result = universityService.addSubject(id, subject);
            if (result) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new BaseResponse(HttpStatus.OK.value(), "Thêm subject thành công", true));
            } else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new BaseResponse(HttpStatus.OK.value(), "Subject đã tồn tại", false));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi thêm subject", false));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getUniversityById(@PathVariable String id) {
        try {
            University university = universityService.getUniversityById(id);
            return ResponseEntity.ok(new BaseResponse(200, "Lấy thông tin university thành công", university));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404)
                    .body(new BaseResponse(404, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new BaseResponse(500, "Lỗi lấy thông tin university", null));
        }
    }
}
