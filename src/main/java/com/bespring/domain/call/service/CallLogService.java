package com.bespring.domain.call.service;

import com.bespring.domain.call.dto.request.CallLogCreateRequest;
import com.bespring.domain.call.entity.CallLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CallLogService {

    CallLog createCallLog(Long userId, CallLogCreateRequest request);

    Page<CallLog> getCallLogs(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Optional<CallLog> findById(Long id);

    Long getTotalCallCount(Long userId);

    Long getSuccessCallCount(Long userId);

    Double getSuccessRate(Long userId);

    Double getAverageWakeTime(Long userId);

    Long getCallCountAfterDate(Long userId, LocalDateTime date);

    Long getSuccessCallCountAfterDate(Long userId, LocalDateTime date);

    // 현재 진행 중인 통화의 스누즈 횟수 조회
    int getCurrentCallSnoozeCount(Long userId);
}