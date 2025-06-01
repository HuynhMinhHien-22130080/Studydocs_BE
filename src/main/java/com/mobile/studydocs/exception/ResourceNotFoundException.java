package com.mobile.studydocs.exception;

import org.springframework.http.HttpStatus;

/**
 * Ngoại lệ được ném ra khi không tìm thấy tài nguyên yêu cầu.
 *
 * <p>Ngoại lệ này được sử dụng để chỉ ra rằng một tài nguyên cụ thể, được xác định
 * bởi tên, trường và giá trị của nó, không thể được tìm thấy. Nó kế thừa từ
 * lớp {@link BaseException} và cung cấp thông tin bổ sung như mã lỗi và trạng thái HTTP.</p>
 *
 *
 * <p>Ngoại lệ này thường được sử dụng trong các tình huống như xác thực sự tồn tại
 * của các thực thể (ví dụ: người dùng, bản ghi) trong lớp dịch vụ, và hỗ trợ
 * cung cấp phản hồi lỗi có mô tả rõ ràng khi không tìm thấy thực thể.</p>
 *
 * @see BaseException
 * @see HttpStatus#NOT_FOUND
 */
public class ResourceNotFoundException extends BaseException {

    /**
     * Tạo một ngoại lệ mới cho trường hợp không tìm thấy tài nguyên.
     *
     * @param resourceName Tên tài nguyên (ví dụ: "Người dùng", "Tài liệu").
     * @param fieldName    Tên trường được sử dụng để tìm kiếm (ví dụ: "id", "email").
     * @param fieldValue   Giá trị cụ thể của trường đã tìm (ví dụ: 123, "example@gmail.com").
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(
                String.format("%s không tìm thấy với thông tin %s : '%s'", resourceName, fieldName, fieldValue),
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND"
        );
    }
}
