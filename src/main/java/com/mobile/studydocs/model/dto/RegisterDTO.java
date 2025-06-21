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
    private String fullName;
    private String avatarUrl;
    private String email;

    // Getter, Setter
    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}