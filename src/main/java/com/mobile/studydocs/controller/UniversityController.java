package com.mobile.studydocs.controller;

import com.mobile.studydocs.model.entity.University;
import com.mobile.studydocs.response.BaseResponse;
import com.mobile.studydocs.service.UniversityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.AllArgsConstructor;

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
        try {
            universityService.uploadUniversity(university);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse(HttpStatus.OK.value(), "Upload university thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi upload university", null));
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
}
