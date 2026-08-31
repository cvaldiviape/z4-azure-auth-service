package com.z4greed.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record LoginRequestDto(
    @Email(message = "Email format is invalid") @NotBlank(message = "Email is required") String email,
    @NotBlank(message = "Password is required") String password) {}
