package com.z4greed.auth.service;

import com.z4greed.auth.dto.LoginRequestDto;
import com.z4greed.auth.dto.RegisterRequestDto;
import com.z4greed.auth.dto.TokenResponseDto;
import com.z4greed.auth.entity.RoleEntity;
import com.z4greed.auth.entity.UserEntity;
import com.z4greed.auth.enums.ErrorCodeEnum;
import com.z4greed.auth.enums.UserStatusEnum;
import com.z4greed.auth.exception.GreedException;
import com.z4greed.auth.repository.RoleRepository;
import com.z4greed.auth.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtEncoder jwtEncoder;
  private final long tokenTimeToLive;

  public AuthService(
      UserRepository userRepository,
      RoleRepository roleRepository,
      PasswordEncoder passwordEncoder,
      JwtEncoder jwtEncoder,
      @Value("${app.jwt.ttl}") long tokenTimeToLive) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtEncoder = jwtEncoder;
    this.tokenTimeToLive = tokenTimeToLive;
  }

  public void register(RegisterRequestDto requestDto) {
    boolean emailAlreadyRegistered =
        this.userRepository.findByEmailIgnoreCase(requestDto.email()).isPresent();
    if (emailAlreadyRegistered) {
      throw new GreedException(ErrorCodeEnum.EMAIL_ALREADY_REGISTERED);
    }

    RoleEntity customerRoleEntity = roleRepository.findByCode("CUSTOMER").orElseThrow();
    String encodedPassword = passwordEncoder.encode(requestDto.password());
    UserEntity userEntity =
        UserEntity.builder()
            .email(requestDto.email().toLowerCase())
            .passwordHash(encodedPassword)
            .firstName(requestDto.firstName())
            .lastName(requestDto.lastName())
            .roleEntity(customerRoleEntity)
            .build();
    this.userRepository.save(userEntity);
  }

  public TokenResponseDto login(LoginRequestDto requestDto) {
    UserEntity userEntity =
        userRepository
            .findByEmailIgnoreCase(requestDto.email())
        .orElseThrow(() -> new GreedException(ErrorCodeEnum.INVALID_CREDENTIALS));

    boolean invalidStatus = userEntity.getStatus() != UserStatusEnum.ACTIVE;
    boolean invalidPassword =
        !passwordEncoder.matches(requestDto.password(), userEntity.getPasswordHash());
    if (invalidStatus || invalidPassword) {
      throw new GreedException(ErrorCodeEnum.INVALID_CREDENTIALS);
    }

    Instant issuedAt = Instant.now();
    Instant expiresAt = issuedAt.plusSeconds(tokenTimeToLive);
    List<String> listRoleCodes =
        userEntity.getRoles().stream().map(RoleEntity::getCode).sorted().toList();
    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .subject(userEntity.getId().toString())
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .claim("email", userEntity.getEmail())
            .claim("roles", listRoleCodes)
            .build();
    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
    JwtEncoderParameters parameters = JwtEncoderParameters.from(header, claims);
    String accessToken = jwtEncoder.encode(parameters).getTokenValue();
    return TokenResponseDto.builder()
        .accessToken(accessToken)
        .tokenType("Bearer")
        .expiresIn(tokenTimeToLive)
        .build();
  }
}
