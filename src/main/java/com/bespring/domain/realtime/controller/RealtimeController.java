package com.bespring.domain.realtime.controller;

import com.bespring.domain.realtime.dto.SessionResponse;
import com.bespring.domain.realtime.service.RealtimeService;
import com.bespring.global.dto.ApiResponse;
import com.bespring.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OpenAI Realtime API", description = "OpenAI Realtime API 세션 관리")
@Slf4j
@RestController
@RequestMapping("/api/realtime")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RealtimeController {

    private final RealtimeService realtimeService;

    @Operation(
            summary = "OpenAI Realtime API 세션 생성",
            description = "음성 통화를 위한 OpenAI ephemeral key를 발급합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "세션 생성 성공"
            )
    })
    @PostMapping("/session")
    public ResponseEntity<ApiResponse<SessionResponse>> createSession(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "알람 ID") @RequestParam Long alarmId,
            @Parameter(description = "현재 스누즈 횟수 (선택, 0-3)") @RequestParam(required = false) Integer snoozeCount) {

        Long userId = userPrincipal.getUserId();
        log.info("Creating OpenAI realtime session for user: {}, alarmId: {}, snoozeCount: {}", userId, alarmId, snoozeCount);

        SessionResponse sessionResponse = realtimeService.createSession(userId, alarmId, snoozeCount);

        return ResponseEntity.status(201).body(ApiResponse.success(sessionResponse));
    }
}
