package com.example.zaran_design_backend.service;

/**
 * 业务异常，携带文档约定的业务错误码（如 4042、4031、403 等）。
 * 由 GlobalExceptionHandler 统一捕获并转换为 Result.error(code, message)。
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
