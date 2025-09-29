package com.bespring.domain.call.service;

import com.bespring.domain.call.dto.request.CallEndRequest;
import com.bespring.domain.call.dto.request.CallStartRequest;
import com.bespring.domain.call.dto.request.TranscriptRequest;
import com.bespring.domain.call.dto.response.CallDetailResponse;
import com.bespring.domain.call.dto.response.CallStartResponse;
import com.bespring.domain.call.entity.CallLog;
import com.bespring.domain.call.repository.CallLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bespring.domain.user.entity.User;
import com.bespring.domain.user.repository.UserRepository;
import com.bespring.global.exception.CustomException;
import com.bespring.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CallService {

    private final CallLogRepository callLogRepository;
    private final UserRepository userRepository;

    public CallStartResponse startCall(Long userId, CallStartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 진행 중인 통화가 있는지 확인
        if (callLogRepository.existsByUserIdAndCallEndIsNull(userId)) {
            throw new CustomException(ErrorCode.CALL_ALREADY_IN_PROGRESS);
        }

        // 새 통화 로그 생성 (아직 대화 내용은 없음)
        CallLog callLog = CallLog.builder()
                .user(user)
                .callStart(LocalDateTime.now())
                .result(CallLog.CallResult.SUCCESS) // 임시, 종료 시 실제 결과로 업데이트
                .snoozeCount(0)
                .build();

        CallLog savedCallLog = callLogRepository.save(callLog);

        log.info("Started call for user {}: callId={}, sessionId={}",
                userId, savedCallLog.getId(), request.getSessionId());

        return new CallStartResponse(
                savedCallLog.getId(),
                request.getSessionId(),
                savedCallLog.getCallStart()
        );
    }

    public void saveTranscript(Long userId, Long callId, TranscriptRequest request) {
        CallLog callLog = callLogRepository.findByIdAndUserId(callId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CALL_NOT_FOUND));

        // 이미 종료된 통화인지 확인
        if (callLog.getCallEnd() != null) {
            throw new CustomException(ErrorCode.CALL_ALREADY_ENDED);
        }

        // 대화 내용을 JSON으로 변환하여 저장 (기존 엔티티 업데이트)
        String conversationJson;
        try {
            conversationJson = new ObjectMapper().writeValueAsString(request.getConversation());
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize transcript for call {}: {}", callId, e.getMessage());
            conversationJson = "[]";
        }

        CallLog updatedCallLog = CallLog.builder()
                .id(callLog.getId())
                .user(callLog.getUser())
                .callStart(callLog.getCallStart())
                .callEnd(callLog.getCallEnd())
                .result(callLog.getResult())
                .snoozeCount(callLog.getSnoozeCount())
                .conversationData(conversationJson)
                .createdAt(callLog.getCreatedAt())
                .build();

        callLogRepository.save(updatedCallLog);

        log.info("Saved transcript for call {}: {} utterances", callId, request.getConversation().size());
    }

    public void endCall(Long userId, Long callId, CallEndRequest request) {
        CallLog callLog = callLogRepository.findByIdAndUserId(callId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CALL_NOT_FOUND));

        // 이미 종료된 통화인지 확인
        if (callLog.getCallEnd() != null) {
            throw new CustomException(ErrorCode.CALL_ALREADY_ENDED);
        }

        // Snooze Policy Enforcement: 3회 이상 스누즈 시 자동 FAIL_SNOOZE 처리 (0점)
        int requestedSnooze = request.getSnoozeCount() == null ? 0 : request.getSnoozeCount();
        // clamp to [0,3]
        int enforcedSnoozeCount = Math.max(0, Math.min(3, requestedSnooze));
        CallLog.CallResult effectiveResult = request.getResult();
        if (enforcedSnoozeCount >= 3) {
            effectiveResult = CallLog.CallResult.FAIL_SNOOZE;
        }

        // 통화 종료 정보 업데이트
        CallLog updatedCallLog = CallLog.builder()
                .id(callLog.getId())
                .user(callLog.getUser())
                .callStart(callLog.getCallStart())
                .callEnd(request.getCallEnd())
                .result(effectiveResult)
                .snoozeCount(enforcedSnoozeCount)
                .conversationData(callLog.getConversationData())
                .createdAt(callLog.getCreatedAt())
                .build();

        callLogRepository.save(updatedCallLog);

        log.info("Ended call {}: result={}, duration={}min (snooze: req={}, enforced={})",
                callId, effectiveResult,
                java.time.Duration.between(callLog.getCallStart(), request.getCallEnd()).toMinutes());

        // 포인트 자동 지급은 비활성화 (요청에 따라 제거)
    }

    @Transactional(readOnly = true)
    public CallDetailResponse getCallDetail(Long userId, Long callId) {
        CallLog callLog = callLogRepository.findByIdAndUserId(callId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CALL_NOT_FOUND));

        return new CallDetailResponse(
                callLog.getId(),
                callLog.getCallStart(),
                callLog.getCallEnd(),
                callLog.getResult(),
                callLog.getSnoozeCount(),
                callLog.getConversationList(),
                callLog.getCreatedAt()
        );
    }
}
