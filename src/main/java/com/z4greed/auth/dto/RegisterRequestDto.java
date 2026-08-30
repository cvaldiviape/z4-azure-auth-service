package com.z4greed.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record RegisterRequestDto(
    @Email(message = "Email format is invalid") @NotBlank(message = "Email is required") String email,
    @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must contain at least 8 characters") String password,
    String firstName,
    String lastName) {}
