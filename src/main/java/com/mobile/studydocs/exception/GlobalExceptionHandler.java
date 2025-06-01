package com.mobile.studydocs.exception;

import com.google.api.client.http.HttpStatusCodes;
import com.mobile.studydocs.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý ngoại lệ thuộc loại {@code BaseException} và trả về phản hồi chuẩn hóa
     * gồm trạng thái HTTP, thông báo lỗi và các thông tin chi tiết liên quan.
     *
     * @param e đối tượng ngoại lệ {@code BaseException} chứa trạng thái HTTP,
     *          thông báo lỗi và mã lỗi.
     * @return {@code ResponseEntity} chứa {@code BaseResponse} với mã trạng thái HTTP,
     * thông báo lỗi và bản đồ thông tin bổ sung bao gồm mã lỗi.
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse> handleBaseException(BaseException e) {
        return ResponseEntity.status(e.getStatus())
                .body(new BaseResponse(e.getStatus().value(), e.getMessage(), Map.of("errorCode", e.getErrorCode())));
    }


    /**
     * Xử lý các ngoại lệ không xác định xảy ra trên server.
     *
     * @param e đối tượng ngoại lệ {@code Exception} được ném ra.
     * @return {@code ResponseEntity} chứa {@code BaseResponse} với mã trạng thái HTTP 500
     * và thông báo lỗi mô tả lỗi không mong muốn.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                .body(new BaseResponse(HttpStatusCodes.STATUS_CODE_SERVER_ERROR,
                        "Đã xảy ra lỗi không mong muốn: " + e.getMessage(), null));
    }
}
