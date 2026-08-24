package com.z4greed.auth.dto;

import com.z4greed.auth.entity.RoleEntity;
import com.z4greed.auth.enums.UserStatusEnum;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record UserCreateDto(
    String email,
    String passwordHash,
    String firstName,
    String lastName,
    UserStatusEnum status,
    Set<RoleEntity> roles,
    LocalDateTime createdAt) {}
