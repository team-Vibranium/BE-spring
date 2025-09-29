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

        // 4. OpenAI API 호출
        try {
            return callOpenAIRealtimeAPI(alarm.getVoice().getApiValue(), finalInstructions, alarmId);
        } catch (Exception e) {
            log.error("Failed to create OpenAI session for user {}, alarm {}: {}",
                    userId, alarmId, e.getMessage());

            // Fallback: Mock 응답
            return createMockSession(alarmId);
        }
    }

    private SessionResponse callOpenAIRealtimeAPI(String voice, String instructions, Long alarmId) throws Exception {
        // OpenAI Realtime API 요청 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-realtime-preview-2024-12-17");

        Map<String, Object> modalities = Map.of("audio", true, "text", false);
        requestBody.put("modalities", modalities);

        requestBody.put("voice", voice);
        requestBody.put("instructions", instructions);

        // 세션 메타데이터
        Map<String, Object> metadata = Map.of("alarm_id", alarmId);
        requestBody.put("metadata", metadata);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // OpenAI API 호출
        ResponseEntity<String> response = restTemplate.exchange(
                openaiApiUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // 응답 파싱
        JsonNode responseJson = objectMapper.readTree(response.getBody());

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

    // Mock 세션 생성 (OpenAI API 실패 시 Fallback)
    private SessionResponse createMockSession(Long alarmId) {
        String sessionId = "sess_mock_" + alarmId + "_" + System.currentTimeMillis();
        String ephemeralKey = "eph_mock_" + sessionId.replace("-", "");
        Long expiresIn = 900L; // 15분

        log.warn("Using mock session for alarm {}: sessionId={}", alarmId, sessionId);

        return new SessionResponse(ephemeralKey, sessionId, expiresIn);
    }
}
