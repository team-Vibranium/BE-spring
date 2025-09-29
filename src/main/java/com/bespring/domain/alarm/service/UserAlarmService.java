package com.bespring.domain.alarm.service;

import com.bespring.domain.alarm.entity.UserAlarm;
import com.bespring.domain.alarm.repository.UserAlarmRepository;
import com.bespring.domain.user.entity.User;
import com.bespring.domain.user.repository.UserRepository;
import com.bespring.global.exception.CustomException;
import com.bespring.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserAlarmService {

    private final UserAlarmRepository userAlarmRepository;
    private final UserRepository userRepository;

    // 알람 생성
    public UserAlarm createAlarm(Long userId, LocalDateTime alarmTime, String instructions, com.bespring.domain.alarm.entity.VoiceType voice) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 과거 시간 검증
        if (alarmTime.isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.ALARM_TIME_PAST,
                String.format("요청된 알람 시간: %s", alarmTime.toString()));
        }

        // 최대 알람 개수 검증 (예: 사용자당 최대 10개)
        long activeAlarmCount = userAlarmRepository.countByUser_IdAndActive(userId, true);
        if (activeAlarmCount >= 10) {
            throw new CustomException(ErrorCode.MAX_ALARMS_EXCEEDED,
                String.format("현재 활성 알람 개수: %d", activeAlarmCount));
        }

        // 동일한 시간의 활성화된 알람이 있는지 확인
        List<UserAlarm> existingAlarms = userAlarmRepository.findByUser_IdAndActiveOrderByAlarmTimeDesc(userId, true);
        boolean duplicateTime = existingAlarms.stream()
                .anyMatch(alarm -> alarm.getAlarmTime().equals(alarmTime));

        if (duplicateTime) {
            throw new CustomException(ErrorCode.DUPLICATE_ALARM_TIME,
                String.format("중복된 알람 시간: %s", alarmTime.toString()));
        }

        UserAlarm userAlarm = UserAlarm.create(user, alarmTime, instructions, voice);
        UserAlarm savedAlarm = userAlarmRepository.save(userAlarm);

        log.info("Created alarm for user {}: alarmId={}, alarmTime={}",
                userId, savedAlarm.getId(), alarmTime);

        return savedAlarm;
    }

    // 사용자의 활성화된 알람 목록 조회
    @Transactional(readOnly = true)
    public List<UserAlarm> getActiveAlarms(Long userId) {
        return userAlarmRepository.findByUser_IdAndActiveOrderByAlarmTimeDesc(userId, true);
    }

    // 사용자의 모든 알람 목록 조회
    @Transactional(readOnly = true)
    public List<UserAlarm> getAllAlarms(Long userId) {
        return userAlarmRepository.findByUser_IdOrderByAlarmTimeDesc(userId);
    }

    // 특정 알람 조회
    @Transactional(readOnly = true)
    public UserAlarm getAlarm(Long userId, Long alarmId) {
        return userAlarmRepository.findByIdAndUser_Id(alarmId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALARM_NOT_FOUND));
    }

    // 알람 삭제 (비활성화)
    public void deleteAlarm(Long userId, Long alarmId) {
        UserAlarm alarm = userAlarmRepository.findByIdAndUser_Id(alarmId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALARM_NOT_FOUND,
                    String.format("알람 ID: %d", alarmId)));

        if (!alarm.isActive()) {
            throw new CustomException(ErrorCode.ALARM_ALREADY_INACTIVE,
                String.format("알람 ID: %d", alarmId));
        }

        alarm.deactivate();
        userAlarmRepository.save(alarm);

        log.info("Deactivated alarm: userId={}, alarmId={}", userId, alarmId);
    }

    // 알람 수정
    public UserAlarm updateAlarm(Long userId, Long alarmId, LocalDateTime alarmTime,
                               String instructions, com.bespring.domain.alarm.entity.VoiceType voice) {
        UserAlarm alarm = userAlarmRepository.findByIdAndUser_Id(alarmId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALARM_NOT_FOUND));

        // 동일한 시간의 다른 활성화된 알람이 있는지 확인 (현재 알람 제외)
        List<UserAlarm> existingAlarms = userAlarmRepository.findByUser_IdAndActiveOrderByAlarmTimeDesc(userId, true);
        boolean duplicateTime = existingAlarms.stream()
                .anyMatch(existingAlarm ->
                    !existingAlarm.getId().equals(alarmId) &&
                    existingAlarm.getAlarmTime().equals(alarmTime));

        if (duplicateTime) {
            throw new CustomException(ErrorCode.DUPLICATE_ALARM_TIME);
        }

        alarm.updateAlarm(alarmTime, instructions, voice);
        UserAlarm updatedAlarm = userAlarmRepository.save(alarm);

        log.info("Updated alarm: userId={}, alarmId={}, newTime={}",
                userId, alarmId, alarmTime);

        return updatedAlarm;
    }

    // 특정 시간대의 알람들 조회 (스케줄러용)
    @Transactional(readOnly = true)
    public List<UserAlarm> getAlarmsInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return userAlarmRepository.findActiveAlarmsBetween(startTime, endTime);
    }

    // 사용자의 다음 알람 조회
    @Transactional(readOnly = true)
    public UserAlarm getNextAlarm(Long userId) {
        return userAlarmRepository.findFirstByUser_IdAndActiveTrueAndAlarmTimeAfterOrderByAlarmTimeAsc(userId, LocalDateTime.now())
                .orElse(null);
    }
}
