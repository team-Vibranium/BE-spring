package com.bespring.domain.auth.service;

import com.bespring.domain.auth.dto.request.LoginRequest;
import com.bespring.domain.auth.dto.request.RegisterRequest;
import com.bespring.domain.auth.dto.response.AuthResponse;
import com.bespring.domain.user.entity.User;
import com.bespring.domain.user.service.UserService;
import com.bespring.global.exception.DuplicateResourceException;
import com.bespring.global.exception.UserNotFoundException;
import com.bespring.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final long TOKEN_EXPIRATION = 24 * 60 * 60; // 24 hours in seconds

    @Override
    public AuthResponse register(RegisterRequest request) {
        // 이메일 중복 확인
        if (userService.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 확인
        if (userService.existsByNickname(request.getNickname())) {
            throw new DuplicateResourceException("이미 존재하는 닉네임입니다.");
        }

        // 사용자 등록
        AuthResponse.UserInfo userInfo = userService.register(request);

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(userInfo.getId());

        return AuthResponse.builder()
                .user(userInfo)
                .token(token)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // 사용자 찾기
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 사용자 정보 조회
        AuthResponse.UserInfo userInfo = userService.getUserInfo(user.getId());

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(user.getId());

        log.info("User logged in: userId={}, email={}", user.getId(), user.getEmail());

        return AuthResponse.builder()
                .user(userInfo)
                .token(token)
                .build();
    }

    @Override
    public void logout(String token) {
        if (token != null) {
            try {
                // Redis에 토큰 블랙리스트 추가
                redisTemplate.opsForValue().set(
                        BLACKLIST_PREFIX + token,
                        "blacklisted",
                        TOKEN_EXPIRATION,
                        TimeUnit.SECONDS
                );
                log.info("Token blacklisted: {}", token.substring(0, Math.min(20, token.length())) + "...");
            } catch (Exception e) {
                log.warn("Redis is not available, token blacklisting skipped: {}", e.getMessage());
            }
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            // Redis 블랙리스트 확인
            String blacklisted = redisTemplate.opsForValue().get(BLACKLIST_PREFIX + token);
            if (blacklisted != null) {
                return false;
            }
        } catch (Exception e) {
            log.warn("Redis is not available, token blacklist check skipped: {}", e.getMessage());
        }

        // JWT 유효성 확인
        return jwtUtil.isTokenValid(token);
    }

    @Override
    public Long getUserIdFromToken(String token) {
        if (!isTokenValid(token)) {
            return null;
        }
        return jwtUtil.getUserIdFromToken(token);
    }

    @Override
    public String generateToken(Long userId) {
        return jwtUtil.generateToken(userId);
    }
}