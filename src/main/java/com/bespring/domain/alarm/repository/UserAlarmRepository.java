package com.bespring.domain.alarm.repository;

import com.bespring.domain.alarm.entity.UserAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAlarmRepository extends JpaRepository<UserAlarm, Long> {

    // 특정 사용자의 활성화된 알람 목록 조회 (최신순)
    List<UserAlarm> findByUser_IdAndActiveOrderByAlarmTimeDesc(Long userId, Boolean active);

    // 특정 사용자의 모든 알람 목록 조회 (최신순)
    List<UserAlarm> findByUser_IdOrderByAlarmTimeDesc(Long userId);

    // 특정 알람 조회 (사용자 검증 포함)
    Optional<UserAlarm> findByIdAndUser_Id(Long alarmId, Long userId);

    // 특정 시간대의 활성화된 알람들 조회 (알람 트리거용)
    @Query("SELECT ua FROM UserAlarm ua WHERE ua.active = true " +
           "AND ua.alarmTime BETWEEN :startTime AND :endTime " +
           "ORDER BY ua.alarmTime ASC")
    List<UserAlarm> findActiveAlarmsBetween(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    // 사용자의 활성화된 알람 개수
    long countByUser_IdAndActive(Long userId, Boolean active);

    // 특정 사용자의 다음 알람 조회 (가장 이른 알람 시간)
    Optional<UserAlarm> findFirstByUser_IdAndActiveTrueAndAlarmTimeAfterOrderByAlarmTimeAsc(
            Long userId, LocalDateTime currentTime);
}
