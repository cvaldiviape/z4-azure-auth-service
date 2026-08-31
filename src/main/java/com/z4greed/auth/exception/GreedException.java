package com.z4greed.auth.exception;

import com.z4greed.auth.enums.ErrorCodeEnum;
import lombok.Getter;

@Getter
public class GreedException extends RuntimeException {
  private final ErrorCodeEnum errorCode;

  public GreedException(ErrorCodeEnum errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
