package com.bespring.domain.call.repository;

import com.bespring.domain.call.entity.CallLog;
import com.bespring.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CallLogRepository extends JpaRepository<CallLog, Long> {

    Page<CallLog> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Page<CallLog> findByUserAndCallStartBetweenOrderByCreatedAtDesc(
            User user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable
    );

    @Query("SELECT c FROM CallLog c JOIN FETCH c.user WHERE c.user = :user ORDER BY c.createdAt DESC")
    Page<CallLog> findByUserWithUserOrderByCreatedAtDesc(@Param("user") User user, Pageable pageable);

    @Query("SELECT c FROM CallLog c JOIN FETCH c.user WHERE c.user = :user AND c.callStart BETWEEN :startDate AND :endDate ORDER BY c.createdAt DESC")
    Page<CallLog> findByUserAndCallStartBetweenWithUserOrderByCreatedAtDesc(
            @Param("user") User user, @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate, Pageable pageable
    );

    List<CallLog> findByUserAndCallStartBetween(User user, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(c) FROM CallLog c WHERE c.user = :user")
    Long countByUser(@Param("user") User user);

    @Query("SELECT COUNT(c) FROM CallLog c WHERE c.user = :user AND c.result = 'SUCCESS'")
    Long countSuccessByUser(@Param("user") User user);

    @Query("SELECT COUNT(c) FROM CallLog c WHERE c.user = :user AND c.callStart >= :startDate")
    Long countByUserAfterDate(@Param("user") User user, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(c) FROM CallLog c WHERE c.user = :user AND c.result = 'SUCCESS' AND c.callStart >= :startDate")
    Long countSuccessByUserAfterDate(@Param("user") User user, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT AVG(HOUR(c.callStart)) FROM CallLog c WHERE c.user = :user AND c.result = 'SUCCESS'")
    Double findAverageWakeTimeByUser(@Param("user") User user);

    @Query("SELECT c FROM CallLog c JOIN FETCH c.user WHERE c.user = :user ORDER BY c.callStart DESC")
    List<CallLog> findRecentByUserWithUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT c FROM CallLog c WHERE c.user = :user ORDER BY c.callStart DESC")
    List<CallLog> findRecentByUser(@Param("user") User user, Pageable pageable);

    // 현재 진행 중인 통화 조회 (callEnd가 null인 통화)
    @Query("SELECT c FROM CallLog c WHERE c.user.id = :userId AND c.callEnd IS NULL")
    Optional<CallLog> findByUserIdAndCallEndIsNull(@Param("userId") Long userId);

    // 사용자의 진행 중인 통화 존재 여부 확인
    boolean existsByUserIdAndCallEndIsNull(Long userId);

    // 특정 통화 조회 (사용자 검증 포함)
    Optional<CallLog> findByIdAndUserId(Long callId, Long userId);
}