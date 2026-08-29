package com.z4greed.auth.controller;

import com.z4greed.auth.dto.LoginRequestDto;
import com.z4greed.auth.dto.RegisterRequestDto;
import com.z4greed.auth.dto.ResponseDto;
import com.z4greed.auth.dto.TokenResponseDto;
import com.z4greed.auth.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(
      AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseDto<Void> register(@Valid @RequestBody RegisterRequestDto requestDto) {
    this.authService.register(requestDto);
    return ResponseDto.<Void>builder()
        .code("USER_REGISTERED")
        .statusCode(HttpStatus.CREATED.value())
        .message("User registered successfully")
        .build();
  }

  @PostMapping("/login")
  public ResponseDto<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
    TokenResponseDto tokenResponseDto = this.authService.login(requestDto);
    return ResponseDto.<TokenResponseDto>builder()
        .code("LOGIN_SUCCESS")
        .statusCode(HttpStatus.OK.value())
        .message("Login successful")
        .data(tokenResponseDto)
        .build();
  }

}
