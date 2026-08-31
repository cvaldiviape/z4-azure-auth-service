package com.z4greed.auth.service.auth;

import com.z4greed.auth.dto.LoginRequestDto;
import com.z4greed.auth.dto.RegisterRequestDto;
import com.z4greed.auth.dto.TokenResponseDto;

public interface AuthService {
  void register(RegisterRequestDto requestDto);
  TokenResponseDto login(LoginRequestDto requestDto);
}