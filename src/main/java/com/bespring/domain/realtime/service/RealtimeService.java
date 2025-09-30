package com.bespring.domain.realtime.service;

import com.bespring.domain.alarm.entity.UserAlarm;
import com.bespring.domain.alarm.service.UserAlarmService;
import com.bespring.domain.call.service.CallLogService;
import com.bespring.domain.realtime.dto.SessionResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealtimeService {

    private final UserAlarmService userAlarmService;
    private final CallLogService callLogService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key:demo_key}")
    private String openaiApiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/realtime/sessions}")
    private String openaiApiUrl;

    // alarmId와 (선택) snoozeCount를 받아서 OpenAI API 호출
    public SessionResponse createSession(Long userId, Long alarmId, Integer snoozeCount) {
        // 1. 알람 정보 조회
        UserAlarm alarm = userAlarmService.getAlarm(userId, alarmId);

        // 2. 스누즈 횟수 결정: 요청값 우선, 없으면 진행 중 통화 기준으로 조회
        int resolvedSnooze = 0;
        if (snoozeCount != null) {
            // clamp to [0,3]
            resolvedSnooze = Math.max(0, Math.min(3, snoozeCount));
        } else {
            resolvedSnooze = getSnoozeCountForCurrentCall(userId);
        }

        // 3. 스누즈 정보가 포함된 instructions 생성
        String finalInstructions = alarm.getInstructionsWithSnooze(resolvedSnooze);

        // 4. OpenAI API 호출 (에러는 전역 예외 처리기로 위임)
        return callOpenAIRealtimeAPI(alarm.getVoice().getApiValue(), finalInstructions, alarmId);
    }

    private SessionResponse callOpenAIRealtimeAPI(String voice, String instructions, Long alarmId) {
        // OpenAI Realtime API 요청 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-realtime-preview-2024-12-17");

        // Realtime API supports only ["text"] or ["audio","text"]
        requestBody.put("modalities", java.util.List.of("audio", "text"));

        requestBody.put("voice", voice);
        requestBody.put("instructions", instructions);

        // Note: Realtime sessions API does not accept arbitrary 'metadata' field; omit to avoid 400

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);
        headers.add("OpenAI-Beta", "realtime=v1");

        // Debug: log request
        try {
            log.info("Calling OpenAI Realtime sessions API: {}", openaiApiUrl);
            log.info("OpenAI request body: {}", objectMapper.writeValueAsString(requestBody));
        } catch (Exception ignore) {
            log.warn("Failed to serialize OpenAI request body for logging");
        }

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // OpenAI API 호출
        ResponseEntity<String> response = restTemplate.exchange(
                openaiApiUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // 응답 파싱 (체크 예외는 런타임으로 래핑)
        JsonNode responseJson;
        try {
            responseJson = objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OpenAI response", e);
        }

        String ephemeralKey = responseJson.path("client_secret").path("value").asText();
        String sessionId = responseJson.path("id").asText();
        Long expiresIn = responseJson.path("client_secret").path("expires_at").asLong();

        log.info("Successfully created OpenAI session for alarm {}: sessionId={}", alarmId, sessionId);

        return new SessionResponse(ephemeralKey, sessionId, expiresIn);
    }

    private int getSnoozeCountForCurrentCall(Long userId) {
        try {
            // 현재 진행 중인 통화가 있는지 확인하고 스누즈 횟수 반환
            // (CallLogService에 해당 메서드가 있다고 가정)
            return callLogService.getCurrentCallSnoozeCount(userId);
        } catch (Exception e) {
            log.warn("Failed to get snooze count for user {}: {}", userId, e.getMessage());
            return 0;
        }
    }

    // 과거: 실패 시 mock 세션을 반환했으나 현재는 예외를 전파하여 전역 예외 처리기로 응답합니다.
}
