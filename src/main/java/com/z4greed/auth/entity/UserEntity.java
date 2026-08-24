package com.z4greed.auth.entity;

import com.z4greed.auth.enums.UserStatusEnum;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  private String firstName;
  private String lastName;

  @Enumerated(EnumType.STRING)
  private UserStatusEnum status = UserStatusEnum.ACTIVE;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<RoleEntity> roles = new HashSet<>();

  private Instant createdAt;
  private Instant updatedAt;

  @Builder
  public UserEntity(
      String email, String passwordHash, String firstName, String lastName, RoleEntity roleEntity) {
    this.email = email;
    this.passwordHash = passwordHash;
    this.firstName = firstName;
    this.lastName = lastName;
    this.roles.add(roleEntity);
    this.createdAt = Instant.now();
  }

  public Set<RoleEntity> getRoles() {
    return Collections.unmodifiableSet(this.roles);
  }
}
