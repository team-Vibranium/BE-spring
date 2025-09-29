package com.bespring.domain.call.service;

import com.bespring.domain.call.dto.request.CallLogCreateRequest;
import com.bespring.domain.call.entity.CallLog;
import com.bespring.domain.call.repository.CallLogRepository;
import com.bespring.domain.user.entity.User;
import com.bespring.domain.user.repository.UserRepository;
import com.bespring.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CallLogServiceImpl implements CallLogService {

    private final CallLogRepository callLogRepository;
    private final UserRepository userRepository;

    @Override
    public CallLog createCallLog(Long userId, CallLogCreateRequest request) {
        User user = getUserById(userId);

        // 비즈니스 로직 검증
        validateCallLogRequest(request);

        CallLog callLog = CallLog.builder()
                .user(user)
                .callStart(request.getCallStart())
                .callEnd(request.getCallEnd())
                .result(request.getResult())
                .snoozeCount(request.getSnoozeCount())
                .build();

        CallLog saved = callLogRepository.save(callLog);

        log.info("Call log created: userId={}, result={}, snoozeCount={}",
                userId, request.getResult(), request.getSnoozeCount());

        return saved;
    }

    private void validateCallLogRequest(CallLogCreateRequest request) {
        // callEnd가 callStart보다 이후인지 검증
        if (request.getCallEnd() != null && request.getCallStart() != null) {
            if (request.getCallEnd().isBefore(request.getCallStart())) {
                throw new com.bespring.global.exception.InvalidRequestException(
                        "통화 종료 시간은 시작 시간보다 이후여야 합니다.");
            }
        }

        // FAIL_SNOOZE인 경우 스누즈 횟수 검증
        if (request.getResult() == CallLog.CallResult.FAIL_SNOOZE && request.getSnoozeCount() == 0) {
            throw new com.bespring.global.exception.InvalidRequestException(
                    "스누즈 실패 시에는 스누즈 횟수가 1 이상이어야 합니다.");
        }

        // SUCCESS인 경우 callEnd 필수
        if (request.getResult() == CallLog.CallResult.SUCCESS && request.getCallEnd() == null) {
            throw new com.bespring.global.exception.InvalidRequestException(
                    "성공한 통화는 종료 시간이 필수입니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CallLog> getCallLogs(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        User user = getUserById(userId);

        // JOIN FETCH를 사용하여 N+1 쿼리 문제 해결
        if (startDate != null && endDate != null) {
            return callLogRepository.findByUserAndCallStartBetweenWithUserOrderByCreatedAtDesc(user, startDate, endDate, pageable);
        } else {
            return callLogRepository.findByUserWithUserOrderByCreatedAtDesc(user, pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CallLog> findById(Long id) {
        return callLogRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalCallCount(Long userId) {
        User user = getUserById(userId);
        return callLogRepository.countByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getSuccessCallCount(Long userId) {
        User user = getUserById(userId);
        return callLogRepository.countSuccessByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getSuccessRate(Long userId) {
        Long totalCalls = getTotalCallCount(userId);
        if (totalCalls == 0) {
            return 0.0;
        }

        Long successCalls = getSuccessCallCount(userId);
        return (successCalls.doubleValue() / totalCalls.doubleValue()) * 100.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageWakeTime(Long userId) {
        User user = getUserById(userId);
        Double avgHour = callLogRepository.findAverageWakeTimeByUser(user);
        return avgHour != null ? avgHour : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCallCountAfterDate(Long userId, LocalDateTime date) {
        User user = getUserById(userId);
        return callLogRepository.countByUserAfterDate(user, date);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getSuccessCallCountAfterDate(Long userId, LocalDateTime date) {
        User user = getUserById(userId);
        return callLogRepository.countSuccessByUserAfterDate(user, date);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCurrentCallSnoozeCount(Long userId) {
        try {
            // 현재 진행 중인 통화 (callEnd가 null인 통화) 조회
            Optional<CallLog> currentCall = callLogRepository.findByUserIdAndCallEndIsNull(userId);

            if (currentCall.isPresent()) {
                return currentCall.get().getSnoozeCount();
            }

            // 진행 중인 통화가 없으면 0 반환
            return 0;
        } catch (Exception e) {
            log.warn("Failed to get current call snooze count for user {}: {}", userId, e.getMessage());
            return 0;
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }
}