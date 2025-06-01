package com.mobile.studydocs.exception;

import org.springframework.http.HttpStatus;

/**
 * Lớp ngoại lệ dùng để biểu thị các lỗi liên quan đến nghiệp vụ (business logic).
 * Ngoại lệ này được ném ra khi một quy tắc nghiệp vụ bị vi phạm.
 * Mặc định trả về mã HTTP 400 Bad Request.
 *
 * <p>Class này kế thừa từ {@link BaseException} và cung cấp các constructor
 * chuyên biệt cho các lỗi nghiệp vụ.</p>
 *
 */
public class BusinessException extends BaseException {

    /**
     * Khởi tạo một đối tượng BusinessException với thông điệp lỗi chi tiết.
     * Mã trạng thái HTTP mặc định là 400 Bad Request, mã lỗi mặc định là "BUSINESS_ERROR".
     *
     * @param message thông điệp chi tiết giải thích lý do gây ra lỗi
     */
    public BusinessException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "BUSINESS_ERROR");
    }

    /**
     * Khởi tạo một đối tượng BusinessException với thông điệp lỗi chi tiết
     * và mã lỗi tùy chỉnh.
     * Mã trạng thái HTTP mặc định là 400 Bad Request.
     *
     * @param message thông điệp chi tiết giải thích lý do gây ra lỗi
     * @param errorCode mã lỗi tùy chỉnh biểu thị lỗi nghiệp vụ
     */
    public BusinessException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }

    /**
     * Khởi tạo một đối tượng BusinessException với thông điệp lỗi chi tiết
     * và nguyên nhân gây lỗi.
     * Mã trạng thái HTTP mặc định là 400 Bad Request, mã lỗi mặc định là "BUSINESS_ERROR".
     *
     * @param message thông điệp chi tiết giải thích lý do gây ra lỗi
     * @param cause nguyên nhân gốc gây ra ngoại lệ (để truy vết lỗi)
     */
    public BusinessException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", cause);
    }
}
