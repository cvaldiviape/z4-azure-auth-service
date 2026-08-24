package com.z4greed.auth.dto;

import lombok.Builder;

@Builder
public record TokenResponseDto(
    String accessToken,
    String tokenType,
    long expiresIn) {}
