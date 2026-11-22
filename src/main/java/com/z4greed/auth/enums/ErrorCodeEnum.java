package com.z4greed.auth.enums;

import com.z4greed.shared.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// VALIDATION_ERROR ya no está aquí: lo aporta CoreErrorCode de z4-shared-core.
@Getter
@RequiredArgsConstructor
public enum ErrorCodeEnum implements ErrorCode {
    EMAIL_ALREADY_REGISTERED(HttpStatus.CONFLICT, "Email already registered"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials");

    private final HttpStatus httpStatus;
    private final String message;
}
