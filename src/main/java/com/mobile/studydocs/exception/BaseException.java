package com.mobile.studydocs.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Lớp cơ sở cho tất cả các ngoại lệ tùy chỉnh trong hệ thống.
 *
 * <p>BaseException là lớp trừu tượng mở rộng từ {@link RuntimeException} và cung cấp thêm
 * hai thông tin quan trọng: {@link HttpStatus} và mã lỗi (errorCode).
 * Các lớp ngoại lệ con nên kế thừa từ lớp này để đảm bảo chuẩn hóa định dạng ngoại lệ.</p>
 *
 * <p>Lớp này hỗ trợ định nghĩa ngoại lệ với thông báo lỗi, trạng thái HTTP tương ứng
 * và mã lỗi để dễ dàng xử lý trong các tầng như RestControllerAdvice, log hoặc phản hồi về client.</p>
 *
 * @see RuntimeException
 * @see HttpStatus
 */
@Getter
public abstract class BaseException extends RuntimeException {
    /**
     * -- GETTER --
     * Lấy trạng thái HTTP tương ứng với ngoại lệ.
     */
    private final HttpStatus status;
    /**
     * -- GETTER --
     * Lấy mã lỗi tùy chỉnh của ngoại lệ.
     */
    private final String errorCode;

    /**
     * Tạo một ngoại lệ mới với thông báo lỗi, mã trạng thái HTTP và mã lỗi tùy chỉnh.
     *
     * @param message   Thông báo lỗi mô tả nguyên nhân xảy ra ngoại lệ.
     * @param status    Trạng thái HTTP tương ứng (ví dụ: 404, 400).
     * @param errorCode Mã lỗi tùy chỉnh dùng để phân loại lỗi.
     */
    protected BaseException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    /**
     * Tạo một ngoại lệ mới với thông báo lỗi, trạng thái HTTP, mã lỗi và nguyên nhân gốc.
     *
     * @param message   Thông báo lỗi mô tả nguyên nhân xảy ra ngoại lệ.
     * @param status    Trạng thái HTTP tương ứng.
     * @param errorCode Mã lỗi tùy chỉnh.
     * @param cause     Ngoại lệ gốc gây ra lỗi này.
     */
    protected BaseException(String message, HttpStatus status, String errorCode, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }

}
