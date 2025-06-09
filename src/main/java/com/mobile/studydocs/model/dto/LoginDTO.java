package com.mobile.studydocs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Bean nhận dữ liệu đăng nhập từ client (Firebase ID Token)
// Sử dụng cho API /auth/login
// idToken: Token xác thực lấy từ Firebase Auth
public class LoginDTO {

    private String idToken; // Token Firebase gửi từ client

    // Getter, Setter
    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }

}
