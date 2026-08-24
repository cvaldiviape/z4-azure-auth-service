package com.z4greed.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record RegisterRequestDto(
    @Email @NotBlank String email,
    @Size(min = 8) String password,
    String firstName,
    String lastName) {}
