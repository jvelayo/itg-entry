package com.itg.accountservice.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiBaseResponse<T> {
    private T data;
    private Map<String, String> errors;
    private boolean success;
    private String message;
    private int status;
    private LocalDateTime timestamp;

    public ApiBaseResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiBaseResponse(T data, boolean success, String message, HttpStatus status) {
        this.data = data;
        this.success = success;
        this.message = message;
        this.status = status.value();
        this.timestamp = LocalDateTime.now();
    }

    public ApiBaseResponse(T data, boolean success, String message, HttpStatus status, Map<String, String> errors) {
        this.data = data;
        this.success = success;
        this.message = message;
        this.status = status.value();
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }

    public static <T> ApiBaseResponse<T> found(T data, String message) {
        return new ApiBaseResponse<>(data, true, message, HttpStatus.FOUND);
    }

    public static <T> ApiBaseResponse<T> ok(T data, String message) {
        return new ApiBaseResponse<>(data, true, message, HttpStatus.OK);
    }

    public static <T> ApiBaseResponse<T> created(T data, String message) {
        return new ApiBaseResponse<>(data, true, message, HttpStatus.CREATED);
    }

    public static <T> ApiBaseResponse<T> badRequest(String message) {
        return new ApiBaseResponse<>(null, false, message, HttpStatus.BAD_REQUEST);
    }

    public static <T> ApiBaseResponse<T> serverError(String message) {
        return new ApiBaseResponse<>(null, false, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
