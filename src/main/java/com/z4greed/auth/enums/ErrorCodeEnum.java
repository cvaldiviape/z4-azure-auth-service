package com.z4greed.auth.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCodeEnum {
  EMAIL_ALREADY_REGISTERED(HttpStatus.CONFLICT, "Email already registered"),
  INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials");

  private final HttpStatus httpStatus;
  private final String message;
}
