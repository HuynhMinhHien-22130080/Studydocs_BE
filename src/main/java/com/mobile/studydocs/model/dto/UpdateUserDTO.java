package com.mobile.studydocs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho việc cập nhật thông tin người dùng
 * Sử dụng cho API /user/update
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    // ID của người dùng cần cập nhật
    private String userId;
    
    // Tên hiển thị của người dùng
    private String displayName;
    
    // URL ảnh đại diện
    private String photoURL;
    
    // Email của người dùng
    private String email;
    
    // Số điện thoại
    private String phoneNumber;
    
    // Mô tả ngắn về người dùng
    private String bio;
}