package com.z4greed.auth.repository;

import com.z4greed.auth.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByEmailIgnoreCase(String email);
}
