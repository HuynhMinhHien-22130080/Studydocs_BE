package com.mobile.studydocs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Bean nhận dữ liệu quên mật khẩu từ client
// Sử dụng cho API /auth/forgot-password
// email: Email tài khoản cần đặt lại mật khẩu
public class ForgotPasswordDTO {
    private String email;

    // Getter, Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}