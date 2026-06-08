package com.example.zaran_design_backend.common;

public class Result<T> {
    private int code;
    private String message;
    private T data;
    private long timestamp;

    private Result() {
    }

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "success";
        result.data = data;
        result.timestamp = System.currentTimeMillis();
        return result;
    }

    public static <T> Result<T> ok(String message, T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = message;
        result.data = data;
        result.timestamp = System.currentTimeMillis();
        return result;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        result.data = null;
        result.timestamp = System.currentTimeMillis();
        return result;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }





///工艺知识
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        // 假设项目中用 500 代表失败/异常，用 200 代表成功
        result.code = 500;
        result.message = message;
        result.data = null;
        return result;
    }


}
