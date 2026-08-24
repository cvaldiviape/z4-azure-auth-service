package com.z4greed.auth.mapper;

import com.z4greed.auth.dto.UserCreateDto;
import com.z4greed.auth.entity.UserEntity;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
  @Named("UserMapper.toEntity")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  UserEntity toEntity(UserCreateDto userCreateDto);
}
