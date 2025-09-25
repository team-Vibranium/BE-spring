package com.bespring.domain.user.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User 엔티티 테스트")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .nickname("testUser")
                .points(100)
                .selectedAvatar("avatar_2")
                .build();
    }

    @Test
    @DisplayName("포인트 추가 테스트")
    void addPoints_ShouldIncreasePoints() {
        // Given
        int initialPoints = user.getPoints();
        int pointsToAdd = 50;

        // When
        user.addPoints(pointsToAdd);

        // Then
        assertEquals(initialPoints + pointsToAdd, user.getPoints());
    }

    @Test
    @DisplayName("포인트 차감 테스트 - 충분한 포인트가 있는 경우")
    void subtractPoints_WithSufficientPoints_ShouldDecreasePoints() {
        // Given
        int initialPoints = user.getPoints();
        int pointsToSubtract = 30;

        // When
        user.subtractPoints(pointsToSubtract);

        // Then
        assertEquals(initialPoints - pointsToSubtract, user.getPoints());
    }

    @Test
    @DisplayName("포인트 차감 테스트 - 포인트가 부족한 경우")
    void subtractPoints_WithInsufficientPoints_ShouldThrowException() {
        // Given
        int pointsToSubtract = user.getPoints() + 10;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> user.subtractPoints(pointsToSubtract));
        assertEquals(100, user.getPoints()); // 포인트가 변경되지 않아야 함
    }

    @Test
    @DisplayName("포인트 차감 테스트 - 정확히 같은 포인트를 차감하는 경우")
    void subtractPoints_WithExactPoints_ShouldSetPointsToZero() {
        // Given
        int pointsToSubtract = user.getPoints();

        // When
        user.subtractPoints(pointsToSubtract);

        // Then
        assertEquals(0, user.getPoints());
    }

    @Test
    @DisplayName("User 빌더 테스트 - 기본값 확인")
    void userBuilder_ShouldSetDefaultValues() {
        // Given & When
        User newUser = User.builder()
                .email("test@example.com")
                .passwordHash("hash")
                .nickname("nick")
                .build();

        // Then
        assertEquals(0, newUser.getPoints());
        assertEquals("avatar_1", newUser.getSelectedAvatar());
    }
}