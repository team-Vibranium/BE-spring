package com.bespring.global.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtUtil 테스트")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String SECRET = "mySecretKeyForTestingPurposesOnlyThisNeedsToBeVeryLong";
    private final long EXPIRATION = 3600; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION);
    }

    @Test
    @DisplayName("토큰 생성 테스트")
    void generateToken_ShouldReturnValidToken() {
        // Given
        Long userId = 1L;

        // When
        String token = jwtUtil.generateToken(userId);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    @DisplayName("토큰에서 사용자 ID 추출 테스트")
    void getUserIdFromToken_ShouldReturnCorrectUserId() {
        // Given
        Long userId = 123L;
        String token = jwtUtil.generateToken(userId);

        // When
        Long extractedUserId = jwtUtil.getUserIdFromToken(token);

        // Then
        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("유효하지 않은 토큰에서 사용자 ID 추출 테스트")
    void getUserIdFromToken_WithInvalidToken_ShouldReturnNull() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        Long extractedUserId = jwtUtil.getUserIdFromToken(invalidToken);

        // Then
        assertNull(extractedUserId);
    }

    @Test
    @DisplayName("토큰 유효성 검증 테스트 - 유효한 토큰")
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        // Given
        Long userId = 1L;
        String token = jwtUtil.generateToken(userId);

        // When
        boolean isValid = jwtUtil.isTokenValid(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("토큰 유효성 검증 테스트 - 유효하지 않은 토큰")
    void isTokenValid_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtUtil.isTokenValid(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("만료된 토큰 검증 테스트")
    void isTokenExpired_WithExpiredToken_ShouldReturnTrue() {
        // Given - 만료 시간이 매우 짧은 JwtUtil 생성
        JwtUtil shortExpirationJwtUtil = new JwtUtil(SECRET, -1); // 과거 시간으로 설정
        Long userId = 1L;
        String expiredToken = shortExpirationJwtUtil.generateToken(userId);

        // When
        boolean isExpired = jwtUtil.isTokenExpired(expiredToken);

        // Then
        assertTrue(isExpired);
    }

    @Test
    @DisplayName("Bearer 토큰 헤더에서 토큰 추출 테스트")
    void extractTokenFromHeader_WithBearerToken_ShouldReturnToken() {
        // Given
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjI2MjQ5NjAwfQ.abc123";
        String bearerToken = "Bearer " + token;

        // When
        String extractedToken = jwtUtil.extractTokenFromHeader(bearerToken);

        // Then
        assertEquals(token, extractedToken);
    }

    @Test
    @DisplayName("Bearer 토큰 헤더에서 토큰 추출 테스트 - Bearer가 없는 경우")
    void extractTokenFromHeader_WithoutBearer_ShouldReturnNull() {
        // Given
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjI2MjQ5NjAwfQ.abc123";

        // When
        String extractedToken = jwtUtil.extractTokenFromHeader(token);

        // Then
        assertNull(extractedToken);
    }

    @Test
    @DisplayName("Bearer 토큰 헤더에서 토큰 추출 테스트 - null인 경우")
    void extractTokenFromHeader_WithNull_ShouldReturnNull() {
        // Given
        String bearerToken = null;

        // When
        String extractedToken = jwtUtil.extractTokenFromHeader(bearerToken);

        // Then
        assertNull(extractedToken);
    }
}