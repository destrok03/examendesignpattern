package com.badwallet.payment.dto.response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record RestResponse<T>(
        boolean success,
        int status,
        String message,
        T data,
        LocalDateTime timestamp
) {
    public static <T> RestResponse<T> success(T data, String message) {
        return new RestResponse<>(true, HttpStatus.OK.value(), message, data, LocalDateTime.now());
    }

    public static <T> RestResponse<T> success(T data, String message, HttpStatus status) {
        return new RestResponse<>(true, status.value(), message, data, LocalDateTime.now());
    }

    public static <T> RestResponse<T> error(String message, HttpStatus status) {
        return new RestResponse<>(false, status.value(), message, null, LocalDateTime.now());
    }

    public static <T> RestResponse<T> error(String message, HttpStatus status, T data) {
        return new RestResponse<>(false, status.value(), message, data, LocalDateTime.now());
    }
}
