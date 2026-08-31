package com.z4greed.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.AccessLevel;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String code;
  private String name;
}
