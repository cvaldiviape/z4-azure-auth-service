package com.z4greed.auth.service.impl;

import com.z4greed.auth.dto.*;
import com.z4greed.auth.entity.*;
import com.z4greed.auth.enums.*;
import com.z4greed.auth.exception.GreedException;
import com.z4greed.auth.mapper.UserMapper;
import com.z4greed.auth.repository.*;
import com.z4greed.auth.service.AuthService;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtEncoder jwtEncoder;
  private final UserMapper userMapper;
  private final long tokenTimeToLive;

  public AuthServiceImpl(
      UserRepository userRepository,
      RoleRepository roleRepository,
      PasswordEncoder passwordEncoder,
      JwtEncoder jwtEncoder,
      UserMapper userMapper,
      @Value("${app.jwt.ttl}") long tokenTimeToLive) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtEncoder = jwtEncoder;
    this.userMapper = userMapper;
    this.tokenTimeToLive = tokenTimeToLive;
  }

  @Override
  public void register(RegisterRequestDto requestDto) {
    this.validateAvailableEmail(requestDto.email());
    UserEntity userEntity = this.createUser(requestDto);
    this.userRepository.save(userEntity);
  }

  private void validateAvailableEmail(String email) {
    boolean emailAlreadyRegistered = this.userRepository.findByEmailIgnoreCase(email).isPresent();
    if (emailAlreadyRegistered) {
      throw new GreedException(ErrorCodeEnum.EMAIL_ALREADY_REGISTERED);
    }
  }

  private UserEntity createUser(RegisterRequestDto requestDto) {
    RoleEntity roleEntity = this.roleRepository.findByCode("CUSTOMER").orElseThrow();
    UserCreateDto userCreateDto = UserCreateDto.builder()
        .email(requestDto.email().toLowerCase())
        .passwordHash(this.passwordEncoder.encode(requestDto.password()))
        .firstName(requestDto.firstName())
        .lastName(requestDto.lastName())
        .status(UserStatusEnum.ACTIVE)
        .roles(Set.of(roleEntity))
        .createdAt(Instant.now())
        .build();
    return this.userMapper.toEntity(userCreateDto);
  }

  @Override
  public TokenResponseDto login(LoginRequestDto requestDto) {
    UserEntity userEntity = this.findUser(requestDto.email());
    this.validateCredentials(userEntity, requestDto.password());
    return this.createToken(userEntity);
  }

  private UserEntity findUser(String email) {
    return this.userRepository.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new GreedException(ErrorCodeEnum.INVALID_CREDENTIALS));
  }

  private void validateCredentials(UserEntity userEntity, String password) {
    boolean invalidStatus = userEntity.getStatus() != UserStatusEnum.ACTIVE;
    boolean invalidPassword = !this.passwordEncoder.matches(password, userEntity.getPasswordHash());
    if (invalidStatus || invalidPassword) {
      throw new GreedException(ErrorCodeEnum.INVALID_CREDENTIALS);
    }
  }

  private TokenResponseDto createToken(UserEntity userEntity) {
    Instant issuedAt = Instant.now();
    Instant expiresAt = issuedAt.plusSeconds(this.tokenTimeToLive);
    JwtClaimsSet claims = this.createClaims(userEntity, issuedAt, expiresAt);
    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
    JwtEncoderParameters parameters = JwtEncoderParameters.from(header, claims);
    String accessToken = this.jwtEncoder.encode(parameters).getTokenValue();
    return TokenResponseDto.builder()
        .accessToken(accessToken)
        .tokenType("Bearer")
        .expiresIn(this.tokenTimeToLive)
        .build();
  }

  private JwtClaimsSet createClaims(UserEntity userEntity, Instant issuedAt, Instant expiresAt) {
    List<String> listRoleCodes = userEntity.getRoles().stream()
        .map(RoleEntity::getCode)
        .sorted()
        .toList();
    return JwtClaimsSet.builder()
        .subject(userEntity.getId().toString())
        .issuedAt(issuedAt)
        .expiresAt(expiresAt)
        .claim("email", userEntity.getEmail())
        .claim("roles", listRoleCodes)
        .build();
  }

}