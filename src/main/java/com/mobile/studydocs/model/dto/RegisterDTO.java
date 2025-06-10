package com.mobile.studydocs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Bean nhận dữ liệu đăng ký từ client (Firebase ID Token)
// Sử dụng cho API /auth/register
// idToken: Token xác thực lấy từ Firebase Auth
public class RegisterDTO {
    private String idToken;

    // Getter, Setter
    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
}