package com.z4greed.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record LoginRequestDto(@Email @NotBlank String email, @NotBlank String password) {}
