package com.bespring.domain.auth.service;

import com.bespring.domain.auth.dto.request.LoginRequest;
import com.bespring.domain.auth.dto.request.RegisterRequest;
import com.bespring.domain.auth.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void logout(String token);

    boolean isTokenValid(String token);

    Long getUserIdFromToken(String token);

    String generateToken(Long userId);
}